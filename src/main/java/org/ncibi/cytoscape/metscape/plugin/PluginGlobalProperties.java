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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import cytoscape.CytoscapeInit;

public class PluginGlobalProperties {
	
	private static final String PluginPropertiesFileName = "metscape.props";
	private static final String PROPERTIES_FILE_COMMENT = "Created by the MetScape Plugin (version "
		+ MetScapePlugin.getPluginVersionString() + ") - DO NOT EDIT";
	
	private Properties globalProperties = new Properties();

	/**
	 *  Uses the plugin properties file to record, restore, and check global plugin state 
	 */
	public static PluginGlobalProperties getGlobalProperties(){
		if (theSingleton == null) {
			theSingleton = new PluginGlobalProperties();
		}
		return theSingleton;
	}

	// quick and dirty singleton!
	private static PluginGlobalProperties theSingleton = null;
	private PluginGlobalProperties(){
		initializeGlobalProperties();
	}
	
	private void initializeGlobalProperties() {
		Properties probe = restore();
		if (probe != null)
			globalProperties = probe;
	}	

	public boolean save() {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(getMetScapePropertiesFile());
			globalProperties.storeToXML(os, PROPERTIES_FILE_COMMENT);
			os.flush();
//			System.out.println("DEBUG: Saved properties file: " + getMetScapePropertiesFile());
//			printProperties();
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException sol) {
				}
			}
		}
		return false;
	}
	
	private Properties restore() {
		FileInputStream in = null;
		try {
			Properties probe = new Properties();
			in = new FileInputStream(getMetScapePropertiesFile());
			probe.loadFromXML(in);
//			System.out.println("DEBUG: Loaded properties file: " + getMetScapePropertiesFile());
//			printProperties();
			globalProperties = probe;
			return probe;
		} catch (InvalidPropertiesFormatException e) {
		} catch (IOException e) {
		} finally {
			if (in != null){
				try {
					in.close();
				} catch (IOException sol) {
				}
			}
		}
		return null;
	}
	
	public void addOrUpdateProperties(String propName, String propValue) {
//		System.out.println("DEBUG: setting property in global properties - name = " + propName + ", value = " + propValue);
		globalProperties.put(propName, propValue);
//		printProperties();
	}

	public String getPropertyOrBlank(String propName) {
		String value = (String)globalProperties.get(propName);
		if (value == null) value = "";
		return value;
	}
	
	private File getMetScapePropertiesFile(){
		return CytoscapeInit.getConfigFile(PluginPropertiesFileName);
	}

//	public void printProperties(){
//		System.out.println("Global Properties...");
//		for (Object key: globalProperties.keySet()) {
//			String value = globalProperties.getProperty(key.toString());
//			System.out.println("  " + key.toString() + " = " + value);
//		}
//	}
}
