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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.visual.VisualStyleFactory;

public class LegendDialog extends JDialog {
	
	//quick and dirty test - used by "Run As Application" in Eclipse
	public static void main(String[] a){ (new LegendDialog(null)).setVisible(true); }

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel legendPanel = null;
	private JPanel titlePanel = null;
	private JLabel titleLabel = null;

	private JLabel compoundLabel = null;
	private JLabel reactionLabel = null;
	private JLabel enzymeLabel = null;
	private JLabel geneLabel = null;
	private JLabel inputLabel = null;
	private JLabel significantLabel = null;
	private JLabel upLabel = null;
	private JLabel downLabel = null;
	private JLabel expansionLabel = null;
	private NodeIcon compoundIcon = 
		new NodeIcon(NodeShape.HEX, VisualStyleFactory.COMPOUND_COLOR,Color.BLACK,22);
	private NodeIcon reactionIcon = 
		new NodeIcon(NodeShape.SQUARE, VisualStyleFactory.REACTION_COLOR,Color.BLACK,22);
	private NodeIcon enzymeIcon = 
		new NodeIcon(NodeShape.ROUNDED, VisualStyleFactory.ENZYME_COLOR,Color.BLACK,22);
	private NodeIcon geneIcon = 
		new NodeIcon(NodeShape.CIRCLE, VisualStyleFactory.GENE_COLOR,Color.BLACK,22);
	private NodeIcon inputCompoundIcon = 
		new NodeIcon(NodeShape.HEX, VisualStyleFactory.INPUT_COMPOUND_COLOR,Color.BLACK,22);
	private NodeIcon inputGeneIcon = 
		new NodeIcon(NodeShape.CIRCLE, VisualStyleFactory.INPUT_GENE_COLOR,Color.BLACK,22);
	private NodeIcon significantCompoundIcon = 
		new NodeIcon(NodeShape.HEX, VisualStyleFactory.COMPOUND_COLOR,Color.GREEN,22);
	private NodeIcon significantGeneIcon = 
		new NodeIcon(NodeShape.CIRCLE, VisualStyleFactory.GENE_COLOR,Color.GREEN,22);
	private NodeIcon upCompoundIcon = 
		new NodeIcon(NodeShape.HEX, VisualStyleFactory.COMPOUND_COLOR,Color.BLACK,31);
	private NodeIcon upGeneIcon = 
		new NodeIcon(NodeShape.CIRCLE, VisualStyleFactory.GENE_COLOR,Color.BLACK,31);
	private NodeIcon downCompoundIcon = 
		new NodeIcon(NodeShape.HEX, VisualStyleFactory.COMPOUND_COLOR,Color.BLACK,13);
	private NodeIcon downGeneIcon = 
		new NodeIcon(NodeShape.CIRCLE, VisualStyleFactory.GENE_COLOR,Color.BLACK,13);
	private EdgeIcon expansionEdgeIcon = 
		new EdgeIcon(VisualStyleFactory.EXPANSION_EDGE_COLOR,Color.BLACK,22);
	
	private JButton closeButton = null;
	
	private enum NodeShape{HEX, ROUNDED, CIRCLE, SQUARE}

	@SuppressWarnings("serial")
	private class NodeIcon extends Canvas {
		
		NodeShape shapeType;
		Color nodeColor;
		Color borderColor;
		Stroke stroke;
		Shape square;
		Shape rounded;
		Shape circle;
		Shape hex;
		
		public NodeIcon(NodeShape shapeType, Color nodeColor, 
				Color borderColor, int size){
			double cx = size * (6.7/11.0);
			double cy = size * (6.7/11.0);
			double r  = size * (6.0/11.0);
			int hexXPoint[] = new int[6];
			int hexYPoint[] = new int[6];
			for (int i = 0; i < 6; i++){
				hexXPoint[i] = (int)(r*Math.cos(2*Math.PI*i/6)+cx);
				hexYPoint[i] = (int)(cy-r*Math.sin(2*Math.PI*i/6));            
			}
			this.setSize(size+4, size+4);
			this.shapeType = shapeType;
			this.nodeColor = nodeColor;
			this.borderColor = borderColor;
			this.stroke = new BasicStroke(2.0f);
			this.square = new Rectangle2D.Double(2.0,2.0,size,size);
			this.rounded = new RoundRectangle2D.Double(2.0,2.0,size,size,2.0,2.0);
			this.circle = new Arc2D.Double((Rectangle2D)square,0.0,360.0,Arc2D.CHORD);
			this.hex = new Polygon(hexXPoint,hexYPoint,6);
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setStroke(stroke);
			Shape shape = square;
			if (shapeType.equals(NodeShape.ROUNDED)){
				shape = rounded;
			}
			else if (shapeType.equals(NodeShape.CIRCLE)){
				shape = circle;
			}
			else if (shapeType.equals(NodeShape.HEX)){
				shape = hex;
			}
			g2.setColor(nodeColor);
			g2.fill(shape);
			g2.setColor(borderColor);
			g2.draw(shape);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class EdgeIcon extends Canvas {
		
		Color edgeColor;
		Color arrowColor;
		Stroke stroke;
		Shape line;
		Shape arrow;
		
		public EdgeIcon(Color edgeColor, 
				Color arrowColor, int size){
			this.setSize(size+4, size+4);
			this.edgeColor = edgeColor;
			this.arrowColor = arrowColor;
			this.stroke = new BasicStroke(2.0f);
			this.line = new Line2D.Double(0, size/2+2, size+2, size/2+2);
			this.arrow = new Polygon(new int[]{size*2/3,size+4,size*2/3},new int[]{size/4+2,size/2+2,3*size/4+2},3);
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setStroke(stroke);
			g2.setColor(edgeColor);
			g2.draw(line);
			g2.setColor(arrowColor);
			g2.fill(arrow);
		}
		
	}
	
	/**
	 * @param owner
	 */
	public LegendDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Legend");
		this.setSize(242, 400);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
	}

	private void close() {
		this.setVisible(false);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getLegendPanel(), BorderLayout.CENTER);
			jContentPane.add(getTitlePanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes legendPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLegendPanel() {
		if (legendPanel == null) {
			legendPanel = new JPanel();
			legendPanel.setLayout(new GridBagLayout());
			compoundLabel = new JLabel();
			compoundLabel.setText("Compound");
			legendPanel.add(compoundLabel, makeGridBagConstraints(1,0));
			reactionLabel = new JLabel();
			reactionLabel.setText("Reaction");
			legendPanel.add(reactionLabel, makeGridBagConstraints(2,0));
			enzymeLabel = new JLabel();
			enzymeLabel.setText("Enzyme");
			legendPanel.add(enzymeLabel, makeGridBagConstraints(3,0));
			geneLabel = new JLabel();
			geneLabel.setText("Gene");
			legendPanel.add(geneLabel, makeGridBagConstraints(4,0));
			inputLabel = new JLabel();
			inputLabel.setText("Input");
			legendPanel.add(inputLabel, makeGridBagConstraints(5,0));
			significantLabel = new JLabel();
			significantLabel.setText("Significant");
			legendPanel.add(significantLabel, makeGridBagConstraints(6,0));
			upLabel = new JLabel();
			upLabel.setText("Up-regulated");
			legendPanel.add(upLabel, makeGridBagConstraints(7,0));
			downLabel = new JLabel();
			downLabel.setText("Down-regulated");
			legendPanel.add(downLabel, makeGridBagConstraints(8,0));
			expansionLabel = new JLabel();
			expansionLabel.setText("Expansion");
			legendPanel.add(expansionLabel, makeGridBagConstraints(9,0));
			
			legendPanel.add(compoundIcon, makeGridBagConstraints(1,1));
			legendPanel.add(reactionIcon, makeGridBagConstraints(2,1));
			legendPanel.add(enzymeIcon, makeGridBagConstraints(3,1));
			legendPanel.add(geneIcon, makeGridBagConstraints(4,1));
			legendPanel.add(inputCompoundIcon, makeGridBagConstraints(5,1));
			legendPanel.add(inputGeneIcon, makeGridBagConstraints(5,2));
			legendPanel.add(significantCompoundIcon, makeGridBagConstraints(6,1));
			legendPanel.add(significantGeneIcon, makeGridBagConstraints(6,2));
			legendPanel.add(upCompoundIcon, makeGridBagConstraints(7,1));
			legendPanel.add(upGeneIcon, makeGridBagConstraints(7,2));
			legendPanel.add(downCompoundIcon, makeGridBagConstraints(8,1));
			legendPanel.add(downGeneIcon, makeGridBagConstraints(8,2));
			legendPanel.add(expansionEdgeIcon, makeGridBagConstraints(9,1));
			legendPanel.add(getCloseButton(), makeGridBagConstraints(10,0,4));
		}
		return legendPanel;
	}

	/**
	 * This method initializes titlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titleLabel = new JLabel();
			titleLabel.setText("<html><h1>Legend</h1></html>");
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(titleLabel, new GridBagConstraints());
		}
		return titlePanel;
	}

	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					close();
				}
			});
		}
		return closeButton;
	}
	
	private GridBagConstraints makeGridBagConstraints(int row, int col, int colSpan){
		GridBagConstraints c = makeGridBagConstraints(row,col);
		c.gridwidth = colSpan;
		return c;
	}
	private GridBagConstraints makeGridBagConstraints(int row, int col) {
		GridBagConstraints c = new GridBagConstraints();
		//c.anchor = GridBagConstraints.LINE_START;
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(2,2,2,2);
		return c;
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
