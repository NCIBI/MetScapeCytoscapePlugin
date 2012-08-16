#!/bin/bash
svnBase=https://www.umms.med.umich.edu/codestore/ncibi
brancheLable=metscape-2.3
message="create branch as $brancheLable for release of MetScape 2.3, Oct 4, 2011, Terry"

echo message = $message	

base=commons
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to -m \"$message\"

base=WebBackend
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to -m \"$message\"

base=WebBackend/ncibi-db
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to -m \"$message\"

base=WebBackend/WebServices/ws-client
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=WebBackend/WebServices/ws-common
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=WebBackend/WebServices/ws-server-common
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=projects/WebServices/ws-ncibi
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=projects/WebServices/ws-ncibi-client
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=projects/WebServices/ws-ncibi-common
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=projects/WebServices/ws-ncibi-executor
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=Metabolomics/metab-ws-common
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=Metabolomics/metab-ws-server
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=Metabolomics/metab-ws-client
from=$svnBase/$base/trunk
to=$svnBase/$base/branches/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=CytoscapePlugin
from=$svnBase/$base/trunk/plugins-top/metscape2-plugin
to=$svnBase/$base/branches/MetScape2/$brancheLable
echo svn copy $from $to  -m \"$message\"

base=CytoscapePlugin
from=$svnBase/$base/trunk/plugins-top/mimi-plugin
to=$svnBase/$base/branches/MiMIPlugin/$brancheLable
echo svn copy $from $to  -m \"$message\"

echo done