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

public class DataParameters {
	
	private String pvalueColumn = null;
	private Double pvalueThreshold = null;
	private String foldChangeColumn = null;
	private Double foldChangeThreshold = null;
	private Double foldChangeUpThreshold = null;
	private Double foldChangeDownThreshold = null;
	
	public DataParameters() {}
	
	public DataParameters(DataParameters clone) {
		this.pvalueColumn = clone.pvalueColumn;
		this.pvalueThreshold = clone.pvalueThreshold;
		this.foldChangeColumn = clone.foldChangeColumn;
		this.foldChangeThreshold = clone.foldChangeThreshold;
		this.foldChangeUpThreshold = clone.foldChangeUpThreshold;
		this.foldChangeDownThreshold = clone.foldChangeDownThreshold;
	}
	
	public String getPvalueColumn() {
		return pvalueColumn;
	}
	
	public void setPvalueColumn(String pvalueColumn) {
		this.pvalueColumn = pvalueColumn;
	}
	
	public Double getPvalueThreshold() {
		return pvalueThreshold;
	}
	
	public void setPvalueThreshold(Double pvalueThreshold) {
		this.pvalueThreshold = pvalueThreshold;
	}
	
	public String getFoldChangeColumn() {
		return foldChangeColumn;
	}
	
	public void setFoldChangeColumn(String foldChangeColumn) {
		this.foldChangeColumn = foldChangeColumn;
	}
	
	public Double getFoldChangeThreshold() {
		return foldChangeThreshold;
	}
	
	public void setFoldChangeThreshold(Double foldChangeThreshold) {
		this.foldChangeThreshold = foldChangeThreshold;
	}

	public Double getFoldChangeUpThreshold() {
		return foldChangeUpThreshold;
	}

	public void setFoldChangeUpThreshold(Double foldChangeUpThreshold) {
		this.foldChangeUpThreshold = foldChangeUpThreshold;
	}

	public Double getFoldChangeDownThreshold() {
		return foldChangeDownThreshold;
	}

	public void setFoldChangeDownThreshold(Double foldChangeDownThreshold) {
		this.foldChangeDownThreshold = foldChangeDownThreshold;
	}

}
