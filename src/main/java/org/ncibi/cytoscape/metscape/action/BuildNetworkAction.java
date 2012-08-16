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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.ui.BuildNetworkPanel;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.BiModalJSplitPane;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

@SuppressWarnings("serial")
public class BuildNetworkAction extends CytoscapeAction {
	
	private static final String NAME = "Build Network";
	private static BuildNetworkPanel panel;
	
	/**
	 * The constructor sets the text that should appear on the menu item.
	 */
	public BuildNetworkAction() {
		super(NAME);
	}

	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
		if(!MetScapePlugin.isInitialized())
			if(!MetScapePlugin.serverIsReady()) return;
		exec();
	}
	
	public static void exec(){
		if(panel == null) {
			panel = new BuildNetworkPanel();
		}
		CytoPanel cytoPanelWest = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST);
		if(cytoPanelWest.indexOfComponent(panel) == -1) {
			cytoPanelWest.add("MetScape", panel);
			MetScapePlugin.getPluginData().setBuildNetworkPanelOpen(true);
		}
		cytoPanelWest.setSelectedIndex(cytoPanelWest
				.indexOfComponent("MetScape"));
		cytoPanelWest.setState(CytoPanelState.DOCK);
		BiModalJSplitPane splitPane = (BiModalJSplitPane) ((Component) cytoPanelWest).getParent();
		splitPane.setDividerLocation(panel.getPreferredSize().width+5);
	}

	public static BuildNetworkPanel getPanel() {
		return panel;
	}

	public static void setPanel(BuildNetworkPanel panel) {
		BuildNetworkAction.panel = panel;
	}
}
