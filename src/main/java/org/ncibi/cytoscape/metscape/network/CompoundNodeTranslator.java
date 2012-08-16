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
import org.ncibi.cytoscape.metscape.data.CompoundMapping;
import org.ncibi.metab.network.attribute.CompoundAttribute;
import org.ncibi.metab.network.node.CompoundNode;
import org.ncibi.metab.network.node.MetabolicNode;

public class CompoundNodeTranslator extends AbstractNodeTranslator implements NodeTranslator
{
	private CompoundMapping compoundMapping;
	
	public CompoundNodeTranslator(CompoundMapping compoundMapping) {
		this.compoundMapping = compoundMapping;
	}
	
    @Override
    protected String getCanonicalName(MetabolicNode node)
    {
        return CompoundNode.getName(node);
    }

    @Override
    protected void addAttributes(String id, MetabolicNode node)
    {
    	Attributes.node.setAttribute(id, CompoundAttribute.CID.toAttributeName(), CompoundNode.getCid(node));
        Attributes.node.setAttribute(id, CompoundAttribute.FORMULA.toAttributeName(),CompoundNode.getFormula(node));
        Attributes.node.setAttribute(id, CompoundAttribute.MOLECULAR_WEIGHT.toAttributeName(), CompoundNode.getMolecularWeight(node));
        Attributes.node.setAttribute(id, CompoundAttribute.APPROXIMATE_MOLECULAR_WEIGHT.toAttributeName(),CompoundNode.getApproximateMolecularWeight(node));
        Attributes.node.setAttribute(id, CompoundAttribute.NAME.toAttributeName(), CompoundNode.getName(node));
        Attributes.node.setAttribute(id, CompoundAttribute.SMILES.toAttributeName(), CompoundNode.getSmiles(node));
        Attributes.node.setAttribute(id, CompoundAttribute.CASNUM.toAttributeName(), CompoundNode.getCasnum(node));
        Attributes.node.setAttribute(id, CompoundAttribute.PUBCHEMCID.toAttributeName(), CompoundNode.getPubchemCid(node));
        Attributes.node.setAttribute(id, CompoundAttribute.BIOCYCID.toAttributeName(), CompoundNode.getBiocycId(node));
        Attributes.node.setAttribute(id, CompoundAttribute.CHEBIID.toAttributeName(), CompoundNode.getChebiId(node));
        Attributes.node.setAttribute(id, CompoundAttribute.HMDBID.toAttributeName(), CompoundNode.getHmdbId(node));
        Attributes.node.setListAttribute(id, CompoundAttribute.SYNONYMS.toAttributeName(), CompoundNode.getSynonyms(node));
        String name = compoundMapping.getName();
        if(compoundMapping.getData(id) != null) {
        	Double[] data = compoundMapping.getData(id);
        	String[] columns = compoundMapping.getColumns();
        	Attributes.node.setAttribute(id, "Compound." + name, true);
        	for (int i = 0; i < data.length; i++)
        	{
        		Attributes.node.setAttribute(id, "Compound." + name + "." + columns[i], NumUtils.truncate(data[i]));
        	}
        }
    }

}
