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

import java.util.List;

public class Concept implements Comparable<Concept> {
	private String conceptName;
	private String conceptType;
	private Integer numUniqueGenes;
	private Double coeff;
	private Double oddsRatio;
	private Double pvalue;
	private Double fdr;
	private String direction;
	private List<String> geneIdsOrSymbols;

	public Concept() {
	
	}
	
	public String getConceptName() {
		return conceptName;
	}
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	public String getConceptType() {
		return conceptType;
	}
	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}
	public Integer getNumUniqueGenes() {
		return numUniqueGenes;
	}
	public void setNumUniqueGenes(Integer numUniqueGenes) {
		this.numUniqueGenes = numUniqueGenes;
	}
	public Double getCoeff() {
		return coeff;
	}
	public void setCoeff(Double coeff) {
		this.coeff = coeff;
	}
	public Double getOddsRatio() {
		return oddsRatio;
	}
	public void setOddsRatio(Double oddsRatio) {
		this.oddsRatio = oddsRatio;
	}
	public Double getPvalue() {
		return pvalue;
	}
	public void setPvalue(Double pvalue) {
		this.pvalue = pvalue;
	}
	public Double getFdr() {
		return fdr;
	}
	public void setFdr(Double fdr) {
		this.fdr = fdr;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public List<String> getGeneIdsOrSymbols() {
		return geneIdsOrSymbols;
	}
	public void setGeneIdsOrSymbols(List<String> geneIds) {
		this.geneIdsOrSymbols = geneIds;
	}
	public int compareTo(Concept o) {
		return this.getConceptName().compareTo(o.getConceptName());
	}
	
	@Override
	public String toString(){
		return conceptName;
	}
}
