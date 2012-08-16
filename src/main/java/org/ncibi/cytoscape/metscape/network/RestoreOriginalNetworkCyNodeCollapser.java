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

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class RestoreOriginalNetworkCyNodeCollapser extends AbstractCyNodeCollapser {

	public RestoreOriginalNetworkCyNodeCollapser(CyNetwork network) {
		super(network);
	}
	
	@Override
	public CyNetwork doCollapse() {
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			
			if(Attributes.node.hasAttribute
					(cyNode.getIdentifier(),"isExpansion." + Networks.getUUID(network))) {
				removeNodeFromNetwork(cyNode,network);
			}
			
			if(Attributes.node.hasAttribute
					(cyNode.getIdentifier(),"isExpansionSeed." + Networks.getUUID(network)) &&
				!Attributes.node.hasAttribute
					(cyNode.getIdentifier(), "isSubnetworkExpansionSeed." + Networks.getUUID(network))) {
				Attributes.node.deleteAttribute(cyNode.getIdentifier(), "isExpansionSeed." + Networks.getUUID(network));
			}
		}
		
		for(Object edge: network.edgesList()) {
			CyEdge cyEdge = (CyEdge) edge;
			if(Attributes.edge.hasAttribute
					(cyEdge.getIdentifier(),"isExpansion." + Networks.getUUID(network)))
				removeEdgeFromNetwork(cyEdge, network);
		}
		
		return network;
	}
}
