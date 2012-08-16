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
package org.ncibi.cytoscape.metscape.multidisplay.gui.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.multidisplay.MultiNet;
import org.ncibi.cytoscape.metscape.multidisplay.StudyUtil;
import org.ncibi.cytoscape.metscape.multidisplay.gui.MultiObservationDialog;
import org.ncibi.cytoscape.metscape.multidisplay.gui.barchart.HistogramBarValue;

import cytoscape.CyNetwork;

public class ControlImplementation implements ControlInterface {

	private MultiNet sourceMultiNet;
	private HistogramColorScaleModel histogramColorScaleModel;
	private MultiObservationDialog multiObservationDialog;
	private AnimationModel animationModel;
	private CyNetwork network;

	// change listeners

	private final Vector<MinMaxChangeListener> minMaxChangeListenerCollection;
	private final Vector<MultiNetChangeListener> multiNetChangeListenerCollection;
	private final Vector<ColorRangeChangeListener> colorRangeChangeListenerCollection;
	private final Vector<AnimationValueListener> animationValueListenerCollection;
	private final Vector<AlignViewsListener> alignViewsListenerCollection;
	private final Vector<AnimationPostionChangeListener> animationPostionChangeListenerCollection;

	public ControlImplementation(CyNetwork network) {
		this.network = network;
		minMaxChangeListenerCollection = new Vector<MinMaxChangeListener>();
		multiNetChangeListenerCollection = new Vector<MultiNetChangeListener>();
		colorRangeChangeListenerCollection = new Vector<ColorRangeChangeListener>();
		animationValueListenerCollection = new Vector<AnimationValueListener>();
		alignViewsListenerCollection = new Vector<AlignViewsListener>();
		animationPostionChangeListenerCollection = new Vector<AnimationPostionChangeListener>();
		histogramColorScaleModel = getModelFromData();
		animationModel = new AnimationModel();
	}

	@Override
	public void startAnimation(MultiNet animation) {
		sourceMultiNet = animation;
		multiObservationDialog = new MultiObservationDialog(this);
		wireListenersUp();
		notifyAllOfNewAnimation();
		multiObservationDialog.setSelectionList(sourceMultiNet.getStudyList());
		multiObservationDialog.setVisible(true);
		restart();
	}

	private void notifyAllOfNewAnimation() {
		notifyAllMultiNetChangeListeners(sourceMultiNet);
	}

	@Override
	public void disposeOfMultiNet() {
		sourceMultiNet.disposeOfStudy();
		sourceMultiNet = null;
	}

	@Override
	public void stopAnimation() {
		multiObservationDialog.stopPlayingAnimation();
	}

	private void wireListenersUp() {
		// this is a mess!
		for (MinMaxChangeListener l : multiObservationDialog
				.getMinMaxChangeListenerList()) {
			addMinMaxListener(l);
		}
		for (MultiNetChangeListener l : multiObservationDialog
				.getMultiNetChangeListenerList()) {
			addMultiNetChangeListener(l);
		}
		for (ColorRangeChangeListener l : multiObservationDialog
				.getColorRangeChangeListenerList()) {
			addColorRangeChangeListener(l);
		}
		for (AnimationValueListener l : multiObservationDialog
				.getAnimationValueListenerList()) {
			addAnimationValueListener(l);
		}
		for (AlignViewsListener l : multiObservationDialog
				.getAlignViewsListenerList()) {
			addAlignViewsListener(l);
		}
		for (MinMaxChangeListener l : sourceMultiNet
				.getMinMaxChangeListenerList()) {
			addMinMaxListener(l);
		}
		for (MultiNetChangeListener l : sourceMultiNet
				.getMultiNetChangeListenerList()) {
			addMultiNetChangeListener(l);
		}
		for (ColorRangeChangeListener l : sourceMultiNet
				.getColorRangeChangeListenerList()) {
			addColorRangeChangeListener(l);
		}
		for (AnimationValueListener l : sourceMultiNet
				.getAnimationValueListenerList()) {
			addAnimationValueListener(l);
		}
		for (AlignViewsListener l : sourceMultiNet.getAlignViewsListenerList()) {
			addAlignViewsListener(l);
		}
		addMultiNetChangeListener(multiObservationDialog);
		addAnimationPostionChangeListener(multiObservationDialog);
	}

	public void restart() {
		multiObservationDialog.setSliderPosition(0);
		multiObservationDialog.setVisible(true);
		changeAnimationPosition(false, 0.0, 0.0);
	}

	public void dismiss() {
		multiObservationDialog.setVisible(false);
	}

	@Override
	public void changeColorRange(ColorRange cr) {
		histogramColorScaleModel.setColorRange(cr);
		notifyAllColorRangeChangeListeners(cr);
	}

	@Override
	public void setMaxValue(double max) {
		histogramColorScaleModel.setMaxValue(max);
		notifyAllMinMaxChangeListeners(histogramColorScaleModel
				.getMinMaxModel());
		replayAnimationPosition();
	}

	@Override
	public void setMinValue(double min) {
		histogramColorScaleModel.setMinValue(min);
		notifyAllMinMaxChangeListeners(histogramColorScaleModel
				.getMinMaxModel());
		replayAnimationPosition();
	}

	@Override
	public HistogramBarValue getBar(int index) {
		return histogramColorScaleModel.getBar(index);
	}

	@Override
	public Color getBarColor(int index) {
		return histogramColorScaleModel.getBarColor(index);
	}

	@Override
	public double getBarWidth() {
		return histogramColorScaleModel.getBarWidth();
	}

	@Override
	public HistogramColorScaleModel getHistogramColorScaleModel() {
		return histogramColorScaleModel;
	}

	@Override
	public double getMaxBarHeight() {
		return histogramColorScaleModel.getMaxBarHeight();
	}

	@Override
	public Color getMaxColor() {
		return histogramColorScaleModel.getMaxColor();
	}

	@Override
	public double getMaxMaxValue() {
		return histogramColorScaleModel.getMaxMaxValue();
	}

	@Override
	public double getMaxValue() {
		return histogramColorScaleModel.getMaxValue();
	}

	@Override
	public Color getMinColor() {
		return histogramColorScaleModel.getMinColor();
	}

	@Override
	public MinMaxValueModel getMinMaxValueModel() {
		return histogramColorScaleModel.getMinMaxModel();
	}

	@Override
	public double getMinMinValue() {
		return histogramColorScaleModel.getMinMinValue();
	}

	@Override
	public double getMinValue() {
		return histogramColorScaleModel.getMinValue();
	}

	@Override
	public boolean noBars() {
		return histogramColorScaleModel.noBars();
	}

	@Override
	public int numberOfBars() {
		return histogramColorScaleModel.numberOfBars();
	}

	@Override
	public void setColorRange(ColorRange cr) {
		histogramColorScaleModel.setColorRange(cr);
		notifyAllColorRangeChangeListeners(cr);
	}

	private static final int NUMBER_OF_BUCKETS = 50;

	private HistogramColorScaleModel defaultModel = null;

	private HistogramColorScaleModel getDefaultModel() {
		if (defaultModel == null) {
			MinMaxValueModel rangeModel = new MinMaxValueModel(0.0, 20.0);
			HistogramBarValue[] bars = new HistogramBarValue[0];
			double barWidth = 1.0;
			ColorRange colorMap = ColorRange.getDefault();
			this.defaultModel = new HistogramColorScaleModel(rangeModel, bars,
					barWidth, colorMap);
		}
		return defaultModel;
	}

	private HistogramColorScaleModel getModelFromData() {
		double[] valueArray = getDataValues();
		if (valueArray == null)
			return getDefaultModel();
		if (valueArray.length == 0)
			return getDefaultModel();
		double min = valueArray[0];
		double max = min;
		for (int i = 1; i < valueArray.length; i++) {
			double value = valueArray[i];
			if (value < min)
				min = value;
			if (value > max)
				max = value;
		}
		// extend min and max by 10%
		double extend = (max-min) * 0.1;
		min -= extend;
		max += extend;
		// push to nearest whole number 
		min = Math.floor(min);
		max = Math.ceil(max);
		double bucketSize = (max - min) / NUMBER_OF_BUCKETS;
		MinMaxValueModel rangeModel = new MinMaxValueModel(min, max);
		HistogramBarValue[] bars = makeHistorgamBars(min, max, valueArray,
				bucketSize);
		ColorRange colorMap = ColorRange.getDefault();
		HistogramColorScaleModel scaleModel = new HistogramColorScaleModel(
				rangeModel, bars, bucketSize, colorMap);
		return scaleModel;
	}

	private HistogramBarValue[] makeHistorgamBars(double min, double max,
			double[] values, double bucketSize) {
		int numberOfBuckets = (int) Math.ceil((max - min) / bucketSize);
		ColorScaleHistogramBarValue[] ret = new ColorScaleHistogramBarValue[numberOfBuckets];
		int size = ret.length;
		double left = min;
		double right = left + bucketSize;
		for (int i = 0; i < size; i++) {
			ret[i] = new ColorScaleHistogramBarValue(left, right);
			left = right;
			right = left + bucketSize;
		}
		for (double d : values) {
			int index = (int) Math.floor((d - min) / (max - min) * size);
			if (index < 0)
				index = 0;
			if (index >= ret.length)
				index = ret.length - 1;
			ret[index].addToBar();
		}
		return ret;
	}

	private double[] getDataValues() {
		ArrayList<Double> holder = getDataForAllCompounds();
		double[] ret = new double[holder.size()];
		int index = 0;
		for (Double d : holder) {
			ret[index++] = d.doubleValue();
		}
		return ret;
	}

	private ArrayList<Double> getDataForAllCompounds() {
		ArrayList<Double> ret = new ArrayList<Double>();
		List<String> compoundIds = StudyUtil.getCompoundIds(network);
		for (String id : compoundIds) {
			for (String attributeName : StudyUtil.getConcentrationAttributes(network)) {
				Double d = Attributes.node.getDoubleAttribute(id, attributeName);
				if ((d != null) && (!d.isNaN())) {
					ret.add(d);
				}
			}
		}
		return ret;
	}

	public class ColorScaleHistogramBarValue implements HistogramBarValue {

		private double barValue = 0.0;
		private final double min;
		private final double max;

		public ColorScaleHistogramBarValue(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public void addToBar() {
			barValue = barValue + 1.0;
		}

		@Override
		public double getBarValue() {
			return barValue;
		}

		@Override
		public String getInfoText() {
			return min + "," + max + ";" + (int) barValue;
		}

		@Override
		public double getXValue() {
			return (min + max) / 2.0;
		}

	}

	private void replayAnimationPosition() {
		changeAnimationPosition(false, animationModel.getScore(),
				animationModel.getPart());
	}

	@Override
	public void changeAnimationPosition(boolean moving, double score,
			double value) {
		animationModel.updatePositionInformation(score, value);
		notifyAllAnimationValueListeners(moving, score, value);
	}
	
	@Override
	public void updateAnimatePosition(double incrementalFraction) {
		animationModel.incrementAnimationPosition(incrementalFraction);
		notifyAllAnimationPostionChangeListeners(animationModel.getAnimationPostion());
	}

	@Override
	public void alignNetworkViews() {
		CyNetwork source = sourceMultiNet.getAlignmentSource();
		notifyAllAlignViewsListeners(source);
	}

	@Override
	public void addAlignViewsListener(AlignViewsListener l) {
		alignViewsListenerCollection.add(l);
	}

	private void notifyAllAlignViewsListeners(CyNetwork source) {
		if (alignViewsListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty alignViewsListenerCollection");
		for (AlignViewsListener l : alignViewsListenerCollection) {
			l.alignNetworkViews(sourceMultiNet.getAlignmentSource());
		}
	}

	@Override
	public void addAnimationValueListener(AnimationValueListener l) {
		animationValueListenerCollection.add(l);
	}

	private void notifyAllAnimationValueListeners(boolean moving, double score,
			double value) {
		if (animationValueListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty animationValueListenerCollection");
		for (AnimationValueListener l : animationValueListenerCollection) {
			l.animationValueChanged(moving, score, value);
		}
	}
	
	@Override
	public void colorRangeChanged(ColorRange cr) {
		notifyAllColorRangeChangeListeners(cr);
	}

	@Override
	public void addColorRangeChangeListener(ColorRangeChangeListener l) {
		colorRangeChangeListenerCollection.add(l);
	}

	private void notifyAllColorRangeChangeListeners(ColorRange cr) {
		if (colorRangeChangeListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty colorRangeChangeListenerCollection");
		for (ColorRangeChangeListener l : colorRangeChangeListenerCollection) {
			l.colorRangeChanged(cr);
		}
	}

	@Override
	public void addMultiNetChangeListener(MultiNetChangeListener l) {
		multiNetChangeListenerCollection.add(l);
	}

	private void notifyAllMultiNetChangeListeners(MultiNet m) {
		if (multiNetChangeListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty multiNetChangeListenerCollection");
		for (MultiNetChangeListener l : multiNetChangeListenerCollection) {
			l.updateFromMultiNet(m);
		}
	}

	@Override
	public void addMinMaxListener(MinMaxChangeListener l) {
		minMaxChangeListenerCollection.add(l);
	}

	private void notifyAllMinMaxChangeListeners(MinMaxValueModel minMaxModel) {
		if (minMaxChangeListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty minMaxChangeListenerCollection");
		for (MinMaxChangeListener l : minMaxChangeListenerCollection) {
			l.valuesChanged(minMaxModel);
		}
	}

	
	@Override
	public void addAnimationPostionChangeListener(
			AnimationPostionChangeListener l) {
		animationPostionChangeListenerCollection.add(l);		
	}

	private void notifyAllAnimationPostionChangeListeners(
			double proportionalPosition) {
		if (animationPostionChangeListenerCollection.isEmpty())
			throw new RuntimeException(
					"notify empty animationPostionChangeListenerCollection");
		for (AnimationPostionChangeListener l : animationPostionChangeListenerCollection) {
			l.setProportionalPosition(proportionalPosition);
		}
	}

}
