%define __jar_repack    %{nil}
%define _tmppath    %{_topdir}/tmp
%define buildroot   %{_topdir}/build-rpm-root

%define name            cdmi-storm
%define jarversion      0.1.0
%define user            cdmi

Name:       %{name}
Version:    %{jarversion}
Release:    1%{?dist}
Summary:    StoRM CDMI backend plugin implementation.

Group:      Applications/Web
License:    apache2
URL:        https://github.com/italiangrid/cdmi-storm

Requires:   jre >= 1.8
Requires:   cdmi-server

%description
SNIA CDMI server StoRM Backend plugin implementation.
The StoRM CDMI QoS module enables the CDMI server to interact with a StoRM Backend instance. 
Through a REST endpoint, it retrieves files and directories metadata in order to return 
resources statuses and trigger recall requests for the nearline files.

%prep

%build

%install
mkdir -p %{buildroot}/usr/lib/cdmi-server/plugins
mkdir -p %{buildroot}/etc/cdmi-server/plugins
cp %{_topdir}/SOURCES/%{name}-%{jarversion}-jar-with-dependencies.jar %{buildroot}/usr/lib/cdmi-server/plugins
cp %{_topdir}/SOURCES/storm-capabilities.json %{buildroot}/etc/cdmi-server/plugins/storm-capabilities.json
cp %{_topdir}/SOURCES/storm-properties.json %{buildroot}/etc/cdmi-server/plugins/storm-properties.json

%files
/usr/lib/cdmi-server/plugins/%{name}-%{jarversion}-jar-with-dependencies.jar
/etc/cdmi-server/plugins/storm-capabilities.json
/etc/cdmi-server/plugins/storm-properties.json

%changelog

%post

chown -R %{user}:%{user} /usr/lib/cdmi-server/plugins/