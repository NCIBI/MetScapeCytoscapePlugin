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

public enum ColorRange {

	RED_BLUE("Red - Blue",Color.BLACK,Color.RED,Color.BLUE,Color.BLACK),
	GREEN_BLUE("Green - Blue",Color.BLACK,Color.GREEN,Color.BLUE,Color.BLACK),
	RED_GREEN("Red - Green",Color.BLACK,Color.RED,Color.GREEN,Color.BLACK),
	BLACK_WHITE("Black - White",Color.BLUE,Color.BLACK,Color.WHITE,Color.RED);
	
	private final String title;
	private final Color minColor;
	private final Color maxColor;
	private final Color underColor;
	private final Color overColor;

	private ColorRange(String title, Color underColor, Color minColor, Color maxColor, Color overColor) {
		this.title = title;
		this.minColor = minColor;
		this.maxColor = maxColor;
		this.underColor = underColor;
		this.overColor = overColor;
	}
	
	public static ColorRange byTitle(String title){
		ColorRange ret = getDefault();
		for (ColorRange crm: ColorRange.values()){
			if (crm.getTitle().equals(title)) ret = crm;
		}
		return ret;
	}
	
	public static ColorRange getDefault(){
		return RED_BLUE;
	}
	
	public String getTitle(){
		return title;
	}
	
	public Color getMinColor(){
		return minColor;
	}
	
	public Color getMaxColor(){
		return maxColor;
	}
	
	public Color getUnderColor() {
		return underColor;
	}

	public Color getOverColor() {
		return overColor;
	}

	public int red(double frac){
		if (frac > 1.0) return overColor.getRed();
		if (frac < 0.0) return underColor.getRed();
		return minColor.getRed() + (int)((maxColor.getRed() - minColor.getRed()) * frac);
	}
	
	public int green(double frac){
		if (frac > 1.0) return overColor.getGreen();
		if (frac < 0.0) return underColor.getGreen();
		return minColor.getGreen() + (int)((maxColor.getGreen() - minColor.getGreen()) * frac);
	}

	public int blue(double frac){
		if (frac > 1.0) return overColor.getBlue();
		if (frac < 0.0) return underColor.getBlue();
		return  minColor.getBlue() + (int)((maxColor.getBlue() - minColor.getBlue()) * frac);
	}
	
	public Color color(double frac){
		if (frac > 1.0) return overColor;
		if (frac < 0.0) return underColor;
		return new Color(red(frac),green(frac),blue(frac));
	}
	
	public String toString(){
		return getTitle();
	}
	
}
