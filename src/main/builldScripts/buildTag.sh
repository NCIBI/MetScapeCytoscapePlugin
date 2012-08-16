#!/bin/bash

# Note: for all package builds, pbuild (part of the config package)
#   must be used to build the final, deployable package (*.tar.gz) and
#   pdeploy (and in the config package) must be used to deploy that package

svnBase=tag
buildtype=production:local

pushd $svnBase

base=ncibi-commons
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ncibi-db
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ncibi-webbackend
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-common
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi-common
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-client
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi-client
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-server-common
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=metab-ws-common
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=metab-ws-client
pushd $base
mvn -q -Dmaven.test.skip=true install
popd

base=ws-ncibi
pushd $base
pbuild $buildtype
popd

base=metab-ws-server
pushd $base
pbuild $buildtype
popd

base=ws-ncibi-executor
pushd $base
pbuild $buildtype
popd

base=metscape2-plugin
pushd $base
pbuild $buildtype
popd

base=mimi-plugin
pushd $base
mvn -q -Dmaven.test.skip=true package
popd

popd