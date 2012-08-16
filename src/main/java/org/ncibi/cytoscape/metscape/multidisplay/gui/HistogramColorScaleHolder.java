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

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AlignViewsListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AnimationValueListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRangeChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MultiNetChangeListener;

public class HistogramColorScaleHolder extends JPanel {

	private static final long serialVersionUID = 1L;
	private final ControlInterface control;
	private HistogramColorScalePanel histogramColorScalePanel = null; //  @jve:decl-index=0:
			
	/**
	 * @param owner
	 */
	public HistogramColorScaleHolder(ControlInterface control) {
		this.control = control;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(350,350);
		this.setLayout(new BorderLayout());
		this.add(getHistogramColorScalePanel(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes histogramColorScalePanel	
	 * 	
	 * @return org.ncibi.cytoscape.metscape.multidisplay.gui.HistogramColorScalePanel	
	 */
	private HistogramColorScalePanel getHistogramColorScalePanel() {
		if (histogramColorScalePanel == null) {
			histogramColorScalePanel = new HistogramColorScalePanel(control);
		}
		return histogramColorScalePanel;
	}
	
	public List<MinMaxChangeListener> getMinMaxChangeListenerList() {
		return getHistogramColorScalePanel().getMinMaxChangeListenerList();
	}

	public List<MultiNetChangeListener> getMultiNetChangeListenerList() {
		return getHistogramColorScalePanel().getMultiNetChangeListenerList();
	}

	public List<ColorRangeChangeListener> getColorRangeChangeListenerList() {
		return getHistogramColorScalePanel().getColorRangeChangeListenerList();
	}

	public List<AnimationValueListener> getAnimationValueListenerList() {
		return getHistogramColorScalePanel().getAnimationValueListenerList();
	}

	public List<AlignViewsListener> getAlignViewsListenerList() {
		return getHistogramColorScalePanel().getAlignViewsListenerList();
	}

}
