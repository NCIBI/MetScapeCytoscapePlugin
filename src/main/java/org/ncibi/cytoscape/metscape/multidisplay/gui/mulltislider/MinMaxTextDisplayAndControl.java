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
package org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxValueModel;

public class MinMaxTextDisplayAndControl extends JPanel implements MinMaxChangeListener {

	private static final long serialVersionUID = 1L;
	
	private JLabel minLabel = null;
	private JLabel maxLabel = null;
	private JLabel minValueLabel = null;
	private JLabel maxValueLabel = null;
	private NumberFormat format = null;

	private JTextField minValueInput = null;

	private JTextField maxValueInput = null;

	private ControlInterface control;  //  @jve:decl-index=0:

	private JLabel minMinTitleLabel = null;

	private JLabel mimMimValueLabel = null;

	private JLabel maxMaxTitleLabel = null;

	private JLabel maxMaxValueLabel = null;

	@Override
	public void valuesChanged(MinMaxValueModel minMaxModel) {
		minValueLabel.setText(format.format(minMaxModel.getMinValue()));
		maxValueLabel.setText(format.format(minMaxModel.getMaxValue()));
		mimMimValueLabel.setText(format.format(minMaxModel.getMinMinValue()));
		maxMaxValueLabel.setText(format.format(minMaxModel.getMaxMaxValue()));
		getMinValueInput().setText(format.format(minMaxModel.getMinValue()));
		getMaxValueInput().setText(format.format(minMaxModel.getMaxValue()));
	}
		
	protected void updateValuesFromInput() {
	}

	/**
	 * This is the default constructor
	 * @param model 
	 */
	public MinMaxTextDisplayAndControl(ControlInterface control) {
		super();
		this.control = control;

		format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);

		initialize();

		valuesChanged(control.getMinMaxValueModel());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 5;
		gridBagConstraints4.gridy = 1;
		maxMaxValueLabel = new JLabel();
		maxMaxValueLabel.setText("0000.00");
		GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
		gridBagConstraints32.gridx = 5;
		gridBagConstraints32.gridy = 0;
		maxMaxTitleLabel = new JLabel();
		maxMaxTitleLabel.setText("HighEnd");
		GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
		gridBagConstraints22.gridx = 0;
		gridBagConstraints22.gridy = 1;
		mimMimValueLabel = new JLabel();
		mimMimValueLabel.setText("0000.00");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		minMinTitleLabel = new JLabel();
		minMinTitleLabel.setText("LowEnd");
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.fill = GridBagConstraints.BOTH;
		gridBagConstraints31.gridy = 1;
		gridBagConstraints31.weightx = 1.0;
		gridBagConstraints31.gridwidth = 2;
		gridBagConstraints31.insets = new Insets(2, 4, 2, 4);
		gridBagConstraints31.gridx = 3;
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.gridy = 1;
		gridBagConstraints21.weightx = 1.0;
		gridBagConstraints21.gridwidth = 2;
		gridBagConstraints21.insets = new Insets(2, 4, 2, 4);
		gridBagConstraints21.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 3;
		gridBagConstraints3.insets = new Insets(3, 3, 3, 3);
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.insets = new Insets(3, 3, 3, 6);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.insets = new Insets(3, 6, 3, 3);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.insets = new Insets(3, 3, 3, 6);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		this.setSize(340, 75);
		maxValueLabel = new JLabel();
		maxValueLabel.setText("0000.00");
		minValueLabel = new JLabel();
		minValueLabel.setText("0000.00");
		maxLabel = new JLabel();
		maxLabel.setText("Max:");
		minLabel = new JLabel();
		minLabel.setText("Min:");
		this.setLayout(new GridBagLayout());
		this.add(maxValueLabel, gridBagConstraints);
		this.add(minValueLabel, gridBagConstraints2);
		this.add(minLabel, gridBagConstraints1);
		this.add(maxLabel, gridBagConstraints3);
		this.add(getMinValueInput(), gridBagConstraints21);
		this.add(getMaxValueInput(), gridBagConstraints31);
		this.add(minMinTitleLabel, gridBagConstraints11);
		this.add(mimMimValueLabel, gridBagConstraints22);
		this.add(maxMaxTitleLabel, gridBagConstraints32);
		this.add(maxMaxValueLabel, gridBagConstraints4);
	}

	/**
	 * This method initializes minValueInput	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMinValueInput() {
		if (minValueInput == null) {
			minValueInput = new JTextField("0000.00");
			minValueInput.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						JTextField tf = (JTextField)e.getSource();
						double value = Double.parseDouble(tf.getText());
						control.setMinValue(value);
					} catch (Throwable ignore){
					}
				}
			});
		}
		return minValueInput;
	}

	/**
	 * This method initializes maxValueInput	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMaxValueInput() {
		if (maxValueInput == null) {
			maxValueInput = new JTextField("0000.00");
			maxValueInput.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						JTextField tf = (JTextField)e.getSource();
						double value = Double.parseDouble(tf.getText());
						control.setMaxValue(value);
					} catch (Throwable ignore){
					}
				}
			});
		}
		return maxValueInput;
	}

}  //  @jve:decl-index=0:visual-constraint="6,10"
