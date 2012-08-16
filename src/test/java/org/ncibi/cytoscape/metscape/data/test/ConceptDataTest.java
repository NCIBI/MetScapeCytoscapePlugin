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
package org.ncibi.cytoscape.metscape.data.test;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.Concept;
import org.ncibi.cytoscape.metscape.data.ConceptMapping;
import org.ncibi.cytoscape.metscape.data.Organism;

public class ConceptDataTest {

	private static final int CONCEPT_COUNT = 131;
	private static final int GENE_COUNT = 92;
	
	private File conceptFile = new File("src/test/resources/testData/rat/p_concepts_rat.xlsx");

	@Test
	public void testParse() throws Exception{
		ConceptData data = parseConcepts();
		Assert.assertTrue("Results from parsing the file are null", data != null);
		ConceptMapping mapping = new ConceptMapping(data,Organism.RAT);

		List<Concept> concepts = mapping.getAllConcepts();
		Set<Integer> geneSet = mapping.idSet();
		Assert.assertTrue("Got " + concepts.size() + " concepts, expected " + CONCEPT_COUNT, concepts.size() == CONCEPT_COUNT);
		Assert.assertTrue("Got " + geneSet.size() + " genes, expected " + GENE_COUNT, geneSet.size() == GENE_COUNT);
		System.out.println("Done with testParse");
	}
	
	private ConceptData parseConcepts() {
		m("Parse concept gene expression data");
		if (conceptFile == null) {
			m("Parsing: No Concept file; aborting step.");
			return null;
		}
		ConceptData ret = ConceptData.parse(conceptFile);
		if (ret == null)
			m("parse failed: " + conceptFile.getAbsolutePath());
		else
			m("parsed file: " + conceptFile.getAbsolutePath());
		return ret;
	}

	private void m(String s){
		System.out.println(s);
	}
}
