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
/*
 * 
 */
package org.ncibi.cytoscape.metscape.multidisplay.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.multidisplay.gui.barchart.Histogram;
import org.ncibi.cytoscape.metscape.multidisplay.gui.barchart.SelectionSet;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AlignViewsListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AnimationValueListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRange;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRangeChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MultiNetChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider.MinMaxTextDisplayAndControl;
import org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider.MinMaxTwoThumbDisplayAndControl;

/**
 * @author Terry Weymouth
 */
public class HistogramColorScalePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final ControlInterface control;
	private MinMaxTwoThumbDisplayAndControl minMaxScrollPanel = null;
	private MinMaxTextDisplayAndControl minMaxTextDisplayAndControl = null;
	private ColorScaleSelectionPanel colorScaleSelectionPanel = null;
	private Histogram histogram = null;

	public void setSelectionSet(SelectionSet selections) {
		getHistogram().setSelectionSet(selections);
	}
	
	public void setModel(ControlInterface control) {
		getMinMaxScrollPanel().initView();
	}

	public void switchColorToSelection(ColorRange crm) {
		control.setColorRange(crm);
	}

	/**
	 * This is the default constructor
	 */
	public HistogramColorScalePanel(ControlInterface model) {
		super();
		this.control = model;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		JPanel holder = new JPanel();
		holder.add(getHistogram());
		this.add(holder);
		this.add(getColorScaleSelectionPanel());
		this.add(getMinMaxScrollPanel());
		this.add(getMinMaxTextDisplayAndControl());
	}

	/**
	 * This method initializes minMaxScrollPanel	
	 * 	
	 * @return org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider.MinMaxScrollPanel	
	 */
	private MinMaxTwoThumbDisplayAndControl getMinMaxScrollPanel() {
		if (minMaxScrollPanel == null) {
			minMaxScrollPanel = new MinMaxTwoThumbDisplayAndControl(control);
			minMaxScrollPanel.initView();
		}
		return minMaxScrollPanel;
	}

	/**
	 * This method initializes colorRangeSelectionPanel	
	 * 	
	 * @return org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider.colorRangeSelectionPanel	
	 */
	private ColorScaleSelectionPanel getColorScaleSelectionPanel() {
		if (colorScaleSelectionPanel == null) {
			colorScaleSelectionPanel = new ColorScaleSelectionPanel(control);
		}
		return colorScaleSelectionPanel;
	}

	/**
	 * This method initializes minMaxTextDisplayAndControl	
	 * 	
	 * @return org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider.MinMaxTextDisplayAndControl	
	 */
	private MinMaxTextDisplayAndControl getMinMaxTextDisplayAndControl() {
		if (minMaxTextDisplayAndControl == null) {
			minMaxTextDisplayAndControl = new MinMaxTextDisplayAndControl(control);
		}
		return minMaxTextDisplayAndControl;
	}

	/**
	 * This method initializes histogram	
	 * 	
	 * @return org.ncibi.cytoscape.metscape.multidisplay.gui.barchart.Histogram	
	 */
	private Histogram getHistogram() {
		if (histogram == null) {
			histogram = new Histogram(control);
		}
		return histogram;
	}
	
	public List<MinMaxChangeListener> getMinMaxChangeListenerList() {
		List<MinMaxChangeListener> ret = new ArrayList<MinMaxChangeListener>();
		ret.add(getMinMaxTextDisplayAndControl());
		ret.add(getMinMaxScrollPanel());
		ret.add(getHistogram());
		return ret;
	}

	public List<MultiNetChangeListener> getMultiNetChangeListenerList() {
		List<MultiNetChangeListener> ret = new ArrayList<MultiNetChangeListener>();
		return ret;
	}

	public List<ColorRangeChangeListener> getColorRangeChangeListenerList() {
		List<ColorRangeChangeListener> ret = new ArrayList<ColorRangeChangeListener>();
		ret.add(getHistogram());
		return ret;
	}

	public List<AnimationValueListener> getAnimationValueListenerList() {
		List<AnimationValueListener> ret = new ArrayList<AnimationValueListener>();
		return ret;
	}

	public List<AlignViewsListener> getAlignViewsListenerList() {
		List<AlignViewsListener> ret = new ArrayList<AlignViewsListener>();
		return ret;
	}
}
