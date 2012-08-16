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
package org.ncibi.cytoscape.metscape.lrpath;

import java.util.ArrayList;
import java.util.List;

import org.ncibi.lrpath.LRPathArguments;
import org.ncibi.lrpath.LRPathResult;
import org.ncibi.task.TaskStatus;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;
import org.ncibi.ws.client.NcibiLRPathService;
import org.ncibi.ws.request.RequestStatus;

public class LRPathServerBridge {

	private TaskStatus lastStatus = TaskStatus.QUEUED;
	private boolean debug = false;
	
	public String getLastStatusString(){
		return lastStatus.toString();
	}
	
	public void setDebugTrace(boolean trace){
		debug = trace;
	}
	
	public List<LRPathResult> requestKeggPathwaysFromLRPath(LRPathArguments args) {

		NcibiLRPathService client = new NcibiLRPathService(HttpRequestType.POST);
		Response<String> response = client.submitLRPathRequest(args);

		Response<RequestStatus<List<LRPathResult>>> r = client
				.lrpathStatus(response.getResponseValue());
		TaskStatus status = r.getResponseValue().getTask().getStatus();
		String uid = r.getResponseValue().getTask().getUuid();
		
		if (debug) debugTrace("UID for task: " + uid);
		
		int count = 0;
		if (debug) debugTrace(count,status);
		
		while (status != TaskStatus.DONE) {
			count++;
			if (debug) debugTrace(count,status);
			if (status == TaskStatus.QUEUED) {
				lastStatus = status;
			} else if (status == TaskStatus.RUNNING) {
				lastStatus = status;
			} else if (status == TaskStatus.ERRORED) {
				throw new RuntimeException("LRPath Task returned error status");
			} else if (status == TaskStatus.CANCELED) {
				return new ArrayList<LRPathResult>(); // empty results
			}
			sleep(4);
			r = client.lrpathStatus(response.getResponseValue());
			status = r.getResponseValue().getTask().getStatus();
		}
		count++;
		if (debug) debugTrace(count,status);
		List<LRPathResult> results = r.getResponseValue().getData();
		if (debug) debugTrace("Results count = " + results.size());
		return results;
	}
	
	private void debugTrace(int count, TaskStatus status){
		debugTrace("count = " + count + ", status = " + status);
	}

	private void debugTrace(String message){
		System.out.println("DEBUG: LRPathServerBridge, " + message);
	}
	
    private static void sleep(int seconds)
    {
        try
        {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException ignore)
        {
        }
    }

}
