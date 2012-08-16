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
package org.ncibi.cytoscape.metscape.ui;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;

import org.ncibi.cytoscape.data.Attributes;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import ding.view.DGraphView;

public class MouseClickHandler extends MouseAdapter {
	
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			DGraphView view = (DGraphView) Cytoscape.getCurrentNetworkView();
			NodeView nv = view.getPickedNodeView(e.getPoint());
			EdgeView ev = view.getPickedEdgeView(e.getPoint());
			if(nv != null || ev != null) {
				GraphObject selected;
				Attributes attr;
				if( nv != null) {
					selected = (GraphObject) nv.getNode();
					attr = Attributes.node;
				}
				else {
					selected = (GraphObject) ev.getEdge();
					attr = Attributes.edge;
				}
				
				CytoPanel cytoPanel =  Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
				int panelIndex = cytoPanel.indexOfComponent("Node/Edge Details");
				if(panelIndex != -1)
					cytoPanel.remove(panelIndex);
				
				String typeAttr = "Type";
				if(attr.getAttribute(selected.getIdentifier(), typeAttr) == null ||
						attr.getAttribute(selected.getIdentifier(), typeAttr).equals("Relation")) {
					cytoPanel.setState(CytoPanelState.HIDE);
					return;
				}
				
				NodeEdgeDetailPanel panel = new NodeEdgeDetailPanel(selected, network);
				cytoPanel.setState(CytoPanelState.DOCK);
				cytoPanel.add(panel);
				cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(panel));
			}
		}
	}

}
