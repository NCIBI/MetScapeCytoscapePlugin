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
package org.ncibi.cytoscape.metscape.file.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.ncibi.commons.file.DataFile;
import org.ncibi.commons.file.ExcelFile;

public class DataFileTest {
	File compoundFile = new File("src/test/resources/testData/rat/compounds_rat.xlsx");
	
	@Test
	public void readTest() throws Exception{
		DataFile base = new ExcelFile();
		base.open(compoundFile);
		Assert.assertTrue("Missing header value: " + base.getString(0,1), base.getString(0,1).trim().equals("LCR-10"));
		Assert.assertTrue("Missing symbol value: " + base.getString(1,0), base.getString(1,0).trim().equals("C00183"));
		Assert.assertTrue("Missing measurement value: " + base.getDouble(1,1), base.getDouble(1,1) > 0.5);
		System.out.println("Done with DataFile test");
	}
}
