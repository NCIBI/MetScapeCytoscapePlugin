#!/bin/bash

svn=/usr/local/bin/svn
echo using svn as $svn

svnBase=trunk
pushd $svnBase

base=ncibi-commons
echo $base
pushd $base
$svn update
popd

base=ncibi-db
echo $base
pushd $base
$svn update
popd

base=ncibi-webbackend
echo $base
pushd $base
$svn update
popd

base=ws-common
echo $base
pushd $base
$svn update
popd

base=ws-ncibi-common
echo $base
pushd $base
$svn update
popd

base=ws-client
echo $base
pushd $base
$svn update
popd

base=ws-ncibi-client
echo $base
pushd $base
$svn update
popd

base=ws-server-common
echo $base
pushd $base
$svn update
popd

base=metab-ws-common
echo $base
pushd $base
$svn update
popd

base=metab-ws-client
echo $base
pushd $base
$svn update
popd

base=ws-ncibi
echo $base
pushd $base
$svn update
popd

base=metab-ws-server
echo $base
pushd $base
$svn update
popd

base=ws-ncibi-executor
echo $base
pushd $base
$svn update
popd

base=metscape2-plugin
echo $base
pushd $base
$svn update
popd

base=mimi-plugin
echo $base
pushd $base
$svn update
popd

popd