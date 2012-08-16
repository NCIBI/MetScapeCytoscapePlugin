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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.ncibi.commons.lang.NumUtils;
import org.ncibi.commons.lang.StrUtils;
import org.ncibi.cytoscape.data.Attributes;
import org.ncibi.cytoscape.metscape.action.AboutAction;
import org.ncibi.cytoscape.metscape.action.AnimateAction;
import org.ncibi.cytoscape.metscape.action.BuildNetworkAction;
import org.ncibi.cytoscape.metscape.action.ConceptFilterAction;
import org.ncibi.cytoscape.metscape.action.PathwayFilterAction;
import org.ncibi.cytoscape.metscape.action.RegistrationAction;
import org.ncibi.cytoscape.metscape.action.SelectExperimentalDataAction;
import org.ncibi.cytoscape.metscape.action.ShowLegendAction;
import org.ncibi.cytoscape.metscape.data.NetworkData;
import org.ncibi.cytoscape.metscape.data.Networks;
import org.ncibi.cytoscape.metscape.data.Organism;
import org.ncibi.cytoscape.metscape.task.BuildNewNetworkTask;
import org.ncibi.cytoscape.metscape.task.GetVersionTask;
import org.ncibi.cytoscape.metscape.ui.MouseClickHandler;
import org.ncibi.cytoscape.metscape.ui.PopupNodeContextMenuListener;
import org.ncibi.cytoscape.metscape.ui.RegistrationDialog;
import org.ncibi.cytoscape.util.ServiceProxyUtil;
import org.ncibi.metab.name.GeneNameAttribute;
import org.ncibi.metab.name.MetabolicName;
import org.ncibi.metab.version.Version;
import org.ncibi.metab.version.VersionFactory;
import org.ncibi.metab.version.VersionUtils;
import org.ncibi.metab.ws.client.MetabolicNameService;
import org.ncibi.ws.HttpRequestType;
import org.ncibi.ws.Response;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.CalculatorCatalog;
import ding.view.DGraphView;

public class MetScapePlugin extends CytoscapePlugin{
	private static JMenu pluginsMenu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
	private static CalculatorCatalog catalog = Cytoscape.getVisualMappingManager().getCalculatorCatalog();
 
	private static JMenu metscapeMenu = null;
	private static JMenuItem buildNetworkMenuItem = null;
	private static JMenuItem selectExperimentalDataMenuItem = null;
	private static JMenuItem animateMenuItem = null;
	private static JMenuItem showLegendMenuItem = null;
	private static JMenu filterMenu = null;
	private static JMenuItem pathwayFilterMenuItem = null;
	private static JMenuItem conceptFilterMenuItem = null;
	private static JMenuItem aboutMenuItem = null;
	private static JMenuItem regMenuItem = null;
	private static PluginData pluginData = null;
    private static Version serverVersion = null;
    private static Version clientVersion = VersionFactory.newClientVersion();
	private static boolean cytoscapeStartupComplete = false;
	private static boolean pluginStateJustRestored = false;
    
	public MetScapePlugin()  {
		// create a new action to respond to menu activation
		if (!pluginsMenu.isMenuComponent(metscapeMenu)) {
			metscapeMenu = new JMenu("MetScape");
			
			buildNetworkMenuItem = new JMenuItem(new BuildNetworkAction());
			metscapeMenu.add(buildNetworkMenuItem);
			
			selectExperimentalDataMenuItem = new JMenuItem(new SelectExperimentalDataAction());
			metscapeMenu.add(selectExperimentalDataMenuItem);
			
			animateMenuItem = new JMenuItem(new AnimateAction());
			metscapeMenu.add(animateMenuItem);
			
			showLegendMenuItem = new JMenuItem(new ShowLegendAction());
			metscapeMenu.add(showLegendMenuItem);
			
			filterMenu = new JMenu("Filter By");
			
			pathwayFilterMenuItem = new JMenuItem(new PathwayFilterAction());
			filterMenu.add(pathwayFilterMenuItem);
			
			conceptFilterMenuItem = new JMenuItem(new ConceptFilterAction());
			filterMenu.add(conceptFilterMenuItem);
			
			metscapeMenu.add(filterMenu);
			
			aboutMenuItem = new JMenuItem(new AboutAction());
			metscapeMenu.add(aboutMenuItem);
			
			regMenuItem = new JMenuItem(new RegistrationAction());
			if(!PluginRegistration.isPluginRegistered())
				metscapeMenu.add(regMenuItem);
			pluginsMenu.add(metscapeMenu);
		}
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED,new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				CyNetwork destroyed = Cytoscape.getNetwork((String)evt.getNewValue());
				if(Networks.getUUID(destroyed) != null) {
					removeDataAndStylesForNetwork(destroyed);
				}
			}
		});
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(cytoscapeStartupComplete) {
					if(pluginData != null) {
						removePanelsAndClearData();
						removeMetScapeVisualStyles();
					}
					return;
				}
				// else
				boolean doBuildNetwork = false;
				if(CytoscapeInit.getProperties().containsKey("cids")) {
					String input = CytoscapeInit.getProperties().getProperty("cids");
					CytoscapeInit.getProperties().remove("cids");
					List<String> inputList = StrUtils.splitCommaOrSpaceSeparatedString(input);
					MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
					Response<Map<String,MetabolicName>> response = ns.retrievePrimaryNamesForCids(inputList);
					if(response != null && 
							response.getResponseValue() != null && 
							!response.getResponseValue().isEmpty()) doBuildNetwork = true;
					for(MetabolicName name: response.getResponseValue().values())
						getPluginData().getDefaultCompounds().put(name.getId(),name.getName());
				}
				if(CytoscapeInit.getProperties().containsKey("geneids")) {
					String input = CytoscapeInit.getProperties().getProperty("geneids");
					int taxid = NumUtils.toInteger(CytoscapeInit.getProperties().getProperty("taxid", "9606"));
					CytoscapeInit.getProperties().remove("geneids");
					CytoscapeInit.getProperties().remove("taxid");
					List<Integer> inputList = NumUtils.splitCommaOrSpaceSeparatedString(input);
					MetabolicNameService ns = new MetabolicNameService(HttpRequestType.POST,ServiceProxyUtil.getWebServiceProxy());
					Response<Map<String,MetabolicName>> response = ns.retrievePrimaryNamesForGeneids(inputList, taxid);
					if(response != null && 
							response.getResponseValue() != null && 
							!response.getResponseValue().isEmpty()) doBuildNetwork = true;
					for(MetabolicName name: response.getResponseValue().values()) {
						Integer orgId = NumUtils.toInteger(name.getAttribute(GeneNameAttribute.HOMOLOG_GENEID));
						String orgName = name.getAttribute(GeneNameAttribute.HOMOLOG_NAME);
						String humanName = name.getName();
						getPluginData().getDefaultGenes().put(orgId, new String[]{orgName, humanName});
						getPluginData().setOrganism(Organism.toOrganism(taxid));
					}
				}
				if(doBuildNetwork) buildNetwork();
				cytoscapeStartupComplete = true;
			}
		});
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(!pluginStateJustRestored ) {
					if(pluginData != null){
						removePanelsAndClearData();
						removeMetScapeVisualStyles();
					}
				}
				else{
					pluginStateJustRestored = false;
				}
			}
		});
	}
	
	private void addPanels() {
		if(pluginData.isBuildNetworkPanelOpen())
			BuildNetworkAction.exec();
		if(pluginData.isPathwayFilterPanelOpen())
			PathwayFilterAction.exec();
		if(pluginData.isConceptFilterPanelOpen())
			ConceptFilterAction.exec();
	}
	
	private void buildNetwork() {
		NetworkData networkData = new NetworkData(pluginData);
		networkData.setCids(new HashSet<String>(pluginData.getDefaultCompounds().keySet()));
		networkData.setGeneids(new HashSet<Integer>(pluginData.getDefaultGenes().keySet()));
		BuildNetworkAction.exec();
		BuildNewNetworkTask.buildUsing(networkData);
	}
	
	private void removePanelsAndClearData() {
		CytoPanel cytoPanel =  Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		int panelIndex = cytoPanel.indexOfComponent("Node/Edge Details");
		if(panelIndex != -1) {
			cytoPanel.remove(panelIndex);
			cytoPanel.setState(CytoPanelState.HIDE);
		}
		if(BuildNetworkAction.getPanel() != null && BuildNetworkAction.getPanel().getParent() != null) {
			BuildNetworkAction.getPanel().close();
		}
		BuildNetworkAction.setPanel(null);
		if(PathwayFilterAction.getPanel() != null && PathwayFilterAction.getPanel().getParent() != null) {
			PathwayFilterAction.getPanel().close();
		}
		PathwayFilterAction.setPanel(null);
		if(ConceptFilterAction.getPanel() != null && ConceptFilterAction.getPanel().getParent() != null) {
			ConceptFilterAction.getPanel().close();
		}
		ConceptFilterAction.setPanel(null);
		pluginData = null;
	}
	
	private void removeMetScapeVisualStyles() {
		List<String> stylesToRemove = new ArrayList<String>();
		for(String vsName: catalog.getVisualStyleNames()) {
			if(vsName.startsWith("MetScape")) {
				stylesToRemove.add(vsName);
			}
		}
		for(String styleToRemove: stylesToRemove)
			catalog.removeVisualStyle(styleToRemove);
	}
	
	private void removeDataAndStylesForNetwork(CyNetwork network) {
		Attributes.node.deleteAttribute("Category." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("direction." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("isSignificant." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("inExpansionFor." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("isExpansion." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("isExpansionSeed." + Networks.getUUID(network));
		Attributes.node.deleteAttribute("isSubnetworkExpansionSeed." + Networks.getUUID(network));
		Attributes.edge.deleteAttribute("inExpansionFor." + Networks.getUUID(network));
		Attributes.edge.deleteAttribute("isExpansion." + Networks.getUUID(network));
		if(catalog.getVisualStyle("MetScape:" + Networks.getUUID(network)) != null)
			catalog.removeVisualStyle("MetScape:" + Networks.getUUID(network));
		pluginData.removeNetworkData(Networks.getUUID(network));
	}
	
	public static PluginData getPluginData() { 
		if(pluginData == null) {
			pluginData = new PluginData();
		}
		return pluginData; 
	}
	
	public static boolean isInitialized() {
		return pluginData != null;
	}
	
	public static boolean serverIsReady() {
		MetScapePlugin.fetchServerVersion();
		if (MetScapePlugin.getServerVersion() == null){
			String message = "Unable to connect to MetScape server.";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Server Unavailable", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (!MetScapePlugin.isVersionCompatible()) {
			String message = MetScapePlugin.getCompatiblityMessage();
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Incompatible Plugin", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (!PluginRegistration.isPluginRegistered() && !PluginRegistration.isPluginRegistrationDeclined()) {
			RegistrationDialog dialog = new RegistrationDialog(Cytoscape.getDesktop());
			dialog.setVisible(true);
		}
		return true;
	}

	public static void fetchServerVersion(){
		if (serverVersion == null) {
			serverVersion = GetVersionTask.getServerVersion();
		}
	}
	
	public static Version getServerVersion() {
		return serverVersion;
	}

	public static boolean isVersionCompatible() {
		return VersionUtils.isCompatible(clientVersion,serverVersion);
	}

	public static String getServerVersionString() {
		if (serverVersion == null)
			return "Server version is null";
		return serverVersion.toString();
	}
	
	public static String getPluginVersionString() {
		return clientVersion.toString();
	}


	public static String getCompatiblityMessage(){
		return VersionUtils.getCompatibilityMessage(clientVersion,serverVersion);
	}
	
	
	public static void removeRegistrationMenuItem() {
		metscapeMenu.remove(regMenuItem);
	}
	
	// override the following two methods to save state in session.
	/**
	 * DOCUMENT ME!
	 * 
	 * @param pStateFileList
	 *            DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
		
		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			//No previous state to restore
			return;
		}
		try {
			if(pluginData != null) 
				removePanelsAndClearData();
			File sessionFile = pStateFileList.get(0);
			FileInputStream fin = new FileInputStream(sessionFile);
			XMLDecoder decoder = new XMLDecoder(fin);
		    pluginData = ((PluginData) decoder.readObject());
		    decoder.close();
		    fin.close();
		    
		    for(CyNetworkView view: Cytoscape.getNetworkViewMap().values()) {
		    	((DGraphView) view).getCanvas().addMouseListener(new MouseClickHandler());
		    	view.addNodeContextMenuListener(new PopupNodeContextMenuListener());
		    }
		    addPanels();
		    pluginStateJustRestored = true;
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		if(pluginData == null) return;
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		File sessionFile = new File(tmpDir, "MetScape.xml");

		try {
			FileOutputStream fout = new FileOutputStream(sessionFile);
			XMLEncoder encoder = new XMLEncoder(fout);
			encoder.writeObject(pluginData);
			encoder.close();
			fout.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		pFileList.add(sessionFile);
	}
 }
