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

public class AnimationModel {

	private double score = 0.0;
	private double part = 0.0;
	private double animationPosition = 0.0;
	
	public void updatePositionInformation(double s, double p) {
		score = s;
		part = p;
	}

	public double getScore(){
		return score;
	}
	
	public double getPart(){
		return part;
	}
	
	public double getAnimationPostion(){
		return animationPosition;
	}
	
	public void setAnimationPosition(double value) {
		if (value < 0.0) value = 0.0;
		if (value > 1.0) value = 1.0;
		animationPosition = value;
	}
	
	public void incrementAnimationPosition(double frac){
		if (frac > 1.0) frac = 1.0;
		if (frac < -1.0) frac = -1.0;
		if (frac == 0.0) return;
		
		double probe = animationPosition + frac;
		if ((probe >= 0.0) && (probe <= 1.0)) 
			setAnimationPosition(probe);
		else if (probe < 0.0) // wrap at low end
			setAnimationPosition(1.0);
		else //(probe > 1.0) - wrap at high end
			setAnimationPosition(0.0);
	}
}
