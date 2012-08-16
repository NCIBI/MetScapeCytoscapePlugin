#!/bin/bash

targetDirectory = trunk

mkdir $targetDirectory

pushd $targetDirectory

svn co https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/ncibi-db/trunk/ ncibi-db

svn co https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-client/trunk/ ws-client

svn co https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-common/trunk/ ws-common

svn co https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/WebServices/ws-server-common/trunk/ ws-server-common

svn co https://www.umms.med.umich.edu/codestore/ncibi/WebBackend/trunk/ ncibi-webbackend

svn co https://www.umms.med.umich.edu/codestore/ncibi/commons/trunk ncibi-commons

svn co https://www.umms.med.umich.edu/codestore/ncibi/projects/WebServices/ws-ncibi-common/trunk/ ws-ncibi-common

svn co https://www.umms.med.umich.edu/codestore/ncibi/projects/WebServices/ws-ncibi/trunk/ ws-ncibi

svn co https://www.umms.med.umich.edu/codestore/ncibi/projects/WebServices/ws-ncibi-client/trunk/ ws-ncibi-client

svn co https://www.umms.med.umich.edu/codestore/ncibi/projects/WebServices/ws-ncibi-executor/trunk/ ws-ncibi-executor

svn co https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-common/trunk/ metab-ws-common

svn co https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-server/trunk/ metab-ws-server

svn co https://www.umms.med.umich.edu/codestore/ncibi/Metabolomics/metab-ws-client/trunk/ metab-ws-client

svn co https://www.umms.med.umich.edu/codestore/ncibi/CytoscapePlugin/trunk/plugins-top/metscape2-plugin/ metscape2-plugin

svn co https://www.umms.med.umich.edu/codestore/ncibi/CytoscapePlugin/trunk/plugins-top/mimi-plugin/ mimi-plugin

svn co https://www.umms.med.umich.edu/codestore/ncibi/CytoscapePlugin/trunk/webstart webstart

popd