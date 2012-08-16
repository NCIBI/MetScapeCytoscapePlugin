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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginRegistration;
import org.ncibi.cytoscape.metscape.task.RecordRegistrationTask;

public class RegistrationDialog extends JDialog {
	
	private PluginRegistration registrationRecord = null;
	
	private String headerString = "<html><center><h3>"
		+ "MetScape Plugin Registration" 
		+ "</h3></center></html>";
	private String footerString = "<html><font size=\"-2\"><i>" +
			"This work supported by NIH Grant #U54DA021519.<br />" +
			"(c) Regents of The University of Michigan, 2010" +
			"</i></font></html>";
	private String explanationString = "<html><p>" +
			"MetScape is a free program. We ask you to register because it helps us to keep track of " +
			"the number of downloads. Your information will be stored in a secure database and we " +
			"will not share it with anyone. We may send you infrequent e-mails about future " +
			"MetScape releases. You may also choose to decline the registration. If you decide to " +
			"register later, the registration window can be found on the main MetScape menu." +
			"</p></html>";

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel header = null;
	private JLabel footer = null;
	private JPanel form = null;
	private JButton registerButton = null;
	private JButton declineButton = null;
	private JLabel explanationLabel = null;
	private JPanel headerPanel = null;
	private JLabel emailLabel = null;
	private JLabel lastNameLabel = null;
	private JLabel institutionLabel = null;
	private JTextField institutionTextField = null;
	private JTextField lastNameTextField = null;
	private JTextField emailTextField = null;
	private JPanel buttonPanel = null;
	private JLabel statusLabel = null;

	private JLabel firstNameLabel = null;

	private JTextField firstNameTextField = null;

	private JPanel footerPanel = null;
	/**
	 * @param owner
	 */
	public RegistrationDialog(Frame owner) {
		super(owner,true);
		initialize();
		registrationRecord = new PluginRegistration();
		fillTextFields();
		updateStatusLabel();
	}
	
	private boolean register() {
		String firstName = getFirstNameTextField().getText().trim();
		String lastName = getLastNameTextField().getText().trim();
		String institution = getInstitutionTextField().getText().trim();
		String email = getEmailTextField().getText().trim();
		if (!verifyRegistrationValues(firstName,lastName,institution,email)){
			showRegistrationError(firstName,lastName,institution,email);
			return false;
		}
		registrationRecord.recordRegistrationData(firstName, lastName, institution, email);
		registrationRecord.markAsUnRegistered(); // in case the "send to server" fails
		RecordRegistrationTask.recordRegistration(registrationRecord);
		updateStatusLabel();
		return true;
	}
	
	private boolean verifyRegistrationValues(String firstName, String lastName,String institution, String email) {
		if (!email.contains("@")) return false;
		if (!email.contains(".")) return false;
		return true;
	}

	private void showRegistrationError(String firstName, String lastName,String institution, String email) {
		JOptionPane.showMessageDialog(this,
			    "Please enter a valid e-mail address.",
			    "Registration Error",
			    JOptionPane.ERROR_MESSAGE);
	}

	private void decline() {
		registrationRecord.markAsDeclinedAndClearRegistrationInformation();
		updateStatusLabel();
		fillTextFields();
	}
	
	private void updateStatusLabel() {
		String statusText;
		if (registrationRecord.isRegistered()) {
			statusText = "Plugin is registered with email = " 
				+ registrationRecord.getEmail()
				+ ".";
		}
		else if (registrationRecord.isDeclined()) {
			statusText = "Plugin registration was declined.";
		}
		else {
			statusText = "Plugin is unregistered.";
		}
		statusLabel.setText(statusText);
	}

	private void fillTextFields(){
		getFirstNameTextField().setText(registrationRecord.getFirstName());
		getLastNameTextField().setText(registrationRecord.getLastName());
		getInstitutionTextField().setText(registrationRecord.getInstitutionI());
		getEmailTextField().setText(registrationRecord.getEmail());
	}
	
	private void close(){
		this.setVisible(false);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 400);
		this.setContentPane(getJContentPane());
		this.setLocationRelativeTo(getOwner());
		this.setTitle("Register");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			footer = new JLabel();
			footer.setText(footerString);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getHeaderPanel(), BorderLayout.NORTH);
			jContentPane.add(getForm(), BorderLayout.CENTER);
			jContentPane.add(getFooterPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes form	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getForm() {
		if (form == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			firstNameLabel = new JLabel();
			firstNameLabel.setText("First Name:");
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridwidth = 2;
			gridBagConstraints41.gridy = 7;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.fill = GridBagConstraints.BOTH;
			gridBagConstraints31.gridy = 5;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.gridwidth = 1;
			gridBagConstraints31.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridwidth = 1;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridwidth = 1;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 5;
			institutionLabel = new JLabel();
			institutionLabel.setText("Institution:");
			lastNameLabel = new JLabel();
			lastNameLabel.setText("Last Name:");
			emailLabel = new JLabel();
			emailLabel.setText("Email:");
			form = new JPanel();
			form.setLayout(new GridBagLayout());
			form.add(emailLabel, gridBagConstraints3);
			form.add(lastNameLabel, gridBagConstraints4);
			form.add(institutionLabel, gridBagConstraints5);
			form.add(getInstitutionTextField(), gridBagConstraints11);
			form.add(getLastNameTextField(), gridBagConstraints21);
			form.add(getEmailTextField(), gridBagConstraints31);
			form.add(getButtonPanel(), gridBagConstraints41);
			form.add(firstNameLabel, gridBagConstraints);
			form.add(getFirstNameTextField(), gridBagConstraints1);
		}
		return form;
	}

	/**
	 * This method initializes registerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRegisterButton() {
		if (registerButton == null) {
			registerButton = new JButton();
			registerButton.setText("           Register          ");
			registerButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (register()) {
						MetScapePlugin.removeRegistrationMenuItem();
						close();
					}
				}
			});
		}
		return registerButton;
	}

	/**
	 * This method initializes declineButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDeclineButton() {
		if (declineButton == null) {
			declineButton = new JButton();
			declineButton.setText("Decline");
			declineButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					decline();
					close();
				}
			});
		}
		return declineButton;
	}

	/**
	 * This method initializes headerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			statusLabel = new JLabel();
			statusLabel.setText("");
			header = new JLabel();
			header.setText(headerString);
			explanationLabel = new JLabel();
			explanationLabel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Color.BLACK, 2),
							BorderFactory.createEmptyBorder(4, 4, 4, 4))
					));
			explanationLabel.setText(explanationString);
			headerPanel = new JPanel();
			headerPanel.setLayout(new BoxLayout(getHeaderPanel(), BoxLayout.Y_AXIS));
			headerPanel.add(header, null);
			headerPanel.add(explanationLabel, null);
			headerPanel.add(statusLabel, null);
		}
		return headerPanel;
	}

	/**
	 * This method initializes institutionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getInstitutionTextField() {
		if (institutionTextField == null) {
			institutionTextField = new JTextField();
		}
		return institutionTextField;
	}

	/**
	 * This method initializes lastNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getLastNameTextField() {
		if (lastNameTextField == null) {
			lastNameTextField = new JTextField();
		}
		return lastNameTextField;
	}

	/**
	 * This method initializes emailTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getEmailTextField() {
		if (emailTextField == null) {
			emailTextField = new JTextField();
		}
		return emailTextField;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.CENTER);
			flowLayout.setVgap(2);
			flowLayout.setHgap(2);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(flowLayout);
			buttonPanel.add(getRegisterButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes firstNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFirstNameTextField() {
		if (firstNameTextField == null) {
			firstNameTextField = new JTextField();
		}
		return firstNameTextField;
	}

	/**
	 * This method initializes footerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new BoxLayout(getFooterPanel(), BoxLayout.X_AXIS));
			footerPanel.add(footer, null);
			footerPanel.add(getDeclineButton(), null);
		}
		return footerPanel;
	}

}
