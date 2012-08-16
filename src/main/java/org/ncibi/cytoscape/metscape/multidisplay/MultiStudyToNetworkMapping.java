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

public class MultiStudyToNetworkMapping {

	private String[] animationLabelArray;
	private String[] timeStepLabelArray;
	private String[][] valueLabelMatrix;

	public MultiStudyToNetworkMapping(String[] animationLabelArray,
			String[] timeStepLabelArray,
			String[][] valueLabelMatrix) {
		this.animationLabelArray = animationLabelArray;
		this.timeStepLabelArray = timeStepLabelArray;
		this.valueLabelMatrix = valueLabelMatrix;
	}

	public int getNumberOfAnimations() {
		if (valueLabelMatrix == null)
			return 0;
		return valueLabelMatrix.length;
	}

	public int getNumberOfStudies() {
		// NOTE: the rows of labelMatrix are assumed to be the same length
		if (valueLabelMatrix == null)
			return 0;
		if (valueLabelMatrix.length == 0)
			return 0;
		if (valueLabelMatrix[0] == null)
			return 0;
		return valueLabelMatrix[0].length;
	}

	public String getAnimationLabel(int animationIndex) {
		return animationLabelArray[animationIndex];
	}
	
	public String getTimeStepLabel(int timeSeriesIndex){
		return timeStepLabelArray[timeSeriesIndex];
	}

	public String getValueLabel(int animationIndex, int valueIndex) {
		return valueLabelMatrix[animationIndex][valueIndex];
	}

}
