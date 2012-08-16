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
package org.ncibi.cytoscape.metscape.visual;

import java.awt.Color;
import java.awt.Font;

import org.ncibi.cytoscape.metscape.data.Networks;

import cytoscape.CyNetwork;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public final class VisualStyleFactory {
	
	private VisualStyleFactory() {}
	
	// also used by org.ncibi.cytoscape.metscape.ui.LegendDialog
	
	public static Color COMPOUND_COLOR = new Color(255,153,153);
	public static Color REACTION_COLOR = new Color(209,217,197);
	public static Color ENZYME_COLOR = new Color(204,255,204);
	public static Color GENE_COLOR = new Color(204,204,255); 
	public static Color INPUT_COMPOUND_COLOR = new Color(255,0,0);
	public static Color INPUT_GENE_COLOR = new Color(51,51,255); 
	public static Color EDGE_COLOR = new Color(0,0,0);
	public static Color EXPANSION_EDGE_COLOR = new Color(0,0,255);

	@SuppressWarnings("deprecation")
	public static VisualStyle createStyle(CyNetwork network) {
		
		VisualStyle style = new VisualStyle("MetScape:"+Networks.getUUID(network));
		NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = style.getGlobalAppearanceCalculator();

		PassThroughMapping labelMapping = new PassThroughMapping(new String(), "canonicalName");
		Calculator labelCalculator = new BasicCalculator("Label Calculator",
				labelMapping, VisualPropertyType.NODE_LABEL);
		nodeAppCalc.setCalculator(labelCalculator);
		
		Calculator tooltipCalculator = new BasicCalculator("Tooltip Calculator",
				labelMapping, VisualPropertyType.NODE_TOOLTIP);
		nodeAppCalc.setCalculator(tooltipCalculator);
		
		DiscreteMapping nodeShapeMapping = new DiscreteMapping(NodeShape.TRIANGLE,"Type",ObjectMapping.NODE_MAPPING);
		nodeShapeMapping.putMapValue("Compound", NodeShape.HEXAGON);
		nodeShapeMapping.putMapValue("Enzyme", NodeShape.ROUND_RECT);
		nodeShapeMapping.putMapValue("Gene", NodeShape.ELLIPSE);
		nodeShapeMapping.putMapValue("Reaction", NodeShape.RECT_3D);
		Calculator shapeCalculator = new BasicCalculator("Shape Calculator",nodeShapeMapping,
				VisualPropertyType.NODE_SHAPE); 
		nodeAppCalc.setCalculator(shapeCalculator);
		
		DiscreteMapping nodeColorMapping = new DiscreteMapping(Color.BLACK,"Category." + Networks.getUUID(network), ObjectMapping.NODE_MAPPING);
		nodeColorMapping.putMapValue("Compound", COMPOUND_COLOR);
		nodeColorMapping.putMapValue("Input Compound", INPUT_COMPOUND_COLOR);
		nodeColorMapping.putMapValue("Reaction", REACTION_COLOR);
		nodeColorMapping.putMapValue("Enzyme", ENZYME_COLOR);
		nodeColorMapping.putMapValue("Gene", GENE_COLOR);
		nodeColorMapping.putMapValue("Input Gene", INPUT_GENE_COLOR);
		Calculator colorCalculator = new BasicCalculator("Color Calculator " + Networks.getUUID(network),nodeColorMapping,
				VisualPropertyType.NODE_FILL_COLOR); 
		nodeAppCalc.setCalculator(colorCalculator);
		
		DiscreteMapping nodeOpacityMapping = new DiscreteMapping(255.0,"isExpansion." + Networks.getUUID(network),ObjectMapping.NODE_MAPPING);
		nodeOpacityMapping.putMapValue(true, 100.0);
		Calculator opacityCalculator = new BasicCalculator("Opacity Calculator " + Networks.getUUID(network),nodeOpacityMapping,
				VisualPropertyType.NODE_OPACITY); 
		nodeAppCalc.setCalculator(opacityCalculator);
		
		DiscreteMapping nodeFontFaceMapping = new DiscreteMapping(new Font("SansSerif.plain", 12, Font.PLAIN),"isExpansionSeed." + Networks.getUUID(network),ObjectMapping.NODE_MAPPING);
		nodeFontFaceMapping.putMapValue(true, new Font("SansSerif.bold", 12, Font.BOLD));
		Calculator fontFaceCalculator = new BasicCalculator("Font Face Calculator " + Networks.getUUID(network), nodeFontFaceMapping,
				VisualPropertyType.NODE_FONT_FACE); 
		nodeAppCalc.setCalculator(fontFaceCalculator);
		
		DiscreteMapping nodeFontSizeMapping = new DiscreteMapping(12.0,"isExpansionSeed." + Networks.getUUID(network),ObjectMapping.NODE_MAPPING);
		nodeFontSizeMapping.putMapValue(true, 20.0);
		Calculator fontSizeCalculator = new BasicCalculator("Font Size Calculator " + Networks.getUUID(network), nodeFontSizeMapping,
				VisualPropertyType.NODE_FONT_SIZE); 
		nodeAppCalc.setCalculator(fontSizeCalculator);
		
		DiscreteMapping nodeBorderColorMapping = new DiscreteMapping(Color.BLACK,"isSignificant." + Networks.getUUID(network) ,ObjectMapping.NODE_MAPPING);
		nodeBorderColorMapping.putMapValue(true, Color.GREEN);
		Calculator borderColorCalculator = new BasicCalculator("Line Width Calculator " + Networks.getUUID(network),nodeBorderColorMapping,
				VisualPropertyType.NODE_BORDER_COLOR); 
		nodeAppCalc.setCalculator(borderColorCalculator);
		
		DiscreteMapping nodeLineWidthMapping = new DiscreteMapping(1.0,"isSignificant." + Networks.getUUID(network) ,ObjectMapping.NODE_MAPPING);
		nodeLineWidthMapping.putMapValue(true, 5.0);
		Calculator lineWidthCalculator = new BasicCalculator("Line Width Calculator " + Networks.getUUID(network),nodeLineWidthMapping,
				VisualPropertyType.NODE_LINE_WIDTH); 
		nodeAppCalc.setCalculator(lineWidthCalculator);
		
		DiscreteMapping nodeSizeMapping = new DiscreteMapping(35.0,"direction." + Networks.getUUID(network) ,ObjectMapping.NODE_MAPPING);
		nodeSizeMapping.putMapValue("up", 50.0);
		nodeSizeMapping.putMapValue("down", 20.0);
		Calculator sizeCalculator = new BasicCalculator("Size Calculator " + Networks.getUUID(network),nodeSizeMapping,
				VisualPropertyType.NODE_SIZE); 
		nodeAppCalc.setCalculator(sizeCalculator);
		
		//use node label color to tell if reaction node reversible
		DiscreteMapping nodeLabelColorMapping =new DiscreteMapping(new Color(0,0,0),"Reaction.reversible",ObjectMapping.NODE_MAPPING);
		nodeLabelColorMapping.putMapValue(true, new Color(151,7,247)); 
		nodeLabelColorMapping.putMapValue(false, new Color(247,126,4));             
		Calculator nodeLabelColorCalculator=new BasicCalculator("Node Label Color Calculator",nodeLabelColorMapping,VisualPropertyType.NODE_LABEL_COLOR); 
		nodeAppCalc.setCalculator(nodeLabelColorCalculator);

		EdgeAppearance edgeApp = new EdgeAppearance();
		edgeApp.set(VisualPropertyType.EDGE_COLOR, EDGE_COLOR);
		edgeAppCalc.setDefaultAppearance(edgeApp);
		
		DiscreteMapping edgeColorMapping =new DiscreteMapping(EDGE_COLOR,"isExpansion." + Networks.getUUID(network),ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.putMapValue(true, EXPANSION_EDGE_COLOR);             
		Calculator edgeColorCalculator=new BasicCalculator("Edge Color Calculator " + Networks.getUUID(network), edgeColorMapping,VisualPropertyType.EDGE_COLOR); 
		edgeAppCalc.setCalculator(edgeColorCalculator);
		
		PassThroughMapping edgeLabelMapping = new PassThroughMapping(new String(), "Reaction.rid");
		Calculator edgeLabelCalculator = new BasicCalculator("Edge Label Calculator",
				edgeLabelMapping, VisualPropertyType.EDGE_LABEL);
		edgeAppCalc.setCalculator(edgeLabelCalculator);
		
		//edge target arrow
		DiscreteMapping edgeTargetArrowMapping =new DiscreteMapping (ArrowShape.NONE,"direction",ObjectMapping.EDGE_MAPPING);
		edgeTargetArrowMapping.putMapValue("directed", ArrowShape.DELTA);
		edgeTargetArrowMapping.putMapValue("bidirectional", ArrowShape.DELTA);
		edgeTargetArrowMapping.putMapValue("undirected", ArrowShape.NONE);
		Calculator edgeTargetArrowCalculator=new BasicCalculator("Edge Target Arrow Calculator",edgeTargetArrowMapping,VisualPropertyType.EDGE_TGTARROW_SHAPE); 
		edgeAppCalc.setCalculator(edgeTargetArrowCalculator);

		//add edge source arrow when the reaction edge is bidirectional
		DiscreteMapping edgeSourceArrowMapping =new DiscreteMapping (ArrowShape.NONE,"direction",ObjectMapping.EDGE_MAPPING);
		edgeSourceArrowMapping.putMapValue("directed", ArrowShape.NONE);
		edgeSourceArrowMapping.putMapValue("bidirectional", ArrowShape.DELTA);
		edgeSourceArrowMapping.putMapValue("undirected", ArrowShape.NONE);
		Calculator edgeSourceArrowCalculator=new BasicCalculator("Edge Source Arrow Calculator",edgeSourceArrowMapping,VisualPropertyType.EDGE_SRCARROW_SHAPE); 
		edgeAppCalc.setCalculator(edgeSourceArrowCalculator);

		globalAppCalc.setDefaultBackgroundColor(Color.WHITE);
		
		return style;
	}
}
