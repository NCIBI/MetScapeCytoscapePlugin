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
package org.ncibi.cytoscape.metscape.multidisplay;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.data.CompoundMapping;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlImplementation;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.plugin.MetScapePlugin;
import org.ncibi.cytoscape.metscape.plugin.PluginData;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class StudyUtil {
	
	public static void startAnimation(CyNetwork network, MultiStudyToNetworkMapping networkMapping) {
		System.out.println("Building a network animation...");
		System.out.println("  network name: " + network.getTitle());
		for (int animation = 0; animation < networkMapping.getNumberOfAnimations(); animation++){
			String text = null;
			for (int study = 0; study < networkMapping.getNumberOfStudies(); study++) {
				if (text == null) text = networkMapping.getValueLabel(animation, study);
				else text += ", " + networkMapping.getValueLabel(animation, study);
			}
			System.out.println("  " + networkMapping.getAnimationLabel(animation) + ": " + text);			
		}
		MultiObservationTimeStudy study =  new MultiObservationTimeStudy(network, networkMapping);		
		ControlInterface control = new ControlImplementation(network);
		MultiNet animation = new MultiNet(network, study, control);
		control.startAnimation(animation);
	}
	
	public static Double makeNumber(String valueString){
		Double value = null;
		if ((valueString != null) && (valueString.length() > 0)) {
			try {
				value = Double.parseDouble(valueString);
			} catch (Throwable ignore){}
		}
		if (value == null) value = Double.NaN;
		return value;
	}
	
	public static String replaceWhitespace(String name){
		name = name.replaceAll("\\W", "_");
		return name;
	}
	
	public static Set<String> greatestCommonSubstrings(Collection<String> in){
		List<String> source = new ArrayList<String>(in);
		Set<String> ret = new HashSet<String>();
		System.out.println("In:" + join(in,","));
		if (source.size() < 2) return ret;
		if (source.size() == 2) return greatestCommonSubstrings(source.get(0),source.get(1));
		Set<String> seed = greatestCommonSubstrings(source.get(0),source.get(1));
		System.out.println("Seed:" + join(seed,","));
		for (int n = 0; n < in.size(); n++){
			Set<String> candidateSet = new HashSet<String>();
			candidateSet.addAll(seed);
			candidateSet.add(source.get(n));
			if (candidateSet.size() > 1) {
				System.out.println("Candidate:" + join(candidateSet,","));
				seed = greatestCommonSubstrings(candidateSet);
			}
			System.out.println("Set:" + join(seed,","));
		}
		return seed;
	}
	
	public static String join(Collection<String> in, String joinS){
		String ret = null;
		for (String s: in) {
			if (ret == null) ret = s;
			else ret += joinS + s;
		}
		if (ret == null) ret = "";
		return ret;
	}

	private static Set<String> greatestCommonSubstrings(String s1,String s2) {
		//Note: this is an adaptation of the algorithm described in 
		// http://en.wikipedia.org/wiki/Longest_common_substring
		// as we only except short strings, no storage optimization was done
		Set<String> ret = new HashSet<String>();
		int[][] marker = new int[s1.length()][s2.length()];
		int longestLength = 0;

		for (int n1 = 0; n1 < s1.length(); n1++){
			for (int n2 = 0; n2 < s2.length(); n2++){
				if (s1.charAt(n1) == s2.charAt(n2)){
					if ((n1 == 0) || (n2==0)) marker[n1][n2] = 1;
					else marker[n1][n2] = marker[n1-1][n2-1] + 1;
					if (marker[n1][n2] > longestLength) {
						longestLength = marker[n1][n2];
						ret.clear(); // 
					}
					if (marker[n1][n2] == longestLength) {
						ret.add(s1.substring(n1-longestLength+1,n1+1));
					}
				}
			}
		}
		return ret;
	}
	
	public static String commonPrefix(Collection<String> in){
		if (in == null) return "";
		if (in.isEmpty()) return "";
		ArrayList<String> list = new ArrayList<String>(in);
		if (list.size() == 1) return list.get(0);
		String ret = commonPrefix(list.get(0),list.get(1));
		for (int i = 2; i < list.size(); i++){
			ret = commonPrefix(ret,list.get(i));
		}
		return ret;
	}

	private static String commonPrefix(String s1, String s2) {
		int limit = Math.min(s1.length(),s2.length());
		int pos = 0;
		while ((pos < limit) && (s1.charAt(pos) == s2.charAt(pos))) pos++;
		return s1.substring(0,pos);
	}

	public static String commonSuffix(Collection<String> in){
		if (in == null) return "";
		if (in.isEmpty()) return "";
		ArrayList<String> list = new ArrayList<String>(in);
		if (list.size() == 1) return list.get(0);
		String ret = commonSuffix(list.get(0),list.get(1));
		for (int i = 2; i < list.size(); i++){
			ret = commonSuffix(ret,list.get(i));
		}
		return ret;
	}

	private static String commonSuffix(String s1, String s2) {
		int limit = Math.min(s1.length(),s2.length());
		int pos = 0;
		while ((pos < limit) && (s1.charAt(s1.length() - pos - 1) == s2.charAt(s2.length() - pos - 1))) pos++;
		return s1.substring(s1.length() - pos, s1.length());
	}
	
	public static List<String> getCompoundIds(CyNetwork network) {
		List<String> ret = new ArrayList<String>();
		for(Object node: network.nodesList()) {
			CyNode cyNode = (CyNode) node;
			if(Attributes.node.getStringAttribute(cyNode.getIdentifier(), "Type").equals("Compound"))
				ret.add(cyNode.getIdentifier());
		}
		return ret;
	}
	
	public static List<String> getConcentrationAttributes(CyNetwork network){
		String baseAttributeName =  makeBaseAttributeName(network);
		if (baseAttributeName == null) return null;

		PluginData session = MetScapePlugin.getPluginData();
		CompoundMapping compoundMapping = session.getNetworkData(Networks.getUUID(network)).getCompoundMapping();
		if(compoundMapping.isEmpty())
			return null;
		
		List<String> ret = new ArrayList<String>();
		for (String label: compoundMapping.getColumns()) {
			if (label != null) // the last of the labels was unexpectedly null! - tew 10/26/10
				ret.add(baseAttributeName + "." + label);
		}
		return ret;
	}
	
	public static List<String> getConcentrationLabels(CyNetwork network){
		PluginData session = MetScapePlugin.getPluginData();
		CompoundMapping compoundMapping = session.getNetworkData(Networks.getUUID(network)).getCompoundMapping();
		if(compoundMapping.isEmpty())
			return null;
		
		List<String> ret = new ArrayList<String>();
		for (String label: compoundMapping.getColumns()) {
			if (label != null) // the last of the labels was unexpectedly null! - tew 10/26/10
				ret.add(label);
		}
		return ret;
	}

	public static String makeBaseAttributeName(CyNetwork network) {
		PluginData session = MetScapePlugin.getPluginData();
		CompoundMapping compoundMapping = session.getNetworkData(Networks.getUUID(network)).getCompoundMapping();
		if(compoundMapping.isEmpty())
			return null;

		return compoundMapping.getFullyQualifiedName();
	}
	
}
