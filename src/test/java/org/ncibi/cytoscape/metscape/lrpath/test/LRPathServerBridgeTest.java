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
package org.ncibi.cytoscape.metscape.lrpath.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.ncibi.cytoscape.metscape.lrpath.LRPathDatabase;
import org.ncibi.cytoscape.metscape.lrpath.LRPathRequestSpecies;
import org.ncibi.cytoscape.metscape.lrpath.LRPathServerBridge;
import org.ncibi.lrpath.LRPathArguments;
import org.ncibi.lrpath.LRPathResult;
import org.ncibi.task.TaskStatus;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;
import org.ncibi.ws.client.NcibiLRPathService;
import org.ncibi.ws.request.RequestStatus;

public class LRPathServerBridgeTest {

	private static final String TEST_FILE_PATH = "src/test/resources/testData/GeneHumanLog.txt";
	private static final boolean dump = false;
//	private static final int TEST_ITERATION_COUNT = 5;
	private static final int TEST_ITERATION_COUNT = 1;

    @Test
    public void testSubmitLRPathRequest()
    {
        LRPathArguments data = new LRPathArguments();
        int[] geneids = { 780, 5982, 3310 };
        double[] sigvals = { 0.004859222, 0.275428947, 0.940720196 };
        double[] direction = { 0.414580082, -0.176934427, 0.01006101 };
        data.setGeneids(geneids);
        data.setSigvals(sigvals);
        data.setDirection(direction);
        data.setMing(1);

        for (int i = 0; i < TEST_ITERATION_COUNT; i++)
        {
            NcibiLRPathService client = new NcibiLRPathService(HttpRequestType.POST);
            System.out.println("\n\n submitting request(" + i + ")...\n\n");
            Response<String> response = client.submitLRPathRequest(data);
            System.out.println(response);
            if (! response.isSuccess())
            {
                try
                {
                    System.out.println("Couldn't submit request, sleeping and trying again.");
                    Thread.sleep(10000);
                    continue;
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            Response<RequestStatus<List<LRPathResult>>> r = client.lrpathStatus(response
                        .getResponseValue());
            
            System.out.println(response.getResponseValue());
            
            while (r.getResponseValue().getTask().getStatus() != TaskStatus.DONE)
            {
                System.out.println("Task not done, status: "
                            + r.getResponseValue().getTask().getStatus());
                sleep(4);
                r = client.lrpathStatus(response.getResponseValue());
            }
            System.out.println("Done, results are: " + r);
            List<LRPathResult> results = r.getResponseValue().getData();
            for (LRPathResult result : results)
            {
                System.out.println(result);
            }
        }
    }

    private void sleep(int seconds)
    {
        try
        {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //normally skip the longer test!
//	@Test
	public void serverBridgeTest() {

		LRPathRequestSpecies species = LRPathRequestSpecies.Human;

		LRPathArguments args = (new FakeArgs()).makeTestArgsFromFileAndParameters(TEST_FILE_PATH,
				species, 5);
		
		//to reflect test in web page
		args.setMing(5);
		args.setMaxg(98989);
		args.setOddsmin(0.001);
		args.setOddsmax(98989.0);
		args.setSigcutoff(0.05);
		args.setApplication("LRPathServerBridgeTest");

		System.out.println(args.getGeneids().length);

		LRPathServerBridge bridge = new LRPathServerBridge();
		bridge.setDebugTrace(true);

		
		if (dump) {
			printRequest(args);
		}
		List<LRPathResult> list = bridge.requestKeggPathwaysFromLRPath(args);

		if (list == null)
			System.out.println("Results is null");
		Assert.assertNotNull(list);

		if (list.size() == 0)
			System.out.println("Results are empty");
		Assert.assertTrue(list.size() != 0);
		
		System.out.println("Number of result concepts = " + list.size());
		if (dump) {
			printResults(list);
		}
	}
	
	private void printRequest(LRPathArguments args){
		PrintStream out = System.out;
//		try {
//			out = new PrintStream(new FileOutputStream("/Users/terry/Desktop/probeInput.txt"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return;
//		}
		int limit = args.getGeneids().length;
		int probe = args.getSigvals().length;
		if (limit != probe) {
			out.println("Warning, sigvalue length, " + probe + "," +
					" is not equal geneid length, " + limit + "," +
					" using shorter value");
			limit = Math.min(limit, probe);
		}
		probe = args.getDirection().length;
		if (limit != probe) {
			out.println("Warning, direction length, " + probe + "," +
					" is not equal geneid length, " + limit + "," +
					" using shorter value");
			limit = Math.min(limit, probe);
		}
		out.printf("Database\t%s\n", args.getDatabase());
		out.printf("ConceptName\t%s\n", args.getConceptName());
		out.printf("Species\t%s\n", args.getSpecies());
		out.printf("Application\t%s\n", args.getApplication());
		out.printf("EMail\t%s\n",args.getEmail());
		out.printf("MinGeneCount\t%d\n",args.getMing());
		out.printf("MaxGeneCount\t%d\n",args.getMaxg());
		out.printf("OddsMin\t%2.6f\n",args.getOddsmin());
		out.printf("OddsMin\t%2.6f\n",args.getOddsmin());
		out.printf("SigCutOff\t%2.6f\n",args.getSigcutoff());
		out.println("GeneID\tp-value\tdirection");
		for (int i = 0; i < limit; i++){
			out.printf("%d\t%f2.6%f2.6\n",args.getGeneids()[i],args.getSigvals()[i],args.getDirection()[i]);
		}
	}
	
	private void printResults(List<LRPathResult> list) {
		PrintStream out = System.out;
//		try {
//			out = new PrintStream(new FileOutputStream("/Users/terry/Desktop/probeOutput.txt"));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return;
//		}
		out.println(
				"Name\tConceptType\t#Genes\tCoeff\tOddsRatio\tP-Value\tFDR\tDirection\tSigGenes");
		for (LRPathResult r: list){
			String glist = null;
			for (String g: r.getSigGenes()) {
				if (glist == null) glist = g.trim();
				else glist += "," + g.trim();
			}
			String format = "'%s'\t'%s'\t%d\t%e\t%e\t%e\t%e\t'%s'\n";
			out.printf(format, r.getConceptName(), r.getConceptType(),
					r.getNumUniqueGenes(), r.getCoeff(), r.getOddsRatio(),
					r.getPValue(), r.getFdr(), glist);
		}
	}

	private class FakeArgs {

		public LRPathArguments makeTestArgsFromFileAndParameters(String filePath,
				LRPathRequestSpecies species, int minG){
			LRPathDatabase database = LRPathDatabase.KEGG;
			
			LRPathArguments args = new LRPathArguments();

			args.setDatabase(database.toString());
			args.setSpecies(species.toString());
			args.setMing(minG);
			args = readTestFile(filePath, args);

			return args;
		}

		private LRPathArguments readTestFile(String file, LRPathArguments args) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				ArrayList<Integer> g = new ArrayList<Integer>();
				ArrayList<Double> s = new ArrayList<Double>();
				ArrayList<Double> d = new ArrayList<Double>();

				while ((line = reader.readLine()) != null) {
					if (line.contains("NA")) continue;
					String[] data = line.split("\t");
					if (data[0] != null && data[1] != null
							&& data[0].matches("\\d+")
							&& data[1].matches("-?\\d+(.\\d+)?")
							&& data[2].matches("-?\\d+(.\\d+)?")) {
						g.add((Integer) Integer.parseInt(data[0]));
						s.add((Double) Double.parseDouble(data[1]));
						d.add((Double) Double.parseDouble(data[2]));
					}
				}
				reader.close();

				int[] geneids = new int[g.size()];
				double[] sigvals = new double[s.size()];
				double[] direction = new double[d.size()];

				for (int i = 0; i < g.size(); i++) {
					geneids[i] = g.get(i).intValue();
					sigvals[i] = s.get(i).doubleValue();
					direction[i] = d.get(i).doubleValue();
				}

				args.setGeneids(geneids);
				args.setSigvals(sigvals);
				args.setDirection(direction);

			} catch (IOException e) {
				System.out.println(e);
			}

			return args;

		}
	}


}
