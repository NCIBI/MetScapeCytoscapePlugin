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
import java.util.Arrays;
import java.util.Comparator;

import org.ncibi.cytoscape.metscape.multidisplay.gui.barchart.HistogramBarValue;

public class HistogramColorScaleModel {

	private final MinMaxValueModel rangeModel;
	private final HistogramBarValue[] bars;
	private final double barWidth;
	private double maxBarHeight;
	private ColorRange colorRange;
	
	public HistogramColorScaleModel(MinMaxValueModel rangeModel,
			HistogramBarValue[] bars, double barWidth, ColorRange colorRange) {
		this.rangeModel = rangeModel;
		this.bars = bars;
		this.barWidth = barWidth;
		this.colorRange = colorRange;
		computeMaxBarHeight();
		sortBars();
	}
	
	private void computeMaxBarHeight() {
		maxBarHeight = Double.MIN_VALUE;
		for (HistogramBarValue bv: bars){
			maxBarHeight = Math.max(maxBarHeight, bv.getBarValue());
		}
	}

	private void sortBars() {
		Arrays.sort(bars, new Comparator<HistogramBarValue>() {
			public int compare(HistogramBarValue a, HistogramBarValue b) {
				double val1 = a.getXValue();
				double val2 = b.getXValue();
				return val1 - val2 > 0 ? 1 : val2 == val1 ? 0 : -1;
			}
		});
	} 


	public HistogramBarValue getBar(int i){
		if (bars == null) return null;
		if (i < 0) return null;
		if (i >= bars.length) return null;
		return bars[i];
	}
	
	public int numberOfBars(){
		if (bars == null) return 0;
		return bars.length;
	}

	public boolean noBars(){
		return numberOfBars() == 0;
	}
	
	public double getBarWidth(){
		return barWidth;
	}

	public double getMaxBarHeight(){
		return maxBarHeight;
	}
	public int blue(double frac) {
		return colorRange.blue(frac);
	}

	public Color color(double frac) {
		return colorRange.color(frac);
	}

	public boolean equals(Object obj) {
		return colorRange.equals(obj);
	}

	public int green(double frac) {
		return colorRange.green(frac);
	}

	public int red(double frac) {
		return colorRange.red(frac);
	}

	public void addMinMaxListener(MinMaxChangeListener u) {
		rangeModel.addMinMaxListener(u);
	}

	public double getMaxMaxValue() {
		return rangeModel.getMaxMaxValue();
	}

	public double getMaxValue() {
		return rangeModel.getMaxValue();
	}

	public double getMinMinValue() {
		return rangeModel.getMinMinValue();
	}

	public double getMinValue() {
		return rangeModel.getMinValue();
	}

	public void removeAllMinMaxListeners() {
		rangeModel.removeAllMinMaxListeners();
	}

	public void removeMinMaxListener(MinMaxChangeListener u) {
		rangeModel.removeMinMaxListener(u);
	}

	public void resetMinMax() {
		rangeModel.resetMinMax();
	}

	public void setMaxMaxValue(double maxMaxValue) {
		rangeModel.setMaxMaxValue(maxMaxValue);
	}

	public void setMaxValue(double maxValue) {
		rangeModel.setMaxValue(maxValue);
	}

	public void setMinMinValue(double minMinValue) {
		rangeModel.setMinMinValue(minMinValue);
	}

	public void setMinValue(double minValue) {
		rangeModel.setMinValue(minValue);
	}

	public MinMaxValueModel getMinMaxModel() {
		return rangeModel;
	}

	public Color getBarColor(int i) {
		double range = (getMaxValue() - getMinValue());
		double offset = (getBar(i).getXValue() - getMinValue());
		double frac = offset / range;
		return colorRange.color(frac);
	}
	
	public Color getMinColor(){
		return colorRange.getMinColor();
	}

	public Color getMaxColor() {
		return colorRange.getMaxColor();
	}

	public ColorRange getColorRange() {
		return colorRange;
	}

	public void setColorRange(ColorRange cr) {
		colorRange = cr;
	}
	
}
