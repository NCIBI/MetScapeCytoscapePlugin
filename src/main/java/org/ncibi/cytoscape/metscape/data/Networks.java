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
package org.ncibi.cytoscape.metscape.data;

import java.util.Collection;
import java.util.UUID;

import org.ncibi.cytoscape.data.Attributes;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

public class Networks {
	
	private static final String NETWORK_UUID = "Network.UUID";

	public static CyNetwork createNetwork(String networkName){
		CyNetwork network = Cytoscape.createNetwork(networkName);
		setUUIDAttribute(network);
		return network;
	}
	public static CyNetwork createNetwork(
			Collection<?> nodes, Collection<?> edges, String networkName){
		CyNetwork network = Cytoscape.createNetwork(nodes, edges, networkName);
		setUUIDAttribute(network);
		return network;
	}
	
	public static CyNetwork createNetwork(
			Collection<?> nodes, Collection<?> edges, String networkName, CyNetwork parent){
		CyNetwork network = Cytoscape.createNetwork(nodes, edges, networkName, parent);
		setUUIDAttribute(network);
		return network;
	}
	
	public static String getUUID(CyNetwork network){
		String id = null;
		try {
			id = Attributes.network.getStringAttribute(network.getIdentifier(), NETWORK_UUID);
		}
		catch (Throwable ignore){}
		return id;
	}
	
	private static void setUUIDAttribute(CyNetwork network){
		String uuid = UUID.randomUUID().toString();
		Attributes.network.setAttribute(network.getIdentifier(), NETWORK_UUID, uuid);
	}
	
}
