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
package org.ncibi.cytoscape.metscape.ui.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class ExtendedJTable extends JTable {

	
	public ExtendedJTable() {
		super(new ExtendedTableModel());
		setAutoCreateRowSorter(true);
	}
	
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return new ExtendedTableCellRenderer();
	}
	
	public boolean isCellEditable(int rowIndex, int vColIndex) { 
		return false; 
	}
	
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if(getRowSorter() != null)
			getRowSorter().allRowsChanged();
		for(int col=0; col<getColumnCount(); col++) {
			TableColumn tableColumn=getColumnModel().getColumn(col);
			JTableHeader tableHeader=getTableHeader();
			TableCellRenderer cellRenderer=getDefaultRenderer(tableColumn.getClass());
			TableCellRenderer headerRenderer=tableHeader.getDefaultRenderer();
			Component header=headerRenderer.getTableCellRendererComponent
			(this, getColumnName(col), false, false, -1, col);
			int columnSize = header.getPreferredSize().width;
			for(int row=0; row<getRowCount(); row++) {
				
				Component cell=cellRenderer.getTableCellRendererComponent
					(this, getValueAt(row,col), false, false, row, col);
				if(cell.getPreferredSize().width > columnSize) {
					columnSize = cell.getPreferredSize().width;
				}
			}
			tableColumn.setPreferredWidth(columnSize);
		}
	}

}
