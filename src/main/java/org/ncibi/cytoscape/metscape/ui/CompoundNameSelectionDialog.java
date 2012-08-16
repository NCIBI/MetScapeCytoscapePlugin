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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.commons.web.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.util.FileUtils;
import org.ncibi.metab.link.CompoundLink;
import org.ncibi.metab.name.MetabolicName;

import cytoscape.task.Task;

public class CompoundNameSelectionDialog extends JDialog {

	private static final long serialVersionUID = 4384224248678352709L;
	private static final Color MISSING_COLOR = new Color(1.0f,0.3f,0.3f);
	private static final Color MULTIPLE_COLOR = new Color(1.0f, 1.0f, 0.3f);

	private static final String TITLE_AND_INTRO = "<html>" +
			"<center><b>Select names for compounds</b></center><br />"
			+ "<i>One or more of the compounds in your query had multiple matches in the database.<br /> "
			+ "Please select the match that is best - or '(none)' if you do not wish the <br /> "
			+ "compound to appear in the results." 
			+ "</i><html>";

	private boolean cancelled = false;
	private CompoundNameSelectionTable table;
	private List<String> inputList;
	private List<List<MetabolicName>> matchesList;
	private CompoundData compoundData;
	
	private JButton saveButton = null;
	
	private List<List<Component>> componentList = new ArrayList<List<Component>>();

	private static CompoundNameSelectionDialog dialog = null;

	public static final Map<String, MetabolicName> select(JFrame parent,
			Task task, CompoundData compoundData, Map<String, List<MetabolicName>> source) {
		if (source.isEmpty())
			return new HashMap<String, MetabolicName>();
		if (dialog == null)
			dialog = new CompoundNameSelectionDialog(parent);
		dialog.setCompoundData(compoundData);
		dialog.setDataModelFromMap(source);
		dialog.setCancelled(true);
		dialog.updateSaveButton();
		dialog.setVisible(true);
		if (dialog.isCancelled()){
			if (task != null) task.halt();
			return null;
		}
		return dialog.makeReturnMapFromData();
	}

	private void setCompoundData(CompoundData compoundData) {
		this.compoundData = compoundData;		
	}

	private void updateSaveButton() {
		saveButton.setEnabled(this.compoundData != null);
	}

	private Map<String, MetabolicName> makeReturnMapFromData() {
		Map<String, MetabolicName> ret = new HashMap<String, MetabolicName>();
		for (int i = 0; i < inputList.size(); i++){
			String key = inputList.get(i);
			List<MetabolicName> names = matchesList.get(i);
			if (names.size() > 0) {
				Object o = componentList.get(i).get(1);
				if ((o != null) && (o instanceof JComboBox)){
					JComboBox cb = (JComboBox)o;
					SelectionCover sc = (SelectionCover) cb.getSelectedItem();
					if(sc.data != null)
						ret.put(key, sc.data);
				}
				else {
					ret.put(key, names.get(0));
				}
			}
		}
		return ret;
	}

	private void setDataModelFromMap(Map<String, List<MetabolicName>> source) {
		this.inputList = new ArrayList<String>(source.keySet());
		this.matchesList = new ArrayList<List<MetabolicName>>();
		this.componentList = new ArrayList<List<Component>>();
		for (String key : inputList) {
			List<Component> columnsForRow = new ArrayList<Component>();
			
			//column 0
			JLabel label = new JLabel(key);
			columnsForRow.add(label);
			
			//column 1
			CompoundSelectionList list = new CompoundSelectionList();
			list.addAll(sortAndBringExactMatchToTop(key,source.get(key)));
			if (list.size() > 1 || 
				(list.size() == 1 && !list.get(0).getName().equalsIgnoreCase(key) )) {
				columnsForRow.add(makeComboBoxWithData(list));
				label.setBackground(MULTIPLE_COLOR);
				label.setOpaque(true);
			}
			else if(list.size() == 1) {
				columnsForRow.add(new JLabel(list.get(0).getName()));
			}
			else{
				columnsForRow.add(new JLabel("Not Found"));
				label.setBackground(MISSING_COLOR);
				label.setOpaque(true);
			}
			
			this.matchesList.add(list);
			
			//column 2
			JTextPane textPane = new JTextPane();
			if(list.size() > 0) {
				String id = list.get(0).getId();
				textPane.setEditable(false); 
				textPane.setHighlighter(null);
		        textPane.setContentType("text/html");
		        textPane.setPreferredSize(textPane.getMinimumSize());
				textPane.setText("<html><a href='"+CompoundLink.KEGG.getURL(id)+"'>" 
						+ id + "</a></html>");
				textPane.setCaretPosition(0);
				textPane.addHyperlinkListener(new HyperlinkListener() {
		        	public void hyperlinkUpdate(HyperlinkEvent e) {
		        		URL url = e.getURL();        
		                if (url != null)        	
		                    if (e.getEventType()
		                            == HyperlinkEvent.EventType.ACTIVATED) 
		                    	BareBonesBrowserLaunch.openURL(url.toString());
		        	}
		        });
			}
			columnsForRow.add(textPane);
			componentList.add(columnsForRow);
		}
		
		TableModel model = new CompoundSelectionDataModel();
		table.setModel(model);
		table.getRowSorter().toggleSortOrder(0);
	}
	
	private List<MetabolicName> sortAndBringExactMatchToTop(String key,
			List<MetabolicName> list) {
		Collections.sort(list,new Comparator<MetabolicName>(){
			@Override
			public int compare(MetabolicName n1, MetabolicName n2) {
				return n1.getName().compareToIgnoreCase(n2.getName());
			}});
		int match = -1;
		for (int i = 0; i < list.size(); i++){
			if (list.get(i).getName().equalsIgnoreCase(key)) match = i;
		} 
		if (match > -1){
			MetabolicName found = list.get(match);
			list.remove(found);
			list.add(0,found);
		}
		return list;
	}

	private CompoundNameSelectionDialog(JFrame parent) {
		super(parent, true);
		initialize();
		setSize(500,500);
		setTitle("Select Compound Mappings");
		setLocationRelativeTo(getOwner());
	}

	private void initialize() {

		JPanel topPanel = createTopPanel();
		JPanel buttonPanel = createButtonPanel();
		JTable table = createTable();
		JScrollPane pane = new JScrollPane(table);

		Container c = this.getContentPane();
		BoxLayout layout = new BoxLayout(c, BoxLayout.Y_AXIS);
		c.setLayout(layout);
		c.add(topPanel);
		c.add(pane);
		c.add(buttonPanel);
	}

	private JPanel createTopPanel() {
		JPanel ret = new JPanel();
		ret.add(new JLabel(TITLE_AND_INTRO));
		return ret;
	}

	private JPanel createButtonPanel() {
		JPanel ret = new JPanel();
		ret.add(makeActionButton("OK", makeOKActionListener(),true));
		ret.add(makeActionButton("Cancel", makeCancelActionListener(),false));
		saveButton = makeActionButton("Save", makeSaveActionListener(),false);
		ret.add(saveButton);
		return ret;
	}

	private JButton makeActionButton(String label, ActionListener l, boolean isDefault) {
		JButton button = new JButton(label);
		button.addActionListener(l);
		if(isDefault) getRootPane().setDefaultButton(button);
		return button;
	}

	private ActionListener makeCancelActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCancelled(true);
				setVisible(false);
			}
		};
	}

	private ActionListener makeOKActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCancelled(false);
				setVisible(false);
			}
		};
	}
	
	private ActionListener makeSaveActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File outputFile = FileUtils.getFile("Save as File", FileUtils.SAVE,
						"csv", "CSV File");
				if (outputFile != null) {
					saveCompoundSelectionToFile(outputFile,compoundData, makeReturnMapFromData());
				}
			}
		};
	}

	private JTable createTable() {
		table = new CompoundNameSelectionTable();
		table.setRowHeight(22);
		table.setDefaultRenderer(CompoundSelectionList.class,
				new CompoundSelectionRenderer());
		table.setDefaultRenderer(String.class,
				new CompoundSelectionRenderer());
		table.setDefaultEditor(CompoundSelectionList.class,
				new CompoundSelectionCellEditor());
		table.setDefaultEditor(String.class,
				new CompoundSelectionCellEditor());
		table.setAutoCreateRowSorter(true);
		table.setCellSelectionEnabled(false);
		return table;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	private void saveCompoundSelectionToFile(File outputFile,
			CompoundData compoundData,
			Map<String, MetabolicName> nameMap) {
		DataFile base = new TextFile();
		for(int i=0; i<compoundData.getColumns().length; i++) {
			String column = compoundData.getColumns()[i];
			base.setValue(column, 0, i+2);
		}
		int j = 1;
		for(int i=0; i<compoundData.getNameOrId().size(); i++) {
			MetabolicName name = nameMap.get(compoundData.getNameOrId().get(i));
			if(name != null) {
				base.setValue(name.getId(),j,0);
				base.setValue(name.getName(),j,1);
				for(int k = 0; k<compoundData.getData().get(i).length; k++ ) {
					base.setValue(compoundData.getData().get(i)[k],j,k+2);
				}
				j++;
			}
		}
		try {
			base.save(outputFile);
			JOptionPane.showMessageDialog(this, "Output file " + outputFile.getCanonicalPath() + " saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Unable to save file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JComboBox makeComboBoxWithData(List<MetabolicName> data) {
		Vector<SelectionCover> selections = new Vector<SelectionCover>();
		for (MetabolicName name: data){
			selections.add(new SelectionCover(name));
		}
		selections.add(new SelectionCover(null));
		JComboBox ret = new JComboBox(selections);
		return ret;
	}

	private class CompoundSelectionDataModel extends DefaultTableModel implements ActionListener {
		private static final long serialVersionUID = 2311718656654289402L;

		CompoundSelectionDataModel() {
			super();
			for(List<Component> components: componentList){
				if(components.get(1) instanceof JComboBox) {
					((JComboBox)components.get(1)).addActionListener(this);
				}
			}
		}

		@Override
		public int getRowCount() {
			if (inputList == null)
				return 0;
			return inputList.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0)
				return "Input Name";
			else if (columnIndex == 1) 
				return "Potential Matches";
			return "KEGG ID";
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex == 1 && matchesList != null)
				return CompoundSelectionList.class;
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return ((columnIndex == 1) && (componentList.get(rowIndex).get(columnIndex) instanceof JComboBox)) ||
					(columnIndex == 2);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return inputList.get(rowIndex);
			else if (columnIndex == 1) 
				return matchesList.get(rowIndex);
			else {
				if(componentList.get(rowIndex).get(columnIndex) instanceof JTextPane) {
					JTextPane textPane= (JTextPane)(componentList.get(rowIndex).get(columnIndex));
					return textPane.getText();
				}
				else
					return "N/A";
			}
				
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(List<Component> components: componentList) {
				if(components.contains(e.getSource())) {
					JTextPane textPane = (JTextPane)components.get(2);
					JComboBox comboBox = ((JComboBox)e.getSource());
					SelectionCover selectionCover = ((SelectionCover)comboBox.getSelectedItem());
					if(selectionCover.data != null) {
						String id = selectionCover.data.getId();
						textPane.setText("<html><a href='"+CompoundLink.KEGG.getURL(id)+"'>" 
								+ id + "</a></html>");
					}
					else {
						textPane.setText(null);
					}
					fireTableCellUpdated(componentList.indexOf(components),2);
					return;
				}
			}
		}

	}

	private class CompoundSelectionList extends ArrayList<MetabolicName> {
		private static final long serialVersionUID = -7563912743923350550L;

		public void addAll(List<MetabolicName> list) {
			super.addAll(list);
		}

	}

	private class CompoundSelectionRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable tableRef,
				Object cellDataRef, boolean isSelected, boolean hasFocus,
				int rowIndex, int colIndex) {

			int modelRowIndex = table.convertRowIndexToModel(rowIndex);
			int modelColIndex = table.convertColumnIndexToModel(colIndex);
			return componentList.get(modelRowIndex).get(modelColIndex);
		}
	}

	private class CompoundSelectionCellEditor extends AbstractCellEditor
			implements TableCellEditor, CompoundNameSelectionTable.TwoStageTableCellEditor {
		private static final long serialVersionUID = -77174325929975220L;

		private int lastRowUsed = 0;
		private int lastColUsed = 0;
		
		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		public Object getCellEditorValue() {
			if (!componentList.isEmpty() && (componentList.get(lastRowUsed) != null)){
				Object o = componentList.get(lastRowUsed).get(lastColUsed);
				if (! (o instanceof JComboBox)) return null;
				return ((JComboBox)componentList.get(lastRowUsed).get(lastColUsed)).getSelectedItem();
			}
			return null;
		}

		// Implement the one method defined by TableCellEditor.
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			int modelRowIndex = table.convertRowIndexToModel(row);
			int modelColIndex = table.convertColumnIndexToModel(column);
			lastRowUsed = modelRowIndex;
			lastColUsed = modelColIndex;
			return componentList.get(modelRowIndex).get(modelColIndex);
		}

		@Override
		public boolean isFullyEngaged() {
			Component c = componentList.get(lastRowUsed).get(lastColUsed);
			boolean flag = (c instanceof JComboBox) && ((JComboBox)c).isPopupVisible();
			return flag;
		}

	}
		
	private class SelectionCover {
		
		private final MetabolicName data;
		
		public SelectionCover(MetabolicName data){
			this.data = data;
		}
		
		@Override
		public String toString(){
			if(data != null)
				return data.getName();
			else return "(none)";
		}
	}

}
