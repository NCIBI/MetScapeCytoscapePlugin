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
import org.ncibi.cytoscape.metscape.data.IDComparator;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.cytoscape.metscape.ui.MissingDataDialog;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.GeneNameAttribute;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.Cytoscape;

public class GetGeneMappingsTask extends AbstractTask {
	
	private String geneString;
	private Organism organism;
	private Map<Integer, String[]> symbolMap;

	public static Map<Integer, String[]> getMappings(String geneString, Organism organism) {
		GetGeneMappingsTask task = new GetGeneMappingsTask(geneString, organism);
		if(configureAndRunTask(task)) {
			return task.symbolMap;
		}
		else return null;
	}

	private GetGeneMappingsTask(String geneString, Organism organism) {
		this.geneString = geneString;
		this.organism = organism;
	}
	
	@Override
	public String getTitle() {
		return "Get Gene Mappings";
	}

	@Override
	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Initializing...");
			List<Integer> geneIds = NumUtils.splitCommaOrSpaceSeparatedString(geneString);
			List<String> geneSymbols = StrUtils.splitCommaOrSpaceSeparatedString(geneString);
			SortedSet<String> missingGenes = new TreeSet<String>(new IDComparator());

			taskMonitor.setStatus("Getting gene mappings from server...");
			MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Map<String,MetabolicName> nameMap;

			if(geneIds != null && geneIds.size() > 0) {
				Response<Map<String, MetabolicName>> response = 
					ns.retrievePrimaryNamesForGeneids(geneIds, organism.getTaxid());
				if(response == null || response.getResponseValue() == null) throw new Exception();
				nameMap = response.getResponseValue();
			}
			else {
				Response<Map<String, List<MetabolicName>>> response = 
					ns.retrieveMatchingGeneSymbols(geneSymbols, organism.getTaxid());
				if(response == null || response.getResponseValue() == null) throw new Exception();
				nameMap = new HashMap<String, MetabolicName>();
				Map<String,List<MetabolicName>> nameListMap = response.getResponseValue();
				for(String key: nameListMap.keySet()) {
					nameMap.put(key, nameListMap.get(key).get(0));
				}
			}
			
			symbolMap = new HashMap<Integer, String[]>();
			for(int i=0; i<geneSymbols.size(); i++) {
				String key = geneSymbols.get(i);
				MetabolicName name = nameMap.get(key);
				if(name == null){
					missingGenes.add(key);
				}
				else {
					Integer orgId = NumUtils.toInteger(name.getAttribute(GeneNameAttribute.HOMOLOG_GENEID));
					String orgName = name.getAttribute(GeneNameAttribute.HOMOLOG_NAME);
					String humanName = name.getName();
					symbolMap.put(orgId, new String[]{orgName, humanName});
				}
			}
			if (!missingGenes.isEmpty()) {
				MissingDataDialog dialog = new MissingDataDialog(
						Cytoscape.getDesktop(), null, missingGenes, null, false);
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
}
