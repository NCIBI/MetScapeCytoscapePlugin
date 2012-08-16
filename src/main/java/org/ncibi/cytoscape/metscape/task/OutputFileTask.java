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

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.network.attribute.GeneAttribute;
import org.ncibi.metab.network.edge.MetabolicEdge;
import org.ncibi.metab.network.node.CompoundNode;
import org.ncibi.metab.network.node.GeneNode;
import org.ncibi.metab.network.node.MetabolicNode;
import org.ncibi.metab.ws.client.MetabolicNetworkService;
import org.ncibi.metab.ws.client.MetabolicPathwayNetworkService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

public class OutputFileTask extends AbstractTask {

	private Collection<String> compoundIds;
	private Collection<Integer> geneIds;
	private String pathwayId;
	private File outputFile;

	public static boolean doOutput(Collection<String> compoundIds,
			Collection<Integer> geneIds, String pathwayId, File outputFile) {
		OutputFileTask task = new OutputFileTask(compoundIds, geneIds, pathwayId, outputFile);
		return configureAndRunTask(task);
	}
	
	private OutputFileTask(Collection<String> compoundIds, Collection<Integer> geneIds,
		String pathwayId, File outputFile) {
		this.compoundIds = compoundIds;
		this.geneIds = geneIds;
		this.pathwayId = pathwayId;
		this.outputFile = outputFile;
	}

	public String getTitle() {
		return "Output as File";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Querying database...");
			Response<MetabolicNetwork> serverResponse;
			if(pathwayId == null) {
				MetabolicNetworkService service = new MetabolicNetworkService(
						HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
				serverResponse = service.retrieveNetworkOfTypeForCidsAndGeneids(
						NetworkType.COMPOUND_GENE, compoundIds, geneIds, MetScapePlugin.getPluginData().getOrganism().getTaxid());
			}
			else {
				MetabolicPathwayNetworkService service = new MetabolicPathwayNetworkService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
				serverResponse = service.retrieveNetworkForPathwayId(pathwayId,NetworkType.COMPOUND_GENE,
						MetScapePlugin.getPluginData().getOrganism().getTaxid());
			}
			if (serverResponse == null || !serverResponse.getResponseStatus().isSuccess() || interrupted)
				throw new Exception();

			taskMonitor.setStatus("Building file...");
			MetabolicNetwork sourceNetwork = serverResponse.getResponseValue();
			if (sourceNetwork == null || interrupted)
				throw new Exception();
			DataFile base = new TextFile();
			base.setValue("Input Compound",0,0);
			base.setValue("Compound ID",0,1);
			base.setValue("Compound Name",0,2);
			base.setValue("Input Gene",0,3);
			base.setValue("Entrez Gene ID",0,4);
			base.setValue("Gene Symbol",0,5);
			base.setValue("Gene Description",0,6);
			int row = 1;
			SortedSet<MetabolicEdge> edges = 
				new TreeSet<MetabolicEdge>(new MetEdgeComparator());
			edges.addAll(sourceNetwork.getEdges());
			for(MetabolicEdge edge: edges) {
				MetabolicNode node1 = edge.getNode1();
				MetabolicNode node2 = edge.getNode2();
				base.setValue(compoundIds != null && 
						compoundIds.contains(CompoundNode.getCid(node1))?"Yes":"No", row, 0);
				base.setValue(CompoundNode.getCid(node1), row, 1);
				base.setValue(CompoundNode.getName(node1), row, 2);
				base.setValue(geneIds != null && 
						geneIds.contains(GeneNode.getOrganismGeneid(node2))?"Yes":"No", row, 3);
				base.setValue(GeneNode.getGeneid(node2), row, 4);
				base.setValue(GeneNode.getSymbol(node2), row, 5);
				base.setValue(GeneNode.getGeneAttribute
						(node2, GeneAttribute.DESCRIPTION), row, 6);
				row++;
				
				if(interrupted)
					throw new Exception();
			}
			base.save(outputFile);

			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "File output failed");
		}

	}
	
	private class MetEdgeComparator implements Comparator<MetabolicEdge> {
		public int compare(MetabolicEdge o1, MetabolicEdge o2) {
			if(CompoundNode.getCid(o1.getNode1()) != CompoundNode.getCid(o2.getNode1()))
				return CompoundNode.getCid(o1.getNode1()).
					compareTo(CompoundNode.getCid(o2.getNode1()));
			else
				return GeneNode.getGeneid(o1.getNode2()).
					compareTo(GeneNode.getGeneid(o2.getNode2()));
		}
	}
}
