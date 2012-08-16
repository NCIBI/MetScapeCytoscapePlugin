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

import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.version.Version;
import org.ncibi.metab.ws.client.MetabolicVersionService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;
import org.ncibi.ws.ResponseStatus;
import org.ncibi.ws.ResponseStatusType;

public class GetVersionTask extends AbstractTask {

	private Version serverVersion = null;
	
	public static Version getServerVersion() {
		GetVersionTask task = new GetVersionTask();
		if(configureAndRunTask(task)) {
			return task.serverVersion;
		}
		else return null;
	}
	
	@Override
	public String getTitle() {
		return "Get Server Version";
	}
	
	@Override
	public void run() {
		taskMonitor.setStatus("Contacting to server...");
		taskMonitor.setPercentCompleted(-1);
		MetabolicVersionService versionService = new MetabolicVersionService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
		taskMonitor.setStatus("Checking status...");
		Response<Version> response = versionService.retrieveServerVersion();
		ResponseStatus responseStatus  = response.getResponseStatus();
		if (responseStatus.isSuccess())
			serverVersion = response.getResponseValue();
		else{
			String message = "The MetScape server is currently unavailable. " +
				"Please try again later. If this problem persists, " +
				"please contact metscape-help@umich.edu.";
			if (response.getResponseStatus().getType().equals(ResponseStatusType.HTTP_ERROR)){
				int code = response.getResponseStatus().getHttpStatus().getStatusCode();
				message = "The MetScape server is not responding (status code = " + code + "). " +
						"If you know that this is transient error, try again later. Otherwise, " +
						"please contact metscape-help@umich.edu.";
			}
			taskMonitor.setException(null, message);
			serverVersion = null;
		}
		taskMonitor.setPercentCompleted(100);
	}
	
}
