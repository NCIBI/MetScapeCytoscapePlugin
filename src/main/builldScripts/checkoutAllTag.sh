#!/bin/bash
svnBase=https://www.umms.med.umich.edu/codestore/ncibi
tagLabel=metscape-2.2

mkdir tag

pushd tag

base=commons
name=ncibi-commons
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=WebBackend
name=ncibi-webbackend
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=WebBackend/ncibi-db
name=ncibi-db
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=WebBackend/WebServices/ws-client
name=ws-client
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=WebBackend/WebServices/ws-common
name=ws-common
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=WebBackend/WebServices/ws-server-common
name=ws-server-common
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=projects/WebServices/ws-ncibi
name=ws-ncibi
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=projects/WebServices/ws-ncibi-client
name=ws-ncibi-client
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=projects/WebServices/ws-ncibi-common
name=ws-ncibi-common
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=projects/WebServices/ws-ncibi-executor
name=ws-ncibi-executor
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=Metabolomics/metab-ws-common
name=metab-ws-common
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=Metabolomics/metab-ws-server
name=metab-ws-server
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=Metabolomics/metab-ws-client
name=metab-ws-client
path=$svnBase/$base/tags/$tagLabel
svn co $path $name

base=CytoscapePlugin
name=metscape2-plugin
path=$svnBase/$base/tags/MetScape2/$tagLabel
svn co $path $name

base=CytoscapePlugin
name=mimi-plugin
path=$svnBase/$base/tags/MiMIPlugin/$tagLabel
svn co $path $name

base=CytoscapePlugin
name=webstart
path=$svnBase/$base/trunk/webstart
svn co $path $name

popd