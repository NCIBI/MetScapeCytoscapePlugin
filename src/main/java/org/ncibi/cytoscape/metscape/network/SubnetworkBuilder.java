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

import java.util.Collection;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class SubnetworkBuilder extends AbstractCyNetworkManipulator {
	private Collection<?> nodes;
	private Collection<?> edges;
	private String networkName;
	private CyNetwork network;
	
	public SubnetworkBuilder(Collection<?> nodes, Collection<?> edges, String networkName, CyNetwork network) {
		super(MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network)));
		this.nodes = nodes;
		this.edges = edges;
		this.networkName = networkName;
		this.network = network;
	}
	
	public CyNetwork doBuild() {
		CyNetwork subnetwork = Networks.createNetwork(nodes, edges, networkName, network);
		
		for(Object node: subnetwork.nodesList()) {
			CyNode cyNode = (CyNode) node;
			mapNodeToRelatedConcepts(cyNode, subnetwork);
			Attributes.node.setAttribute(cyNode.getIdentifier(), "Category." + Networks.getUUID(subnetwork),
					Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Category." + Networks.getUUID(network)));
			Attributes.node.setAttribute(cyNode.getIdentifier(), "direction." + Networks.getUUID(subnetwork),
					Attributes.node.getStringAttribute(cyNode.getIdentifier(), "direction." + Networks.getUUID(network)));
			Attributes.node.setAttribute(cyNode.getIdentifier(), "isSignificant." + Networks.getUUID(subnetwork),
					Attributes.node.getBooleanAttribute(cyNode.getIdentifier(), "isSignificant." + Networks.getUUID(network)));
			
		}
		MetScapePlugin.getPluginData().addNetworkData(Networks.getUUID(subnetwork), networkData);
		return subnetwork;
	}
}
