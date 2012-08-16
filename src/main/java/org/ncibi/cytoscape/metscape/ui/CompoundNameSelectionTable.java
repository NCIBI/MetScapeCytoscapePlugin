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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class CompoundNameSelectionTable extends JTable {
	private static final long serialVersionUID = -7380827679823377597L;

	private final MouseAdapter twoStageEditingListener;

	public CompoundNameSelectionTable() {
		super();
		twoStageEditingListener = makeTwoStageEditingListener();
		this.addMouseListener(twoStageEditingListener);
		this.addMouseMotionListener(twoStageEditingListener);
	}

	private MouseAdapter makeTwoStageEditingListener() {

		return new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				possiblySwitchEditors(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				possiblySwitchEditors(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				possiblySwitchEditors(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				possiblySwitchEditors(e);
			}
		};
	}

	private void possiblySwitchEditors(MouseEvent e) {
		Point p = e.getPoint();
		if (p != null) {
			int row = rowAtPoint(p);
			int col = columnAtPoint(p);
			if (row != getEditingRow() || col != getEditingColumn()) {
				if (isEditing()) {
					TableCellEditor editor = getCellEditor();
					if (editor instanceof TwoStageTableCellEditor
							&& !((TwoStageTableCellEditor) editor).isFullyEngaged()) {
						if (!editor.stopCellEditing()) {
							editor.cancelCellEditing();
						}
					}
				}

				if (!isEditing()) {
					if (row != -1 && isCellEditable(row, col)) {
						editCellAt(row, col);
					}
				}
			}
		}
	}

	public interface TwoStageTableCellEditor extends TableCellEditor {
		public boolean isFullyEngaged();
	}

}
