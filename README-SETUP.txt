To build and run MetScape from within Eclipse, there are several things you must do.  
To proceed, you will need a relatively recent version of Eclipse (3.4-3.6 should work), 
along with the Subclipse and m2eclipse plugins.

To start out, it is recommended that you create a new workspace in Eclipse.  We suggest creating this
in a directory that is stored on your locally drive (and not from a network drive).

Inside your new (empty) workspace, you need to check out several projects from SVN.  This can
be done either by using the "Check out Maven Projects from SVN" option in Maven (File-Import),
or by first checking out the projects to your workspace directoiry using a standard SVN client
and the using the "Existing Maven Projects" import option within Eclipse.


These projects (and their location on the SVN repository) are as follows:

metscape2-plugin (https://www.umms.med.umich.edu/codestore/ncibi/CytoscapePlugin/trunk/plugins-top/metscape2-plugin)
metab-ws-client (https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-client/trunk
metab-ws-common (https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-common/trunk)

If you want to build and run the MetScape server locally, you will also need the following:

metab-ws-server (https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-server/trunk)
ncibi-commons (https://www.umms.med.umich.edu/codestore/ncibi/commons/trunk/)
ncibi-db (https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/ncibi-db/trunk)
ncibi-db-api (https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/ncibi-db-api/trunk)
ws-client (https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-client/trunk/)
ws-common (https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-common/trunk/)
ws-ncibi-client (https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-ncibi-client/trunk/

If you want to build the LRpath server code locally, you will also need the following:

<insert text here>

After getting all necessary projects from SVN and importing them into Eclipse, a few more steps are necessary:

1. In metab-ws-client, in src/main/resources, copy 
metab-client.properties.template to metab-client.properties 
(this is the location of the service used for MetScape, e.g metabws)
Modify the file if necessary i.e. if using a locally-run MetScape server).

2. In metscape2-plugin, under src/main/resources, copy
template.for.lrpath.client.properties to client.properties 
(this is the location of the service used for LRPath, e.g. ncibiws)
Modify the file if necessary (i.e. if using a locally-run LRpath server).

3. If you are building the MetScape server code locally, you will need to copy
and edit metab-ws-server.properties.template in src/main/resources to metab-ws-server.properties.
You will need to modify the file in order to specify a password for the NCIBI databases.  

4. If you are building the LRpath server code locally....

5. As the API for the MetScape trunk code frequently becomes out of sync with the unit tests,
you will likely want to disable the running of these tests.  You can do this a variety of ways

1) In Eclipse, create a custom Maven Run Configuration for each project with the base directory set to
the project, the goal set to install, and Skip Tests checked. Use these to build the MetScape source code.

2) Add the following to your Maven installation's settings.xml (you will need to point Eclipse to
an external Maven installation under Preferences-Maven-Installations to do this), 
paste the following text directly under the <profiles> tag:

<profile>
		<id>skipTests</id>
		<activation>
			<activeByDefault>true</activeByDefault>
		</activation>
		<properties>
			<skipTests>true</skipTests>
		</properties>
</profile>

6. Run a Maven install on all projects you have installed. Do this in the following order:

...

7. Once done, you can create a run configuration to run and debug the latest trunk build of
Metscape from within Cytoscape. First, make sure that you have Cytoscape (version 2.8.1) and MetScape (version 2.0
or above) installed locally.  Once that is done, create a Run Configuration in Eclipse with the following settings:

Project: metscape2-plugin
Main class: cytoscape.CyMain
Program arguments: -p "<path to local Cytoscape installation>\plugins"

Save this run configuration.  Now, whenever you make changes to the trunk build of Cytoscape or update the code,
all you have to do to test the updated code in Cytoscape is to run Cytoscape from Eclipse using this configuration.
You will not have to do a Maven install again unless you want to create a JAR file.





