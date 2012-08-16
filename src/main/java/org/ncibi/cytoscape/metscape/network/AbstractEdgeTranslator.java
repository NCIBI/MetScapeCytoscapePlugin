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
import org.ncibi.metab.network.edge.MetabolicEdge;
import org.ncibi.metab.network.node.MetabolicNode;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

public abstract class AbstractEdgeTranslator implements EdgeTranslator
{
    protected abstract void addEdgeAttributes(String id, MetabolicEdge edge);
    protected abstract String getInteraction(MetabolicEdge edge);

    @Override
    public CyEdge doTranslate(MetabolicEdge edge)
    {
        MetabolicNode sourceNode = edge.getSource();
        CyNode source = Cytoscape.getCyNode(sourceNode.getId(), true);

        MetabolicNode targetNode = edge.getTarget();
        CyNode target = Cytoscape.getCyNode(targetNode.getId(), true);

        CyEdge cyEdge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION,
        		getInteraction(edge), true, edge.isDirected());
        addCommonAttributes(cyEdge.getIdentifier(), edge);
        addEdgeAttributes(cyEdge.getIdentifier(), edge);
        return cyEdge;
    }
    
    private void addCommonAttributes(String id, MetabolicEdge edge) {
    	Attributes.edge.setAttribute(id, "Type",
    			edge.getType().toDisplayName());
    	Attributes.edge.setAttribute(id, "direction",
    			edge.getDirection().toDirectionAttribute());
	}
}
