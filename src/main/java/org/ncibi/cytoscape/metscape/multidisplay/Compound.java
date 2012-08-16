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

import java.util.HashMap;

public class Compound {

	private String label;
	
	private HashMap<Study,HashMap<TimeSeriesPosition,TypedValue>> map = new HashMap<Study,HashMap<TimeSeriesPosition,TypedValue>>();
	
	public Compound(String compoundLabel) {
		label = compoundLabel;
	}

	public String getLabel() {
		return label;
	}

	public void addValue(Study study, TimeSeriesPosition ts, TypedValue value) {
		HashMap<TimeSeriesPosition,TypedValue> valuesForTsLabel = map.get(study);
		if (valuesForTsLabel == null){
			valuesForTsLabel = new HashMap<TimeSeriesPosition,TypedValue>();
			map.put(study, valuesForTsLabel);
		}
		valuesForTsLabel.put(ts,value);
		study.updateMinMax(value.getValue());
	}

	public TypedValue getValue(Study study, TimeSeriesPosition ts){
		HashMap<TimeSeriesPosition,TypedValue> valuesForTsLable = map.get(study);
		if (valuesForTsLable == null) return null;
		return valuesForTsLable.get(ts);
	}
		
}
