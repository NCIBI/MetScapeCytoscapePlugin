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
 * Date: Oct 16, 2001
 * Time: 6:09:23 PM
 * Describe file
 */

import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

@SuppressWarnings("serial")
public class TableRowHeaderRenderer extends JLabel implements ListCellRenderer {
	private JTable table;
	private Border selectedBorder;
	private Border normalBorder;
	private Font selectedFont;
	private Font normalFont;
	private Vector<String> rowHeaderVector;

	TableRowHeaderRenderer(JTable table, Vector<String> rowHeaderVector) {
		this.table = table;
		this.rowHeaderVector = rowHeaderVector;
		normalBorder = UIManager.getBorder("TableHeader.cellBorder");
		selectedBorder = BorderFactory.createRaisedBevelBorder();
		final JTableHeader header = table.getTableHeader();
		normalFont = header.getFont();
		selectedFont = normalFont.deriveFont(normalFont.getStyle() | Font.BOLD);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setOpaque(true);
		setHorizontalAlignment(CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (table.getSelectionModel().isSelectedIndex(index)) {
			setFont(selectedFont);
			setBorder(selectedBorder);
		} else {
			setFont(normalFont);
			setBorder(normalBorder);
		}
		String label = TableUtil.animationLabel(index+1);
		if (index < rowHeaderVector.size())
			label = rowHeaderVector.elementAt(index);
		setText(label);
		return this;
	}
}
