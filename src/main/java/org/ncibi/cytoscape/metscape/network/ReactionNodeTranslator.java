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
import org.ncibi.metab.network.attribute.ReactionAttribute;
import org.ncibi.metab.network.node.MetabolicNode;
import org.ncibi.metab.network.node.ReactionNode;

public class ReactionNodeTranslator extends AbstractNodeTranslator {

	@Override
    public String getCanonicalName(MetabolicNode node) {
    	return node.getId();
    }

	@Override
	public void addAttributes(String id, MetabolicNode node) {
		Attributes.node.setAttribute(id, ReactionAttribute.PATHWAY.toAttributeName(),
                ReactionNode.getPathway(node));
		Attributes.node.setAttribute(id, ReactionAttribute.REVERSIBLE.toAttributeName(),
				ReactionNode.isReversible(node));
		Attributes.node.setAttribute(id, ReactionAttribute.RID.toAttributeName(),
				ReactionNode.getRid(node));
		Attributes.node.setAttribute(id, ReactionAttribute.EQUATION.toAttributeName(),
				ReactionNode.getEquation(node));
		Attributes.node.setListAttribute(id, ReactionAttribute.LOCATIONS.toAttributeName(),
				ReactionNode.getLocations(node));
	}



}
