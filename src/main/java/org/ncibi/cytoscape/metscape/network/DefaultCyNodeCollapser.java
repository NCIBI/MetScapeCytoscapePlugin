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

import java.util.List;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.CyAttributesUtils.AttributeType;

public class DefaultCyNodeCollapser extends AbstractCyNodeCollapser {
	
	private String cid;
	
	public DefaultCyNodeCollapser(CyNetwork network, String cid) {
		super(network);
		this.cid = cid;
	}
	
	@Override
	public CyNetwork doCollapse() {
		return doCollapse(network,cid);
	}

	private CyNetwork doCollapse(CyNetwork network, String cid) {
		List<String> expansionNodeIds = CyAttributesUtils.getIDListFromAttributeValue
		(AttributeType.NODE, "inExpansionFor." + Networks.getUUID(network), cid);
		List<String> expansionEdgeIds = CyAttributesUtils.getIDListFromAttributeValue
		(AttributeType.EDGE, "inExpansionFor." + Networks.getUUID(network), cid);

		for (String id : expansionNodeIds) {
			List<?> inExpansionFor = Attributes.node
			.getListAttribute(id, "inExpansionFor." + Networks.getUUID(network));
			inExpansionFor.remove(cid);
			if (inExpansionFor.isEmpty()) {
				removeNodeFromNetwork(Cytoscape.getCyNode(id), network);
				Boolean isExpansionSeed = Attributes.node.hasAttribute(id, "isExpansionSeed." + Networks.getUUID(network));
				if (isExpansionSeed) {
					doCollapse(network, id);
				}
			} else {
				Attributes.node.setListAttribute(id, "inExpansionFor." + Networks.getUUID(network), inExpansionFor);
			}
		}

		for (String id : expansionEdgeIds) {
			List<?> inExpansionFor = Attributes.edge
			.getListAttribute(id, "inExpansionFor." + Networks.getUUID(network));
			inExpansionFor.remove(cid);
			if (inExpansionFor.isEmpty()) {
				removeEdgeFromNetwork(Cytoscape.getRootGraph().getEdge(id),network);
			} else {
				Attributes.edge.setListAttribute(id, "inExpansionFor." + Networks.getUUID(network), inExpansionFor);
			}
		}
		Attributes.node.deleteAttribute(cid, "isExpansionSeed." + Networks.getUUID(network));
		
		return network;
	}

}
