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
package org.ncibi.cytoscape.metscape.multidisplay;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AlignViewsListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AnimationValueListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRange;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRangeChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.HistogramColorScaleModel;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxValueModel;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MultiNetChangeListener;
import org.ncibi.cytoscape.metscape.visual.VisualStyleFactory;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyDesktopManager;
import cytoscape.view.CyDesktopManager.Arrange;
import cytoscape.view.CyNetworkView;
import cytoscape.view.NetworkViewManager;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.ObjectMapping;
import ding.view.DGraphView;

public class MultiNet implements AlignViewsListener, AnimationValueListener,
		ColorRangeChangeListener, MultiNetChangeListener, MinMaxChangeListener {

	private final ControlInterface control;
	
	private MultiObservationTimeStudy results;
	private HashMap<String, CyNetwork> studyToNetwork = new HashMap<String, CyNetwork>();
	private HashMap<String, VisualStyle> studyToVisualStyle = new HashMap<String, VisualStyle>();
	private List<String> timeSeriesLabelArray = new ArrayList<String>();
	private List<BoundaryColorHolder> boundryColorHolderList = new ArrayList<BoundaryColorHolder>();
	private NumberFormat format = null;
	private double lastScore = -1.0;
	private static final double sliderThreshold = 0.05;
	private static final double inverseThreshold = 1.0 - sliderThreshold;
	private boolean sliderWasMoving = true;
	private static final double RESCALE_MIN_VALUE = 0.0;
	private static final double RESCALE_MAX_VALUE = 100.0;

	public MultiNet(CyNetwork sourceNetwork, MultiObservationTimeStudy study,
			ControlInterface control) {
		this.control = control;
		if (format == null) {
			format = NumberFormat.getNumberInstance();
			format.setMinimumFractionDigits(2);
			format.setMaximumFractionDigits(2);
		}
		results = study;
		String labelsString = null;
		for (TimeSeriesPosition tsp : study.getTimeSeriesList()) {
			if (labelsString == null) labelsString = tsp.getLabel();
			else labelsString += "," + tsp.getLabel();
			timeSeriesLabelArray.add(tsp.getLabel());
		}
		System.out.println("  Time Series labels: " + labelsString);
		buildNets(sourceNetwork, control.getHistogramColorScaleModel());
	}

	public String getName() {
		if (results == null)
			return "No Name (null results)";
		return results.getName();
	}

	public void buildNets(CyNetwork sourceNetwork,
			HistogramColorScaleModel minMaxColorModel) {
		List<Compound> compoundList = results.getCompoundList();
		String idList = "";
		for (Compound c : compoundList)
			idList += c.getLabel().trim() + " ";
		
		List<JInternalFrame> frames = new ArrayList<JInternalFrame>();
		NetworkViewManager nvm = Cytoscape.getDesktop().getNetworkViewManager();
		for(CyNetworkView view: Cytoscape.getNetworkViewMap().values()) {
			frames.add(nvm.getInternalFrame(view));
			nvm.getDesktopPane().remove(nvm.getInternalFrame(view));
		}
		
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setSelectedIndex(0);

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		VisualStyle oldVisualStyle = manager.getVisualStyle();

		System.out.println("Network creation, source visual style: "
				+ oldVisualStyle.getName());

		for (Study study : results.getStudyList()) {
			CyNetwork network = copyNetworkForStudy(study, sourceNetwork);
			CyNetworkView view = Cytoscape.getNetworkView(network
					.getIdentifier());
			try {
				nvm.getInternalFrame(view).setMaximum(false);
			}
			catch(PropertyVetoException e) {
			}

			String networkName = CyNetworkNaming.getSuggestedNetworkTitle(study
					.getLabel());
			networkName.replace(' ','_'); // no blanks in name
			network.setTitle(networkName);

			studyToNetwork.put(study.getLabel(), network);

			VisualStyle style = VisualStyleFactory.createStyle(network);
			BasicCalculator c = createCalculator(network, study,
					minMaxColorModel);
			style.getNodeAppearanceCalculator().setCalculator(c);
			studyToVisualStyle.put(study.getLabel(), style);
			manager.getCalculatorCatalog().removeVisualStyle(style.getName());
			manager.getCalculatorCatalog().addVisualStyle(style);
			view.setVisualStyle(style.getName());
			view.redrawGraph(true, true);
		}
		
		alignNetworkViews(sourceNetwork);
		CyDesktopManager.arrangeFrames(Arrange.VERTICAL);
		for(JInternalFrame frame: frames) {
			nvm.getDesktopPane().add(frame);
		}
		Study firstStudy = results.getStudyList().get(0);
		CyNetwork firstStudyNetwork = studyToNetwork.get(firstStudy.getLabel());
		Cytoscape.getDesktop().setFocus(firstStudyNetwork.getIdentifier());
		
		refreshAll();
	}
		
	public void disposeOfStudy(){
		deleteDynamicAttributes();
		deleteAllNetworks();
		results = null;
	}
	private void deleteDynamicAttributes() {
		for(Study study: results.getStudyList()) {
			String attributeLabel = makeStudyDynamicLabel(study);
			Attributes.node.deleteAttribute(attributeLabel);
		}
	}
	private void deleteAllNetworks() {
		for (String key: studyToNetwork.keySet()){
			CyNetwork net = studyToNetwork.get(key);
			Cytoscape.destroyNetwork(net);
		}
	}

	private CyNetwork copyNetworkForStudy(Study study, CyNetwork sourceNetwork) {
		String originalTitle = sourceNetwork.getTitle();
		String title = originalTitle + " - " + study.getLabel();
		CyNetwork newNetwork = Networks.createNetwork(title);
		List<CyNode> nodeList = nodes(sourceNetwork);
		for (CyNode node : nodeList) {
			newNetwork.addNode(node);
		}
		List<CyEdge> edgeList = edges(sourceNetwork);
		for (CyEdge edge : edgeList) {
			newNetwork.addEdge(edge);
		}
		return newNetwork;
	}

	@SuppressWarnings("unchecked")
	private List<CyNode> nodes(CyNetwork sourceNetwork) {
		return sourceNetwork.nodesList();
	}

	@SuppressWarnings("unchecked")
	private List<CyEdge> edges(CyNetwork sourceNetwork) {
		return sourceNetwork.edgesList();
	}
	
	public List<Study> getStudyList() {
		return results.getStudyList();
	}

	public void valueUpdate(boolean moving, double score, double value) {
		// System.out.println("Update value: " + format.format(score));
		int left = (int) Math.floor(score);
		int right = (int) Math.ceil(score);
		double frac = score - (double) left;
		if (frac < sliderThreshold)
			frac = 0.0;
		if (frac > inverseThreshold)
			frac = 1.0;
		score = ((double) left) + frac;
		// only update the moving pointer if the change is large enough
		if (moving && !sliderWasMoving) {
			sliderWasMoving = true;
		}
		if (moving && (Math.abs(lastScore - score) < sliderThreshold))
			return;
		// else

		lastScore = score;
		if (!moving && sliderWasMoving) {
			sliderWasMoving = false;
		}
		if (!moving)
			setDynamicAttributes(left, right, frac);
	}

	private void setDynamicAttributes(int left, int right, double frac) {
		// System.out.println("Between " + left + " and " + right + "; frac = "
		// + format.format(frac));
		TimeSeriesPosition tsLeft = results.getTimeSeriesList().get(left);
		TimeSeriesPosition tsRight = results.getTimeSeriesList().get(right);
		for (Study study : results.getStudyList()) {
			String attributeLabel = makeStudyDynamicLabel(study);
			// System.out.print("Values in " + attributeLabel + ":");
			for (Compound c : results.getCompoundList()) {
				double leftValue = c.getValue(study, tsLeft).getValue()
						.doubleValue();
				double rightValue = c.getValue(study, tsRight).getValue()
						.doubleValue();
				double value = leftValue + (rightValue - leftValue) * frac;
				Attributes.node.setAttribute(c.getLabel(), attributeLabel, rescaledValue(value));
				// System.out.print(" " + format.format(value));
			}
			// System.out.println();
		}
		//Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		refreshAll();
	}
	
	private double rescaledValue(double value) {
		// rescale the attribute value so that MinValue maps to RESCALE_MIN_VALUE
		// and MaxValue maps to RESCALE_MAX_VALUE; to permit minValue and maxValue to vary
		double minValue = control.getMinValue();
		double maxValue = control.getMaxValue();
		double frac = (value - minValue)/(maxValue - minValue);
		double ret = RESCALE_MIN_VALUE + frac * (RESCALE_MAX_VALUE - RESCALE_MIN_VALUE);
		return ret;
	}

	private void refreshAll() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refreshAllHelper();
			}
		});
	}

	private void refreshAllHelper() {
		String currentId = Cytoscape.getCurrentNetwork().getIdentifier();
		for (String s : studyToNetwork.keySet()) {
			CyNetwork net = studyToNetwork.get(s);
			String netId = net.getIdentifier();
			Cytoscape.getDesktop().setFocus(netId);
			CyNetworkView view = Cytoscape.getNetworkView(netId);
			view.redrawGraph(true, true);
		}
		Cytoscape.getDesktop().setFocus(currentId);
	}

	private String makeStudyDynamicLabel(Study study) {
		return study.getLabel() + ":dynamicValue";
	}

	public List<String> getTimeSeriesLabels() {
		return timeSeriesLabelArray;
	}

	@SuppressWarnings("deprecation")
	private BasicCalculator createCalculator(CyNetwork network, Study study,
			HistogramColorScaleModel minMaxColorModel) {

		BasicCalculator ret;
		String attributeName = makeStudyDynamicLabel(study);
		Double minValue = RESCALE_MIN_VALUE;
		Double maxValue = RESCALE_MAX_VALUE;

		// the color is a model value
		ContinuousMapping contMapping = new ContinuousMapping(
				new Color(0, 0, 0), ObjectMapping.NODE_MAPPING);
		contMapping.setControllingAttributeName(attributeName, network, true);
		contMapping.setInterpolator(new LinearNumberToColorInterpolator());

		// Get minColor and maxColor, and make change listener
		ColorRange colorRange = minMaxColorModel.getColorRange();
		final BoundaryRangeValues boundaryForMin = new BoundaryRangeValues();
		final BoundaryRangeValues boundaryForMax = new BoundaryRangeValues();

		setColorBoundaries(boundaryForMin, boundaryForMax, colorRange);
		
		boundryColorHolderList.add(new BoundaryColorHolder(boundaryForMin,boundaryForMax));

		contMapping.addPoint(minValue, boundaryForMin);
		contMapping.addPoint(maxValue, boundaryForMax);

		String probeName = StudyUtil.replaceWhitespace(attributeName
				+ " Calculator");
		String name = Cytoscape.getVisualMappingManager()
				.getCalculatorCatalog().checkCalculatorName(probeName,
						VisualPropertyType.NODE_FILL_COLOR);

		ret = new BasicCalculator(name, contMapping,
				VisualPropertyType.NODE_FILL_COLOR);

		System.out.println("Calculator name: " + name);
		System.out.println("Calculator for " + attributeName + ": "
				+ format.format(minValue) + "," + format.format(maxValue));
		System.out.println("Color range: " + colorRange.getMinColor() + ";"
				+ colorRange.getMaxColor());

		return ret;

	}// createCalculator

	private void setColorBoundaries(final BoundaryRangeValues boundaryForMin,
			final BoundaryRangeValues boundaryForMax, ColorRange colorRangeModel) {

		Color underColor = colorRangeModel.getUnderColor();
		Color minColor = colorRangeModel.getMinColor();
		Color maxColor = colorRangeModel.getMaxColor();
		Color overColor = colorRangeModel.getOverColor();

		boundaryForMin.lesserValue = underColor;
		boundaryForMin.equalValue = minColor;
		boundaryForMin.greaterValue = minColor;

		boundaryForMax.lesserValue = maxColor;
		boundaryForMax.equalValue = maxColor;
		boundaryForMax.greaterValue = overColor;
	}

	private void performAlignNetworkViews(CyNetwork source) {
		HashMap<String, double[]> map = new HashMap<String, double[]>();
		CyNetworkView sourceView = Cytoscape.getNetworkView(source
				.getIdentifier());
		DGraphView dgv = (DGraphView) sourceView;
		Point2D center = dgv.getCenter();
		Double zoom = sourceView.getZoom();
		for (Iterator<NodeView> i = getNodeViewIterator(sourceView); i
				.hasNext();) {
			NodeView nv = i.next();
			CyNode node = (CyNode) nv.getNode();
			String id = node.getIdentifier();
			double[] pos = new double[2]; // x, y
			pos[0] = nv.getXPosition();
			pos[1] = nv.getYPosition();
			map.put(id, pos);
		}
		for (String s : studyToNetwork.keySet()) {
			CyNetwork target = studyToNetwork.get(s);
			CyNetworkView targetView = Cytoscape.getNetworkView(target
					.getIdentifier());
			if (!target.equals(source)) {
				alignNetView(targetView, center, zoom, map);
			}
		}
		refreshAll();
	}

	private void alignNetView(CyNetworkView targetView, Point2D center,
			double zoom, HashMap<String, double[]> map) {
		// same center and zoom
		DGraphView dgv = (DGraphView) targetView;
		dgv.setCenter(center.getX(), center.getY());
		targetView.setZoom(zoom);
		// copy positions
		for (Iterator<NodeView> i = getNodeViewIterator(targetView); i
				.hasNext();) {
			NodeView nv = i.next();
			CyNode node = (CyNode) nv.getNode();
			String id = node.getIdentifier();
			double[] pos = map.get(id);
			if (pos != null) {
				nv.setOffset(pos[0], pos[1]); // x, y
			}
		}

	}
	
	private void changeBoundaryColors(ColorRange cr){
		for (BoundaryColorHolder bch: boundryColorHolderList){
			setColorBoundaries(bch.getBoundaryForMin(),bch.boundaryForMax,cr);
		}
		refreshAll();
	}

	@SuppressWarnings("unchecked")
	private Iterator<NodeView> getNodeViewIterator(CyNetworkView view) {
		return view.getNodeViewsIterator();
	}

	public CyNetwork getAlignmentSource() {
		return Cytoscape.getCurrentNetwork();
	}

	// listener methods
	
	public List<MinMaxChangeListener> getMinMaxChangeListenerList() {
		List<MinMaxChangeListener> ret = new ArrayList<MinMaxChangeListener>();
		ret.add(this);
		return ret;
	}

	public List<MultiNetChangeListener> getMultiNetChangeListenerList() {
		List<MultiNetChangeListener> ret = new ArrayList<MultiNetChangeListener>();
		ret.add(this);
		return ret;
	}

	public List<ColorRangeChangeListener> getColorRangeChangeListenerList() {
		List<ColorRangeChangeListener> ret = new ArrayList<ColorRangeChangeListener>();
		ret.add(this);
		return ret;
	}

	public List<AnimationValueListener> getAnimationValueListenerList() {
		List<AnimationValueListener> ret = new ArrayList<AnimationValueListener>();
		ret.add(this);
		return ret;
	}

	public List<AlignViewsListener> getAlignViewsListenerList() {
		List<AlignViewsListener> ret = new ArrayList<AlignViewsListener>();
		ret.add(this);
		return ret;
	}

	@Override
	public void colorRangeChanged(ColorRange cr) {
		changeBoundaryColors(cr);
	}

	@Override
	public void alignNetworkViews(CyNetwork source) {
		performAlignNetworkViews(source);
	}

	@Override
	public void animationValueChanged(boolean moving, double score, double value) {
		valueUpdate(moving,score,value);
	}

	@Override
	public void updateFromMultiNet(MultiNet m) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void valuesChanged(MinMaxValueModel t) {
		// TODO Auto-generated method stub	
	}

	private class BoundaryColorHolder{
		
		private final BoundaryRangeValues boundaryForMin;
		private final BoundaryRangeValues boundaryForMax;

		public BoundaryColorHolder(BoundaryRangeValues boundaryForMin,
				BoundaryRangeValues boundaryForMax) {
			this.boundaryForMin = boundaryForMin;
			this.boundaryForMax = boundaryForMax;
		}

		public BoundaryRangeValues getBoundaryForMin() {
			return boundaryForMin;
		}

		public BoundaryRangeValues getBoundaryForMax() {
			return boundaryForMax;
		}
		
	}

	@Override
	public void incrementAnimationPosition(double fractionalIncrement) {
		// TODO Auto-generated method stub
		
	}
}
