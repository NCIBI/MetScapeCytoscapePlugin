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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.network.NullNetworkTranslator;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.GeneNameAttribute;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.network.MetabolicNetwork;
import org.ncibi.metab.network.NetworkType;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.metab.ws.client.MetabolicNetworkService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class ConceptMapping {

	private List<Concept> allConcepts = new ArrayList<Concept>();	
	private Map<String, List<Concept>> conceptMap = new HashMap<String, List<Concept>>();
	private Map<Integer,String[]> symbolMap = new HashMap<Integer,String[]>();
	private SortedSet<String> missingGenes = new TreeSet<String>(new IDComparator());
	private SortedSet<Concept> missingConcepts = new TreeSet<Concept>();
	private boolean geneSymbols = false;
	
	public ConceptMapping(ConceptData conceptData, Organism organism) throws Exception {
		allConcepts = conceptData.getAllConcepts();
		getMappings(conceptData.getAllConcepts(), organism.getTaxid());
	}
	
	public ConceptMapping() {
	}

	public void getMappings(List<Concept> allConcepts, int taxid) throws Exception {
		conceptMap.clear();
		symbolMap.clear();
		missingGenes.clear();
		missingConcepts.clear();
		List<Integer> geneids = new ArrayList<Integer>();
		Set<String> symbolOrId = new HashSet<String>();
		for(Concept concept: allConcepts) {
			for(String value : concept.getGeneIdsOrSymbols())
				symbolOrId.add(value);
		}
		Map<String,MetabolicName> nameMap = new HashMap<String, MetabolicName>();
		for(String value: symbolOrId){
			Integer intValue = NumUtils.toInteger(value);
			if(intValue != null) {
				geneids.add(intValue);
			}
		}
		if(geneids.size() > (symbolOrId.size()/2)) {
			MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			Response<Map<String, MetabolicName>> response = 
				ns.retrievePrimaryNamesForGeneids(geneids, taxid);
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
		Map<String, Integer> inputToHumanGeneId = new HashMap<String,Integer>();
		for(String value: symbolOrId) {
			MetabolicName name = nameMap.get(value);
			if(name == null){
				missingGenes.add(value);
			}
			else {
				inputToHumanGeneId.put(value,NumUtils.toInteger(name.getId()));
				Integer orgId = NumUtils.toInteger(name.getAttribute(GeneNameAttribute.HOMOLOG_GENEID));
				String orgName = name.getAttribute(GeneNameAttribute.HOMOLOG_NAME);
				String humanName = name.getName();
				symbolMap.put(orgId, new String[]{orgName, humanName});
			}
		}
		MetabolicNetworkService networkService = new MetabolicNetworkService(
				HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
		Response<MetabolicNetwork> networkResponse = networkService
		.retrieveNetworkOfTypeForCidsAndGeneids(
				NetworkType.CREG, null, symbolMap.keySet(), taxid);

		if (networkResponse == null || !networkResponse.getResponseStatus().isSuccess())
			throw new Exception("Failed to retrieve concepts.");

		MetabolicNetwork net = networkResponse.getResponseValue();
		NullNetworkTranslator translator = new NullNetworkTranslator(net, new NetworkData());
		translator.doTranslate();

		for(Concept concept: allConcepts) {
			boolean hasMapping = false;
			for(String dataId: concept.getGeneIdsOrSymbols()) {
				Integer geneIdInt = inputToHumanGeneId.get(dataId);
				if (geneIdInt != null) {
					String geneId = inputToHumanGeneId.get(dataId).toString();
					if ((geneId != null) && (Cytoscape.getCyNode(geneId) != null)) {
						hasMapping = true;
						addBranchToConceptMap(geneId, concept);
					}
				}
			}

			if(!hasMapping) 
				missingConcepts.add(concept);
		}
	}
	
	public List<Concept> getConcepts(String id) {
		return conceptMap.get(id);
	}
	
	public Map<String, List<Concept>> getConceptMap() {
		return conceptMap;
	}

	public void setConceptMap(Map<String, List<Concept>> conceptMap) {
		this.conceptMap = conceptMap;
	}
	
	public Map<Integer, String[]> getSymbolMap() {
		return symbolMap;
	}

	public List<Concept> getAllConcepts() {
		return allConcepts;
	}

	public void setAllConcepts(List<Concept> allConcepts) {
		this.allConcepts = allConcepts;
	}

	public void setSymbolMap(Map<Integer, String[]> symbolMap) {
		this.symbolMap = symbolMap;
	}

	public SortedSet<String> getMissingGenes() {
		return missingGenes;
	}

	public void setMissingGenes(SortedSet<String> missingGenes) {
		this.missingGenes = missingGenes;
	}

	public SortedSet<Concept> getMissingConcepts() {
		return missingConcepts;
	}

	public void setMissingConcepts(SortedSet<Concept> missingConcepts) {
		this.missingConcepts = missingConcepts;
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
	
	public boolean isEmpty() {
		return conceptMap.isEmpty();
	}

	private void addBranchToConceptMap(String nodeId, Concept concept) {
		List<Concept> concepts = conceptMap.get(nodeId);
		if (concepts == null) {
			concepts = new ArrayList<Concept>();
		}
		concepts.add(concept);
		conceptMap.put(nodeId, concepts);
		String nodeType = Attributes.node.getStringAttribute(nodeId,"Type");
		String neighborType;
		if(nodeType.equals("Gene")) {
			neighborType = "Enzyme";
		}	
		else if(nodeType.equals("Enzyme")) {
			neighborType = "Reaction";
		}
		else if(nodeType.equals("Reaction")) {
			neighborType = "Compound";
		}
		else return;
			
		int[] edgeIndices = Cytoscape.getRootGraph().
			getAdjacentEdgeIndicesArray(Cytoscape.getCyNode(nodeId).getRootGraphIndex(), true,true,true);
		for(int edgeIndex: edgeIndices){ // need to make source/target compliant
			int sourceIndex = Cytoscape.getRootGraph().getEdgeSourceIndex(edgeIndex);
			int targetIndex = Cytoscape.getRootGraph().getEdgeTargetIndex(edgeIndex);
			CyNode source = (CyNode) Cytoscape.getRootGraph().getNode(sourceIndex);
			CyNode target = (CyNode) Cytoscape.getRootGraph().getNode(targetIndex);
			String neighborId;
			if(!target.getIdentifier().equals(nodeId))
				neighborId = target.getIdentifier();
			else neighborId = source.getIdentifier();
			if(Attributes.node.getAttribute(neighborId,"Type").equals(neighborType)
					&& !concepts.contains(neighborId)) {
				addBranchToConceptMap(neighborId,concept);
			}
		}
	}

}
