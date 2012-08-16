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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class MultiObservationTimeStudy {

	private String name = "";
	private HashMap<String, Study> studyMap = new HashMap<String, Study>();
	private ArrayList<Study> studyList = new ArrayList<Study>();
	private HashMap<String, TimeSeriesPosition> timeSeriesMap = new HashMap<String, TimeSeriesPosition>();
	private ArrayList<TimeSeriesPosition> timeSeriesList = new ArrayList<TimeSeriesPosition>();
	private HashMap<String,Compound> compoundMap = new HashMap<String,Compound>();
	private ArrayList<Compound> compoundList = new ArrayList<Compound>();
	
	public MultiObservationTimeStudy(CyNetwork network, MultiStudyToNetworkMapping networkMapping) {
		setName(network.getTitle());
		List<String> compoundIdList = extractCompoundIdList(network);
		ValueType valueType = ValueType.BASIC; // Note: no value types for now
		String baseAttributeName = StudyUtil.makeBaseAttributeName(network);
		for (int animationIndex = 0; animationIndex < networkMapping.getNumberOfAnimations(); animationIndex++) {
			String animationLabel = networkMapping.getAnimationLabel(animationIndex);
			for (int valueLableIndex = 0; valueLableIndex < networkMapping.getNumberOfStudies(); valueLableIndex++) {
				String timeStepLabel = networkMapping.getTimeStepLabel(valueLableIndex);
				String valueLabel = networkMapping.getValueLabel(animationIndex, valueLableIndex);
				for (String compoundId: compoundIdList){
					Double value = getValue(baseAttributeName, compoundId,valueLabel);
					if (value == null) value = new Double(Double.NaN);
					insertObservation(animationLabel,timeStepLabel,compoundId,valueType,value);
				}
			}
		}
	}

	private Double getValue(String baseName, String id, String valueLabel) {
		if (valueLabel == null) return Double.NaN;
		String attributeName = baseName + "." +  valueLabel;
		return Attributes.node.getDoubleAttribute(id, attributeName);
	}

	private List<String> extractCompoundIdList(CyNetwork network) {
		List<String> compoundIdList = new ArrayList<String>();
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			if(MetScapePlugin.getPluginData().getDefaultCompounds().
					containsKey(cyNode.getIdentifier()))
				compoundIdList.add(cyNode.getIdentifier());
		}
		return compoundIdList;
	}

	public void insertObservation(String studyLabel, String timeSeriesLabel, String compoundLabel, ValueType valueType, Double value) {
		// NOTE: strong assumptions are made about the order of insertions...
		//  1) observations within a time series have their first insertions in order
		//  2) the order of first insertions in time series implies an ordering of the time series groups
		//  3) the order of first insertions in compounds implies an ordering of the compounds
		TypedValue typedValue = new TypedValue(valueType,value);

		Study study = studyMap.get(studyLabel);
		if (study == null) {
			study = new Study(studyLabel);
			studyList.add(study);
			studyMap.put(studyLabel, study);
		}

		// preserve order of TimeSeries elements
		TimeSeriesPosition ts = timeSeriesMap.get(timeSeriesLabel);
		if (ts == null) {
			ts = new TimeSeriesPosition(timeSeriesLabel);
			timeSeriesList.add(ts);
			timeSeriesMap.put(timeSeriesLabel,ts);
		}
		
		Compound c = compoundMap.get(compoundLabel);
		if (c == null){
			c = new Compound(compoundLabel);
			compoundList.add(c);
			compoundMap.put(compoundLabel, c);
		}		
		c.addValue(study,ts,typedValue);
	}
	
	public Set<String> getStudyNameSet(){
		return studyMap.keySet();
	}
	
	public List<Study> getStudyList(){
		return studyList;
	}

	public Set<String> getTimeSeriesNameSet(){
		return timeSeriesMap.keySet();
	}

	public List<TimeSeriesPosition> getTimeSeriesList(){
		return timeSeriesList;
	}
	
	public Set<String> getCompoundNameSet(){
		return compoundMap.keySet();
	}
	
	public List<Compound> getCompoundList() {
		return compoundList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
}
