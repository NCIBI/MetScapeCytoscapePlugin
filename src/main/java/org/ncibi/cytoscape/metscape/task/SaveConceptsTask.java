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

import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.TextFile;
import org.ncibi.cytoscape.metscape.data.Concept;
import org.ncibi.cytoscape.metscape.data.ConceptMapping;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;

import cytoscape.CyNetwork;

public class SaveConceptsTask extends AbstractTask {

	private CyNetwork network;
	private File conceptFile;

	public static boolean save(CyNetwork network, File conceptFile) {
		SaveConceptsTask task = new SaveConceptsTask(network, conceptFile);
		return configureAndRunTask(task);
	}
	
	private SaveConceptsTask(CyNetwork network, File conceptFile) {
		this.network = network;
		this.conceptFile = conceptFile;
	}

	public String getTitle() {
		return "Output as File";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Building file...");
			DataFile base = new TextFile();
			base.setValue("Name",0,0);
			base.setValue("ConceptType",0,1);
			base.setValue("#Genes",0,2);
			base.setValue("Coeff",0,3);
			base.setValue("OddsRatio",0,4);
			base.setValue("P-Value",0,5);
			base.setValue("FDR",0,6);
			base.setValue("Direction",0,7);
			base.setValue("SigGenes",0,8);
			int row = 1;
			
			NetworkData networkData = MetScapePlugin.getPluginData().getNetworkData(Networks.getUUID(network));
			if(networkData == null) throw new Exception();
			
			ConceptMapping conceptMapping = networkData.getConceptMapping();
			for(Concept concept: conceptMapping.getAllConcepts()) {
				base.setValue(concept.getConceptName(),row,0);
				base.setValue(concept.getConceptType(),row,1);
				base.setValue(concept.getNumUniqueGenes(),row,2);
				base.setValue(concept.getCoeff(),row,3);
				base.setValue(concept.getOddsRatio(),row,4);
				base.setValue(concept.getPvalue(),row,5);
				base.setValue(concept.getFdr(),row,6);
				base.setValue(concept.getDirection(),row,7);
				String geneList = concept.getGeneIdsOrSymbols().toString();
				geneList = geneList.replace("[", "");
				geneList = geneList.replace("]", "");
				base.setValue(geneList,row,8);
				
				row++;
				
				if(interrupted)
					throw new Exception();
			}
			base.save(conceptFile);

			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "File output failed");
		}

	}
}
