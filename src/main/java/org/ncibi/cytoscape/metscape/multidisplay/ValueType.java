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

public enum ValueType {
	UNKNOWN // first: ordinal value is zero!
	, BASIC
	, RATIO
	, LOG_RATIO
	, FOLD_CHANGE
	, LOG_FOLD_CHANGE
	, P_VALUE
	, LOG_P_VALUE
	, FALSE_DISCORVERY_RATE
	, LOG_FALSE_DISCOVERY_RATE
	, INTENSITY
	, LOG_INTENSITY
	;
	
	private static final String[] labels = {"Unknown"
		, "Basic"
		, "Ratio"
		, "Log Ratio"
		, "Fold Change"
		, "Log Fold Change"
		, "P Value"
		, "Log P Value"
		, "False Discovery Rate"
		, "Log False Discovary Rate"
		, "Intensity"
		, "Log Intensity"
		};
	
	public static String getLabelForType(ValueType type){
		int index = type.ordinal();
		if ((index > 0) && (index < labels.length)) return labels[index];
		else return labels[0];
	}
	
	public static ValueType getTypeForLabel(String label) {
		int probe = 0;
		for (int i = 0; i < labels.length; i++) {
			if (labels[i].equals(label)) {
				probe = i;
			}
		}
		return ValueType.values()[probe];
	}
	
	public static String[] getTypeLabelArray(){
		return labels;
	}

	public static String getDefaultTypeLabel() {
		return getLabelForType(FOLD_CHANGE);
	}
}
