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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextInputDialog extends JDialog {
	
	private JLabel messageLabel;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton okButton;
	private JButton cancelButton;
	
	private String returnValue = null;
		
	public static String showDialog(Component parentComponent, String message, String title) {
		TextInputDialog dialog = new TextInputDialog(parentComponent, message, title);
		dialog.setVisible(true);
		return dialog.returnValue;
	}
	
	private TextInputDialog(Component parentComponent, String message, String title) {
		super(JOptionPane.getFrameForComponent(parentComponent), true);
		setTitle(title);
		Container c = this.getContentPane();
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		this.messageLabel = new JLabel(message);
		this.messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.messageLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		c.add(Box.createVerticalStrut(5));
		c.add(messageLabel);
		c.add(Box.createVerticalStrut(5));
		this.textArea = new JTextArea();
		this.scrollPane = new JScrollPane(textArea);
		this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		c.add(scrollPane);
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = textArea.getText();
				setVisible(false);
			}
		});
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		c.add(new Box(BoxLayout.X_AXIS) {
			{
				setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				add(okButton);
				add(cancelButton);
			}
		});
		pack();
		setLocationRelativeTo(parentComponent);
		setSize(300,300);
	}
	

}
