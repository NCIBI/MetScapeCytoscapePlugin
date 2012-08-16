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
package org.ncibi.cytoscape.metscape.multidisplay.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorPickerComboBoxModel;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRange;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;

public class ColorScaleSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox colorSelectionComboBox = null;
	private final ColorPickerComboBoxModel model;  //  @jve:decl-index=0:visual-constraint="-161,-109"
	private JLabel colorSelectionLabel = null;
	private final ControlInterface control;
	
	private ColorRange getSelection() {
		ColorRange ret = ColorRange.getDefault();
		Object selectObj = model.getSelectedItem();
		if ((selectObj != null) && (selectObj instanceof ColorRange))
			ret = (ColorRange)selectObj;
		return ret;
	}
	
	/**
	 * This is the default constructor
	 */
	public ColorScaleSelectionPanel(ControlInterface c) {
		super();
		this.control = c;
		model = new ColorPickerComboBoxModel(c);
		initialize();
		getColorSelectionComboBox().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorRange cr = getSelection();
				control.changeColorRange(cr);
			}});
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.gridy = 0;
		colorSelectionLabel = new JLabel();
		colorSelectionLabel.setText("Select Color Range: ");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 1;
		this.setSize(255, 37);
		this.setLayout(new GridBagLayout());
		this.add(getColorSelectionComboBox(), gridBagConstraints);
		this.add(colorSelectionLabel, gridBagConstraints5);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
	}

	/**
	 * This method initializes colorSelectionComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getColorSelectionComboBox() {
		if (colorSelectionComboBox == null) {
			colorSelectionComboBox = new JComboBox(model);
		}
		return colorSelectionComboBox;
	}

}  //  @jve:decl-index=0:visual-constraint="-170,-159"
