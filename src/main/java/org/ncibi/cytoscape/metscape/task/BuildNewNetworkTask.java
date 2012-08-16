/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product may includes software developed by others; in that case see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
package org.ncibi.cytoscape.metscape.task;

import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;

import org.ncibi.cytoscape.metscape.action.ConceptFilterAction;
import org.ncibi.cytoscape.metscape.action.PathwayFilterAction;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.network.DefaultNetworkTranslator;
import org.ncibi.cytoscape.metscape.network.NetworkTranslator;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.pathway.Pathway;
import org.ncibi.metab.ws.client.MetabolicNetworkService;
import org.ncibi.metab.ws.client.MetabolicPathwayNetworkService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;

public class BuildNewNetworkTask extends AbstractBuildNetworkTask {

	private NetworkData networkData;
	private Pathway pathway;

	public static boolean buildUsing(NetworkData networkData) {
		BuildNewNetworkTask task = new BuildNewNetworkTask(networkData);
		return configureAndRunTask(task);
	}
	
	private BuildNewNetworkTask(NetworkData networkData) {
		this.networkData = networkData;
		this.pathway = null;
	}
	
	public static boolean buildUsing(NetworkData networkData, Pathway pathway) {
		BuildNewNetworkTask task = new BuildNewNetworkTask(networkData, pathway);
		return configureAndRunTask(task);
	}
	
	private BuildNewNetworkTask(NetworkData networkData, Pathway pathway) {
		this.networkData = networkData;
		this.pathway = pathway;
	}

	public String getTitle() {
		return "Build Network";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Querying database...");
			Response<MetabolicNetwork> serverResponse;
			String networkName;
			if(pathway == null) {
				MetabolicNetworkService service = new MetabolicNetworkService(
						HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
				serverResponse = service.retrieveNetworkOfTypeForCidsAndGeneids(
						networkData.getNetworkType(), 
						networkData.getCids(), networkData.getGeneids(),
						networkData.getOrganism().getTaxid());
				networkName = createNetworkName(networkData.getNetworkType());
			}
			else {
				MetabolicPathwayNetworkService service = new MetabolicPathwayNetworkService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
				serverResponse = service.retrieveNetworkForPathwayId(pathway.getId(),
						networkData.getNetworkType(),
						networkData.getOrganism().getTaxid());
				networkName = createNetworkName(networkData.getNetworkType(),pathway);
			}
			if (serverResponse == null || !serverResponse.getResponseStatus().isSuccess() || interrupted)
				throw new Exception();

			taskMonitor.setStatus("Building network...");
			MetabolicNetwork sourceNetwork = serverResponse.getResponseValue();
			if (sourceNetwork == null || interrupted)
				throw new Exception();

			NetworkTranslator translator = new DefaultNetworkTranslator(networkData, sourceNetwork, networkName);
			CyNetwork network = translator.doTranslate();
			
			if (network == null || interrupted)
				throw new Exception();
			configureNetwork(network);
			
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			cytoPanel.setSelectedIndex(0);
			
			CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			JInternalFrame networkFrame = 
				Cytoscape.getDesktop().getNetworkViewManager().getInternalFrame(view);
			networkFrame.setMaximum(true);
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					PathwayFilterAction.exec();
					if(!networkData.getConceptMapping().isEmpty()) {
						ConceptFilterAction.exec();
					}
				}
			});
			
			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "Network build failed");
		}

	}
	
	private String createNetworkName(NetworkType networkType) {
		return CyNetworkNaming.getSuggestedNetworkTitle(networkType.toLongName());
	}
	
	private String createNetworkName(NetworkType networkType, Pathway pathway) {
		return CyNetworkNaming.getSuggestedNetworkTitle
			(pathway.getName() + " (" + networkType.toLongName() + ")");
	}
}
