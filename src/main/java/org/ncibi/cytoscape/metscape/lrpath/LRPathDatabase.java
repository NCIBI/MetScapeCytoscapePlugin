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
package org.ncibi.cytoscape.metscape.lrpath;

public enum LRPathDatabase {

	KEGG("KEGG Pathway"), GO("GO"), GOBiologicalProcess("GO Biological Process"),
	GOCellularComponent("GO Cellular Component"), GOMolecularFunction("GO Molecular Function"),
	GO_OMIM("OMIM"), DrugBank("Drug Bank"), miRBase("miRBase"), Panther("Panther Pathway"),
	BIOCARTA("Biocarta Pathway"), Metabolite("Metabolite"), MeSH("MeSH"), Pfam("pFAM"),
	MiMI("Protein Interaction (MiMI)"),Cytoband("Cytoband");
	
	private final String databaseNameForLRPath;
	
	private LRPathDatabase(String databaseNameForLRPath){
		this.databaseNameForLRPath = databaseNameForLRPath;
	}
	
	@Override
	public String toString(){
		return databaseNameForLRPath;
	}
}

