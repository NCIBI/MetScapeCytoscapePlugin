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
package org.ncibi.cytoscape.metscape.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.ui.ConceptFilterPanel;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

@SuppressWarnings("serial")
public class ConceptFilterAction extends CytoscapeAction {
	
	private static final String NAME = "Concepts";
	private static ConceptFilterPanel panel;
	
	/**
	 * The constructor sets the text that should appear on the menu item.
	 */
	public ConceptFilterAction() {
		super(NAME);
	}

	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
		exec();
	}
	
	public static void exec() {
		if(panel == null) {
			panel = new ConceptFilterPanel();
		}
		CytoPanel cytoPanelSouth = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
		if(cytoPanelSouth.indexOfComponent(panel) == -1) {
			cytoPanelSouth.add("Concept Filter", panel);
			MetScapePlugin.getPluginData().setConceptFilterPanelOpen(true);
		}
		cytoPanelSouth.setSelectedIndex(cytoPanelSouth.indexOfComponent(panel));
		cytoPanelSouth.setState(CytoPanelState.DOCK);
	}

	public static ConceptFilterPanel getPanel() {
		return panel;
	}

	public static void setPanel(ConceptFilterPanel panel) {
		ConceptFilterAction.panel = panel;
	}		
}
