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

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.task.BuildSubnetworkTask;
import org.ncibi.cytoscape.metscape.task.CollapseNetworkTask;
import org.ncibi.cytoscape.metscape.task.ExpandInExistingNetworkTask;
import org.ncibi.cytoscape.metscape.task.ExpandInSubnetworkTask;
import org.ncibi.cytoscape.metscape.task.RestoreNetworkTask;

import cytoscape.CyNetwork;
import cytoscape.util.CyNetworkNaming;
import ding.view.NodeContextMenuListener;

public class PopupNodeContextMenuListener implements NodeContextMenuListener {
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu) {
		final String id = nodeView.getNode().getIdentifier();
		final CyNetwork network = (CyNetwork) nodeView.getGraphView().getGraphPerspective();
		if(Networks.getUUID(network) == null)
			return;
		
		if(menu == null){
			menu=new JPopupMenu();
		}
		
		JMenu metscapeMenu = new JMenu("MetScape");
		JMenuItem createSubnetwork = new JMenuItem("Create Subnetwork");
		if(network.getSelectedNodes().isEmpty())
			createSubnetwork.setEnabled(false);
		createSubnetwork.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			String subnetworkName = CyNetworkNaming.getSuggestedSubnetworkTitle(network);
    			BuildSubnetworkTask.buildUsing
    				(network.getSelectedNodes(), network.getSelectedEdges(), subnetworkName, network);
    		}
    	});
		metscapeMenu.add(createSubnetwork);
		
		JMenu expandMenu = new JMenu("Expand");
		if(!Attributes.node.getStringAttribute(id, "Type").equals("Compound") ||
				Attributes.node.hasAttribute(id, "isExpansionSeed." + Networks.getUUID(network)))
			expandMenu.setEnabled(false);
		JMenuItem expandInExistingNetwork = new JMenuItem("Expand in Existing Network");
		expandInExistingNetwork.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			ExpandInExistingNetworkTask.expandUsing(id, network);
    		}
    	});
		expandMenu.add(expandInExistingNetwork);
		JMenuItem expandInSubnetwork = new JMenuItem("Expand in Subnetwork");
		expandInSubnetwork.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			ExpandInSubnetworkTask.expandUsing(id, network);
    		}
    	});
		expandMenu.add(expandInSubnetwork);
		metscapeMenu.add(expandMenu);
		
		JMenuItem collapse = new JMenuItem("Collapse");
		if(!Attributes.node.hasAttribute(id, "isExpansionSeed." + Networks.getUUID(network)))
			collapse.setEnabled(false);
		collapse.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){
    			if(Attributes.node.hasAttribute(id, "isSubnetworkExpansionSeed." + Networks.getUUID(network))) {
    				RestoreNetworkTask.restoreCyNetwork(network);
    			} else {
    				CollapseNetworkTask.doCollapse(network, id);
    			}
    		}
    	});
		metscapeMenu.add(collapse);
		JMenuItem restoreOriginalNetwork = new JMenuItem("Restore Original Network");
		restoreOriginalNetwork.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			RestoreNetworkTask.restoreCyNetwork(network);
    		}
    	});
		metscapeMenu.add(restoreOriginalNetwork);  
		menu.add(metscapeMenu);
	}

}
