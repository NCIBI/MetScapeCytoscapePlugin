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
package org.ncibi.cytoscape.metscape.network;

import java.util.ArrayList;
import java.util.List;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.edge.MetabolicEdge;
import org.ncibi.metab.network.node.MetabolicNode;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class ExpandInNewNetworkTranslator extends AbstractNetworkTranslator {

	private String networkName;
	private CyNetwork network;
	private String cid;

	public ExpandInNewNetworkTranslator(MetabolicNetwork sourceNetwork, String networkName, CyNetwork network, String cid){
		super(MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network)), sourceNetwork);
		this.network = network;
		this.networkName = networkName;
		this.cid = cid;
	}

	@Override
	public CyNetwork doTranslate() {

		List<CyNode> nodesList = new ArrayList<CyNode>();
		List<CyEdge> edgesList = new ArrayList<CyEdge>();

		for (MetabolicNode node : sourceNetwork.getAllNodes())
		{
			CyNode cyNode = getTranslatorForNode(node).doTranslate(node);
			nodesList.add(cyNode);
		}

		for (MetabolicEdge edge : sourceNetwork.getEdges())
		{
			CyEdge cyEdge = getTranslatorForEdge(edge).doTranslate(edge);
			edgesList.add(cyEdge);
		}
		
		CyNetwork subnetwork = Networks.createNetwork(nodesList, edgesList, networkName, network);
		hideAttributes(subnetwork);
				
		for(CyNode cyNode: nodesList) {
			String category = Attributes.node.getStringAttribute
			(cyNode.getIdentifier(), "Category." + Networks.getUUID(network));
			if(category == null)
				category = Attributes.node.getStringAttribute
					(cyNode.getIdentifier(), "Type");

			Attributes.node.setAttribute(cyNode.getIdentifier(), "Category." + Networks.getUUID(subnetwork), category);
			mapNodeToRelatedConcepts(cyNode, subnetwork);
			setNodeSignificanceAndDirectionAttribute(cyNode,network);
		}
		Attributes.node.setAttribute(cid, "isExpansionSeed." + Networks.getUUID(subnetwork), true);
		Attributes.node.setAttribute(cid, "isSubnetworkExpansionSeed." + Networks.getUUID(subnetwork), true);
		MetScapePlugin.getPluginData().addNetworkData(Networks.getUUID(subnetwork), networkData);

		return subnetwork;
	}
}
