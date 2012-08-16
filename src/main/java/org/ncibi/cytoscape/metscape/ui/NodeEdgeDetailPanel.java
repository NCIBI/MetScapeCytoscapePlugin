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

import giny.model.GraphObject;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.ncibi.commons.web.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.metab.link.EnzymeLink;
import org.ncibi.metab.link.MetabolicLink;
import org.ncibi.metab.link.ReactionLink;
import org.ncibi.metab.network.attribute.CompoundAttribute;
import org.ncibi.metab.network.attribute.EnzymeAttribute;
import org.ncibi.metab.network.attribute.EnzymeReactionAttribute;
import org.ncibi.metab.network.attribute.GeneAttribute;
import org.ncibi.metab.network.attribute.MetabolicAttribute;
import org.ncibi.metab.network.attribute.ReactionAttribute;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

@SuppressWarnings("serial")
public class NodeEdgeDetailPanel extends JPanel{
	private JButton closeButton;
	private JTextPane textPane;
	private JScrollPane textScrollPane;
	public NodeEdgeDetailPanel(GraphObject selected, CyNetwork network) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setName("Node/Edge Details");
		setPreferredSize(new Dimension(183,400));
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CytoPanel cytoPanel = ((CytoPanel) getParent().getParent());
				cytoPanel.remove(NodeEdgeDetailPanel.this);
				if(cytoPanel.getCytoPanelComponentCount() <= 0)
					cytoPanel.setState(CytoPanelState.HIDE);
			}
			
		});
		closeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(closeButton);
		
		textPane = new JTextPane();
		textPane.setEditable(false); 
        textPane.setContentType("text/html");
        textPane.setPreferredSize(textPane.getMinimumSize());
		textPane.setText(getDetailsText(selected, network));
		textPane.setCaretPosition(0);
        textPane.addHyperlinkListener(new HyperlinkListener() {
        	public void hyperlinkUpdate(HyperlinkEvent e) {
        		URL url = e.getURL();        
                if (url != null)        	
                    if (e.getEventType()
                            == HyperlinkEvent.EventType.ACTIVATED) 
                    	BareBonesBrowserLaunch.openURL(url.toString()); 
        		
        	}
        });
        
		textScrollPane = new JScrollPane(textPane);
		textScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(textScrollPane);
	}
	
	private String getDetailsText(GraphObject selected, CyNetwork network) {
		StringBuffer html = new StringBuffer();
		html.append("<html><head><style type='text/css'> p.title{color:green; font-weight: bold; font-size: 10px} " +
				"h3 {color: #347C2C; font-weight: bold; margin-bottom:0px;}"+
				"h4 {color: #C35617; font-weight: bold; margin-bottom:0px;}" +
				"ul {list-style-type: none; margin-left: 10px; font-size: 10px; margin-top:0px;} " +
				"p  {margin-left: 10px; font-size: 10px; margin-top:0px;}"+
				"</style></head><body>");
		
		if(selected instanceof CyNode) {
			String type = Attributes.node.
				getStringAttribute(selected.getIdentifier(), "Type");
			html.append("<h3>"+type+"</h3>");
			if(type.equalsIgnoreCase("Compound")) {
				appendAttributes(selected.getIdentifier(), network, Attributes.node,
						CompoundAttribute.values(), html);
				html.append("<h4>KEGG 2D structure:</h4>");
				html.append("<img src='http://www.genome.jp/Fig/compound/"+selected.getIdentifier()+".gif' alt='No Compound Structure  '>");
			}
			else if(type.equalsIgnoreCase("Reaction")) {
				appendAttributes(selected.getIdentifier(), network, Attributes.node,
						ReactionAttribute.values(), html);
			}
			else if(type.equalsIgnoreCase("Enzyme")) {
				appendAttributes(selected.getIdentifier(), network, Attributes.node,
						EnzymeAttribute.values(), html);
			}
			else if(type.equalsIgnoreCase("Gene")) {
				appendAttributes(selected.getIdentifier(), network, Attributes.node,
						GeneAttribute.values(), html);
			}
		}
		else if(selected instanceof CyEdge) {
			String type = Attributes.edge.
				getStringAttribute(selected.getIdentifier(), "Type");
			if(type.equalsIgnoreCase("Reaction")) {
				html.append("<h3>"+type+"</h3>");
				appendAttributes(selected.getIdentifier(), network, Attributes.edge,
						ReactionAttribute.values(), html);
			}
			else if(type.equalsIgnoreCase("Enzyme-Reaction")) {
				appendEnzymes(selected.getIdentifier(), Attributes.edge, html);
				appendReactions(selected.getIdentifier(), Attributes.edge, html);
				appendPathways(selected.getIdentifier(), Attributes.edge, html);
			}
		}
		html.append("</body></html>");
		return html.toString();
	}
	
	private void appendAttributes(String id, CyNetwork network, Attributes attr, MetabolicAttribute[] metAttrs, StringBuffer html) {
		StringBuffer linkHtml = new StringBuffer();	
		for(MetabolicAttribute metAttr: metAttrs) {
			if(!attr.hasAttribute(id, metAttr.toAttributeName())) continue;
			
			if(attr.getUserVisible(metAttr.toAttributeName())) {
				String attrName = metAttr.toDescriptiveName();
				Object value = attr.getAttribute(id, metAttr.toAttributeName());
				
				html.append("<h4>" +attrName+ ":</h4>");
				if(value instanceof List<?>){
					for(Object item: ((List<?>) value)) {
						html.append("<li>"+item+"</li>");
					}
				}
				else {
					html.append("<p>"+value+"</p>");
				}
			}
			if(metAttr.getLinkouts() != null && metAttr.getLinkouts().length > 0) {
				for(MetabolicLink metLink: metAttr.getLinkouts()) {
					String attrId = attr.getAttribute(id, metAttr.toAttributeName()).toString();
					linkHtml.append("<li><a href='"+metLink.getURL(attrId)+"'>" + metLink.getName(attrId) + "</a></li>");
				}
			}
		}
		if(attr == Attributes.node && Cytoscape.getCyNode(id).getGroups() != null) {
			Set<CyGroup> groupSet = new HashSet<CyGroup>(CyGroupManager.getGroupList(network));
			groupSet.retainAll(Cytoscape.getCyNode(id).getGroups());
			if(!groupSet.isEmpty()) {
				html.append("<h4>Concepts:</h4>");
				for(CyGroup group: groupSet) {
					String name = 
						Attributes.node.getStringAttribute(group.getGroupNode().getIdentifier(), "Concept.name");
					html.append("<li>"+name+"</li>");
				}
			}
		}
		if(linkHtml.length() > 0) {
			html.append("<h4>Links:</h4>");
			html.append(linkHtml);
		}
	}
	
	private void appendEnzymes(String id, Attributes attr, StringBuffer html){
		List<?> ecnums = attr.getListAttribute(id, 
				EnzymeReactionAttribute.ECNUMS.toAttributeName());
		List<?> names = attr.getListAttribute(id, 
				EnzymeReactionAttribute.NAMES.toAttributeName());
		if(ecnums == null || names == null) return;
		
		for(int i=0; i<Math.min(ecnums.size(),names.size()); i++) {
			html.append("<h3>Enzyme</h3>");
			html.append("<h4>"+EnzymeAttribute.ECNUM.toDescriptiveName()+"</h4>");
			html.append("<p>"+ecnums.get(i).toString()+"</p>");
			html.append("<h4>"+EnzymeAttribute.NAME.toDescriptiveName()+"</h4>");
			html.append("<p>"+names.get(i).toString()+"</p>");
			html.append("<h4>Links:</h4>");	
			for(MetabolicLink metLink: EnzymeLink.values()) {
				html.append("<li><a href='"+metLink.getURL(ecnums.get(i).toString())+"'>" 
						+ metLink.getName(ecnums.get(i).toString()) + "</a></li>");
			}
			html.append("<hr>");
		}
	}
	
	private void appendReactions(String id, Attributes attr, StringBuffer html) {
		List<?> rids = attr.getListAttribute(id, 
				EnzymeReactionAttribute.RIDS.toAttributeName());
		if(rids == null) return;
		
		for(int i=0; i<rids.size(); i++) {
			html.append("<h3>Reaction</h3>");
			html.append("<h4>"+ReactionAttribute.RID.toDescriptiveName()+"</h4>");
			html.append("<p>"+rids.get(i).toString()+"</p>");
			html.append("<h4>Links:</h4>");	
			for(MetabolicLink metLink: ReactionLink.values()) {
				html.append("<li><a href='"+metLink.getURL(rids.get(i).toString())+"'>" 
						+ metLink.getName(rids.get(i).toString()) + "</a></li>");
			}
			if(i < rids.size()-1)
				html.append("<hr>");
		}
	}
	
	private void appendPathways(String id, Attributes attr, StringBuffer html) {
		List<?> pathways = attr.getListAttribute(id, 
				EnzymeReactionAttribute.PATHWAYS.toAttributeName());
		if(pathways != null && !pathways.isEmpty()) {
			html.append("<hr>");
			html.append("<h3>Pathways</h3>");
			for(Object pathway: pathways) {
				html.append("<li>"+pathway+"</li>");
			}
		}
	}
}
