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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.GeneNameAttribute;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

public class GeneMapping implements MultiColumnData {

	private String[] columns = new String[0];
	private Map<String, Boolean> columnIsSigned = new HashMap<String, Boolean>();
	private Map<Integer,Double[]> dataMap = new HashMap<Integer, Double[]>();
	private Map<Integer,String[]> symbolMap = new HashMap<Integer,String[]>();
	private SortedSet<String> missingGenes = new TreeSet<String>(new IDComparator());
	private String name = "(none)";
	private boolean geneSymbols = false;
	
	public GeneMapping(GeneData geneData, Organism organism) {
		columns = geneData.getColumns();
		columnIsSigned = geneData.getColumnIsSigned();
		name = geneData.getName();
		getMappings(geneData.getSymbolOrId(), geneData.getData(), organism.getTaxid());
	}
	
	public GeneMapping() {
	}

	public void getMappings(List<String> symbolOrId, List<Double[]> data, int taxid) {
		List<Integer> geneIds = new ArrayList<Integer>();
		Map<String,MetabolicName> nameMap = new HashMap<String, MetabolicName>();
		for(String value: symbolOrId){
			Integer intValue = NumUtils.toInteger(value);
			if(intValue != null) {
				geneIds.add(intValue);
			}
		}
		if(geneIds.size() > (symbolOrId.size()/2)) {
			MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Response<Map<String, MetabolicName>> response = 
				ns.retrievePrimaryNamesForGeneids(geneIds, taxid);
			nameMap = response.getResponseValue();
		}
		else {
			geneSymbols = true;
			MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Response<Map<String, List<MetabolicName>>> response = 
				ns.retrieveMatchingGeneSymbols(symbolOrId, taxid);
			Map<String,List<MetabolicName>> nameListMap = response.getResponseValue();
			for(String key: nameListMap.keySet()) {
				nameMap.put(key, nameListMap.get(key).get(0));
			}
		}
		
		for(int i=0; i<symbolOrId.size(); i++) {
			String key = symbolOrId.get(i);
			MetabolicName name = nameMap.get(key);
			if(name == null){
				missingGenes.add(key);
			}
			else {
				Integer orgId = NumUtils.toInteger(name.getAttribute(GeneNameAttribute.HOMOLOG_GENEID));
				String orgName = name.getAttribute(GeneNameAttribute.HOMOLOG_NAME);
				String humanName = name.getName();
				symbolMap.put(orgId, new String[]{orgName, humanName});
				dataMap.put(orgId,data.get(i));
			}
		}
	}
	
	public Double[] getData(Integer geneId) {
		return dataMap.get(geneId);
	}
	
	public Map<Integer, Double[]> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<Integer, Double[]> dataMap) {
		this.dataMap = dataMap;
	}

	public String[] getColumns() {
		return columns;
	}
	
	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public Map<String, Boolean> getColumnIsSigned() {
		return columnIsSigned;
	}

	public void setColumnIsSigned(Map<String, Boolean> columnIsSigned) {
		this.columnIsSigned = columnIsSigned;
	}

	public Map<Integer, String[]> getSymbolMap() {
		return symbolMap;
	}

	public void setSymbolMap(Map<Integer, String[]> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullyQualifiedName() {
		return "Gene." + name;
	}
	
	public SortedSet<String> getMissingGenes() {
		return missingGenes;
	}

	public void setMissingGenes(SortedSet<String> missingGenes) {
		this.missingGenes = missingGenes;
	}
	
	public boolean isGeneSymbols() {
		return geneSymbols;
	}

	public void setGeneSymbols(boolean geneSymbols) {
		this.geneSymbols = geneSymbols;
	}

	public Set<Integer> idSet(){
		return symbolMap.keySet();
	}
	
	public boolean isEmpty(){
		return symbolMap.isEmpty();
	}

}
