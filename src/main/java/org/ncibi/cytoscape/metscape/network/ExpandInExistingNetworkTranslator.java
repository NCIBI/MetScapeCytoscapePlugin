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

public class ExpandInExistingNetworkTranslator extends AbstractNetworkTranslator {

	private CyNetwork network;
	private String cid;
	
	public ExpandInExistingNetworkTranslator(MetabolicNetwork sourceNetwork, CyNetwork network,  String cid){
		super(MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network)), 
				sourceNetwork);
		this.network = network;
		this.cid = cid;
	}
	
	@Override
	public CyNetwork doTranslate() {
		boolean networkExpanded = false;
		hideAttributes(network);
		for (MetabolicNode node : sourceNetwork.getAllNodes())
        {
			CyNode cyNode = getTranslatorForNode(node).doTranslate(node);
			String id = cyNode.getIdentifier();
			if(!network.containsNode(cyNode)) {
				networkExpanded = true;
				Attributes.node.setAttribute(cyNode.getIdentifier(), "Category." + Networks.getUUID(network), 
						Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Type"));
				Attributes.node.setAttribute(id, "isExpansion." + Networks.getUUID(network), true);
				List<Object> inExpansionFor = new ArrayList<Object>();
				if(Attributes.node.hasAttribute(id,"inExpansionFor." + Networks.getUUID(network))) 
					inExpansionFor.addAll
						(Attributes.node.getListAttribute(id, "inExpansionFor." + Networks.getUUID(network)));					
				if(!inExpansionFor.contains(cid)) {
					inExpansionFor.add(cid);
					Attributes.node.setListAttribute(id, "inExpansionFor." + Networks.getUUID(network), inExpansionFor);
				}
				network.addNode(cyNode);
				mapNodeToRelatedConcepts(cyNode, network);
				setNodeSignificanceAndDirectionAttribute(cyNode,network);
			}
        }
		
		for (MetabolicEdge edge : sourceNetwork.getEdges())
        {	
			CyEdge cyEdge = getTranslatorForEdge(edge).doTranslate(edge);
			String id = cyEdge.getIdentifier();
            if(!network.containsEdge(cyEdge)) {
            	Attributes.edge.setAttribute(id, "isExpansion." + Networks.getUUID(network), true);
            	List<Object> inExpansionFor = new ArrayList<Object>();
				if(Attributes.edge.hasAttribute(id,"inExpansionFor." + Networks.getUUID(network)))
					inExpansionFor.addAll
						(Attributes.edge.getListAttribute(id, "inExpansionFor." + Networks.getUUID(network)));					
				if(!inExpansionFor.contains(cid)) {
					inExpansionFor.add(cid);
					Attributes.edge.setListAttribute(id, "inExpansionFor." + Networks.getUUID(network), inExpansionFor);
				}
            	network.addEdge(cyEdge);
            }
        }
		if(networkExpanded) {
			Attributes.node.setAttribute(cid, "isExpansionSeed." + Networks.getUUID(network), true);
			return network;
		}
		else return null;
	}

}
