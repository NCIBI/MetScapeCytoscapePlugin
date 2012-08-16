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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.DataParameters;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.MultiColumnData;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginData;
import org.ncibi.cytoscape.metscape.task.GetDataMappingsTask;
import org.ncibi.cytoscape.metscape.task.ImportCompoundFileTask;
import org.ncibi.cytoscape.metscape.task.ImportConceptFileTask;
import org.ncibi.cytoscape.metscape.task.ImportGeneFileTask;
import org.ncibi.cytoscape.metscape.task.LRpathTask;
import org.ncibi.cytoscape.metscape.utils.ImageUtils;
import org.ncibi.cytoscape.util.FileUtils;

import cytoscape.Cytoscape;

@SuppressWarnings("serial")
public class SelectDataDialog extends JDialog  {
	
	private List<CompoundData> newCompoundData = new ArrayList<CompoundData>();
	private List<GeneData> newGeneData = new ArrayList<GeneData>();
	private List<ConceptData> newConceptData = new ArrayList<ConceptData>();
	
	private JLabel organismLabel;
	private JComboBox organismComboBox;
	private JPanel dataPanel;
	private TitledBorder dataBorder;
	private JPanel compoundsPanel;
	private TitledBorder compoundsBorder;
	private JLabel selectCompoundData;
	private JComboBox compoundDataComboBox;
	private JButton importCompoundFileButton;
	private JLabel compoundsPvalueLabel;
	private JComboBox compoundsPvalueComboBox;
	private JLabel compoundsPvalueThresholdLabel;
	private JTextField compoundsPvalueThresholdField;
	private JLabel compoundsFoldChangeLabel;
	private JComboBox compoundsFoldChangeComboBox;
	private JLabel compoundsFoldChangeThresholdLabel;
	private JTextField compoundsFoldChangeThresholdField;
	private JPanel genesPanel;
	private TitledBorder genesBorder;
	private JLabel selectGeneData;
	private JComboBox geneDataComboBox;
	private JButton importGeneFileButton;
	private JLabel genesPvalueLabel;
	private JComboBox genesPvalueComboBox;
	private JLabel genesPvalueThresholdLabel;
	private JTextField genesPvalueThresholdField;
	private JLabel genesFoldChangeLabel;
	private JComboBox genesFoldChangeComboBox;
	private JLabel genesFoldChangeThresholdLabel;
	private JTextField genesFoldChangeThresholdField;
	private JPanel conceptsPanel;
	private TitledBorder conceptsBorder;
	private JLabel selectConceptData;
	private JComboBox conceptDataComboBox;
	private JButton importConceptFileButton;
	private JButton generateFromGenesUsingLRpath;
	private ImageIcon LRpathHelpIcon;
	private JButton LRpathHelp;
	
	private JButton okButton;
	private JButton cancelButton;
	private boolean cancelled;
	
	public static boolean selectExperimentalData() {
		SelectDataDialog dialog = new SelectDataDialog(Cytoscape.getDesktop());
		dialog.setVisible(true);
		return (!dialog.cancelled);
	}
	
	private SelectDataDialog(Frame owner) {
		super(owner,true);
		setTitle("Select Experimental Data");
		createControls();
	}
	
	private void createControls() {
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		dataPanel = new JPanel();
		dataBorder = BorderFactory.createTitledBorder("Data");
		dataPanel.setBorder(dataBorder);
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
		organismLabel = new JLabel("Organism");
		organismComboBox = new JComboBox();
		for(Organism organism: Organism.values() ) {
			organismComboBox.addItem(organism);
		}
		organismComboBox.setSelectedItem(MetScapePlugin.getPluginData().getOrganism());
		dataPanel.add(new Box(BoxLayout.X_AXIS){
			{
				setAlignmentX(Component.RIGHT_ALIGNMENT);
				add(organismLabel);
				add(Box.createHorizontalStrut(5));
				add(organismComboBox);
			}
		});
		compoundsPanel = new JPanel();
		compoundsBorder = BorderFactory.createTitledBorder("Compounds");
		compoundsBorder.setTitleFont(BuildNetworkPanel.boldFontForTitlePanel(compoundsBorder));
		compoundsPanel.setBorder(compoundsBorder);
		compoundsPanel.setLayout(new BoxLayout(compoundsPanel, BoxLayout.Y_AXIS));
		compoundsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectCompoundData = new JLabel("Select experimental data");
		compoundsPanel.add(selectCompoundData);
		compoundDataComboBox = new JComboBox();
		compoundDataComboBox.setEnabled(true);
		compoundDataComboBox.setEditable(false);
		compoundDataComboBox.setPrototypeDisplayValue("");
		for(CompoundData compoundData: MetScapePlugin.getPluginData().getCompoundDataStore())
			compoundDataComboBox.insertItemAt(compoundData, 0);
		compoundDataComboBox.setSelectedItem(MetScapePlugin.getPluginData().getCompoundData());
		compoundDataComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					CompoundData compoundData = (CompoundData) compoundDataComboBox.getSelectedItem();
					populateDataFields(compoundData.getColumns(), new DataParameters(), compoundsPvalueComboBox,
							compoundsPvalueThresholdField, compoundsFoldChangeComboBox, compoundsFoldChangeThresholdField);
				}
			}
		});
		importCompoundFileButton = new JButton("Import File...");
		importCompoundFileButton.setEnabled(true);
		importCompoundFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File compoundFile = FileUtils.getFile("Import Compound File", FileUtils.LOAD);
				if(compoundFile != null) {
					CompoundData compoundData = ImportCompoundFileTask.importFrom(compoundFile);
					if(compoundData != null) {
						compoundData.setName(generateUniqueName(compoundData.getName(),compoundDataComboBox));
						compoundDataComboBox.insertItemAt(compoundData, 0);
						compoundDataComboBox.setSelectedItem(compoundData);
						newCompoundData.add(compoundData);
					}
				}
			}
		});
		compoundsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(compoundDataComboBox);
				add(importCompoundFileButton);
			}
		});
		compoundsPvalueLabel = new JLabel("P-value");
		compoundsPvalueComboBox = new JComboBox();
		compoundsPvalueComboBox.setPrototypeDisplayValue("");
		compoundsPvalueComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					boolean enableThreshold = compoundsPvalueComboBox.getSelectedItem() != null && 
						!compoundsPvalueComboBox.getSelectedItem().equals("(none)");
					compoundsPvalueThresholdField.setEnabled(enableThreshold);
					compoundsPvalueThresholdLabel.setEnabled(enableThreshold);
					if(enableThreshold && compoundsPvalueThresholdField.getText().trim().equals("")) {
						compoundsPvalueThresholdField.setText("0.05");
						compoundsPvalueThresholdField.setInputVerifier(new NumberVerifier(compoundsPvalueThresholdField));
					}
					else if(!enableThreshold && !compoundsPvalueThresholdField.getText().trim().equals("")) {
						compoundsPvalueThresholdField.setText(null);
						compoundsPvalueThresholdField.setInputVerifier(null);
					}
				}
			}
		});
		compoundsPvalueThresholdLabel = new JLabel("Threshold");
		compoundsPvalueThresholdField = new JTextField();
		compoundsPvalueThresholdField.setColumns(6);
		compoundsPvalueThresholdField.setMaximumSize(compoundsPvalueThresholdField.getPreferredSize());
		compoundsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(compoundsPvalueLabel);
				add(compoundsPvalueComboBox);
				add(Box.createHorizontalStrut(3));
				add(compoundsPvalueThresholdLabel);
				add(compoundsPvalueThresholdField);
			}
		});
		compoundsFoldChangeLabel = new JLabel("Fold Change");
		compoundsFoldChangeComboBox = new JComboBox();
		compoundsFoldChangeComboBox.setPrototypeDisplayValue("");
		compoundsFoldChangeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					boolean enableThreshold = compoundsFoldChangeComboBox.getSelectedItem() != null && 
						!compoundsFoldChangeComboBox.getSelectedItem().equals("(none)");
					compoundsFoldChangeThresholdField.setEnabled(enableThreshold);
					compoundsFoldChangeThresholdLabel.setEnabled(enableThreshold);
					if(enableThreshold && compoundsFoldChangeThresholdField.getText().trim().equals("")) {
						CompoundData compoundData = (CompoundData) compoundDataComboBox.getSelectedItem();
						if(compoundData.getColumnIsSigned().get(compoundsFoldChangeComboBox.getSelectedItem())) {
							compoundsFoldChangeThresholdField.setText("0.0");
							compoundsFoldChangeThresholdField.setInputVerifier
								(new NumberVerifier(compoundsFoldChangeThresholdField));
						}
						else {
							compoundsFoldChangeThresholdField.setText("1.0");
							compoundsFoldChangeThresholdField.setInputVerifier
								(new PositiveNumberVerifier(compoundsFoldChangeThresholdField));
						}
					}
					if(!enableThreshold && !compoundsFoldChangeThresholdField.getText().trim().equals("")) {
						compoundsFoldChangeThresholdField.setText(null);
						compoundsFoldChangeThresholdField.setInputVerifier(null);
					}
				}
			}
		});
		compoundsFoldChangeThresholdLabel = new JLabel("Threshold");
		compoundsFoldChangeThresholdField = new JTextField();
		compoundsFoldChangeThresholdField.setColumns(6);
		compoundsFoldChangeThresholdField.setMaximumSize(compoundsPvalueThresholdField.getPreferredSize());	
		compoundsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(compoundsFoldChangeLabel);
				add(compoundsFoldChangeComboBox);
				add(Box.createHorizontalStrut(3));
				add(compoundsFoldChangeThresholdLabel);
				add(compoundsFoldChangeThresholdField);
			}
		});
		dataPanel.add(compoundsPanel);
		
		genesPanel = new JPanel();
		genesBorder = BorderFactory.createTitledBorder("Genes");
		genesBorder.setTitleFont(BuildNetworkPanel.boldFontForTitlePanel(genesBorder));
		genesPanel.setBorder(genesBorder);
		genesPanel.setLayout(new BoxLayout(genesPanel, BoxLayout.Y_AXIS));
		genesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectGeneData = new JLabel("Select experimental data");
		genesPanel.add(selectGeneData);
		geneDataComboBox = new JComboBox();
		geneDataComboBox.setEnabled(true);
		geneDataComboBox.setEditable(false);
		geneDataComboBox.setPrototypeDisplayValue("");
		for(GeneData geneData: MetScapePlugin.getPluginData().getGeneDataStore())
			geneDataComboBox.insertItemAt(geneData,0);
		geneDataComboBox.setSelectedItem(MetScapePlugin.getPluginData().getGeneData());
		geneDataComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					GeneData geneData = (GeneData) geneDataComboBox.getSelectedItem();
					populateDataFields(geneData.getColumns(), new DataParameters(), genesPvalueComboBox,
							genesPvalueThresholdField, genesFoldChangeComboBox, genesFoldChangeThresholdField);
					updateLRpathStatus();
				}
			}
		});
		importGeneFileButton = new JButton("Import File...");
		importGeneFileButton.setEnabled(true);
		importGeneFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File geneFile = FileUtils.getFile("Import Gene File", FileUtils.LOAD);
				if(geneFile != null) {
					GeneData geneData = ImportGeneFileTask.importFrom(geneFile);
					if(geneData != null) {
						geneData.setName(generateUniqueName(geneData.getName(),geneDataComboBox));
						geneDataComboBox.insertItemAt(geneData, 0);
						geneDataComboBox.setSelectedItem(geneData);
						newGeneData.add(geneData);
					}
				}
			}
		});
		genesPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(geneDataComboBox);
				add(importGeneFileButton);
			}
		});
		genesPvalueLabel = new JLabel("P-value");
		genesPvalueComboBox = new JComboBox();
		genesPvalueComboBox.setPrototypeDisplayValue("");
		genesPvalueComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					boolean enableThreshold = genesPvalueComboBox.getSelectedItem() != null && 
						!genesPvalueComboBox.getSelectedItem().equals("(none)");
					genesPvalueThresholdField.setEnabled(enableThreshold);
					genesPvalueThresholdLabel.setEnabled(enableThreshold);
					if(enableThreshold && genesPvalueThresholdField.getText().trim().equals("")) {
						genesPvalueThresholdField.setText("0.05");
						genesPvalueThresholdField.setInputVerifier(new NumberVerifier(genesPvalueThresholdField));
					}
					else if(!enableThreshold && !genesPvalueThresholdField.getText().trim().equals("")) {
						genesPvalueThresholdField.setText(null);
						genesPvalueThresholdField.setInputVerifier(null);
					}
					updateLRpathStatus();
				}
			}
		});
		genesPvalueThresholdLabel = new JLabel("Threshold");
		genesPvalueThresholdField = new JTextField();
		genesPvalueThresholdField.setColumns(6);
		genesPvalueThresholdField.setMaximumSize(genesPvalueThresholdField.getPreferredSize());
		genesPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(genesPvalueLabel);
				add(genesPvalueComboBox);
				add(Box.createHorizontalStrut(3));
				add(genesPvalueThresholdLabel);
				add(genesPvalueThresholdField);
			}
		});
		genesFoldChangeLabel = new JLabel("Fold Change");
		genesFoldChangeComboBox = new JComboBox();
		genesFoldChangeComboBox.setPrototypeDisplayValue("");
		genesFoldChangeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == "comboBoxChanged") {
					boolean enableThreshold = genesFoldChangeComboBox.getSelectedItem() != null && 
						!genesFoldChangeComboBox.getSelectedItem().equals("(none)");
					genesFoldChangeThresholdField.setEnabled(enableThreshold);
					genesFoldChangeThresholdLabel.setEnabled(enableThreshold);
					if(enableThreshold && genesFoldChangeThresholdField.getText().trim().equals("")) {
						GeneData geneData = (GeneData) geneDataComboBox.getSelectedItem();
						if(geneData.getColumnIsSigned().get(genesFoldChangeComboBox.getSelectedItem())) {
							genesFoldChangeThresholdField.setText("0.0");
							genesFoldChangeThresholdField.setInputVerifier
								(new NumberVerifier(genesFoldChangeThresholdField));
						}
						else {
							genesFoldChangeThresholdField.setText("1.0");
							genesFoldChangeThresholdField.setInputVerifier
								(new PositiveNumberVerifier(genesFoldChangeThresholdField));
						}
					}
					if(!enableThreshold && !genesFoldChangeThresholdField.getText().trim().equals("")) {
						genesFoldChangeThresholdField.setText(null);
						genesFoldChangeThresholdField.setInputVerifier(null);
					}
					updateLRpathStatus();
				}
			}
		});
		genesFoldChangeThresholdLabel = new JLabel("Threshold");
		genesFoldChangeThresholdField = new JTextField();
		genesFoldChangeThresholdField.setColumns(6);
		genesFoldChangeThresholdField.setMaximumSize(genesPvalueThresholdField.getPreferredSize());
		genesPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(genesFoldChangeLabel);
				add(genesFoldChangeComboBox);
				add(Box.createHorizontalStrut(3));
				add(genesFoldChangeThresholdLabel);
				add(genesFoldChangeThresholdField);
			}
		});
		dataPanel.add(genesPanel);
		
		conceptsPanel = new JPanel();
		conceptsBorder = BorderFactory.createTitledBorder("Concepts");
		conceptsBorder.setTitleFont(BuildNetworkPanel.boldFontForTitlePanel(conceptsBorder));
		conceptsPanel.setBorder(conceptsBorder);
		conceptsPanel.setLayout(new BoxLayout(conceptsPanel, BoxLayout.Y_AXIS));
		conceptsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectConceptData = new JLabel("Select experimental data");
		conceptsPanel.add(selectConceptData);
		conceptDataComboBox = new JComboBox();
		conceptDataComboBox.setEnabled(true);
		conceptDataComboBox.setEditable(false);
		conceptDataComboBox.setPrototypeDisplayValue("");
		for(ConceptData conceptData: MetScapePlugin.getPluginData().getConceptDataStore())
			conceptDataComboBox.insertItemAt(conceptData,0);
		conceptDataComboBox.setSelectedItem(MetScapePlugin.getPluginData().getConceptData());
		importConceptFileButton = new JButton("Import File...");
		importConceptFileButton.setEnabled(true);
		importConceptFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File conceptFile = FileUtils.getFile("Import Concept File", FileUtils.LOAD);
				if(conceptFile != null) {
					ConceptData conceptData = ImportConceptFileTask.importFrom(conceptFile);
					if(conceptData != null) {
						conceptData.setName(generateUniqueName(conceptData.getName(),conceptDataComboBox));
						conceptDataComboBox.insertItemAt(conceptData, 0);
						conceptDataComboBox.setSelectedItem(conceptData);
						newConceptData.add(conceptData);
					}
				}
			}
		});
		conceptsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(conceptDataComboBox);
				add(importConceptFileButton);
			}
		});
		JLabel generateFromGenesUsingLRpathHelpLabel = 
			new JLabel("<html><i>To use LRPath, import Gene data with " +
					"P-value and Fold Change.</i></html>");
		conceptsPanel.add(generateFromGenesUsingLRpathHelpLabel);
		generateFromGenesUsingLRpath = new JButton("Generate concept data from genes using LRpath");
		generateFromGenesUsingLRpath.setEnabled(false);
		generateFromGenesUsingLRpath.setSelected(false);
		generateFromGenesUsingLRpath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConceptData conceptData = LRpathTask.runUsingGeneData((GeneData) geneDataComboBox.getSelectedItem(), 
						(Organism) organismComboBox.getSelectedItem(), 
						(String) genesPvalueComboBox.getSelectedItem(),
						(String) genesFoldChangeComboBox.getSelectedItem());
				if(conceptData != null) {
					conceptData.setName(generateUniqueName(conceptData.getName(),conceptDataComboBox));
					conceptDataComboBox.insertItemAt(conceptData, 0);
					conceptDataComboBox.setSelectedItem(conceptData);
					newConceptData.add(conceptData);
				}
			}
		});
		LRpathHelpIcon = ImageUtils.createImageIcon("/icons/question_mark.png", "LRpath Help");
		LRpathHelp = new JButton(LRpathHelpIcon);
		LRpathHelp.setBorder(null);
		LRpathHelp.setFocusPainted(false);
		LRpathHelp.setContentAreaFilled(false);
		LRpathHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLRPathHelpDialog();
			}
		});
		LRpathHelp.setEnabled(true);
		LRpathHelp.setFocusable(false);
		conceptsPanel.add(new Box(BoxLayout.X_AXIS) {
			{
				setAlignmentX(Component.LEFT_ALIGNMENT);
				add(generateFromGenesUsingLRpath);
				add(LRpathHelp);
			}
		});
		dataPanel.add(conceptsPanel);
		
		getContentPane().add(dataPanel);
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSession();
			}
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		getContentPane().add(new Box(BoxLayout.X_AXIS) {
			{
				add(okButton);
				add(cancelButton);
			}
		});
		populateDataFields(MetScapePlugin.getPluginData().getCompoundData().getColumns(),
				MetScapePlugin.getPluginData().getCompoundParameters(), 
				compoundsPvalueComboBox, compoundsPvalueThresholdField, 
				compoundsFoldChangeComboBox,compoundsFoldChangeThresholdField);
		populateDataFields(MetScapePlugin.getPluginData().getGeneData().getColumns(),
				MetScapePlugin.getPluginData().getGeneParameters(), 
				genesPvalueComboBox, genesPvalueThresholdField, 
				genesFoldChangeComboBox,genesFoldChangeThresholdField);
		pack();
		addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            	cancelled = true;
            }
		});
		setSize(new Dimension(500,getSize().height));
		setLocationRelativeTo(getOwner());
	}

	private String generateUniqueName(String baseName, JComboBox comboBox) {
		for (int i = 0; true; i++) {
			String name = baseName + ((i == 0) ? "" : ("." + i));
			boolean inUse = false;
			for(int j = 0; j < comboBox.getItemCount(); j++) {
				if(comboBox.getItemAt(j).toString().equals(name)) {
					inUse = true;
					break;
				}
			}
			if(!inUse)
				return name;
		}
	}
	
	private void populateDataFields(String[] columns, DataParameters parameters, JComboBox pvalueComboBox,
			JTextField pvalueThresholdField, JComboBox foldChangeComboBox, 
			JTextField foldChangeThresholdField) {
		pvalueComboBox.removeAllItems(); 
		foldChangeComboBox.removeAllItems();
		pvalueComboBox.addItem("(none)");
		pvalueComboBox.setSelectedIndex(0);
		foldChangeComboBox.addItem("(none)");
		foldChangeComboBox.setSelectedIndex(0);
		for(String column: columns)
		{
			pvalueComboBox.addItem(column);
			foldChangeComboBox.addItem(column);
		}
		if(parameters.getPvalueColumn() != null)
			pvalueComboBox.setSelectedItem(parameters.getPvalueColumn());
		else
			pvalueComboBox.setSelectedItem("(none)");

		if(parameters.getPvalueThreshold() != null)
			pvalueThresholdField.setText(parameters.getPvalueThreshold().toString());
		else
			pvalueThresholdField.setText(null);

		if(parameters.getFoldChangeColumn() != null)
			foldChangeComboBox.setSelectedItem(parameters.getFoldChangeColumn());
		else
			foldChangeComboBox.setSelectedItem("(none)");

		if(parameters.getFoldChangeThreshold() != null)
			foldChangeThresholdField.setText(parameters.getFoldChangeThreshold().toString());
		else
			foldChangeThresholdField.setText(null);
	}
	
	private void updateSession() {
		CompoundData compoundData = (CompoundData) compoundDataComboBox.getSelectedItem();
		GeneData geneData = (GeneData) geneDataComboBox.getSelectedItem();
		ConceptData conceptData = (ConceptData) conceptDataComboBox.getSelectedItem();
		Organism organism = (Organism) organismComboBox.getSelectedItem();
		PluginData pluginData = MetScapePlugin.getPluginData();;
		if(!compoundData.equals(pluginData.getCompoundData()) 
				|| !geneData.equals(pluginData.getGeneData())
				|| !conceptData.equals(pluginData.getConceptData())
				|| !organism.equals(pluginData.getOrganism())) {

			if(!GetDataMappingsTask.getMappings(compoundData, geneData, conceptData, organism))
				return;
		}
		pluginData.setCompoundData(compoundData);
		pluginData.setGeneData(geneData);
		pluginData.setConceptData(conceptData);
		pluginData.setOrganism(organism);
		DataParameters compoundParameters = pluginData.getCompoundParameters();
		if(!compoundsPvalueComboBox.getSelectedItem().equals("(none)"))
			compoundParameters.setPvalueColumn(compoundsPvalueComboBox.getSelectedItem().toString());
		else
			compoundParameters.setPvalueColumn(null);
		compoundParameters.setPvalueThreshold(NumUtils.toDouble(compoundsPvalueThresholdField.getText()));
		if(!compoundsFoldChangeComboBox.getSelectedItem().equals("(none)"))
			compoundParameters.setFoldChangeColumn(compoundsFoldChangeComboBox.getSelectedItem().toString());
		else
			compoundParameters.setFoldChangeColumn(null);
		compoundParameters.setFoldChangeThreshold(NumUtils.toDouble(compoundsFoldChangeThresholdField.getText()));
		setFoldChangeUpDownThresholds(compoundData,compoundParameters);
		
		DataParameters geneParameters = pluginData.getGeneParameters();
		if(!genesPvalueComboBox.getSelectedItem().equals("(none)"))
			geneParameters.setPvalueColumn(genesPvalueComboBox.getSelectedItem().toString());
		else
			geneParameters.setPvalueColumn(null);
		geneParameters.setPvalueThreshold(NumUtils.toDouble(genesPvalueThresholdField.getText()));
		if(!genesFoldChangeComboBox.getSelectedItem().equals("(none)"))
			geneParameters.setFoldChangeColumn(genesFoldChangeComboBox.getSelectedItem().toString());
		else
			geneParameters.setFoldChangeColumn(null);
		geneParameters.setFoldChangeThreshold(NumUtils.toDouble(genesFoldChangeThresholdField.getText()));
		setFoldChangeUpDownThresholds(geneData,geneParameters);
		
		for(CompoundData compoundDataItem: newCompoundData) {
			pluginData.getCompoundDataStore().add(compoundDataItem);
			Attributes.node.setUserVisible("Compound." + compoundDataItem.getName(),false);
		}
		for(GeneData geneDataItem: newGeneData) {
			pluginData.getGeneDataStore().add(geneDataItem);
			Attributes.node.setUserVisible("Gene." + geneDataItem.getName(),false);
		}
		pluginData.getConceptDataStore().addAll(newConceptData);
		cancelled = false;
		setVisible(false);
	}
	
	private void updateLRpathStatus() {
		if(genesPvalueComboBox.getSelectedItem() != null && !genesPvalueComboBox.getSelectedItem().equals("(none)") &&
				genesFoldChangeComboBox.getSelectedItem() != null && !genesFoldChangeComboBox.getSelectedItem().equals("(none)")) {
			generateFromGenesUsingLRpath.setEnabled(true);
		}
		else generateFromGenesUsingLRpath.setEnabled(false);
	}
	
	private void setFoldChangeUpDownThresholds(MultiColumnData data, DataParameters parameters) {
		if(parameters.getFoldChangeThreshold() == null) return;
		Double upThreshold = parameters.getFoldChangeThreshold();
		Double downThreshold = parameters.getFoldChangeThreshold();
		if(data.getColumnIsSigned().get(parameters.getFoldChangeColumn())) {
			upThreshold = Math.abs(upThreshold);
			downThreshold = -Math.abs(downThreshold);
		}
		else {
			if(downThreshold >= 1) {
				downThreshold = 1/downThreshold;
			}
			else {
				upThreshold = 1/upThreshold;
			}
		}
		parameters.setFoldChangeUpThreshold(upThreshold);
		parameters.setFoldChangeDownThreshold(downThreshold);
	}
	
	private void showLRPathHelpDialog() {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"<html><body>" +
				"<b>Pathway Analysis Using Logistic Regression</b>" +
				"<ul>" +
				"<li>LRpath performs gene set enrichment testing, " +
				"an approach used to test for predefined biologically-relevant<br /> " +
				"gene sets that contain more significant genes from an experimental dataset" +
				" than expected by chance.<br />" +
				"For more information see Sartor et al., Bioinformatics. 2009 Jan 15;25(2):211-7 " +
				"(http://www.ncbi.nlm.nih.gov/pubmed/19038984).</li>" +
				"<li>To run LRpath you need a Gene Expression file with fold change " +
				"(or log fold change) values and p-values.</li>" +
				"<li>The Gene Expression file should contain all the genes records, " +
				"not just the significant ones, <br />" +
				"as LRpath will determine the significant genes from the input." +
				"</li></ul></body></html>", 
				"About LRpath", JOptionPane.INFORMATION_MESSAGE);
	}
	
	class NumberVerifier extends InputVerifier {
		String lastGood;
		String message;
		
		public NumberVerifier(JTextField textField) {
			lastGood = textField.getText();
			message = "Threshold must be a number.";
		}
		
		public boolean shouldYieldFocus(JComponent input) {
			JTextField textField = (JTextField) input;
			if(verify(input)) {
				lastGood = textField.getText();
				return true;
			}
			else {
				JOptionPane.showMessageDialog(SelectDataDialog.this, message, 
						"Invalid Input", JOptionPane.ERROR_MESSAGE);
				textField.setText(lastGood);
				return false;
			}
		}

		public boolean verify(JComponent input) {
			JTextField textField = (JTextField) input;
			if(NumUtils.toDouble(textField.getText()) != null)
				return true;
			else
				return false;
		}
	}
	
	class PositiveNumberVerifier extends NumberVerifier {

		public PositiveNumberVerifier(JTextField textField) {
			super(textField);
			message = "Threshold must be a positive number greater than zero.";
		}
		
		public boolean verify(JComponent input) {
			JTextField textField = (JTextField) input;
			Double value = NumUtils.toDouble(textField.getText());
			if(value != null && value > 0)
				return true;
			else
				return false;
		}
		
	}
}
