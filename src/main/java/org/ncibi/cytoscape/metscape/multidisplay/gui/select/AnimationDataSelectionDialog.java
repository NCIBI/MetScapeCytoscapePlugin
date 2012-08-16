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
package org.ncibi.cytoscape.metscape.multidisplay.gui.select;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.CompoundMapping;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.multidisplay.MultiStudyToNetworkMapping;
import org.ncibi.cytoscape.metscape.multidisplay.StudyUtil;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginData;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class AnimationDataSelectionDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private static final String INSTRUCTIONS = "<html><i>Drag and drop labels from the list on "
			+ "the left into the table of data labels for each animation. Set the number of animations "
			+ "(number of rows) and the number of data items per animation (number of columns) in the "
			+ "text fields and reconfigure Layout.</i></html>";

	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable animationConfigurationTable = null;

	private JPanel jPanel = null;
	private JList jList = null;

	private DefaultListModel listModel = null;
	private DefaultTableModel tableModel = null;
	private List<String> originalData = null;

	private CyNetwork network; // @jve:decl-index=0:

	// used for debugging
	private boolean supressNetworkCheck = false;

	private boolean optionsInitialized = false;

	static final String UNDEFINED = "(Undefined)";
	private static final String PROTOTYPE = "This is a data label"; // @jve:decl-index=0:
	private JPanel controlPanel = null;
	private JButton doneButton = null;
	private JButton resetButton = null;
	private JLabel rowsLabel = null;
	private JTextField rowsCountText = null;
	private JLabel colsLabel = null;
	private JTextField colsCountText = null;
	private JButton clearButton = null;
	private JLabel headerLabel = null;
	private JButton cancelButton = null;
	private JLabel instructionLabel = null;

	private TableRowHeader tableRowHeader = null;
	private final Vector<String> colHeaderVector = new Vector<String>();
	private final Vector<String> rowHeaderVector = new Vector<String>();

	public void start() {
		network = Cytoscape.getCurrentNetwork();
		if (!verifyNetworkConfiguration())
			return;
		headerLabel
				.setText("<html><h3>Initialize Coupled Animation of Multiple Data Columns</h3>"
						+ "Network title: <b>"
						+ network.getTitle()
						+ "</b></html>");
		initializeOptions();
		reset();
		setVisible(true);
	}

	private void initializeOptions() {
		if (!optionsInitialized) {
			tableRowHeader = TableUtil.setRowHeader(
					getAnimationConfigurationTable(), rowHeaderVector);
			getAnimationConfigurationTable().setFillsViewportHeight(true);
			tableModel = new DefaultTableModel();
			getAnimationConfigurationTable().setModel(tableModel);
			listModel = new DefaultListModel();
			getJList().setModel(listModel);
			initializeLabelList();
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener
				(Cytoscape.ATTRIBUTES_CHANGED, this);
			optionsInitialized = true;
		}
	}

	private void initializeLabelList() {
		List<String> labels = StudyUtil.getConcentrationLabels(network);
		List<String> args = new ArrayList<String>();
		for (Object o : labels)
			args.add(o.toString());
		System.out.println(labels);
		initializeData(args);
	}

	private void initializeData(List<String> data) {
		originalData = data;
		listModel.clear();
		for (Object o : data)
			listModel.addElement(o);
		getJList().setModel(listModel);
	}

	private void reConfigureLayout(int rows, int cols) {
		ArrayList<String> cache = new ArrayList<String>();
		for (int row = 0; row < tableModel.getRowCount(); row++)
			for (int col = 0; col < tableModel.getColumnCount(); col++)
				cache.add(tableModel.getValueAt(row, col).toString());
		colHeaderVector.clear();
		for (int col = 0; col < cols; col++)
			colHeaderVector.add("Column " + col);
		rowHeaderVector.clear();
		for (int row = 0; row < rows; row++)
			rowHeaderVector.add(TableUtil.animationLabel(row));
		tableModel = new DefaultTableModel(null, colHeaderVector);

		for (int row = 0; row < rows; row++) {
			Vector<String> rowData = new Vector<String>();
			for (int col = 0; col < cols; col++) {
				rowData.add(UNDEFINED);
			}
			tableModel.addRow(rowData);
		}
		int index = 0;
		for (int row = 0; row < tableModel.getRowCount(); row++)
			for (int col = 0; col < tableModel.getColumnCount(); col++)
				if (index < cache.size()) {
					String item = cache.get(index++);
					if (!item.equals(UNDEFINED)) {
						tableModel.setValueAt(item, row, col);
						updateHeader(item, row, col);
					}
				}

		if (index < cache.size()) {
			for (int i = index; i < cache.size(); i++) {
				String item = cache.get(i);
				if (!item.equals(UNDEFINED))
					addToListInOrder(item);
			}
		}
		getAnimationConfigurationTable().setModel(tableModel);
		TableTransferHandler th = new TableTransferHandler(
				getAnimationConfigurationTable(), tableModel, listModel);
		th.setTransferCallback(new TableTransferCallback() {
			@Override
			public void droppedDataIn(String data, int row, int col) {
				tableHeaderUpdateFromTransfer(data, row, col);
			}
		});
		getAnimationConfigurationTable().setTransferHandler(th);
		checkLayoutButton();
		tableModel.fireTableStructureChanged();
		tableRowHeader.updateDisplay();
	}

	private void tableHeaderUpdateFromTransfer(String data, int row, int col) {
		updateHeader(data, row, col);
		tableModel.fireTableStructureChanged();
		tableRowHeader.updateDisplay();
	}

	private void updateHeader(String data, int row, int col) {
		Vector<Vector<String>> rowsVector = getTableModelRowsVector();
		List<String> source = new ArrayList<String>();
		// for the effected column
		for (int rowIndex = 0; rowIndex < rowsVector.size(); rowIndex++) {
			String item = rowsVector.get(rowIndex).get(col);
			if (!item.equals(UNDEFINED)) {
				source.add(item);
			}
		}
		if (source.size() > 1) {
			String suffix = StudyUtil.commonSuffix(source).trim();
			if (suffix.length() > 0) {
				setColumnHeader(col, suffix);
			}
		}

		// for the effected row
		source = new ArrayList<String>();
		for (int colIndex = 0; colIndex < rowsVector.get(0).size(); colIndex++) {
			String item = rowsVector.get(row).get(colIndex);
			if (!item.equals(UNDEFINED)) {
				source.add(item);
			}
		}
		if (source.size() > 1) {
			String prefix = StudyUtil.commonPrefix(source).trim();
			if (prefix.length() > 0) {
				setRowHeader(row, prefix);
			}
		}
	}

	private void setColumnHeader(int col, String suffix) {
		if (col < 0)
			return;
		if (col >= colHeaderVector.size())
			return;
		colHeaderVector.set(col, suffix);
	}

	private void setRowHeader(int row, String prefix) {
		if (row < 0)
			return;
		if (row >= rowHeaderVector.size())
			return;
		rowHeaderVector.set(row, prefix);
	}

	@SuppressWarnings("unchecked")
	private Vector<Vector<String>> getTableModelRowsVector() {
		return tableModel.getDataVector();
	}

	private boolean verifyNetworkConfiguration() {
		// used for debugging
		if (supressNetworkCheck)
			return true;
		if ((network == null) || (network.getIdentifier().equals("0"))) {
			JOptionPane.showMessageDialog(this,
					"No network selected. Please select a starting MetScape network.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		PluginData pluginData = MetScapePlugin.getPluginData();
		NetworkData networkData = pluginData.getNetworkData(Networks.getUUID(network));
		
		if (networkData == null) {
			JOptionPane.showMessageDialog(this,
							"This network appears to not be a MetScape network. Please select a MetScape network.",
							"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		CompoundMapping compoundMapping = pluginData.getNetworkData(Networks.getUUID(network)).getCompoundMapping();

		if (compoundMapping.isEmpty()){
			JOptionPane.showMessageDialog(this,
			"There is no compound concentration data associated with the network.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		String baseAttributeName = StudyUtil.makeBaseAttributeName(network);
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			if(Attributes.node.hasAttribute(cyNode.getIdentifier(), baseAttributeName)) {
				return true;
			}
		}
		JOptionPane.showMessageDialog
			(this, "There is no compound concentration data for any of the nodes in the network.", "Error", JOptionPane.ERROR_MESSAGE);
		return false;
	}

	private void addToListInOrder(String item) {
		listModel.addElement(item);
		// TODO: add item to list in original order
	}

	public void addDataList(ArrayList<Object> target) {
		initializeOptions();
		for (Object o : target) {
			listModel.addElement(o);
		}
	}

	private void reset() {
		int rows = getRowTextInt();
		int cols = getColTextInt();
		if ((rows < 0) || (cols < 0)) {
			return;
		}
		reConfigureLayout(rows, cols);
	}

	private int getRowTextInt() {
		int value = -1;
		try {
			value = Integer.parseInt(getRowsCountText().getText());
		} catch (Throwable ignore) {
		}
		return value;
	}

	private int getColTextInt() {
		int value = -1;
		try {
			value = Integer.parseInt(getColsCountText().getText());
		} catch (Throwable ignore) {
		}
		return value;
	}

	private void checkLayoutButton() {
		int rowCount = tableModel.getRowCount();
		int colCount = tableModel.getColumnCount();
		int rows = getRowTextInt();
		int cols = getColTextInt();
		getResetButton().setEnabled(false);
		if ((rows < 0) || (cols < 0))
			return;
		if ((rows == rowCount) && (cols == colCount))
			return;
		getResetButton().setEnabled(true);
	}

	private void clearLayout() {
		for (int row = 0; row < tableModel.getRowCount(); row++)
			for (int col = 0; col < tableModel.getColumnCount(); col++)
				tableModel.setValueAt(UNDEFINED, row, col);
		initializeData(originalData);
	}

	private void done() {
		int numberOfAnimations = this.getRowTextInt();
		int stepsPerAnimation = this.getColTextInt();
		if ((numberOfAnimations == -1) || (stepsPerAnimation == -1)) {
			JOptionPane.showMessageDialog(this, "Rows (="
					+ getRowsCountText().getText() + ") " + "and cols (="
					+ getColsCountText().getText() + ") "
					+ "must be integer values");
			return;
		}

		String[][] labelMatrix = new String[numberOfAnimations][stepsPerAnimation];
		String[] animationLabelArray = new String[numberOfAnimations];
		String[] timeStepLabelArray = new String[stepsPerAnimation];
		for (int animationIndex = 0; animationIndex < numberOfAnimations; animationIndex++) {
			for (int valueLableIndex = 0; valueLableIndex < stepsPerAnimation; valueLableIndex++) {
				String label = tableModel.getValueAt(animationIndex,
						valueLableIndex).toString();
				if ((label == null) || label.equals(UNDEFINED)) {
					JOptionPane.showMessageDialog(this, "Table cell value (= "
							+ label + ") " + "at location (row,col) = ("
							+ animationIndex + "," + valueLableIndex + ") "
							+ "can not be undefined");
					return;
				}
				labelMatrix[animationIndex][valueLableIndex] = tableModel
						.getValueAt(animationIndex, valueLableIndex).toString();
			}
			animationLabelArray[animationIndex] = rowHeaderVector
					.elementAt(animationIndex);
		}
		for (int valueLableIndex = 0; valueLableIndex < stepsPerAnimation; valueLableIndex++) {
			timeStepLabelArray[valueLableIndex] = colHeaderVector.elementAt(valueLableIndex);
		}
		if (supressNetworkCheck) {
			// used for debugging
			System.out.println("Animation labels:");
			System.out.print("  ");
			for (String s : animationLabelArray)
				System.out.print(" " + s);
			System.out.println();
			System.out.println("Label matrix :");
			for (String[] a : labelMatrix) {
				System.out.print("   ");
				for (String s : a)
					System.out.print(" " + s);
				System.out.println();
			}
		} else {
			MultiStudyToNetworkMapping map = new MultiStudyToNetworkMapping(
					animationLabelArray, timeStepLabelArray, labelMatrix);
			try {
				StudyUtil.startAnimation(network, map);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		setVisible(false);
	}

	private void cancel() {
		setVisible(false);
	}

	public void setSupressNetworkCheck(boolean b) {
		// used for debugging!
		supressNetworkCheck = true;
	}

	// Eclipse generated code - below this line

	/**
	 * @param owner
	 * 
	 *            Default constructor; used by GUI interface in Eclipse
	 */
	public AnimationDataSelectionDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setBorder(BorderFactory.createEtchedBorder());
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getJPanel(), BorderLayout.EAST);
			jContentPane.add(getControlPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(BorderFactory.createEtchedBorder());
			jScrollPane.setViewportView(getAnimationConfigurationTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes animationConfigurationTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getAnimationConfigurationTable() {
		if (animationConfigurationTable == null) {
			animationConfigurationTable = new JTable();
			animationConfigurationTable.setDropMode(DropMode.ON);
		}
		return animationConfigurationTable;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJList(), gridBagConstraints);
		}
		return jPanel;
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setBorder(BorderFactory.createEtchedBorder());
			jList.setPrototypeCellValue(PROTOTYPE);
			jList.setDragEnabled(true);
		}
		return jList;
	}

	/**
	 * This method initializes controlPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getControlPanel() {
		if (controlPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridwidth = 8;
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 1;
			instructionLabel = new JLabel("text");
			instructionLabel.setBorder(BorderFactory
					.createLineBorder(Color.BLACK));
			instructionLabel.setText(INSTRUCTIONS);
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 6;
			gridBagConstraints9.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridwidth = 8;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			headerLabel = new JLabel();
			headerLabel.setText("Header Text");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 5;
			gridBagConstraints7.gridy = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 2;
			colsLabel = new JLabel();
			colsLabel.setText("Cols");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 2;
			rowsLabel = new JLabel();
			rowsLabel.setText("Rows");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 7;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 4;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.gridy = 2;
			controlPanel = new JPanel();
			controlPanel.setLayout(new GridBagLayout());
			controlPanel.add(getDoneButton(), gridBagConstraints2);
			controlPanel.add(getResetButton(), gridBagConstraints1);
			controlPanel.add(rowsLabel, gridBagConstraints3);
			controlPanel.add(getRowsCountText(), gridBagConstraints4);
			controlPanel.add(colsLabel, gridBagConstraints5);
			controlPanel.add(getColsCountText(), gridBagConstraints6);
			controlPanel.add(getClearButton(), gridBagConstraints7);
			controlPanel.add(headerLabel, gridBagConstraints8);
			controlPanel.add(getCancelButton(), gridBagConstraints9);
			controlPanel.add(instructionLabel, gridBagConstraints10);
		}
		return controlPanel;
	}

	/**
	 * This method initializes doneButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDoneButton() {
		if (doneButton == null) {
			doneButton = new JButton();
			doneButton.setText("Build Animation");
			doneButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					done();
				}
			});
		}
		return doneButton;
	}

	/**
	 * This method initializes resetButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton();
			resetButton.setText("Reconfigure Layout");
			resetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					reset();
				}
			});
		}
		return resetButton;
	}

	/**
	 * This method initializes rowsCountText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRowsCountText() {
		if (rowsCountText == null) {
			rowsCountText = new JTextField(3);
			rowsCountText.setText("2");
			rowsCountText
					.addCaretListener(new javax.swing.event.CaretListener() {
						public void caretUpdate(javax.swing.event.CaretEvent e) {
							checkLayoutButton();
						}
					});
		}
		return rowsCountText;
	}

	/**
	 * This method initializes colsCountText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getColsCountText() {
		if (colsCountText == null) {
			colsCountText = new JTextField(3);
			colsCountText.setText("3");
			colsCountText
					.addCaretListener(new javax.swing.event.CaretListener() {
						public void caretUpdate(javax.swing.event.CaretEvent e) {
							checkLayoutButton();
						}
					});
		}
		return colsCountText;
	}

	/**
	 * This method initializes clearButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Restart");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					clearLayout();
				}
			});
		}
		return clearButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});
		}
		return cancelButton;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		optionsInitialized = false;
		Cytoscape.getPropertyChangeSupport().
			removePropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
	}

}
