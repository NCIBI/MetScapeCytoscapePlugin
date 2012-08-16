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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.DataParameters;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.cytoscape.metscape.data.QuerySubtype;
import org.ncibi.cytoscape.metscape.data.QueryType;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginData;
import org.ncibi.cytoscape.metscape.task.BuildNewNetworkTask;
import org.ncibi.cytoscape.metscape.task.GetCompoundMappingsTask;
import org.ncibi.cytoscape.metscape.task.GetGeneMappingsTask;
import org.ncibi.cytoscape.metscape.task.OutputFileTask;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedJTable;
import org.ncibi.cytoscape.metscape.ui.table.ExtendedTableModel;
import org.ncibi.cytoscape.util.FileUtils;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.pathway.Pathway;
import org.ncibi.metab.ws.client.MetabolicPathwaysService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

@SuppressWarnings("serial")
public class BuildNetworkPanel extends JPanel {

	private JLabel panelLabel;
	private JButton clearAllButton;
	private JButton closeButton;

	private JPanel inputPanel;
	private TitledBorder inputBorder;

	private JLabel organismLabel;
	private JComboBox organismComboBox;

	private JPanel experimentalDataPanel;
	private TitledBorder experimentalDataBorder;
	private JLabel experimentalDataLabel;
	private JButton selectDataButton;

	private JPanel compoundsPanel;
	private TitledBorder compoundsBorder;
	private ExtendedJTable compoundsTable;
	private ExtendedTableModel compoundsTableModel;
	private JScrollPane compoundsScrollPane;
	private JButton compoundsAddButton;
	private JButton compoundsRemoveButton;
	private JButton compoundsClearButton;
	private JButton compoundsResetButton;

	private JPanel genesPanel;
	private TitledBorder genesBorder;
	private ExtendedJTable genesTable;
	private ExtendedTableModel genesTableModel;
	private TableColumn genesHomologColumn;
	private JScrollPane genesScrollPane;
	private JButton genesAddButton;
	private JButton genesRemoveButton;
	private JButton genesClearButton;
	private JButton genesResetButton;

	private JPanel optionsPanel;
	private TitledBorder optionsBorder;
	private JPanel networkTypePanel;
	private TitledBorder networkTypeBorder;
	private JComboBox networkTypeComboBox;

	private JPanel queryPanel;
	private TitledBorder queryBorder;
	private ButtonGroup queryGroup;
	private JRadioButton useCompoundsGenes;
	private JComboBox compoundsGenesComboBox;
	private JRadioButton usePathway;
	private JComboBox pathwayComboBox;

	private JButton buildNetworkButton;
	private JButton outputAsFileButton;

	public BuildNetworkPanel() {
		createControls();
	}

	private void createControls() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		panelLabel = new JLabel("Build Network");
		clearAllButton = new JButton("Clear All");
		clearAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearAll();
			}
		});
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		add(new Box(BoxLayout.X_AXIS) {
			{
				add(panelLabel);
				add(Box.createHorizontalGlue());
				add(clearAllButton);
				add(closeButton);
			}
		});

		inputPanel = new JPanel();
		inputBorder = BorderFactory.createTitledBorder("Input");
		inputBorder.setTitleFont(BuildNetworkPanel.boldFontForTitlePanel(inputBorder));
		inputPanel.setBorder(inputBorder);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		organismLabel = new JLabel("Organism");
		organismComboBox = new JComboBox();
		for (Organism organism : Organism.values()) {
			organismComboBox.addItem(organism);
		}
		organismComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchOrganism();
			}
		});
		inputPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(organismLabel);
				add(Box.createHorizontalStrut(5));
				add(organismComboBox);
			}
		});

		experimentalDataPanel = new JPanel();
		experimentalDataPanel.setLayout(new BoxLayout(experimentalDataPanel,
				BoxLayout.Y_AXIS));
		experimentalDataPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		experimentalDataBorder = BorderFactory
				.createTitledBorder("Experimental Data");
		experimentalDataPanel.setBorder(experimentalDataBorder);
		experimentalDataLabel = new JLabel();
		selectDataButton = new JButton("Select...");
		selectDataButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		experimentalDataPanel.add(selectDataButton);
		experimentalDataPanel.add(new Box(BoxLayout.Y_AXIS) {
			{
				setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(experimentalDataLabel);
			}
		});
		selectDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectData();
			}
		});
		inputPanel.add(experimentalDataPanel);

		compoundsPanel = new JPanel();
		compoundsPanel
				.setLayout(new BoxLayout(compoundsPanel, BoxLayout.Y_AXIS));
		compoundsBorder = BorderFactory.createTitledBorder("Compounds");
		compoundsPanel.setBorder(compoundsBorder);
		compoundsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		compoundsTable = new ExtendedJTable();
		compoundsTableModel = (ExtendedTableModel) compoundsTable.getModel();
		Vector<?> currentCompounds = MetScapePlugin.getPluginData()
				.getCurrentCompounds();
		Vector<?> compoundColumns = new Vector<String>(
				Arrays.asList(new String[] { "Input ID", "Input Name" }));
		if (currentCompounds != null) {
			compoundsTableModel
					.setDataVector(currentCompounds, compoundColumns);
		} else {
			MetScapePlugin.getPluginData().setCurrentCompounds(
					compoundsTableModel.getDataVector());
			compoundsTableModel.setColumnIdentifiers(compoundColumns);
			resetCompounds();
		}
		compoundsTableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updateStatus();
			}
		});
		compoundsTable.getRowSorter().toggleSortOrder(0);
		compoundsScrollPane = new JScrollPane(compoundsTable);
		compoundsScrollPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		compoundsPanel.add(compoundsScrollPane);
		compoundsAddButton = new JButton("Add");
		compoundsAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToCompounds();
			}
		});
		compoundsRemoveButton = new JButton("Remove");
		compoundsRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFromCompounds();
			}
		});
		compoundsClearButton = new JButton("Clear");
		compoundsClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearCompounds();
			}
		});
		compoundsResetButton = new JButton("Reset");
		compoundsResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetCompounds();
			}
		});
		compoundsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(Box.createHorizontalGlue());
				add(compoundsAddButton);
				add(compoundsRemoveButton);
				add(compoundsClearButton);
				add(compoundsResetButton);
				add(Box.createHorizontalGlue());
			}
		});
		inputPanel.add(compoundsPanel);

		genesPanel = new JPanel();
		genesPanel.setLayout(new BoxLayout(genesPanel, BoxLayout.Y_AXIS));
		genesBorder = BorderFactory.createTitledBorder("Genes");
		genesPanel.setBorder(genesBorder);
		genesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		genesTable = new ExtendedJTable();
		genesTableModel = (ExtendedTableModel) genesTable.getModel();
		Vector<?> currentGenes = MetScapePlugin.getPluginData()
				.getCurrentGenes();
		Vector<?> geneColumns = new Vector<String>(Arrays.asList(new String[] {
				"Input ID", "Input Symbol", "Human Symbol" }));
		if (currentGenes != null) {
			genesTableModel.setDataVector(currentGenes, geneColumns);
		} else {
			MetScapePlugin.getPluginData().setCurrentGenes(
					genesTableModel.getDataVector());
			genesTableModel.setColumnIdentifiers(geneColumns);
			resetGenes();
		}
		genesTableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updateStatus();
			}
		});
		genesTable.getRowSorter().toggleSortOrder(0);
		genesHomologColumn = genesTable.getColumn("Human Symbol");
		genesScrollPane = new JScrollPane(genesTable);
		genesScrollPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
		genesPanel.add(genesScrollPane);
		genesAddButton = new JButton("Add");
		genesAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToGenes();
			}
		});
		genesRemoveButton = new JButton("Remove");
		genesRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFromGenes();
			}
		});
		genesClearButton = new JButton("Clear");
		genesClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearGenes();
			}
		});
		genesResetButton = new JButton("Reset");
		genesResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetGenes();
			}
		});
		genesPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(Box.createHorizontalGlue());
				add(genesAddButton);
				add(genesRemoveButton);
				add(genesClearButton);
				add(genesResetButton);
				add(Box.createHorizontalGlue());
			}
		});
		inputPanel.add(genesPanel);
		add(inputPanel);

		optionsPanel = new JPanel();
		optionsBorder = BorderFactory.createTitledBorder("Options");
		optionsBorder.setTitleFont(BuildNetworkPanel.boldFontForTitlePanel(optionsBorder));
		optionsPanel.setBorder(optionsBorder);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		networkTypePanel = new JPanel();
		networkTypeBorder = BorderFactory.createTitledBorder("Network Type");
		networkTypePanel.setBorder(networkTypeBorder);
		networkTypePanel.setLayout(new BoxLayout(networkTypePanel,
				BoxLayout.Y_AXIS));
		networkTypePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		networkTypeComboBox = new JComboBox(NetworkType.values());
		if (MetScapePlugin.getPluginData().getNetworkType() != null) {
			networkTypeComboBox.setSelectedItem(MetScapePlugin.getPluginData().getNetworkType());
		}
		networkTypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCompoundTypeStatus();
				MetScapePlugin
						.getPluginData()
						.setNetworkType(
								(NetworkType) networkTypeComboBox
										.getSelectedItem());
			}
		});
		networkTypePanel.add(networkTypeComboBox);
		networkTypePanel.add(Box.createHorizontalGlue());
		optionsPanel.add(networkTypePanel);

		queryPanel = new JPanel();
		queryBorder = BorderFactory.createTitledBorder("Query");
		queryPanel.setBorder(queryBorder);
		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.Y_AXIS));
		queryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		queryGroup = new ButtonGroup();
		useCompoundsGenes = new JRadioButton("Use compounds/genes");
		useCompoundsGenes.setSelected(true);
		useCompoundsGenes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MetScapePlugin.getPluginData().setCurrentQueryType(
						QueryType.COMPOUND_GENE);
				compoundsGenesComboBox.setEnabled(true);
				pathwayComboBox.setEnabled(false);
				updateStatus();
			}
		});
		queryGroup.add(useCompoundsGenes);
		compoundsGenesComboBox = new JComboBox();
		compoundsGenesComboBox.setVisible(false);
		queryPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(useCompoundsGenes);
				add(compoundsGenesComboBox);
			}
		});
		Set<Pathway> pathways = MetScapePlugin.getPluginData()
				.getDefaultPathways();
		if (pathways == null) {
			MetabolicPathwaysService pathwayService = new MetabolicPathwaysService(
					HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Response<Set<Pathway>> pathwayResponse = pathwayService
					.retrievePathways();
			pathways = pathwayResponse.getResponseValue();
			if (pathways == null)
				pathways = new LinkedHashSet<Pathway>();
			MetScapePlugin.getPluginData().setDefaultPathways(pathways);
		}
		usePathway = new JRadioButton("Use selected pathway");
		if (pathways.isEmpty())
			usePathway.setEnabled(false);
		usePathway.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MetScapePlugin.getPluginData().setCurrentQueryType(
						QueryType.PATHWAY);
				compoundsGenesComboBox.setEnabled(false);
				pathwayComboBox.setEnabled(true);
				updateStatus();
			}
		});
		queryGroup.add(usePathway);
		queryPanel.add(usePathway);
		pathwayComboBox = new JComboBox(pathways.toArray());
		pathwayComboBox.setPrototypeDisplayValue("");
		pathwayComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (MetScapePlugin.getPluginData().getCurrentPathway() != null) {
			pathwayComboBox.setSelectedItem(MetScapePlugin.getPluginData()
					.getCurrentPathway());
		} else
			for (Pathway pathway : pathways) {
				if (pathway.getName().equals("TCA cycle")) {
					pathwayComboBox.setSelectedItem(pathway);
					break;
				}
			}
		pathwayComboBox.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				updateStatus();
				MetScapePlugin.getPluginData().setCurrentPathway(
						(Pathway) pathwayComboBox.getSelectedItem());
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
		queryPanel.add(pathwayComboBox);
		queryPanel.add(Box.createVerticalStrut(7));
		if (MetScapePlugin.getPluginData().getCurrentQueryType() == QueryType.PATHWAY) {
			pathwayComboBox.setEnabled(true);
			compoundsGenesComboBox.setEnabled(false);
		} else {
			compoundsGenesComboBox.setEnabled(true);
			pathwayComboBox.setEnabled(false);
		}

		optionsPanel.add(queryPanel);
		add(optionsPanel);

		buildNetworkButton = new JButton("Build Network");
		buildNetworkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		buildNetworkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildNetwork();
			}
		});

		buildNetworkButton.setEnabled(false);
		add(buildNetworkButton);

		outputAsFileButton = new JButton("Output as File...");
		outputAsFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		outputAsFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputAsFile();
			}
		});
		outputAsFileButton.setEnabled(false);

		add(new Box(BoxLayout.X_AXIS) {
			{
				add(buildNetworkButton);
				add(outputAsFileButton);
			}
		});
		updateFilesLabel();
		updateOrganism();
		updateStatus();
		if (MetScapePlugin.getPluginData().getCurrentQuerySubtype() == null) {
			compoundsGenesComboBox
					.setSelectedItem((QuerySubtype) MetScapePlugin
							.getPluginData().getCurrentQuerySubtype());
		}
		setPreferredSize(getMinimumSize());
	}

	private void clearAll() {
		organismComboBox.setSelectedIndex(0);
		clearData();

		for (int i = 0; i <= pathwayComboBox.getItemCount(); i++) {
			Pathway pathway = (Pathway) pathwayComboBox.getItemAt(i);
			if (pathway.getName().equals("TCA cycle")) {
				pathwayComboBox.setSelectedItem(pathway);
				break;
			}
		}
		useCompoundsGenes.setSelected(true);
		pathwayComboBox.setEnabled(false);
		networkTypeComboBox.setSelectedIndex(0);
		refresh();
	}

	public void refresh() {
		updateOrganism();
		updateFilesLabel();
		resetCompounds();
		resetGenes();
		updateStatus();
	}

	public void close() {
		CytoPanel cytoPanel = ((CytoPanel) getParent().getParent());
		cytoPanel.remove(BuildNetworkPanel.this);
		if (cytoPanel.getCytoPanelComponentCount() <= 0)
			cytoPanel.setState(CytoPanelState.HIDE);
		MetScapePlugin.getPluginData().setBuildNetworkPanelOpen(false);
	}

	private void switchOrganism() {
		organismComboBox.hidePopup();
		if (MetScapePlugin.getPluginData().getOrganism() != organismComboBox.getSelectedItem()) {
			if(noDataLoaded() || JOptionPane.showConfirmDialog
					(this, "All existing data will be lost. Continue?", "Warning", JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION) {
				MetScapePlugin.getPluginData().setOrganism((Organism) organismComboBox.getSelectedItem());
				clearData();
				refresh();
			}
			else {
				updateOrganism();
			}
		}
	}
	
	private boolean noDataLoaded() {
		PluginData pluginData = MetScapePlugin.getPluginData();
		return (pluginData.getDefaultCompounds().isEmpty() &&
				pluginData.getDefaultGenes().isEmpty() &&
				pluginData.getCurrentCompounds().isEmpty() &&
				pluginData.getCurrentGenes().isEmpty());
	}

	private void selectData() {
		if (SelectDataDialog.selectExperimentalData()) {
			refresh();
		}
	}

	private void clearData() {
		MetScapePlugin.getPluginData().setCompoundData(new CompoundData());
		MetScapePlugin.getPluginData().setGeneData(new GeneData());
		MetScapePlugin.getPluginData().setConceptData(new ConceptData());
		MetScapePlugin.getPluginData().setCompoundParameters(new DataParameters());
		MetScapePlugin.getPluginData().setGeneParameters(new DataParameters());
		MetScapePlugin.getPluginData().getDefaultCompounds().clear();
		MetScapePlugin.getPluginData().getDefaultGenes().clear();
	}

	private void addToCompounds() {
		String compoundString = "";
		while(compoundString.equals("")) {
			compoundString = TextInputDialog.showDialog(this, "<html>Enter a list of compound names or KEGG IDs. " +
					"KEGG IDs may be entered all on one line (separated by commas or spaces), or one per line. " +
					"Compound names should be entered one per line.</html> ",
					"Add Compounds");
			if(compoundString == null) return;
			else if(compoundString.equals("")) {
				JOptionPane.showMessageDialog
				(this, "At least one compound name or ID is needed to proceed.", "Input Needed", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(compoundString == null || compoundString.trim().length() <= 0)
			JOptionPane.showMessageDialog
			(this, "Valid input shall consist only of one or more KEGG IDs or compound names.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
		else {
			Map<String, String> nameMap = GetCompoundMappingsTask.getMappings(compoundString);
			if(nameMap == null) return;
			for(String key: nameMap.keySet()) {
				String name = nameMap.get(key);
				boolean containsNode = false;
				for(int i=0; i<compoundsTableModel.getRowCount(); i++) {
					if(compoundsTableModel.getValueAt(i, 0).equals(key)) {
						containsNode = true;
						break;
					}
				}
				if(!containsNode)
					compoundsTableModel.addRow(new Object[]{key,name});
			}
		}
	}

	private void removeFromCompounds() {
		int selectedRowCount = compoundsTable.getSelectedRowCount();
		for (int i = 0; i < selectedRowCount; i++) {
			compoundsTableModel.removeRow(compoundsTable
					.convertRowIndexToModel(compoundsTable.getSelectedRow()));
		}
	}

	private void clearCompounds() {
		compoundsTableModel.setRowCount(0);
	}

	private void resetCompounds() {
		compoundsTableModel.setRowCount(0);
		Map<String, String> compounds = MetScapePlugin.getPluginData()
				.getDefaultCompounds();
		if (compounds != null && !compounds.isEmpty()) {
			for (String id : compounds.keySet()) {
				compoundsTableModel
						.addRow(new Object[] { id, compounds.get(id) });
			}
		}
	}

	private void addToGenes() {
		String geneString = "";
		while (geneString.equals("")) {
			geneString = TextInputDialog.showDialog(this,
					"<html>Enter a list of gene symbols or Entrez gene IDs. " +
					"The list may be entered all on one line (separated by commas or spaces), " +
					"or one per line.</html>",
					"Add Genes");
			if (geneString == null)
				return;
			else if (geneString.equals("")) {
				JOptionPane.showMessageDialog(this,
						"At least one gene symbol or Entrez gene ID is needed to proceed.",
						"Input Needed", JOptionPane.ERROR_MESSAGE);
			}
		}
		Map<Integer, String[]> symbolMap = 
			GetGeneMappingsTask.getMappings(geneString, MetScapePlugin.getPluginData().getOrganism());
		if(symbolMap == null) return;
		for (Integer key : symbolMap.keySet()) {
			String[] symbols = symbolMap.get(key);
			boolean containsGene = false;
			for (int i = 0; i < genesTableModel.getRowCount(); i++) {
				if (genesTableModel.getValueAt(i, 0).equals(key)) {
					containsGene = true;
					break;
				}
			}
			if (!containsGene)
				genesTableModel.addRow(new Object[] { key, symbols[0],symbols[1]});
		}
	}

	private void removeFromGenes() {
		int selectedRowCount = genesTable.getSelectedRowCount();
		for (int i = 0; i < selectedRowCount; i++)
			genesTableModel.removeRow(genesTable
					.convertRowIndexToModel(genesTable.getSelectedRow()));
	}

	private void clearGenes() {
		genesTableModel.setRowCount(0);
	}

	private void resetGenes() {
		genesTableModel.setRowCount(0);
		Map<Integer, String[]> genes = MetScapePlugin.getPluginData()
				.getDefaultGenes();
		if (genes != null && !genes.isEmpty()) {
			for (Integer id : genes.keySet()) {
				genesTableModel.addRow(new Object[] { id, genes.get(id)[0], genes.get(id)[1] });
			}
		}
	}

	private void updateOrganism() {
		Organism organism = MetScapePlugin.getPluginData().getOrganism();
		organismComboBox.setSelectedItem(organism);
		genesTable.removeColumn(genesHomologColumn);
		if(organism != Organism.HUMAN){
			genesTable.addColumn(genesHomologColumn);
		}
	}

	private void updateFilesLabel() {
		String compoundsName = "(none)";
		if (MetScapePlugin.getPluginData().getCompoundData() != null)
			compoundsName = MetScapePlugin.getPluginData().getCompoundData().getName();
		String genesName = "(none)";
		if (MetScapePlugin.getPluginData().getGeneData() != null)
			genesName = MetScapePlugin.getPluginData().getGeneData().getName();
		String conceptsName = "(none)";
		if (MetScapePlugin.getPluginData().getConceptData() != null)
			conceptsName = MetScapePlugin.getPluginData().getConceptData().getName();
		experimentalDataLabel.setText("<html>Compounds: " + compoundsName
				+ "<br>Genes: " + genesName + "<br>Concepts: " + conceptsName
				+ "<br></html>");
	}

	private void updateStatus() {
		updateCompoundTypeStatus();
		updateBuildNetworkStatus();
	}

	private void updateCompoundTypeStatus() {
		Object selected = compoundsGenesComboBox.getSelectedItem();
		compoundsGenesComboBox.removeAllItems();
		if (networkTypeComboBox.getSelectedItem() == NetworkType.COMPOUND) {
			if (compoundsTableModel.getRowCount() > 0) {
				compoundsGenesComboBox.addItem(QuerySubtype.COMPOUNDS);
			}
			if (genesTableModel.getRowCount() > 0) {
				compoundsGenesComboBox.addItem(QuerySubtype.GENES);
			}
			useCompoundsGenes.setText("Use");
			compoundsGenesComboBox.setVisible(true);
		} else { // not compound network
			compoundsGenesComboBox.setVisible(false);
			useCompoundsGenes.setText("Use compounds/genes");
		}
		if (compoundsGenesComboBox.getItemCount() > 0) {
			compoundsGenesComboBox.setEnabled(true);
			if (selected != null) {
				compoundsGenesComboBox.setSelectedItem(selected);
			} else {
				compoundsGenesComboBox.setSelectedIndex(0);
			}
		} else {
			compoundsGenesComboBox.setEnabled(false);
		}
	}

	private void updateBuildNetworkStatus() {
		if ((genesTableModel.getRowCount() > 0)
				|| (compoundsTableModel.getRowCount() > 0)
				|| (usePathway.isSelected() && !pathwayComboBox
						.getSelectedItem().equals(""))) {
			buildNetworkButton.setEnabled(true);
			outputAsFileButton.setEnabled(true);
		} else {
			buildNetworkButton.setEnabled(false);
			outputAsFileButton.setEnabled(false);
		}
	}

	private Set<String> getCids() {
		Set<String> cids = new HashSet<String>();
		for (int i = 0; i < compoundsTableModel.getRowCount(); i++)
			cids.add((String) compoundsTableModel.getValueAt(i, 0));
		return cids;
	}

	private Set<Integer> getGeneids() {
		Set<Integer> geneids = new HashSet<Integer>();
		for (int i = 0; i < genesTableModel.getRowCount(); i++)
			geneids.add((Integer) genesTableModel.getValueAt(i, 0));
		return geneids;
	}

	private void buildNetwork() {
		NetworkData networkData = new NetworkData(MetScapePlugin.getPluginData());
		networkData.setCids(getCids());
		networkData.setGeneids(getGeneids());
		NetworkType networkType = (NetworkType) networkTypeComboBox
				.getSelectedItem();
		if (useCompoundsGenes.isSelected()) {
			if (networkType == NetworkType.COMPOUND) {
				// remove possible conflicting lists
				if (compoundsGenesComboBox.getSelectedItem() == QuerySubtype.COMPOUNDS) {
					networkData.setGeneids(new HashSet<Integer>());
				} else {
					networkData.setCids(new HashSet<String>());
				}
			}
			BuildNewNetworkTask.buildUsing(networkData);
		} else if (usePathway.isSelected()) {
			Pathway pathway = (Pathway) pathwayComboBox.getSelectedItem();
			BuildNewNetworkTask.buildUsing(networkData, pathway);
		}
	}

	private void outputAsFile() {
		File outputFile = FileUtils.getFile("Output as File", FileUtils.SAVE,
				"csv", "CSV File");
		if (outputFile != null) {
			Set<String> compoundIds = getCids();
			Set<Integer> geneIds = getGeneids();
			if (useCompoundsGenes.isSelected()) {
				OutputFileTask.doOutput(compoundIds, geneIds, null, outputFile);
			} else if (usePathway.isSelected()) {
				Pathway pathway = (Pathway) pathwayComboBox.getSelectedItem();
				OutputFileTask.doOutput(compoundIds, geneIds, pathway.getId(),
						outputFile);
			}
		}
	}
	
	private static Font baseFontForTitlePanel = null;
	static Font boldFontForTitlePanel(TitledBorder border){
		//see http://bugs.sun.com/view_bug.do?bug_id=7022041 - getTitleFont() can return null - tew 8/14/12
		// A special thanks to zq (signed 'thomas') from gdufs.edu.cn and Dr. Zaho at kiz.ac.cn for spotting
		// the bug and assisting with the fix.
		if (baseFontForTitlePanel != null) return baseFontForTitlePanel;
		Font font = border.getTitleFont();
		if (font == null) {
			font = UIManager.getDefaults().getFont("TitledBorder.font");
			if (font == null) {
				font = new Font("SansSerif", Font.BOLD, 12);
			} else {
				font = font.deriveFont(Font.BOLD);
			}
		} else {
			font = font.deriveFont(Font.BOLD);			
		}
		baseFontForTitlePanel = font;
		return font;
	}
}
