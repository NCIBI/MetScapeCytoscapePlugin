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

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.metscape.data.CompoundMapping;
import org.ncibi.cytoscape.metscape.data.CompoundSelector;
import org.ncibi.cytoscape.metscape.data.Concept;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.ConceptMapping;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.GeneMapping;
import org.ncibi.cytoscape.metscape.data.IDComparator;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginData;
import org.ncibi.cytoscape.metscape.ui.CompoundNameSelectionDialog;
import org.ncibi.cytoscape.metscape.ui.MissingDataDialog;
import org.ncibi.metab.name.MetabolicName;

import cytoscape.Cytoscape;
import cytoscape.task.Task;

public class GetDataMappingsTask extends AbstractTask {

	private CompoundData compoundData;
	private GeneData geneData;
	private ConceptData conceptData;
	private Organism organism;

	public static boolean getMappings(CompoundData compoundData, GeneData geneData,
			ConceptData conceptData, Organism organism) {
		GetDataMappingsTask task = new GetDataMappingsTask(compoundData, geneData, 
				conceptData, organism);
		return configureAndRunTask(task);
	}
	
	private GetDataMappingsTask(CompoundData compoundData, GeneData geneData, 
			ConceptData conceptData, Organism organism) {
		this.compoundData = compoundData;
		this.geneData = geneData;
		this.conceptData = conceptData;
		this.organism = organism;
	}

	public String getTitle() {
		return "Get Data Mappings";
	}

	public void run() {
		try {
			interrupted = false;
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Initializing...");
						
			Map<String, String> mappedCompounds = new TreeMap<String,String>();
			Map<Integer, String[]> mappedGenes = new TreeMap<Integer, String[]>();
	
			SortedSet<String> missingCompounds = new TreeSet<String>(new IDComparator());
			SortedSet<String> missingGenes = new TreeSet<String>(new IDComparator());
			SortedSet<Concept> missingConcepts = new TreeSet<Concept>();
			boolean cmpdNames = false;
						
			if(!compoundData.isEmpty()){
				taskMonitor.setStatus("Getting compound mappings from server...");
				CompoundSelector compoundSelector = makeCompoundSelector(compoundData,this);
				CompoundMapping compoundMapping = new CompoundMapping(compoundData,compoundSelector);
				compoundData.setMapping(compoundMapping);
				mappedCompounds = compoundMapping.getSymbolMap();
				missingCompounds = compoundMapping.getMissingCompounds();
				cmpdNames = compoundMapping.isCmpdNames();
			}
			if (interrupted) return;
			if(!geneData.isEmpty()){
				taskMonitor.setStatus("Getting gene mappings from server...");
				GeneMapping geneMapping = new GeneMapping(geneData,organism);
				geneData.setMapping(geneMapping);
			}
			if (interrupted) return;
			if(!conceptData.isEmpty()) {
				taskMonitor.setStatus("Getting concept mappings from server...");
				ConceptMapping conceptMapping = new ConceptMapping(conceptData, organism);
				conceptData.setMapping(conceptMapping);
				mappedGenes = conceptMapping.getSymbolMap();
				missingGenes = conceptMapping.getMissingGenes();
				missingConcepts = conceptMapping.getMissingConcepts();
			}
			else if(!geneData.isEmpty()) {
				mappedGenes = geneData.getMapping().getSymbolMap();
				missingGenes = geneData.getMapping().getMissingGenes();
			}
			if (interrupted) return;
			
			
			if( (mappedCompounds.isEmpty() && !compoundData.isEmpty()) ||
					(mappedGenes.isEmpty() && 
							!geneData.isEmpty() && !conceptData.isEmpty() )) {
					throw new Exception("We were unable to map any of the genes and/or compounds " +
							"that you provided. Please make sure that the correct organism is selected " +
							"and the data is in the correct format, and try again.");
			}
			
			PluginData session = MetScapePlugin.getPluginData();
			session.setDefaultCompounds(mappedCompounds);
			session.setDefaultGenes(mappedGenes);
			
			if(!missingCompounds.isEmpty() ||
					!missingGenes.isEmpty() ||
					!missingConcepts.isEmpty()) {
				MissingDataDialog missingDataDialog = new MissingDataDialog
					(Cytoscape.getDesktop(),missingCompounds,missingGenes,missingConcepts, cmpdNames);
				missingDataDialog.setVisible(true);
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

	private CompoundSelector makeCompoundSelector(
			final CompoundData compoundData, final Task task) {
		return new CompoundSelector() {
			@Override
			public Map<String, MetabolicName> select(
					Map<String, List<MetabolicName>> compoundNameMap) {
				return CompoundNameSelectionDialog
						.select(Cytoscape.getDesktop(),task,compoundData,compoundNameMap);
			}
		};
	}

}
