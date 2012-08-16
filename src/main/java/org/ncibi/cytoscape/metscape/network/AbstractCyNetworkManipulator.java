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
import org.ncibi.cytoscape.metscape.data.Concept;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

public class AbstractCyNetworkManipulator {
	
	protected NetworkData networkData;
	
	public AbstractCyNetworkManipulator(NetworkData networkData) {
		this.networkData = networkData;
	}
	
	public void mapNodeToRelatedConcepts(CyNode cyNode, CyNetwork network) {
		List<Concept> concepts = networkData.getConceptMapping().getConcepts(cyNode.getIdentifier());
		if(concepts != null)
			for(Concept concept: concepts) {
				CyGroup group = CyGroupManager.findGroup(Networks.getUUID(network) + "-" + concept.getConceptName());
				if(group == null) {
					group = CyGroupManager.createGroup
					(Networks.getUUID(network) + "-" + concept.getConceptName(), null, network);
					CyNode groupNode = group.getGroupNode();
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.name", concept.getConceptName());
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.numUniqueGenes", concept.getNumUniqueGenes());
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork", 0);
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.pvalue", concept.getPvalue());
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.fdr", concept.getFdr());
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.direction", concept.getDirection());
				}
				if(Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Type").equals("Gene")) {
					CyNode groupNode = group.getGroupNode();
					Integer numGenesInNetwork = Attributes.node.getIntegerAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork");
					Attributes.node.setAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork", ++numGenesInNetwork);
				}
				group.addNode(cyNode);
			}
	}

}
