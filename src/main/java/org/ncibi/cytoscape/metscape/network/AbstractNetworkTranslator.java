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
import org.ncibi.cytoscape.metscape.data.DataParameters;
import org.ncibi.cytoscape.metscape.data.MultiColumnData;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.attribute.CompoundAttribute;
import org.ncibi.metab.network.edge.MetabolicEdge;
import org.ncibi.metab.network.node.MetabolicNode;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public abstract class AbstractNetworkTranslator extends AbstractCyNetworkManipulator implements NetworkTranslator
{
	protected MetabolicNetwork sourceNetwork;
	
	private EdgeTranslator generalEdgeTranslator;
    private EdgeTranslator reactionEdgeTranslator;
    private EdgeTranslator enzymeReactionEdgeTranslator;
	private NodeTranslator compoundNodeTranslator;
	private NodeTranslator reactionNodeTranslator;
	private NodeTranslator enzymeNodeTranslator;
	private NodeTranslator geneNodeTranslator;
	
    public abstract CyNetwork doTranslate();

	public AbstractNetworkTranslator(NetworkData networkData, MetabolicNetwork sourceNetwork)
    {
        super(networkData);
        this.sourceNetwork = sourceNetwork;
        
        this.generalEdgeTranslator = new DefaultEdgeTranslator();
        this.reactionEdgeTranslator = new ReactionEdgeTranslator();
        this.enzymeReactionEdgeTranslator = new EnzymeReactionEdgeTranslator();
		this.compoundNodeTranslator = new CompoundNodeTranslator(networkData.getCompoundMapping());
		this.reactionNodeTranslator = new ReactionNodeTranslator();
		this.enzymeNodeTranslator = new EnzymeNodeTranslator();
		this.geneNodeTranslator = new GeneNodeTranslator(networkData.getGeneMapping(),networkData.getOrganism());
    }
    
    protected void setNodeSignificanceAndDirectionAttribute(CyNode node, CyNetwork network) {
		String type = Attributes.node.getStringAttribute(node.getIdentifier(), "Type");
		if(type.equalsIgnoreCase("Compound")){
			setNodeSignificanceAndDirectionAttribute(node, network, networkData.getCompoundMapping(), networkData.getCompoundParameters());
		}
		else if(type.equalsIgnoreCase("Gene")){
			setNodeSignificanceAndDirectionAttribute(node, network, networkData.getGeneMapping(), networkData.getGeneParameters());
		}
	}

	private void setNodeSignificanceAndDirectionAttribute(CyNode node, CyNetwork network, MultiColumnData data, DataParameters parameters) {
		Double pValue = Attributes.node.getDoubleAttribute(node.getIdentifier(), data.getFullyQualifiedName() + "." + parameters.getPvalueColumn());
		if(pValue != null) {
			if(parameters.getPvalueThreshold() == null || pValue <= parameters.getPvalueThreshold()) {
				Attributes.node.setAttribute(node.getIdentifier(),"isSignificant."+Networks.getUUID(network),true);
			}
		}
		else {
			Boolean hasData = Attributes.node.getBooleanAttribute(node.getIdentifier(), data.getFullyQualifiedName());
			if(hasData != null)
				Attributes.node.setAttribute(node.getIdentifier(), "isSignificant."+Networks.getUUID(network), true);
		}
		
		Double foldChange = Attributes.node.getDoubleAttribute(node.getIdentifier(), data.getFullyQualifiedName() + "." + parameters.getFoldChangeColumn());
		if(foldChange != null) {
			if(foldChange >= parameters.getFoldChangeUpThreshold()) {
				Attributes.node.setAttribute(node.getIdentifier(),"direction."+Networks.getUUID(network),"up");
			}
			else if(foldChange <= parameters.getFoldChangeDownThreshold()) {
				Attributes.node.setAttribute(node.getIdentifier(),"direction."+Networks.getUUID(network),"down");
			}
		}
	}
	
	protected void hideAttributes(CyNetwork network) {
		String uuid = Networks.getUUID(network);
		Attributes.node.setUserVisible(CompoundAttribute.PUBCHEMCID.toAttributeName(), false);
		Attributes.node.setUserVisible(CompoundAttribute.BIOCYCID.toAttributeName(), false);
		Attributes.node.setUserVisible(CompoundAttribute.CHEBIID.toAttributeName(), false);
		Attributes.node.setUserVisible(CompoundAttribute.HMDBID.toAttributeName(), false);
		Attributes.node.setUserVisible("Concept.name", false);
		Attributes.node.setUserVisible("Concept.numUniqueGenes", false);
		Attributes.node.setUserVisible("Concept.numGenesInNetwork", false);
		Attributes.node.setUserVisible("Concept.oddsRatio", false);
		Attributes.node.setUserVisible("Concept.direction", false);
		Attributes.node.setUserVisible("Concept.pvalue", false);
		Attributes.node.setUserVisible("Concept.fdr", false);
		Attributes.node.setUserVisible("__groupIsLocal", false);
		Attributes.node.setUserVisible("Category." + uuid, false);
		Attributes.node.setUserVisible("direction." + uuid, false);
		Attributes.node.setUserVisible("isSignificant." + uuid, false);
		Attributes.node.setUserVisible("inExpansionFor." + uuid, false);
		Attributes.node.setUserVisible("isExpansion." + uuid, false);
		Attributes.node.setUserVisible("isExpansionSeed." + uuid, false);
		Attributes.node.setUserVisible("isSubnetworkExpansionSeed." + uuid, false);
		Attributes.node.setUserVisible("Compound." + networkData.getCompoundMapping().getName(),false);
		Attributes.node.setUserVisible("Gene." + networkData.getGeneMapping().getName(),false);
		Attributes.edge.setUserVisible("Type", false);
		Attributes.edge.setUserVisible("direction", false);
		Attributes.edge.setUserVisible("inExpansionFor." + uuid, false);
		Attributes.edge.setUserVisible("isExpansion." + uuid, false);
	}

	protected NodeTranslator getTranslatorForNode(MetabolicNode node) {
		switch (node.getType()) {
		case COMPOUND:
			return compoundNodeTranslator;
		case REACTION:
			return reactionNodeTranslator;
		case ENZYME:
			return enzymeNodeTranslator;
		case GENE:
			return geneNodeTranslator;
		default:
			return null;
		}
	}

	protected EdgeTranslator getTranslatorForEdge(MetabolicEdge edge) {
		switch(edge.getType()) {
		case REACTION:
			return reactionEdgeTranslator;
		case ENZYME_REACTION:
			return enzymeReactionEdgeTranslator;
		default:
			return generalEdgeTranslator;
		}
	}
    
}
