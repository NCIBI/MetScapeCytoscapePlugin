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

import java.util.Arrays;

import javax.swing.JOptionPane;

import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.network.ExpandInExistingNetworkTranslator;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.ws.client.MetabolicNetworkService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;

public class ExpandInExistingNetworkTask extends AbstractTask {

	private String cid;
	private CyNetwork network;

	public static boolean expandUsing(String cid, CyNetwork network) {
		ExpandInExistingNetworkTask task = new ExpandInExistingNetworkTask(network,cid);
		return configureAndRunTask(task);
	}
	
	private ExpandInExistingNetworkTask(CyNetwork network, String cid) {
		this.network = network;
		this.cid = cid;
	}

	public String getTitle() {
		return "Expand In Existing Network";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Querying database...");
			NetworkData networkData = MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network));
			int taxid = networkData.getOrganism().getTaxid();
			NetworkType networkType = networkData.getNetworkType();
			
			Response<MetabolicNetwork> serverResponse;
			MetabolicNetworkService service = new MetabolicNetworkService(
					HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			serverResponse = service.retrieveNetworkOfTypeForCidsAndGeneids(
					networkType, Arrays.asList(cid), 
					null, taxid);
			
			if (serverResponse == null || !serverResponse.getResponseStatus().isSuccess() || interrupted)
				throw new Exception();

			
			MetabolicNetwork sourceNetwork = serverResponse.getResponseValue();
			if (sourceNetwork == null || interrupted)
				throw new Exception();
			
			ExpandInExistingNetworkTranslator translator = new ExpandInExistingNetworkTranslator(
					sourceNetwork, network, cid);
			network = translator.doTranslate();
			
			if (network == null) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
						"No expansion relations were found for the selected node.", 
						"Relations Not Found", JOptionPane.INFORMATION_MESSAGE); 
			}
			
			else {
				CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
				//workaround for edge label redraw problem
				VisualStyle style = view.getVisualStyle();
				Calculator edgeLabelCalculator = style.getEdgeAppearanceCalculator().getCalculator(VisualPropertyType.EDGE_LABEL);
				style.getEdgeAppearanceCalculator().removeCalculator(VisualPropertyType.EDGE_LABEL);
				view.redrawGraph(true,true);
				style.getEdgeAppearanceCalculator().setCalculator(edgeLabelCalculator);
				view.redrawGraph(true,true);
				CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
				layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, network);
			}
			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "Network expansion failed");
		}

	}
}
