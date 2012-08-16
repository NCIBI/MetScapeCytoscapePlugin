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

import java.io.File;

import org.ncibi.cytoscape.metscape.data.ConceptData;

public class ImportConceptFileTask extends AbstractTask {

	private File conceptFile;
	private ConceptData conceptData;

	public static ConceptData importFrom(File conceptFile) {
		ImportConceptFileTask task = new ImportConceptFileTask(conceptFile);
		if(configureAndRunTask(task)) {
			return task.conceptData;
		}
		else return null;
	}

	private ImportConceptFileTask(File conceptFile) {
		this.conceptFile = conceptFile;
	}

	public String getTitle() {
		return "Import Concept File";
	}

	public void run() {
		try {
			interrupted = false;
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Initializing...");

			if (conceptFile != null) {
					taskMonitor.setStatus("Importing concept file...");
					conceptData = ConceptData.parse(conceptFile);
					if (conceptData == null
							|| conceptData.getAllConcepts().isEmpty()
							|| interrupted) 
						throw new Exception("Importing concept file failed. Either the file could not be read, or it may not be in the correct format.");
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
