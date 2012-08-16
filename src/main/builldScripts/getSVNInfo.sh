#!/bin/bash

# getSVNInfo.sh
# Created by Terry Weymouth on 7/18/11.
# Copyright 2011 University of Michigan. All rights reserved.

cd trunk

base=ncibi-db
pushd $base
svn info
popd

base=ncibi-webbackend
pushd $base
svn info
popd

base=ws-client
pushd $base
svn info
popd

base=ws-common
pushd $base
svn info
popd

base=ws-server-common
pushd $base
svn info
popd

base=ncibi-commons
pushd $base
svn info
popd

base=ws-ncibi-common
pushd $base
svn info
popd

base=ws-ncibi
pushd $base
svn info
popd

base=ws-ncibi-client
pushd $base
svn info
popd

base=ws-ncibi-executor
pushd $base
svn info
popd

base=metab-ws-common
pushd $base
svn info
popd

base=metab-ws-server
pushd $base
svn info
popd

base=metab-ws-client
pushd $base
svn info
popd

base=metscape2-plugin
pushd $base
svn info
popd


