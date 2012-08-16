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

import java.util.HashSet;
import java.util.Set;

import org.ncibi.metab.network.NetworkType;

public class NetworkData {
	private CompoundMapping compoundMapping = new CompoundMapping();
	private GeneMapping geneMapping = new GeneMapping();
	private ConceptMapping conceptMapping = new ConceptMapping();
	private Organism organism = Organism.HUMAN;
	private DataParameters compoundParameters = new DataParameters();
	private DataParameters geneParameters = new DataParameters();
	private Set<String> cids = new HashSet<String>();
	private Set<Integer> geneids = new HashSet<Integer>();
	private NetworkType networkType = NetworkType.CREG;
	
	public NetworkData() {}
	
	public NetworkData (NetworkData clone) {
		this.compoundMapping = clone.compoundMapping;
		this.geneMapping = clone.geneMapping;
		this.conceptMapping = clone.conceptMapping;
		this.organism = clone.organism;
		this.networkType = clone.networkType;
		this.compoundParameters = new DataParameters(clone.compoundParameters);
		this.geneParameters = new DataParameters(clone.geneParameters);
		this.cids = new HashSet<String>(clone.cids);
		this.geneids = new HashSet<Integer>(clone.geneids);
	}

	public CompoundMapping getCompoundMapping() {
		return compoundMapping;
	}

	public void setCompoundMapping(CompoundMapping compoundMapping) {
		this.compoundMapping = compoundMapping;
	}

	public GeneMapping getGeneMapping() {
		return geneMapping;
	}

	public void setGeneMapping(GeneMapping geneMapping) {
		this.geneMapping = geneMapping;
	}

	public ConceptMapping getConceptMapping() {
		return conceptMapping;
	}

	public void setConceptMapping(ConceptMapping conceptMapping) {
		this.conceptMapping = conceptMapping;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}
	
	public NetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}

	public DataParameters getCompoundParameters() {
		return compoundParameters;
	}

	public void setCompoundParameters(DataParameters compoundParameters) {
		this.compoundParameters = compoundParameters;
	}

	public DataParameters getGeneParameters() {
		return geneParameters;
	}

	public void setGeneParameters(DataParameters geneParameters) {
		this.geneParameters = geneParameters;
	}

	public Set<String> getCids() {
		return cids;
	}

	public void setCids(Set<String> cids) {
		this.cids = cids;
	}

	public Set<Integer> getGeneids() {
		return geneids;
	}

	public void setGeneids(Set<Integer> geneids) {
		this.geneids = geneids;
	}

}
