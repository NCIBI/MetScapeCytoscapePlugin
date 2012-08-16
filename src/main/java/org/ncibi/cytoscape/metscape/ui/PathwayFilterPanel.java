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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.task.BuildSubnetworkTask;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedJTable;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedTableModel;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.network.attribute.EnzymeReactionAttribute;
import org.ncibi.metab.network.attribute.ReactionAttribute;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;

@SuppressWarnings("serial")
public class PathwayFilterPanel extends JPanel implements ListSelectionListener, ComponentListener{
	
	private boolean initialized = false;
	private JButton selectAll;
	private JButton deselectAll;
	private JButton createSubnetwork;
	private JButton reapplySelection;
	private JButton close;
	private JPanel buttonPanel;
	private Map<CyNetwork, ExtendedJTable> tables;
	private ExtendedJTable currentTable;
	private ExtendedTableModel currentModel;
	private JScrollPane scrollPane;
	private PropertyChangeListener networkModifiedListener;
	private PropertyChangeListener networkFocusListener;
	
	public PathwayFilterPanel() {
		super(new BorderLayout());
		createControls();
		createListeners();
	}
	
	private void createControls() {
		buttonPanel = new JPanel();

		selectAll = new JButton("Select All Pathways");
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});
		buttonPanel.add(selectAll);

		deselectAll = new JButton("Deselect All Pathways");
		deselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deselectAll();
			}
		});
		buttonPanel.add(deselectAll);

		createSubnetwork = new JButton("Create Subnetwork");
		createSubnetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildSubnetwork();
			}
		});
		buttonPanel.add(createSubnetwork);
		
		reapplySelection = new JButton("Reapply Selection");
		reapplySelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applySelectionToNetwork();
			}
		});
		buttonPanel.add(reapplySelection);
		
		close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		buttonPanel.add(close);

		add(buttonPanel, BorderLayout.NORTH);
		tables = new HashMap<CyNetwork, ExtendedJTable>();
		currentTable = new ExtendedJTable();
		tables.put(Cytoscape.getCurrentNetwork(), currentTable);
		currentTable.getSelectionModel().addListSelectionListener(this);
		currentModel = (ExtendedTableModel) currentTable.getModel();
		currentModel.addColumn("Pathways");
		currentTable.getRowSorter().toggleSortOrder(0);
		scrollPane = new JScrollPane(currentTable);
		refreshPathwayListForCurrentNetwork();

		add(scrollPane, BorderLayout.CENTER);
		addComponentListener(this);
	}
	
	public void componentShown(ComponentEvent e) {
		addListeners();
		switchActiveTable();
	}
	
	public void componentHidden(ComponentEvent e) {
		removeListeners();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			applySelectionToNetwork();
		}
	}
	
	private void createListeners() {
		networkModifiedListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				currentTable.getSelectionModel().removeListSelectionListener(PathwayFilterPanel.this);
				refreshPathwayListForCurrentNetwork();
				currentTable.getSelectionModel().addListSelectionListener(PathwayFilterPanel.this);
			}
		};
		
		networkFocusListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				switchActiveTable();
			}
		};
	}
	
	private void addListeners() {
		Cytoscape.getPropertyChangeSupport().
			addPropertyChangeListener(Cytoscape.NETWORK_MODIFIED,networkModifiedListener);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().
			addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS,networkFocusListener);
	}

	private void removeListeners() {
		Cytoscape.getPropertyChangeSupport().
			removePropertyChangeListener(Cytoscape.NETWORK_MODIFIED,networkModifiedListener);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().
			removePropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS,networkFocusListener);
	}
	
	private void selectAll() {
		currentTable.selectAll();
	}

	private void deselectAll() {
		currentTable.clearSelection();
	}
	
	private void buildSubnetwork() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String subnetworkName = CyNetworkNaming.getSuggestedSubnetworkTitle(network);
		BuildSubnetworkTask.buildUsing
			(network.getSelectedNodes(), network.getSelectedEdges(), subnetworkName, network);
		refreshPathwayListForCurrentNetwork();
	}
	
	public void close() {
		CytoPanel cytoPanel = ((CytoPanel) getParent().getParent());
		cytoPanel.remove(this);
		removeListeners();
		MetScapePlugin.getPluginData().setPathwayFilterPanelOpen(false);
	}
	
	private void refreshPathwayListForCurrentNetwork() {
		Set<String> selectedPathwaySet = new HashSet<String>();
		for(int i: currentTable.getSelectedRows())
			selectedPathwaySet.add(currentTable.getValueAt(i,0).toString());
		currentModel.setRowCount(0);
		
		SortedSet<String> pathwaySet;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		NetworkData networkData = MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network));
		NetworkType networkType = null;
		if(networkData != null)
			networkType = networkData.getNetworkType();
		
		if(networkType == NetworkType.COMPOUND_REACTION || networkType == NetworkType.CREG){ //for C-R and C-R-E-G
			pathwaySet = getPathwaySetFromReactionNodes(network);
		}
		else if(networkType == NetworkType.COMPOUND) {
			pathwaySet = getPathwaySetFromReactionEdges(network);
		}
		else if(networkType == NetworkType.COMPOUND_GENE) {
			pathwaySet = getPathwaySetFromEnzymeReactionEdges(network);
		}
		else {
			pathwaySet = new TreeSet<String>();
		}
		for(String pathway: pathwaySet) {
			currentModel.addRow(new Object[]{pathway});
			if(selectedPathwaySet.contains(pathway))
				currentTable.addRowSelectionInterval
					(currentTable.getRowCount()-1, currentTable.getRowCount()-1);
		}
		applySelectionToNetwork();
	}
	
	private SortedSet<String> getPathwaySetFromReactionNodes(CyNetwork network) {
		SortedSet<String> pathwaySet = new TreeSet<String>();
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			String pathway = Attributes.node.getStringAttribute(cyNode.getIdentifier(), 
					ReactionAttribute.PATHWAY.toAttributeName());
			if(pathway != null) {
				pathwaySet.add(pathway);
			}
		}
		return pathwaySet;
	}
	
	private SortedSet<String> getPathwaySetFromReactionEdges(CyNetwork network) {
		SortedSet<String> pathwaySet = new TreeSet<String>();
		for(Object edge: network.edgesList()) {
			CyEdge cyEdge = (CyEdge) edge;
			String pathway = Attributes.edge.getStringAttribute(cyEdge.getIdentifier(), 
					ReactionAttribute.PATHWAY.toAttributeName());
			if(pathway != null) {
				pathwaySet.add(pathway);
			}
		}
		return pathwaySet;
	}
	
	private SortedSet<String> getPathwaySetFromEnzymeReactionEdges(CyNetwork network) {
		SortedSet<String> pathwaySet = new TreeSet<String>();
		for(Object edge: network.edgesList()) {
			CyEdge cyEdge = (CyEdge) edge;
			List<?> pathways = Attributes.edge.getListAttribute(cyEdge.getIdentifier(), 
					EnzymeReactionAttribute.PATHWAYS.toAttributeName());
			if(pathways != null) {
				for(Object pathway: pathways)
					pathwaySet.add(pathway.toString());
			}
		}
		return pathwaySet;
	}
	
	private void applySelectionToNetwork() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		network.unselectAllEdges();
		
		Set<String> selection = new HashSet<String>();
		for(int i: currentTable.getSelectedRows()) {
			selection.add(currentTable.getValueAt(i, 0).toString());
		}
		
		NetworkData networkData = MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network));
		if(networkData == null)
			return;
		NetworkType networkType = networkData.getNetworkType();
		
		
		if(networkType == NetworkType.CREG)
			applySelectionToCREGNetwork(network, selection);
		else if(networkType == NetworkType.COMPOUND_REACTION)
			applySelectionToCRNetwork(network, selection);
		else if(networkType == NetworkType.COMPOUND_GENE)
			applySelectionToCGNetwork(network, selection);
		else if(networkType == NetworkType.COMPOUND)
			applySelectionToCompoundNetwork(network, selection);
		
		CyNetworkView view = Cytoscape.getNetworkView(network
				.getIdentifier());
		if (view != null && initialized)
			view.redrawGraph(true, true);
		else if(!initialized)
			initialized = true;
	}
	
	private void applySelectionToCREGNetwork(CyNetwork network, Set<String> selection) {
		Set<CyNode> nodesSet = new HashSet<CyNode>();
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			String pathway = Attributes.node.getStringAttribute(cyNode.getIdentifier(),
					ReactionAttribute.PATHWAY.toAttributeName());
			if(pathway != null && selection.contains(pathway)) {
				nodesSet.add(cyNode);
				for(Object neighbor: network.neighborsList(cyNode)) {
					CyNode neighborNode = (CyNode) neighbor;
					nodesSet.add(neighborNode);
					if(Attributes.node.getAttribute(neighborNode.getIdentifier(), "Type").equals("Enzyme")){
						//get the genes
						for(Object enzymeNeighbor: network.neighborsList(neighborNode)) {
							CyNode enzymeNeighborNode = (CyNode) enzymeNeighbor;
							if(Attributes.node.getAttribute
									(enzymeNeighborNode.getIdentifier(), "Type").equals("Gene"))
								nodesSet.add(enzymeNeighborNode);
						}
					}
				}
			}
		}
		network.setSelectedNodeState(nodesSet, true);
		network.setSelectedEdgeState
			(network.getConnectingEdges(new ArrayList<CyNode>(nodesSet)),true);
	}
	
	private void applySelectionToCRNetwork(CyNetwork network, Set<String> selection) {
		Set<CyNode> nodesSet = new HashSet<CyNode>();
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			String pathway = Attributes.node.getStringAttribute(cyNode.getIdentifier(),
					ReactionAttribute.PATHWAY.toAttributeName());
			if(pathway != null && selection.contains(pathway)) {
				nodesSet.add(cyNode);
				for(Object neighbor: network.neighborsList(cyNode)) {
					CyNode neighborNode = (CyNode) neighbor;
					nodesSet.add(neighborNode);
				}
			}
		}
		network.setSelectedNodeState(nodesSet, true);
		network.setSelectedEdgeState
			(network.getConnectingEdges(new ArrayList<CyNode>(nodesSet)),true);
	}
	
	private void applySelectionToCGNetwork(CyNetwork network, Set<String> selection) {
		Set<CyNode> nodesSet = new HashSet<CyNode>();
		Set<CyEdge> edgesSet = new HashSet<CyEdge>();
		for(Object edge: network.edgesList()) {
			CyEdge cyEdge = (CyEdge) edge;
			List<?> pathways = Attributes.edge.getListAttribute(cyEdge.getIdentifier(),
					EnzymeReactionAttribute.PATHWAYS.toAttributeName());
			for(Object pathway: pathways)
			{
				if(selection.contains(pathway)) {
					nodesSet.add((CyNode) cyEdge.getSource());
					nodesSet.add((CyNode) cyEdge.getTarget());
					edgesSet.add(cyEdge);
					break;
				}
			}
		}
		network.setSelectedNodeState(nodesSet, true);
		network.setSelectedEdgeState(edgesSet, true);
	}
	
	private void applySelectionToCompoundNetwork(CyNetwork network, Set<String> selection) {
		Set<CyNode> nodesSet = new HashSet<CyNode>();
		Set<CyEdge> edgesSet = new HashSet<CyEdge>();
		for(Object edge: network.edgesList()) {
			CyEdge cyEdge = (CyEdge) edge;
			String pathway = Attributes.edge.getStringAttribute(cyEdge.getIdentifier(),
					ReactionAttribute.PATHWAY.toAttributeName());
			if(pathway != null && selection.contains(pathway)) {
				nodesSet.add((CyNode) cyEdge.getSource());
				nodesSet.add((CyNode) cyEdge.getTarget());
				edgesSet.add(cyEdge);
			}
		}
		network.setSelectedNodeState(nodesSet, true);
		network.setSelectedEdgeState(edgesSet, true);
	}
	
	private void switchActiveTable() {
		if(tables.get(Cytoscape.getCurrentNetwork()) != currentTable){
			currentTable.getSelectionModel().removeListSelectionListener(PathwayFilterPanel.this);
			scrollPane.getViewport().setView(null);
			currentTable = tables.get(Cytoscape.getCurrentNetwork());
			if(currentTable == null) {
				currentTable = new ExtendedJTable();
				tables.put(Cytoscape.getCurrentNetwork(), currentTable);
				((ExtendedTableModel) currentTable.getModel()).addColumn("Pathways");
				currentTable.getRowSorter().toggleSortOrder(0);
				currentModel = (ExtendedTableModel) currentTable.getModel();
				refreshPathwayListForCurrentNetwork();
			}
			currentModel = (ExtendedTableModel) currentTable.getModel();
			scrollPane.getViewport().setView(currentTable);
			currentTable.getSelectionModel().addListSelectionListener(PathwayFilterPanel.this);
		}
		applySelectionToNetwork();
	}
	
	//not implemented
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}

}
