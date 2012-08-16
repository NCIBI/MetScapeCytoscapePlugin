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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ncibi.cytoscape.metscape.data.CompoundData;
import org.ncibi.cytoscape.metscape.data.CompoundMapping;
import org.ncibi.cytoscape.metscape.data.CompoundSelector;
import org.ncibi.metab.name.MetabolicName;

public class CompoundDataTest {
	
	private File compoundFile = new File("src/test/resources/testData/rat/compounds_rat.xlsx");
	private final static int EXPECTED_SIZE = 35;
	private final static String TEST_SYMBOL = "C00311";
	
	@Test
	public void testParse(){
		CompoundData results = parseConcentration();
		Assert.assertTrue("Results from parsing the file are null", results != null);
 		System.out.println("Done with testParse");
	}
	
	
	@Test
	public void testMapping(){
		CompoundData data = parseConcentration();
		CompoundMapping results = new CompoundMapping(data,makeTestSelector());
		Assert.assertTrue("Results from parsing the file are null", results != null);
		Assert.assertTrue("No Symbols in symbol set",results.idSet().size() > 0);
		System.out.println("Number of symbols: " + results.idSet().size());
		Assert.assertTrue("Expected " + EXPECTED_SIZE + " symbols, got " + results.idSet().size(), results.idSet().size() == EXPECTED_SIZE);
		for (String s: results.idSet()) {
			System.out.println(s);
		}
 		Assert.assertTrue("Missing test symbol," + TEST_SYMBOL, results.idSet().contains(TEST_SYMBOL));
 		System.out.println("Done with testMapping");
	}

	private CompoundSelector makeTestSelector() {
		return new CompoundSelector(){
			@Override
			public Map<String, MetabolicName> select(
					Map<String, List<MetabolicName>> input) {
				Map<String, MetabolicName> ret = new HashMap<String, MetabolicName>();
				for (String key: input.keySet()){
					List<MetabolicName> names = input.get(key);
					if (names.size() > 0) {
						ret.put(key,names.get(0));
					}
				}
				return ret;
			}};
	}
	
	private void m(int indent, String s){
		System.out.println(s);
	}
	
	private CompoundData parseConcentration() {
		m(0,"Parse metabolomics concentration data");
		if (compoundFile == null) {
			m(1,"Parsing: No Compound Concentration file; aborting step.");
			return null;
		}
		CompoundData ret = CompoundData.parse(compoundFile);
		if (ret == null)
			m(1,"parsed concentration failed: " + compoundFile.getAbsolutePath());
		else
			m(1,"parsed concentration file: " + compoundFile.getAbsolutePath());
		return ret;
	}


}
