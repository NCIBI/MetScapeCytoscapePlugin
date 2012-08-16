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

import org.ncibi.cytoscape.metscape.network.DefaultCyNodeCollapser;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;

public class CollapseNetworkTask extends AbstractTask {

	private CyNetwork network;
	private String cid;

	public static boolean doCollapse(CyNetwork network, String cid) {
		CollapseNetworkTask task = new CollapseNetworkTask(network,cid);
		return configureAndRunTask(task);
	}
	
	private CollapseNetworkTask(CyNetwork network, String cid) {
		this.network = network;
		this.cid = cid;
	}

	public String getTitle() {
		return "Collapse Network";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Collapsing network...");
			DefaultCyNodeCollapser collapser = new DefaultCyNodeCollapser(network, cid);
			network = collapser.doCollapse();
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, network);
			CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
			layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "Network collapse failed");
		}

	}
}
