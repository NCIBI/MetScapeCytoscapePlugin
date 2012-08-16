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

import junit.framework.Assert;

import org.junit.Test;
import org.ncibi.cytoscape.metscape.data.ConceptData;
import org.ncibi.cytoscape.metscape.data.Concept;

public class ParseConceptFile {
	
	public static File humanTestDir = new File("src/test/resources/testData/human/");
	public static File humanConceptFileWithIds = new File(humanTestDir, "p_concepts_human.xlsx");
	public static File humanConceptFileWithSymbols = new File(humanTestDir, "p_concepts_symbol_human.xlsx");
	
	public static String conceptCheck = "Oxidative phosphorylation";

	@Test
	public void parseHumanConceptFileWithIds(){
		ConceptData conceptData = ConceptData.parse(humanConceptFileWithIds);

		Concept found = null;
		for (Concept c: conceptData.getAllConcepts()){
			if ((c.getConceptName() != null) && (c.getConceptName().equals(conceptCheck))) found = c;
		}
		Assert.assertNotNull(found);

		for (String s: found.getGeneIdsOrSymbols()){
			System.out.println(s);		
		}
	}
	
	@Test
	public void parseHumanConceptFileWithSymbols(){
		ConceptData conceptData = ConceptData.parse(humanConceptFileWithSymbols);

		Concept found = null;
		for (Concept c: conceptData.getAllConcepts()){
			if ((c.getConceptName() != null) && (c.getConceptName().equals(conceptCheck))) found = c;
		}
		Assert.assertNotNull(found);

		for (String s: found.getGeneIdsOrSymbols()){
			System.out.println(s);		
		}
	}
	
}
