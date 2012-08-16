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
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.edge.MetabolicEdge;
import org.ncibi.metab.network.node.MetabolicNode;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class DefaultNetworkTranslator extends AbstractNetworkTranslator implements NetworkTranslator
{
	private String networkName;

	public DefaultNetworkTranslator(NetworkData networkData, MetabolicNetwork sourceNetwork, String networkName)
    {
        super(networkData, sourceNetwork);
        this.networkName = networkName;
    }

    @Override
    public CyNetwork doTranslate()
    {
		
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
		
        CyNetwork network = Networks.createNetwork(nodesList, edgesList, networkName);
        hideAttributes(network);
		
        for(CyNode cyNode: nodesList) {
        	String category = Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Type");
        	
            if( networkData.getCids().contains(cyNode.getIdentifier()) ||
            	networkData.getGeneids().contains(Attributes.node.getAttribute(cyNode.getIdentifier(),"Gene."+ 
            		networkData.getOrganism().toString().toLowerCase()+".geneid")))
            	category = "Input " + category;
            Attributes.node.setAttribute
            	(cyNode.getIdentifier(), "Category." + Networks.getUUID(network), category);
        	mapNodeToRelatedConcepts(cyNode, network);
        	setNodeSignificanceAndDirectionAttribute(cyNode,network);
        }
        MetScapePlugin.getPluginData().addNetworkData(Networks.getUUID(network), networkData);
        return network;
    }
}
