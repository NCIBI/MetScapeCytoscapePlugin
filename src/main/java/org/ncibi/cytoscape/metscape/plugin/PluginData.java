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
package org.ncibi.cytoscape.metscape.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.QuerySubtype;
import org.ncibi.cytoscape.metscape.data.QueryType;
import org.ncibi.metab.pathway.Pathway;

public class PluginData extends NetworkData {

	// panel and menu states
	private boolean buildNetworkPanelOpen = false;
	private boolean pathwayFilterPanelOpen = false;
	private boolean conceptFilterPanelOpen = false;
	// all input data for session
	private List<CompoundData> compoundDataStore = new ArrayList<CompoundData>(Arrays.asList(new CompoundData()));
	private List<GeneData> geneDataStore = new ArrayList<GeneData>(Arrays.asList(new GeneData()));
	private List<ConceptData> conceptDataStore = new ArrayList<ConceptData>(Arrays.asList(new ConceptData()));
	// current data from file sources
	private CompoundData compoundData = new CompoundData();
	private GeneData geneData = new GeneData();
	private ConceptData conceptData = new ConceptData();	
	// active input data
	private Map<String, NetworkData> networkDataStore = new HashMap<String, NetworkData>();
	// default compounds/genes/pathways
	private Map<String, String> defaultCompounds = new TreeMap<String, String>();
	private Map<Integer, String[]> defaultGenes = new TreeMap<Integer, String[]>();
	private Set<Pathway> defaultPathways = null;
	// current compounds/genes/pathway and network type
	private Vector<?> currentCompounds = null;
	private Vector<?> currentGenes = null;
	private Pathway currentPathway = null;
	private QueryType currentQueryType = QueryType.COMPOUND_GENE;
	private QuerySubtype currentQuerySubtype = QuerySubtype.COMPOUNDS;
	
	public GeneData getGeneData() {
		return geneData;
	}

	public void setGeneData(GeneData geneData) {
		this.geneData = geneData;
		this.setGeneMapping(geneData.getMapping());
	}

	public CompoundData getCompoundData() {
		return compoundData;
	}

	public void setCompoundData(CompoundData compoundData) {
		this.compoundData = compoundData;
		this.setCompoundMapping(compoundData.getMapping());
	}

	public ConceptData getConceptData() {
		return conceptData;
	}

	public void setConceptData(ConceptData conceptData) {
		this.conceptData = conceptData;
		this.setConceptMapping(conceptData.getMapping());
	}

	public List<CompoundData> getCompoundDataStore() {
		return compoundDataStore;
	}

	public void setCompoundDataStore(List<CompoundData> compoundDataStore) {
		this.compoundDataStore = compoundDataStore;
	}

	public List<GeneData> getGeneDataStore() {
		return geneDataStore;
	}

	public void setGeneDataStore(List<GeneData> geneDataStore) {
		this.geneDataStore = geneDataStore;
	}

	public List<ConceptData> getConceptDataStore() {
		return conceptDataStore;
	}

	public void setConceptDataStore(List<ConceptData> conceptDataStore) {
		this.conceptDataStore = conceptDataStore;
	}
	public Map<String, NetworkData> getNetworkDataStore() {
		return networkDataStore;
	}

	public void setNetworkDataStore(Map<String, NetworkData> networkDataStore) {
		this.networkDataStore = networkDataStore;
	}
	
	public void addNetworkData(String id, NetworkData networkData) {
		networkDataStore.put(id,networkData);
	}
	
	public NetworkData getNetworkData(String id) {
		return networkDataStore.get(id);
	}
	
	public void removeNetworkData(String id) {
		networkDataStore.remove(id);
	}

	public boolean isBuildNetworkPanelOpen() {
		return buildNetworkPanelOpen;
	}

	public void setBuildNetworkPanelOpen(boolean buildNetworkPanelOpen) {
		this.buildNetworkPanelOpen = buildNetworkPanelOpen;
	}

	public boolean isPathwayFilterPanelOpen() {
		return pathwayFilterPanelOpen;
	}

	public void setPathwayFilterPanelOpen(boolean pathwayFilterPanelOpen) {
		this.pathwayFilterPanelOpen = pathwayFilterPanelOpen;
	}

	public boolean isConceptFilterPanelOpen() {
		return conceptFilterPanelOpen;
	}

	public void setConceptFilterPanelOpen(boolean conceptFilterPanelOpen) {
		this.conceptFilterPanelOpen = conceptFilterPanelOpen;
	}
	
	public Map<String, String> getDefaultCompounds() {
		return defaultCompounds;
	}

	public void setDefaultCompounds(Map<String, String> defaultCompounds) {
		this.defaultCompounds = defaultCompounds;
	}

	public Map<Integer, String[]> getDefaultGenes() {
		return defaultGenes;
	}

	public void setDefaultGenes(Map<Integer, String[]> defaultGenes) {
		this.defaultGenes = defaultGenes;
	}
	
	public Set<Pathway> getDefaultPathways() {
		return defaultPathways;
	}

	public void setDefaultPathways(Set<Pathway> defaultPathways) {
		this.defaultPathways = defaultPathways;
	}

	public Vector<?> getCurrentCompounds() {
		return currentCompounds;
	}

	public void setCurrentCompounds(Vector<?> currentCompounds) {
		this.currentCompounds = currentCompounds;
	}

	public Vector<?> getCurrentGenes() {
		return currentGenes;
	}

	public void setCurrentGenes(Vector<?> currentGenes) {
		this.currentGenes = currentGenes;
	}

	public QueryType getCurrentQueryType() {
		return currentQueryType;
	}

	public void setCurrentQueryType(QueryType currentQueryType) {
		this.currentQueryType = currentQueryType;
	}

	public QuerySubtype getCurrentQuerySubtype() {
		return currentQuerySubtype;
	}

	public void setCurrentQuerySubtype(QuerySubtype currentQuerySubtype) {
		this.currentQuerySubtype = currentQuerySubtype;
	}

	public Pathway getCurrentPathway() {
		return currentPathway;
	}

	public void setCurrentPathway(Pathway currentPathway) {
		this.currentPathway = currentPathway;
	}
}
