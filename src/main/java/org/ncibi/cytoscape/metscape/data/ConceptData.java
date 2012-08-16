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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.ExcelFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.commons.lang.NumUtils;
import org.ncibi.commons.lang.StrUtils;
import org.ncibi.cytoscape.metscape.lrpath.LRPathDatabase;
import org.ncibi.cytoscape.metscape.lrpath.LRPathRequestSpecies;
import org.ncibi.cytoscape.metscape.lrpath.LRPathServerBridge;
import org.ncibi.lrpath.LRPathArguments;
import org.ncibi.lrpath.LRPathResult;

public class ConceptData {
	
	private List<Concept> allConcepts = new ArrayList<Concept>();
	private String name = "(none)";
	private ConceptMapping mapping = new ConceptMapping();
	
	public static ConceptData parse(File conceptFile) {
		
		ConceptData ret = null;
		try {
			// method to get file; method to built ret; method to process record
			ConceptData ex = new ConceptData();
			DataFile base;
			if(conceptFile.getName().endsWith(".xls") || conceptFile.getName().endsWith(".xlsx"))
				base = new ExcelFile(conceptFile);
			else
				base = new TextFile(conceptFile);
			ex.name = FilenameUtils.removeExtension(conceptFile.getName());
			int startRow = base.getStartRowIndex();
			int endRow = base.getEndRowIndex();
			// first row - labels; ignore for now
			startRow ++;
			for (int row = startRow; row < (endRow + 1); row++){
				if(base.getString(row, 0) == null || base.getString(row, 0).equals("") ||
						StrUtils.splitCommaOrSpaceSeparatedString(base.getString(row, 8)) == null ||
						StrUtils.splitCommaOrSpaceSeparatedString(base.getString(row, 8)).isEmpty() ||
						base.getString(row, 8).trim().equals("")) continue;
				Concept rec = ex.makeRecord();
				
				rec.setConceptName(base.getString(row, 0));
				rec.setConceptType(base.getString(row, 1));
				rec.setNumUniqueGenes(base.getInteger(row,2));
				rec.setCoeff(base.getDouble(row, 3));
				rec.setOddsRatio(base.getDouble(row, 4));
				rec.setPvalue(base.getDouble(row, 5));
				rec.setFdr(base.getDouble(row, 6));
				rec.setDirection(base.getString(row, 7));
				rec.setGeneIdsOrSymbols(StrUtils.splitCommaOrSpaceSeparatedString(makeIdValue(base.getString(row, 8))));
				ex.addRecord(rec);
			}
			ret = ex;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ret;
	}
	
	public static ConceptData generateUsingLRpath(GeneData geneData, GeneMapping geneMapping, Organism organism, String pValueColumn, String foldChangeColumn) {
		ConceptData ret = null;
		try {
			
			LRPathDatabase database = LRPathDatabase.KEGG;
			LRPathRequestSpecies species = LRPathRequestSpecies.Human;
			if(organism == Organism.HUMAN)
				species = LRPathRequestSpecies.Human;
			else if(organism == Organism.RAT)
				species = LRPathRequestSpecies.Rat;
			else if(organism == Organism.MOUSE)
				species = LRPathRequestSpecies.Mouse;
			int pValueColumnIndex = -1;
			int foldChangeColumnIndex = -1;
			for(int i=0; i<geneData.getColumns().length; i++) {
				if(geneData.getColumns()[i].equals(pValueColumn))
					pValueColumnIndex = i;
				if(geneData.getColumns()[i].equals(foldChangeColumn))
					foldChangeColumnIndex = i;
				if(pValueColumnIndex != -1 && foldChangeColumnIndex != -1)
					break;
			}
			int[] geneidsTemp = new int[geneMapping.idSet().size()];
			double[] sigvalsTemp = new double[geneMapping.idSet().size()];
			double[] directionsTemp = new double[geneMapping.idSet().size()];
			
			int index = 0;
			// fold change is direction, and pvalue is sigvals
			if(!geneData.getColumnIsSigned().get(foldChangeColumn)) {
				for(Integer geneId: geneMapping.idSet()) {
					if((geneMapping.getData(geneId)[foldChangeColumnIndex] != null) && 
							(geneMapping.getData(geneId)[pValueColumnIndex] != null)){
						geneidsTemp[index] = geneId;
						sigvalsTemp[index] = geneMapping.getData(geneId)[pValueColumnIndex];
						directionsTemp[index] = Math.log(geneMapping.getData(geneId)[foldChangeColumnIndex]);
						index++;
					}
				}
			}
			else {
				for(Integer geneId: geneMapping.idSet()) {
					if((geneMapping.getData(geneId)[foldChangeColumnIndex] != null) && 
							(geneMapping.getData(geneId)[pValueColumnIndex] != null)) {
						geneidsTemp[index] = geneId;
						sigvalsTemp[index] = geneMapping.getData(geneId)[pValueColumnIndex];
						directionsTemp[index] = geneMapping.getData(geneId)[foldChangeColumnIndex];
						index++;
					}
				}
			}
			
			if(index == 0) //empty case
				return new ConceptData();
			
			int[] geneids = new int[index];
			double[] sigvals = new double[index];
			double[] directions = new double[index];

			System.arraycopy(geneidsTemp, 0, geneids, 0, index);
			System.arraycopy(sigvalsTemp, 0, sigvals, 0, index);
			System.arraycopy(directionsTemp, 0, directions, 0, index);
			
	        LRPathArguments args = new LRPathArguments();
	        args.setDatabase(database.toString());
	        args.setSpecies(species.toString());
	        args.setMing(5);
	        args.setGeneids(geneids);
	        args.setSigvals(sigvals);
	        args.setDirection(directions);
	        			
			LRPathServerBridge lrPathConnection = new LRPathServerBridge();
			lrPathConnection.setDebugTrace(true);
	        List<LRPathResult> list = lrPathConnection.requestKeggPathwaysFromLRPath(args);
			
			ConceptData ex = new ConceptData();
			ex.name = "LRpath Results for " + geneData.getName();
			for (LRPathResult record : list){
				if(record.getPValue() > 0.05 || record.getConceptName() == null || record.getSigGenes().size() <= 0 ||
						(record.getSigGenes().size() == 1 && NumUtils.toInteger(record.getSigGenes().get(0)) == null))
					continue;
				Concept rec = ex.makeRecord();
				rec.setConceptName(record.getConceptName());
				rec.setConceptType(record.getConceptType());
				rec.setNumUniqueGenes(record.getNumUniqueGenes());
				rec.setCoeff(record.getCoeff());
				rec.setOddsRatio(record.getOddsRatio());
				rec.setPvalue(record.getPValue());
				rec.setFdr(record.getFdr());
				rec.setDirection(record.getOddsRatio()>=1?"up":"down");
				List<String> geneIdList = new ArrayList<String>();
				for(String geneId: record.getSigGenes()) {
					if(geneId != null) {
						String idString = geneId.trim();
						if(idString != null)
							geneIdList.add(idString);
					}
				}
				rec.setGeneIdsOrSymbols(geneIdList);
				ex.addRecord(rec);
			}
			ret = ex;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ret;
	}
	
	private static String makeIdValue(String string) {
		Integer probe = NumUtils.toInteger(string);
		if (probe != null) return probe.toString();
		return string;
	}

	private Concept makeRecord() {
		return new Concept();
	}

	private void addRecord(Concept rec) {
		allConcepts.add(rec);
	}

	public List<Concept> getAllConcepts() {
		return allConcepts;
	}

	public void setAllConcepts(List<Concept> allConcepts) {
		this.allConcepts = allConcepts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ConceptMapping getMapping() {
		return mapping;
	}

	public void setMapping(ConceptMapping mapping) {
		this.mapping = mapping;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((allConcepts == null) ? 0 : allConcepts.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConceptData other = (ConceptData) obj;
		if (allConcepts == null) {
			if (other.allConcepts != null)
				return false;
		} else if (!allConcepts.equals(other.allConcepts))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public boolean isEmpty(){
		return allConcepts.isEmpty();
	}
	
	public String toString() {
		return name;
	}
	
}
