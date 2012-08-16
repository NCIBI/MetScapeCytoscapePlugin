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
package org.ncibi.cytoscape.metscape.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.commons.lang.StrUtils;
import org.ncibi.cytoscape.metscape.data.CompoundSelector;
import org.ncibi.cytoscape.metscape.data.IDComparator;
import org.ncibi.cytoscape.metscape.ui.CompoundNameSelectionDialog;
import org.ncibi.cytoscape.metscape.ui.MissingDataDialog;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.Cytoscape;

public class GetCompoundMappingsTask extends AbstractTask {
	
	private String compoundString;
	private Map<String, String> symbolMap;

	public static Map<String, String> getMappings(String compoundString) {
		GetCompoundMappingsTask task = new GetCompoundMappingsTask(compoundString);
		if(configureAndRunTask(task)) {
			return task.symbolMap;
		}
		else return null;
	}

	private GetCompoundMappingsTask(String compoundString) {
		this.compoundString = compoundString;
	}
	
	@Override
	public String getTitle() {
		return "Get Compound Mappings";
	}

	@Override
	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Initializing...");
			List<String> nameOrId = StrUtils.splitCommaOrSpaceSeparatedString(compoundString);
			SortedSet<String> missingCompounds = new TreeSet<String>(new IDComparator());
			boolean cmpdNames = false;

			taskMonitor.setStatus("Getting compound mappings from server...");
			MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Map<String,MetabolicName> nameMap;

			if(isCids(nameOrId)) {
				Response<Map<String, MetabolicName>> response = 
					ns.retrievePrimaryNamesForCids(nameOrId);
				if(response == null || response.getResponseValue() == null) throw new Exception();
				nameMap = response.getResponseValue();
			}
			else {
				cmpdNames = true;
				nameOrId = StrUtils.splitNewlineSeparatedString(compoundString);
				Response<Map<String, List<MetabolicName>>> response = 
					ns.retrieveMatchingCompoundNames(nameOrId);
				if(response == null || response.getResponseValue() == null) throw new Exception();
				CompoundSelector compoundSelector = makeCompoundSelector();
				nameMap = compoundSelector.select(response.getResponseValue());
				if(nameMap == null) return;
			}

			symbolMap = new HashMap<String, String>();
			for(int i=0; i<nameOrId.size(); i++) {
				String key = nameOrId.get(i);
				MetabolicName name = nameMap.get(key);
				if(name == null){
					missingCompounds.add(key);
				}
				else {
					symbolMap.put(name.getId(), name.getName());
				}
			}
			if (!missingCompounds.isEmpty()) {
				MissingDataDialog dialog = new MissingDataDialog(
						Cytoscape.getDesktop(), missingCompounds, null, null, cmpdNames);
				dialog.setVisible(true);
			}
			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);
		}
		catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t,
						"An error occurred while loading data for the MetScape session: "
								+ t.getLocalizedMessage());
		}
	}
	
	private CompoundSelector makeCompoundSelector() {
		return new CompoundSelector() {
			@Override
			public Map<String, MetabolicName> select(
					Map<String, List<MetabolicName>> compoundNameMap) {
				return CompoundNameSelectionDialog
						.select(Cytoscape.getDesktop(),null,null,compoundNameMap);
			}
		};
	}


	private boolean isCids(List<String> symbolOrId) {
		int cidCount = 0;
		for (String c : symbolOrId) {
			if (isCid(c)) cidCount++; 
		}
		return (double)cidCount/(double)symbolOrId.size() > 0.5;
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

}
