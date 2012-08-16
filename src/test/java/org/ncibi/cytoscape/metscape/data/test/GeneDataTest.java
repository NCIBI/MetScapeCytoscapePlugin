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
import java.util.Map;
import java.util.SortedSet;

import junit.framework.Assert;

import org.junit.Test;
import org.ncibi.cytoscape.metscape.data.GeneData;
import org.ncibi.cytoscape.metscape.data.GeneMapping;
import org.ncibi.cytoscape.metscape.data.Organism;

public class GeneDataTest {
	
	File humanGeneSymbolFile = new File("src/test/resources/testData/Human/gene_symbol_file_human.xlsx");
	File humanGeneIdFile =  new File("src/test/resources/testData/Human/gene_file_human.xlsx");
	File ratGeneIdFile =  new File("src/test/resources/testData/rat/genes_rat.xlsx");
	
	@Test
	public void test1(){
		GeneData testData = GeneData.parse("NAT1,NAT2,AANAT,AARS,ABAT,PWP1");
		GeneMapping mapping = new GeneMapping(testData,Organism.HUMAN);
		
		Assert.assertNotNull(mapping.getSymbolMap().keySet());
		Assert.assertNotNull(mapping.getSymbolMap().containsKey("PWP1"));
	}

	@Test
	public void fileInputTestHumanGeneSymbol(){
		GeneData testData = GeneData.parse(humanGeneSymbolFile);
		GeneMapping mapping = new GeneMapping(testData,Organism.HUMAN);

		Assert.assertNotNull(mapping.getSymbolMap().keySet());
		Assert.assertNotNull(mapping.getSymbolMap().containsKey("PWP1"));
		printMaps(mapping);
	}
	
	@Test
	public void fileInputTestHumanGeneId(){
		GeneData testData = GeneData.parse(humanGeneIdFile);
		GeneMapping mapping = new GeneMapping(testData,Organism.HUMAN);

		Assert.assertNotNull(mapping.getSymbolMap().keySet());
		Assert.assertNotNull(mapping.getSymbolMap().containsKey("PWP1"));
		Assert.assertNotNull(mapping.idSet());
		Assert.assertTrue(!mapping.idSet().isEmpty());
		printMaps(mapping);
	}
	
	@Test
	public void fileInputTestRatGeneId(){
		GeneData testData = GeneData.parse(ratGeneIdFile);
		GeneMapping mapping = new GeneMapping(testData,Organism.RAT);

		Assert.assertNotNull(mapping.getSymbolMap().keySet());
		Assert.assertNotNull(mapping.getSymbolMap().containsKey("PWP1"));
		printMaps(mapping);
	}

	private void printMaps(GeneMapping mapping) {
		Map<Integer,String[]> symbolMap = mapping.getSymbolMap();
		Map<Integer,Double[]> dataMap = mapping.getDataMap();
		SortedSet<String> missing = mapping.getMissingGenes();
		
		System.out.println("-----------------");
		for (Integer key : symbolMap.keySet())
			System.out.println(key + "->" + symbolMap.get(key));
		for (Integer key : dataMap.keySet())
			System.out.println(key + "->" + dataMap.get(key)[0]);
		for (String s: missing)
			System.out.println(s);
	}
}
