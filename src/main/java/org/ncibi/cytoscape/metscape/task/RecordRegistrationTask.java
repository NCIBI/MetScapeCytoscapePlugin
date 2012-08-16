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

import org.ncibi.cytoscape.metscape.plugin.PluginRegistration;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.ws.client.MetabolicRegistrationService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

public class RecordRegistrationTask extends AbstractTask {

	private final PluginRegistration registrationRecord;

	public static boolean recordRegistration(PluginRegistration registrationRecord) {
		RecordRegistrationTask task = new RecordRegistrationTask(registrationRecord);
		return configureAndRunTask(task);
	}
	
	private RecordRegistrationTask(PluginRegistration registrationRecord) {
		this.registrationRecord = registrationRecord;
	}

	public String getTitle() {
		return "Saving registration data";
	}

	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Saving registration data...");

			Response<Boolean> serverResponse;
			MetabolicRegistrationService service = new MetabolicRegistrationService(
					HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
			System.out.println("Making registration server request");
			serverResponse = service.register(registrationRecord.getDataForServer());
			boolean registered = false;
			if (serverResponse != null) {
				boolean success = serverResponse.getResponseStatus().isSuccess();
				registered = (success)?serverResponse.getResponseValue().booleanValue():false;
				if (registered)
					registrationRecord.markAsRegistered();
				System.out.println("Server registration request was returned; success = " + success + "; registered = " + registered);
			}
			taskMonitor.setStatus("Complete");
			taskMonitor.setPercentCompleted(100);

		} catch (Throwable t) {
			if (!interrupted)
				taskMonitor.setException(t, "Save failed (server unavailable)");
		}

	}
}
