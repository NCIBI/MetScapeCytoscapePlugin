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
package org.ncibi.cytoscape.metscape.plugin;

import org.ncibi.metab.registration.Registration;

public class PluginRegistration {
	
	private RegistrationState registrationState = RegistrationState.UNREGISTERED;
	private Registration dataForCommunicationToDatabase = new Registration("","","","");
	
	private static final String REGISTRATION_STATE_PROP_NAME = "Metscape.Registration.State";
	private static final String REGISTRATION_FIRST_NAME_PROP_NAME = "Metscape.Registration.FirstName";
	private static final String REGISTRATION_LAST_NAME_PROP_NAME = "Metscape.Registration.LastName";
	private static final String REGISTRATION_INSTITUTION_PROP_NAME = "Metscape.Registration.Institution";
	private static final String REGISTRATION_EMAIL_PROP_NAME = "Metscape.Registration.Email";
	
	private enum RegistrationState {
		UNREGISTERED, REGISTERED, DECLINED;

		public static RegistrationState determineRegistrationStateFromGlobalProperties(PluginGlobalProperties prop) {
			String value = prop.getPropertyOrBlank(REGISTRATION_STATE_PROP_NAME);
			if ((value == null) || (value.length() == 0)) return UNREGISTERED;
			if (value.equals(DECLINED.toString())) return DECLINED;
			if (value.equals(REGISTERED.toString())) return REGISTERED;
			return UNREGISTERED;
		}
	}

	public PluginRegistration() {
		setDataFromGlobalProperties();
	}

	private void setDataFromGlobalProperties(){
		PluginGlobalProperties globalProperties = PluginGlobalProperties.getGlobalProperties();
		registrationState = RegistrationState.determineRegistrationStateFromGlobalProperties(globalProperties);
		String firstName = globalProperties.getPropertyOrBlank(REGISTRATION_FIRST_NAME_PROP_NAME);
		String lastName = globalProperties.getPropertyOrBlank(REGISTRATION_LAST_NAME_PROP_NAME);
		String institution = globalProperties.getPropertyOrBlank(REGISTRATION_INSTITUTION_PROP_NAME);
		String email = globalProperties.getPropertyOrBlank(REGISTRATION_EMAIL_PROP_NAME);
		dataForCommunicationToDatabase = new Registration(firstName,lastName,institution,email);
	}
		
	public void recordRegistrationToGlobalProperties() {
		PluginGlobalProperties globalProperties = PluginGlobalProperties.getGlobalProperties();
		globalProperties.addOrUpdateProperties(REGISTRATION_STATE_PROP_NAME, registrationState.toString());
		globalProperties.addOrUpdateProperties(REGISTRATION_FIRST_NAME_PROP_NAME,dataForCommunicationToDatabase.getFirstName());
		globalProperties.addOrUpdateProperties(REGISTRATION_LAST_NAME_PROP_NAME,dataForCommunicationToDatabase.getLastName());
		globalProperties.addOrUpdateProperties(REGISTRATION_INSTITUTION_PROP_NAME,dataForCommunicationToDatabase.getInstitution());
		globalProperties.addOrUpdateProperties(REGISTRATION_EMAIL_PROP_NAME,dataForCommunicationToDatabase.getEmail());
		globalProperties.save();
	}

	public void recordRegistrationData(String firstName, String lastName, String institution, String email) {
		dataForCommunicationToDatabase = new Registration(firstName, lastName, institution, email);
		recordRegistrationToGlobalProperties();
	}

	public Registration getDataForServer() {
		return dataForCommunicationToDatabase;
	}

	public void markAsRegistered() {
		registrationState = RegistrationState.REGISTERED;
		recordRegistrationToGlobalProperties();
	}
	
	public void markAsUnRegistered() {
		registrationState = RegistrationState.UNREGISTERED;
		recordRegistrationToGlobalProperties();
	}

	public void markAsDeclinedAndClearRegistrationInformation() {
		registrationState = RegistrationState.DECLINED;
		dataForCommunicationToDatabase = new Registration("","","","");
		recordRegistrationToGlobalProperties();
	}

	public boolean isRegistered() {
		return (registrationState == RegistrationState.REGISTERED);
	}

	public boolean isDeclined() {
		return (registrationState == RegistrationState.DECLINED);
	}

	public String getFirstName() {
		return dataForCommunicationToDatabase.getFirstName();
	}

	public String getLastName() {
		return dataForCommunicationToDatabase.getLastName();
	}

	public String getInstitutionI() {
		return dataForCommunicationToDatabase.getInstitution();
	}
	
	public String getEmail() {
		return dataForCommunicationToDatabase.getEmail();
	}

	public static boolean isPluginRegistered() {
		PluginRegistration registration = new PluginRegistration();
		return registration.isRegistered();
	}

	public static boolean isPluginRegistrationDeclined() {
		PluginRegistration registration = new PluginRegistration();
		return registration.isDeclined();
	}

}
