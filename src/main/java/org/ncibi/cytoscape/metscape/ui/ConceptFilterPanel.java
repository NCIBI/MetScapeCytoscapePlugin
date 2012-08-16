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
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.task.BuildSubnetworkTask;
import org.ncibi.cytoscape.metscape.task.SaveConceptsTask;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedJTable;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedTableModel;
import org.ncibi.cytoscape.util.FileUtils;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;

@SuppressWarnings("serial")
public class ConceptFilterPanel extends JPanel implements ListSelectionListener, ComponentListener{

	private boolean initialized = false;
	private JButton createSubnetwork;
	private JButton selectAll;
	private JButton deselectAll;
	private JButton saveConcepts;
	private JButton reapplySelection;
	private JButton close;
	private JPanel buttonPanel;
	private Map<CyNetwork, ExtendedJTable> tables;
	private ExtendedJTable currentTable;
	private ExtendedTableModel currentModel;
	private JScrollPane scrollPane;
	private PropertyChangeListener networkModifiedListener;
	private PropertyChangeListener networkFocusListener;

	public ConceptFilterPanel() {
		super(new BorderLayout());
		createControls();
		createListeners();
	}
	
	private void createControls() {
		
		buttonPanel = new JPanel();

		selectAll = new JButton("Select All Concepts");
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});
		buttonPanel.add(selectAll);

		deselectAll = new JButton("Deselect All Concepts");
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
		
		saveConcepts = new JButton("Save Concepts...");
		saveConcepts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		buttonPanel.add(saveConcepts);
		
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
		currentModel = (ExtendedTableModel) currentTable.getModel();
		currentModel.setColumnIdentifiers(new String[] { "Concept Name", 
				"Number of Enriched-Driving Genes","Number of Genes in Network",
				"\u2191\u2193", "P-value", "False Discovery Rate" });
		currentTable.getRowSorter().toggleSortOrder(0);
		currentTable.getSelectionModel().addListSelectionListener(this);
		scrollPane = new JScrollPane(currentTable);
		refreshConceptListForCurrentNetwork();

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
				currentTable.getSelectionModel().removeListSelectionListener(ConceptFilterPanel.this);
				refreshConceptListForCurrentNetwork();
				currentTable.getSelectionModel().addListSelectionListener(ConceptFilterPanel.this);
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
		currentTable.getSelectionModel().clearSelection();
	}
	
	private void buildSubnetwork() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String subnetworkName = CyNetworkNaming.getSuggestedSubnetworkTitle(network);
		BuildSubnetworkTask.buildUsing
			(network.getSelectedNodes(), network.getSelectedEdges(), subnetworkName, network);
	}
	
	private void save() {
		File conceptFile = FileUtils.getFile("Save Concepts", FileUtils.SAVE, "csv", "CSV File");
		if(conceptFile != null)
			SaveConceptsTask.save(Cytoscape.getCurrentNetwork(),conceptFile);
	}
	
	public void close() {
		CytoPanel cytoPanel = ((CytoPanel) getParent().getParent());
		cytoPanel.remove(this);
		removeListeners();
		MetScapePlugin.getPluginData().setConceptFilterPanelOpen(false);
	}
	
	private void refreshConceptListForCurrentNetwork() {
		Set<String> selectedConceptSet = new HashSet<String>();
		for(int i: currentTable.getSelectedRows())
			selectedConceptSet.add(currentTable.getValueAt(i,0).toString());
		currentModel.setRowCount(0);
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<CyGroup> groupList = CyGroupManager.getGroupList(network);
		for(CyGroup group: groupList) {
			CyNode groupNode = group.getGroupNode();
			String name = 
				Attributes.node.getStringAttribute(groupNode.getIdentifier(), "Concept.name");
    		Integer numUniqueGenes = 
    			Attributes.node.getIntegerAttribute(groupNode.getIdentifier(), "Concept.numUniqueGenes");
    		Integer numGenesInNetwork = 
    			Attributes.node.getIntegerAttribute(groupNode.getIdentifier(), "Concept.numGenesInNetwork");
    		String direction = 
    			Attributes.node.getStringAttribute(groupNode.getIdentifier(), "Concept.direction").equals("up")?"\u2191":"\u2193";
    		Double pvalue = 
    			Attributes.node.getDoubleAttribute(groupNode.getIdentifier(), "Concept.pvalue");
    		Double fdr = 
    			Attributes.node.getDoubleAttribute(groupNode.getIdentifier(), "Concept.fdr");
    		currentModel.addRow(new Object[] {name,numUniqueGenes, numGenesInNetwork, direction, 
    				NumUtils.truncate(pvalue), NumUtils.truncate(fdr)});
    		if(selectedConceptSet.contains(name))
				currentTable.addRowSelectionInterval
					(currentTable.getRowCount()-1, currentTable.getRowCount()-1);
		}
		applySelectionToNetwork();
	}
	
	private void applySelectionToNetwork() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		network.unselectAllEdges();
		for(int i: currentTable.getSelectedRows()) {
			try {
				CyGroup group = CyGroupManager.getGroupList(network).get(currentTable.convertRowIndexToModel(i));
				network.setSelectedNodeState(group.getNodes(), true);
				network.setSelectedEdgeState(group.getInnerEdges(), true);
			}
			catch(IndexOutOfBoundsException e) {
				// do nothing
			}
		}
		CyNetworkView view = Cytoscape.getNetworkView(network
				.getIdentifier());
		if (view != null && initialized)
			view.redrawGraph(true, true);
		else if(!initialized)
			initialized = true;
	}
	
	private void switchActiveTable() {
		if(tables.get(Cytoscape.getCurrentNetwork()) != currentTable) {
			currentTable.getSelectionModel().removeListSelectionListener(ConceptFilterPanel.this);
			scrollPane.getViewport().setView(null);
			currentTable = tables.get(Cytoscape.getCurrentNetwork());
			if(currentTable == null) {
				currentTable = new ExtendedJTable();
				tables.put(Cytoscape.getCurrentNetwork(), currentTable);
				((ExtendedTableModel) currentTable.getModel()).setColumnIdentifiers(new String[] { "Concept Name", 
						"Number of Enriched-Driving Genes","Number of Genes in Network",
						"\u2191\u2193", "P-value", "False Discovery Rate" });
				currentTable.getRowSorter().toggleSortOrder(0);
				currentModel = (ExtendedTableModel) currentTable.getModel();
				refreshConceptListForCurrentNetwork();
			}
			currentModel = (ExtendedTableModel) currentTable.getModel();
			scrollPane.getViewport().setView(currentTable);
			currentTable.getSelectionModel().addListSelectionListener(ConceptFilterPanel.this);
		}
		applySelectionToNetwork();
	}
	
	//not implemented
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
}
