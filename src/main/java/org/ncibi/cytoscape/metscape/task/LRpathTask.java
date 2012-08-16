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

import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.GeneMapping;
import org.ncibi.cytoscape.metscape.data.Organism;

public class LRpathTask extends AbstractTask {

	private GeneData geneData;
	private Organism organism;
	private ConceptData conceptData;
	private String pValueColumn;
	private String foldChangeColumn;

	public static ConceptData runUsingGeneData(GeneData geneData, Organism organism, 
			String pValueColumn, String foldChangeColumn) {
		LRpathTask task = new LRpathTask(geneData, organism, pValueColumn, foldChangeColumn);
		if(configureAndRunTask(task)) {
			return task.conceptData;
		}
		else return null;
	}

	private LRpathTask(GeneData geneData, Organism organism, String pValueColumn, String foldChangeColumn) {
		this.geneData = geneData;
		this.organism = organism;
		this.pValueColumn = pValueColumn;
		this.foldChangeColumn = foldChangeColumn;
	}

	public String getTitle() {
		return "Running LRpath";
	}

	public void run() {
		try {
			interrupted = false;
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Initializing...");

			if (geneData != null) {
				taskMonitor.setStatus("Getting gene mappings...");
				GeneMapping geneMapping = new GeneMapping(geneData,organism);
				geneData.setMapping(geneMapping);
				taskMonitor.setStatus("Generating concept data using LRpath...");
				conceptData = ConceptData.generateUsingLRpath(geneData, geneData.getMapping(),
						organism, pValueColumn, foldChangeColumn);
				if (conceptData == null
						|| interrupted) 
					throw new Exception("Generating concept data failed");
				else if(conceptData.isEmpty())
					throw new Exception("We were unable to find any significant concepts for the gene data and columns " +
					"that you selected.  Please make sure the correct data, organism, and columns are selected, and try again.");

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
