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

import java.util.HashSet;
import java.util.Set;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

public abstract class AbstractCyNodeCollapser implements CyNodeCollapser {
	protected CyNetwork network;
	
	public abstract CyNetwork doCollapse();
	
	public AbstractCyNodeCollapser(CyNetwork network) {
		this.network = network;
	}
	
	protected void removeNodeFromNetwork(CyNode cyNode, CyNetwork network) {
		network.hideNode(cyNode);
		if(cyNode.getGroups() != null) {
			Set<CyGroup> groupSet = new HashSet<CyGroup>(CyGroupManager.getGroupList(network));
			groupSet.retainAll(cyNode.getGroups());
			for(CyGroup group: groupSet) {
				if(Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Type").equals("Gene")) {
            		CyNode groupNode = group.getGroupNode();
            		Integer numGenesInNetwork = Attributes.node.getIntegerAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork");
            		Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork", --numGenesInNetwork);
            	}
				group.removeNode(cyNode);
				if(group.getNodes().isEmpty())
					CyGroupManager.removeGroup(group);
			}
		}
		Attributes.node.deleteAttribute(cyNode.getIdentifier(), "Category." + Networks.getUUID(network));
		Attributes.node.deleteAttribute(cyNode.getIdentifier(), "direction." + Networks.getUUID(network));
		Attributes.node.deleteAttribute(cyNode.getIdentifier(), "isSignificant." + Networks.getUUID(network));
		Attributes.node.deleteAttribute(cyNode.getIdentifier(), "inExpansionFor." + Networks.getUUID(network));
		Attributes.node.deleteAttribute(cyNode.getIdentifier(), "isExpansion." + Networks.getUUID(network));
	}
	
	protected void removeEdgeFromNetwork(CyEdge cyEdge, CyNetwork network) {
		network.hideEdge(cyEdge);
		Attributes.edge.deleteAttribute(cyEdge.getIdentifier(), "inExpansionFor." + Networks.getUUID(network));
		Attributes.edge.deleteAttribute(cyEdge.getIdentifier(), "isExpansion." + Networks.getUUID(network));
	}
}
