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
import org.ncibi.commons.lang.StrUtils;

public class CompoundData implements MultiColumnData {
	
	private String[] columns = new String[0];
	private Map<String, Boolean> columnIsSigned = new HashMap<String, Boolean>();
	private List<String> nameOrId = new ArrayList<String>();
	private List<Double[]> data = new ArrayList<Double[]>();
	private String name = "(none)";
	private CompoundMapping mapping = new CompoundMapping();
	
	public static CompoundData parse(File compoundFile) {
		CompoundData ret = null;
		try {
			CompoundData ex = new CompoundData();
			DataFile base;
			if(compoundFile.getName().endsWith(".xls") || compoundFile.getName().endsWith(".xlsx"))
				base = new ExcelFile(compoundFile);
			else
				base = new TextFile(compoundFile);
			ex.name = FilenameUtils.removeExtension(compoundFile.getName());
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
			if(endCol > 0) {
				ex.columns = new String[endCol-startCol+1];
				for (int col = startCol; col <= endCol; col++){
					String s = base.getString(0, col);
					ex.columns[col - startCol] = s.trim();
					ex.columnIsSigned.put(s.trim(), false);
				}
			}
			// get remaining rows, the data
			startRow ++;
			for (int row = startRow; row < (endRow + 1); row++){
				String nameOrId = base.getString(row, 0);
				if (nameOrId == null) continue;
				nameOrId = nameOrId.trim();
				Double[] data = null;
				if(startCol > 0) {
					data = new Double[ex.columns.length];
					for (int col = 0; col <= endCol-startCol; col++){
						data[col] = base.getDouble(row, col+startCol);
						if(data[col] != null && data[col] < 0) {
							ex.columnIsSigned.put(ex.columns[col-1], true);
						}
					}
				}
				ex.addRecord(nameOrId,data);
			}
			ret = ex;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return ret;
	}
	
	public static CompoundData parse(String compoundString) {
		CompoundData ret = null;
		try {
			CompoundData ex = new CompoundData();
			List<String> compounds = StrUtils.splitCommaOrSpaceSeparatedString(compoundString);
			for(String cid: compounds) {
				ex.addRecord(cid,null);
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
	
	
	private void addRecord(String nameOrCid, Double[] data) {
		this.nameOrId.add(nameOrCid);
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

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullyQualifiedName() {
		return "Compound." + name;
	}
	
	public List<String> getNameOrId() {
		return nameOrId;
	}

	public void setNameOrId(List<String> nameOrId) {
		this.nameOrId = nameOrId;
	}

	public List<Double[]> getData() {
		return data;
	}

	public void setData(List<Double[]> data) {
		this.data = data;
	}
	
	public CompoundMapping getMapping() {
		return mapping;
	}

	public void setMapping(CompoundMapping mapping) {
		this.mapping = mapping;
	}

	public boolean isEmpty(){
		return nameOrId.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnIsSigned == null) ? 0 : columnIsSigned.hashCode());
		result = prime * result + Arrays.hashCode(columns);
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nameOrId == null) ? 0 : nameOrId.hashCode());
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
		CompoundData other = (CompoundData) obj;
		if (columnIsSigned == null) {
			if (other.columnIsSigned != null)
				return false;
		} else if (!columnIsSigned.equals(other.columnIsSigned))
			return false;
		if (!Arrays.equals(columns, other.columns))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameOrId == null) {
			if (other.nameOrId != null)
				return false;
		} else if (!nameOrId.equals(other.nameOrId))
			return false;
		return true;
	}

	public String toString() {
		return name;
	}
}
