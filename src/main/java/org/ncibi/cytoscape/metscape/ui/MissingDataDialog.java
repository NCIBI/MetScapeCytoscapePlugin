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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.ncibi.cytoscape.metscape.utils.ImageUtils;
import org.ncibi.cytoscape.util.FileUtils;

import cytoscape.Cytoscape;

@SuppressWarnings("serial")
public class MissingDataDialog extends JDialog {
	
	private Collection<?> missingCompounds;
	private Collection<?> missingGenes;
	private Collection<?> missingConcepts;
	private boolean cmpdNames;
	private String message;
	private JTextArea textArea;
	private JScrollPane textScrollPane;
	private JButton okButton;
	private JButton saveButton;
	private JPanel headerPanel;
	private JLabel headerLabel;
	private JButton whyButton;
	private static final ImageIcon icon = ImageUtils.createImageIcon("/icons/question_mark.png", "Missing Data Explanation.");

	
	private static final String missingDataPopupLabel = "Why are these elements missing";
	private static final String missingDataPopupTitle = "Missing Elements";
	private static final String missingDataPopupMessage = "<html><body><center><h3>" +
			"Why are these elements missing?</h3>" +
			"Genes, Compounds, and Concepts may appear on this missing elements list.</center>" +
			"<ul><li>Genes and Compounds that you supply may not be in the database. <br />" +
			"If they are not found in the database then they are reported as missing.</li>" +
			"<li>If your input genes are not human (Rat, for example), then they are<br />" +
			"mapped to human genes using homologs from NCBI's HomoloGene.<br />" +
			"If this mapping fails, then those genes are reported as missing.</li>" +
			"<li>MetScape will display only the genes that encode metabolic enzymes.<br />" +
			"If an input gene does not encode metabolic enzymes, it will appear on<br />" +
			"the missing elements list.</li>" +
			"<li>A concept (pathway) will appear on the missing list if all of it's significant genes <br />" +
			"are missing. The list of significant genes for a concept comes from the input file <br />" +
			"or from LRpath.</li>" +
			"</ul></body></html>";
	
	public MissingDataDialog(Frame owner, 
			Collection<?> missingCompounds, Collection<?> missingGenes, Collection<?> missingConcepts, boolean cmpdNames) {
		super(owner, true);
		this.missingCompounds = missingCompounds;
		this.missingGenes = missingGenes;
		this.missingConcepts = missingConcepts;
		this.cmpdNames = cmpdNames;
		setTitle("Missing Data");
		createMessage();
		createControls();
		setSize(300,300);
		setLocationRelativeTo(getOwner());
		getRootPane().setDefaultButton(okButton);
	}
	
	private void createMessage() {
		message = "The following input data could not be found in the MetScape database:";
		String cmpdSep = " ";
		if(cmpdNames) cmpdSep = "\n";
		
		if(missingCompounds != null && !missingCompounds.isEmpty()) {
			message = message + "\n\nCompounds:\n";
			for(Object compound: missingCompounds) {
				message = message + compound + cmpdSep;
			}
		}
		if(missingGenes != null && !missingGenes.isEmpty()) {
			message = message + "\n\nGenes:\n";
			for(Object gene: missingGenes) {
				message = message + gene + " ";
			}
		}
		if(missingConcepts != null && !missingConcepts.isEmpty()) {
			message = message + "\n\nConcepts:\n";
			for(Object concept: missingConcepts) {
				message = message + concept + "\n";
			}
		}
	}
	
	private void createControls() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		headerPanel = new JPanel();
		headerLabel = new JLabel(missingDataPopupLabel);
		whyButton = new JButton();
		whyButton.setIcon(icon);
		whyButton.setFocusable(false);
		whyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), missingDataPopupMessage,
						missingDataPopupTitle,JOptionPane.INFORMATION_MESSAGE,icon);
			}
		});
		headerPanel.add(headerLabel);
		headerPanel.add(whyButton);
		textArea = new JTextArea(message);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(UIManager.getColor("JPanel.background"));
		textArea.setFont(UIManager.getFont("JTextField.font"));
		textScrollPane = new JScrollPane(textArea);
		textScrollPane.setPreferredSize(textScrollPane.getMaximumSize());
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				File saveFile = FileUtils.getFile("Save to File", FileUtils.SAVE, "txt", "Text File");
				if(saveFile != null) {
					try {
						PrintWriter writer = new PrintWriter(saveFile);
						for(String messageLine : message.split("\n"))
							writer.println(messageLine);
						writer.close();
						setVisible(false);
					}
					catch(Exception ex) {
						JOptionPane.showMessageDialog(MissingDataDialog.this,
								"An error occurred writing the file", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(headerPanel);
		getContentPane().add(textScrollPane);
		getContentPane().add(new Box(BoxLayout.X_AXIS) {
			{
				add(okButton);
				add(saveButton);
			}
		});
		pack();
	}
}
