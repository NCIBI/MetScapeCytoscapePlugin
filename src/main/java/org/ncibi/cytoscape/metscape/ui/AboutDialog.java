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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginRegistration;
import org.ncibi.cytoscape.metscape.utils.ImageUtils;
import org.ncibi.cytoscape.metscape.utils.WebUtils;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L; //  @jve:decl-index=0:
	
	private static final String headerMessage = "<html><center><h1>MetScape</h1>" +
			"<b><font size=\"-1\">Version " + MetScapePlugin.getPluginVersionString() +"</font></b><br />" +
			"<font size=\"-2\">(Plugin is " + (PluginRegistration.isPluginRegistered() ? "registered" : "unregistered") + ") </font>" +
			"</center></html>";  

	private static final String centerMessage = "<html>" +
			"MetScape allows users to upload a list of metabolites with " +
			"experimentally determined concentrations, identify genes and pathways and display them " +
			"in the context of relevant metabolic networks. For more information, click " +
			"on the MetScape icon. Please report any problems to " +
			"metscape-help@umich.edu. Developed by the NCIBI development group; " +
			"more at the NCIBI icon below." +
			"</html>";  

	private ImageIcon ncibiIcon = ImageUtils.getNcibiIcon();  //  @jve:decl-index=0:
	private ImageIcon metscapeIcon = ImageUtils.getMetscapeIcon(); 
	
	private final Frame parent;
	
	private JPanel headerPanel = null; 
	private JPanel centerPanel = null;
	private JPanel jContentPane = null;
	private JLabel headerLabel = null;
	private JPanel bottomPanel = null; 
	private JLabel iconTextLabel = null;

	private JPanel iconHolderPanel = null;

	private JButton iconButton = null;

	private JButton metscapeButton = null;

	private JLabel centerLabel = null;


	/**
	 * @param owner
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		parent = owner;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(345, 303);
		this.setResizable(false);
		this.setLocationRelativeTo(parent);
		this.setTitle("About Metscape 2");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			headerLabel = new JLabel();
			headerLabel.setText(headerMessage);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
			jContentPane.add(getHeaderPanel(), BorderLayout.NORTH);
			jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes bottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
		}
		return bottomPanel;
	}

	/**
	 * This method initializes headerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.weightx = 0.1;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(0, 20, 0, 0);
			gridBagConstraints4.gridwidth = 3;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridheight = 2;
			gridBagConstraints1.gridy = 0;
			headerPanel = new JPanel();
			headerPanel.setLayout(new GridBagLayout());
			headerPanel.add(headerLabel, gridBagConstraints4);
			headerPanel.add(getMetscapeButton(), gridBagConstraints1);
		}
		return headerPanel;
	}

	/**
	 * This method initializes centerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerLabel = new JLabel();
			centerLabel.setBorder(BorderFactory.createEmptyBorder(14,7,7,7));
			centerLabel.setText(centerMessage);
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(getIconHolderPanel(), BorderLayout.SOUTH);
			centerPanel.add(centerLabel, BorderLayout.CENTER);
		}
		return centerPanel;
	}

	/**
	 * This method initializes iconHolderPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIconHolderPanel() {
		if (iconHolderPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridy = 2;
			iconTextLabel = new JLabel();
			iconTextLabel.setText("<html><font size=\"-2\"><i>This work supported by NIH Grant #U54DA021519.<br />(c) Regents of The University of Michigan, 2011</i></font></html>");
			iconHolderPanel = new JPanel();
			iconHolderPanel.setLayout(new GridBagLayout());
			iconHolderPanel.add(iconTextLabel, gridBagConstraints);
			iconHolderPanel.add(getIconButton(), gridBagConstraints2);
		}
		return iconHolderPanel;
	}

	/**
	 * This method initializes iconButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getIconButton() {
		if (iconButton == null) {
			iconButton = new JButton();
			iconButton.setIcon(ncibiIcon);
			iconButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					WebUtils.ncibiHomePage();
				}
			});
		}
		return iconButton;
	}

	/**
	 * This method initializes metscapeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMetscapeButton() {
		if (metscapeButton == null) {
			metscapeButton = new JButton();
			metscapeButton.setIcon(metscapeIcon);
			metscapeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					WebUtils.metscapeHomePage();
				}
			});
		}
		return metscapeButton;
	}

	public static void main(String[] args){
		AboutDialog d = new AboutDialog(null);
		d.setVisible(true);
	}
}  //  @jve:decl-index=0:visual-constraint="10,18"
