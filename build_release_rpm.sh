#!/bin/bash
set -e

VERSION=0.1.0
NAME=cdmi-storm
TOPDIR=`pwd`/rpm

mvn clean package

mkdir -p $TOPDIR/SOURCES
cp target/$NAME-$VERSION-jar-with-dependencies.jar $TOPDIR/SOURCES
cp config/storm-capabilities.json $TOPDIR/SOURCES/storm-capabilities.json
cp config/storm-properties.json $TOPDIR/SOURCES/storm-properties.json
ls $TOPDIR/SOURCES/

rpmbuild --define "_topdir ${TOPDIR}" -ba $TOPDIR/SPECS/$NAME.spec

cp ${TOPDIR}/RPMS/x86_64/cdmi-storm-${VERSION}-1.el7.centos.x86_64.rpm .