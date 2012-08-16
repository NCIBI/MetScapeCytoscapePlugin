#!/bin/bash

# Note: for all package builds, pbuild (part of the config package)
#   must be used to build the final, deployable package (*.tar.gz) and
#   pdeploy (and in the config package) must be used to deploy that package

svn=/usr/local/bin/svn
echo using svn as $svn

svnBase=branches
buildtype=test:local

pushd $svnBase

base=ncibi-commons
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ncibi-db
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ncibi-webbackend
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-common
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi-common
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-client
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi-client
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-server-common
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=metab-ws-common
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=metab-ws-client
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi
pushd $base
$svn update
pbuild $buildtype
popd

base=metab-ws-server
pushd $base
$svn update
pbuild $buildtype
popd

base=ws-ncibi-executor
pushd $base
$svn update
pbuild $buildtype
popd

base=metscape2-plugin
pushd $base
$svn update
pbuild $buildtype
popd

base=mimi-plugin
pushd $base
$svn update
mvn -q -Dmaven.test.skip=true package
popd

popd