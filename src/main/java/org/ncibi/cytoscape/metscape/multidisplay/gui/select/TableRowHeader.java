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

/*
 * Copy modified from http://www.jguru.com/faq/view.jsp?EID=87579
 * User: mkovalenko 
 * Date: Oct 22, 2001 
 * Time: 5:17:14 PM 
 */

import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

@SuppressWarnings("serial")
public class TableRowHeader extends JList {
	private JTable table;
	
	public TableRowHeader(JTable table, Vector<String> rowHeaderVector) {
		super(new TableRowHeaderModel(table));
		this.table = table;
		setFixedCellHeight(table.getRowHeight());
		setFixedCellWidth(preferredHeaderWidth());
		setCellRenderer(new TableRowHeaderRenderer(table,rowHeaderVector));
		setSelectionModel(table.getSelectionModel());
	}

	/**
	 * * Returns the bounds of the specified range of items in JList *
	 * coordinates. Returns null if index isn't valid. * * @param index0 the
	 * index of the first JList cell in the range * @param index1 the index of
	 * the last JList cell in the range * @return the bounds of the indexed cells
	 * in pixels
	 */
	public Rectangle getCellBounds(int index0, int index1) {
		Rectangle rect0 = table.getCellRect(index0, 0, true);
		Rectangle rect1 = table.getCellRect(index1, 0, true);
		int y, height;
		if (rect0.y < rect1.y) {
			y = rect0.y;
			height = rect1.y + rect1.height - y;
		} else {
			y = rect1.y;
			height = rect0.y + rect0.height - y;
		}
		return new Rectangle(0, y, getFixedCellWidth(), height);
	} // assume that row header width should be big enough to display row number
		// Integer.MAX_VALUE completely

	private int preferredHeaderWidth() {
		JLabel longestRowLabel = new JLabel("Animation - 0000");
		JTableHeader header = table.getTableHeader();
		longestRowLabel.setBorder(header.getBorder());
		// UIManager.getBorder("TableHeader.cellBorder"));
		longestRowLabel.setHorizontalAlignment(JLabel.CENTER);
		longestRowLabel.setFont(header.getFont());
		return longestRowLabel.getPreferredSize().width;
	}
	
	public void updateDisplay(){
		table.validate();
	}

}