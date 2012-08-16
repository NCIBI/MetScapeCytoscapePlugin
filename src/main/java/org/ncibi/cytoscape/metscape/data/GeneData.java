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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.ExcelFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.commons.lang.NumUtils;

public class GeneData implements MultiColumnData {
	
	private String[] columns = new String[0];
	private Map<String, Boolean> columnIsSigned = new HashMap<String, Boolean>();
	private List<String> symbolOrId = new ArrayList<String>();
	private List<Double[]> data = new ArrayList<Double[]>();
	private String name = "(none)";
	private GeneMapping mapping = new GeneMapping();
	
	public static GeneData parse(File geneFile) {
		
		GeneData ret = null;
		try {
			GeneData ex = new GeneData();
			DataFile base;
			if(geneFile.getName().endsWith(".xls") || geneFile.getName().endsWith(".xlsx") )
				base = new ExcelFile(geneFile);
			else
				base = new TextFile(geneFile);
			ex.name = FilenameUtils.removeExtension(geneFile.getName());
			int startRow = base.getStartRowIndex();
			int endRow = base.getEndRowIndex();
			int startCol = 0;
			for(int i=1; i<=base.getEndColIndex(0); i++)  {
				if(isDataColumn(i, base)) {
					startCol = i;
					break;
				}
			}
			int endCol = startCol;
			if(startCol > 0) {
				for(int i=startCol; i<=base.getEndColIndex(0); i++) {
					if(base.getString(0, i+1) == null) break;
					endCol++;
				}
			}
			if (endCol > 0) {
				ex.columns = new String[endCol-startCol+1];
				for (int col = startCol; col <= endCol; col++){
					String s = base.getString(0, col);
					ex.columns[col - startCol] = s.trim();
					ex.columnIsSigned.put(s.trim(), false);
				}
			}
			startRow ++;
			for (int row = startRow; row < (endRow + 1); row++){
				String geneId = base.getString(row, 0);
				if (geneId == null) continue;
				geneId = makeIdValue(geneId);
				Double[] data = null;
				if(startCol > 0) {
					data = new Double[ex.columns.length];
					for (int col = 0; col <= endCol-startCol; col++){
						data[col] = base.getDouble(row, col+startCol);
						if(data[col] != null && data[col] < 0) {
							ex.columnIsSigned.put(ex.columns[col], true);
						}
					}
				}
				ex.addRecord(geneId, data);
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
		return string.trim();
	}
	
	public static GeneData parse(String geneString) {
		GeneData ret = null;
		try {
			GeneData ex = new GeneData();
			String[] genes = geneString.split(",");
			if(genes == null) throw new Exception();
			if (genes.length == 0) throw new Exception();
			for(String geneId: genes) {
				ex.addRecord(geneId,null);
			}
			ret = ex;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ret;
	}
	
	private static boolean isDataColumn(int col, DataFile base) {
		int numCount = 0;
		for (int row = 1; row<=base.getEndRowIndex(); row++) {
			if(base.getDouble(row, col) != null) numCount++;
		}
		return (double)numCount/(double)base.getEndRowIndex() > 0.5;
	}
	
	private void addRecord(String geneIdOrSymbol, Double[] data) {
		this.symbolOrId.add(geneIdOrSymbol);
		this.data.add(data);
	}

	public String[] getColumns(){
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getFullyQualifiedName() {
		return "Gene." + name;
	}
	
	public List<String> getSymbolOrId() {
		return symbolOrId;
	}

	public void setSymbolOrId(List<String> symbolOrId) {
		this.symbolOrId = symbolOrId;
	}

	public List<Double[]> getData() {
		return data;
	}

	public void setData(List<Double[]> data) {
		this.data = data;
	}

	
	public GeneMapping getMapping() {
		return mapping;
	}

	public void setMapping(GeneMapping mapping) {
		this.mapping = mapping;
	}

	public boolean isEmpty(){
		return symbolOrId.isEmpty();
	}
	
	public boolean equals(Object o) {
		if(o instanceof GeneData)
		{
			GeneData other = (GeneData) o;
			if(Arrays.equals(columns,other.getColumns())
					&& symbolOrId.equals(other.getSymbolOrId()) 
					&& data.equals(other.getData()) 
					&& name.equals(other.getName()))
				return true;
		}
		return false;
	}
	
	public String toString() {
		return name;
	}

}
