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

import java.awt.Container;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

public class TableUtil {
	protected TableUtil() {
	}

	public static boolean isRowHeaderVisible(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport rowHeaderViewPort = scrollPane.getRowHeader();
				if (rowHeaderViewPort != null)
					return rowHeaderViewPort.getView() != null;
			}
		}
		return false;
	}

	/**
	 * * Creates row header for table with row number (starting with 1)
	 * displayed
	 */
	public static void removeRowHeader(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setRowHeader(null);
			}
		}
	}

	/**
	 * * Creates row header for table with row number (starting with 1)
	 * displayed
	 * @param rowHeaderVector 
	 */
	public static TableRowHeader setRowHeader(JTable table, Vector<String> rowHeaderVector) {
		TableRowHeader header = null;
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				header = new TableRowHeader(table,rowHeaderVector);
				scrollPane.setRowHeaderView(header);
				return header;
			}
		}
		return null;
	}

	public static String animationLabel(int i) {
		return "DataSeries_" + String.valueOf(i);
	}
} 
