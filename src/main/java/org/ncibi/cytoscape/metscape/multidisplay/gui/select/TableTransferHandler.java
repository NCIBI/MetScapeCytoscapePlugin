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

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class TableTransferHandler extends TransferHandler {

    private JTable table;
    private DefaultTableModel tableModel;
    private DefaultListModel listModel;
    private TableTransferCallback tableTransferCallback = null;
    
    public TableTransferHandler(JTable table, DefaultTableModel tableModel, DefaultListModel listModel) {
		super();
		this.table = table;
		this.tableModel = tableModel;
		this.listModel = listModel;
	}
    
    public void setTransferCallback(TableTransferCallback ttc){
    	tableTransferCallback = ttc;
    }

	public boolean canImport(TransferSupport support) {
        // for the demo, we'll only support drops (not clipboard paste)
        if (!support.isDrop()) {
            return false;
        }

        // we only import Strings
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }

        return true;
    }

    public boolean importData(TransferSupport support) {
        // if we can't handle the import, say so
        if (!canImport(support)) {
            return false;
        }

        // fetch the drop location
        JTable.DropLocation dl = (JTable.DropLocation)support.getDropLocation();

        int row = dl.getRow();
        int col = dl.getColumn();
        
//        System.out.println("DnD in TestTransfer to: " + row + "," + col);

        // fetch the data and bail if this fails
        String data;
        try {
            data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

//        System.out.println("Got data: " + data);
//        System.out.println("Table Model size: rows = " + tableModel.getRowCount() + ", " +
//        		"cols = " + tableModel.getColumnCount());
        
        Object oldData = tableModel.getValueAt(row, col);
        if (!oldData.equals(AnimationDataSelectionDialog.UNDEFINED))
        	listModel.addElement(oldData);
		tableModel.setValueAt(data, row, col);
		listModel.removeElement(data);
        
        Rectangle rect = table.getCellRect(row, 0, false);
        if (rect != null) {
            table.scrollRectToVisible(rect);
        }
        
        if (tableTransferCallback != null)
        	tableTransferCallback.droppedDataIn(data,row,col);

        return true;
    }

}
