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
package org.ncibi.cytoscape.metscape.task;

import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.ui.MouseClickHandler;
import org.ncibi.cytoscape.metscape.ui.PopupNodeContextMenuListener;
import org.ncibi.cytoscape.metscape.visual.VisualStyleFactory;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;

public abstract class AbstractBuildNetworkTask extends AbstractTask {

	protected void configureNetwork(CyNetwork network) {
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		Tunable discrete = layout.getSettings().get("discrete");
		Tunable deterministic = layout.getSettings().get("deterministic");
		if(discrete != null) {
			discrete.setValue(true);
			layout.updateSettings();
		}
		else if(deterministic != null) {
			deterministic.setValue(true);
			layout.updateSettings();
		}
		CyNetworkView view = Cytoscape.getNetworkView(network
				.getIdentifier());
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		VisualStyle style = VisualStyleFactory.createStyle(network);
		if(catalog.getVisualStyle("MetScape:"+Networks.getUUID(network)) != null) {
			catalog.removeVisualStyle("MetScape:"+Networks.getUUID(network));
		}
		catalog.addVisualStyle(style);
		view.setVisualStyle(style.getName());
		manager.setVisualStyle(style);
		view.addNodeContextMenuListener(new PopupNodeContextMenuListener());
		((DGraphView) view).getCanvas().addMouseListener(new MouseClickHandler());
		layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
		view.redrawGraph(true, true);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, network);
	}

}
