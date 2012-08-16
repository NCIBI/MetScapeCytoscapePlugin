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
package org.ncibi.cytoscape.metscape.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

public class CompoundMapping implements MultiColumnData {
	private String[] columns = new String[0];
	private Map<String, Boolean> columnIsSigned = new HashMap<String, Boolean>();
	private Map<String,Double[]> dataMap = new HashMap<String, Double[]>();
	private Map<String,String> symbolMap = new HashMap<String,String>();
	private SortedSet<String> missingCompounds = new TreeSet<String>(new IDComparator());
	private String name = "(none)";
	private boolean cmpdNames = false;
	
	public CompoundMapping(CompoundData compoundData, CompoundSelector selector) {
		columns = compoundData.getColumns();
		columnIsSigned = compoundData.getColumnIsSigned();
		name = compoundData.getName();
		getMappings(compoundData.getNameOrId(), compoundData.getData(), selector);
	}
	
	public CompoundMapping() {
	}

	public void getMappings(List<String> nameOrId, List<Double[]> data, CompoundSelector compoundSelector) {
		MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
		Map<String,MetabolicName> nameMap = new HashMap<String, MetabolicName>();
		if(isCids(nameOrId)) {
			Response<Map<String, MetabolicName>> response = 
				ns.retrievePrimaryNamesForCids(nameOrId);
			nameMap = response.getResponseValue();
		}
		else {
			cmpdNames = true;
			Response<Map<String, List<MetabolicName>>> response = 
				ns.retrieveMatchingCompoundNames(nameOrId);
			nameMap = compoundSelector.select(response.getResponseValue());
		}
		
		for(int i=0; i<nameOrId.size(); i++) {
			String key = nameOrId.get(i);
			MetabolicName name = nameMap.get(key);
			if(name == null){
				missingCompounds.add(key);
			}
			else {
				symbolMap.put(name.getId(), name.getName());
				dataMap.put(name.getId(),data.get(i));
			}
		}
	}
	
	protected boolean isCids(List<String> nameOrId) {
		int cidCount = 0;
		for (String c : nameOrId) {
			if (isCid(c)) cidCount++; 
		}
		return (double)cidCount/(double)nameOrId.size() > 0.5;
	}
	
	private boolean isCid(String probe){
		// ids are always of lenght 6
		if (probe.trim().length() != 6)
			return false;
		// either CE, CN followed by a number or...
		if (startsWithValidDoubleCharacter(probe)) {
			if (NumUtils.toInteger(probe.substring(2,6)) == null) return false;
		}
		// or C, D, G followed by a number or...
		else if (startsWithValidSingleCharacter(probe)) {
			if (NumUtils.toInteger(probe.substring(1,6)) == null) return false;
		}
		return true;
	}

	private boolean startsWithValidDoubleCharacter(String c) {
		String two = c.substring(0, 2);
		return two.equalsIgnoreCase("CE")
			|| two.equalsIgnoreCase("CN");
	}

	private boolean startsWithValidSingleCharacter(String c) {
		String first = c.substring(0, 1);
		return first.equalsIgnoreCase("C")
				|| first.equalsIgnoreCase("G") 
				|| first.equalsIgnoreCase("D");
	}
	
	public Double[] getData(String cid) {
		return dataMap.get(cid);
	}
	
	public Map<String, Double[]> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Double[]> dataMap) {
		this.dataMap = dataMap;
	}

	public String[] getColumns() {
		return columns;
	}

	public Map<String, Boolean> getColumnIsSigned() {
		return columnIsSigned;
	}

	public void setColumnIsSigned(Map<String, Boolean> columnIsSigned) {
		this.columnIsSigned = columnIsSigned;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}
	
	public String getFullyQualifiedName() {
		return "Compound." + name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getSymbolMap() {
		return symbolMap;
	}

	public void setSymbolMap(Map<String, String> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public SortedSet<String> getMissingCompounds() {
		return missingCompounds;
	}

	public void setMissingCompounds(SortedSet<String> missingCompounds) {
		this.missingCompounds = missingCompounds;
	}
	
	public boolean isCmpdNames() {
		return cmpdNames;
	}

	public void setCmpdNames(boolean cmpdNames) {
		this.cmpdNames = cmpdNames;
	}

	public Set<String> idSet(){
		return symbolMap.keySet();
	}
	
	public boolean isEmpty(){
		return symbolMap.isEmpty();
	}
}
