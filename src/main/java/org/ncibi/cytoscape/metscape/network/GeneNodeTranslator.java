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

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.GeneMapping;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.metab.network.attribute.GeneAttribute;
import org.ncibi.metab.network.node.GeneNode;
import org.ncibi.metab.network.node.MetabolicNode;

public class GeneNodeTranslator extends AbstractNodeTranslator {

    
	private GeneMapping geneMapping;
	private Organism organism;
	
	public GeneNodeTranslator(GeneMapping geneMapping, Organism organism) {
		this.geneMapping = geneMapping;
		this.organism = organism;
	}
	
	protected String getCanonicalName(MetabolicNode node) {
    	return GeneNode.getSymbol(node);
    }

	protected void addAttributes(String id, MetabolicNode node){
		Integer orgId = GeneNode.getOrganismGeneid(node);
		
		if(GeneNode.getHomologGeneid(node) != null) {
			Attributes.node.setAttribute(id, "Gene." + organism.toString().toLowerCase() 
					+ ".geneid", GeneNode.getHomologGeneid(node));
		}
		Attributes.node.setAttribute(id, GeneAttribute.GENEID.toAttributeName(), GeneNode.getGeneid(node));
		Attributes.node.setAttribute(id, GeneAttribute.SYMBOL.toAttributeName(), GeneNode.getSymbol(node));
		Attributes.node.setAttribute(id, GeneAttribute.DESCRIPTION.toAttributeName(), GeneNode.getDescription(node));
		Attributes.node.setListAttribute(id, GeneAttribute.LOCATIONS.toAttributeName(), GeneNode.getLocations(node));
		String name = geneMapping.getName();
		if(geneMapping.getData(orgId) != null) {
			Double[] data = geneMapping.getData(orgId);
			String[] columns = geneMapping.getColumns();
			Attributes.node.setAttribute(id, "Gene." + name, true);
			for(int i=0; i<data.length; i++) {
				Attributes.node.setAttribute
				(id,"Gene." + name + "." + columns[i], NumUtils.truncate(data[i]));
			}
		}
	}
}
