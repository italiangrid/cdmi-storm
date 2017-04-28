# Deployment and Administration guide

This is the INDIGO CDMI StoRM Backend Plugin deployment and administration guide.

## Install

Install INDIGO repository:

    cd /etc/yum.repos.d
    wget http://repo.indigo-datacloud.eu/repos/2/indigo2.repo
    rpm --import http://repo.indigo-datacloud.eu/repository/RPM-GPG-KEY-indigodc

Install cdmi-storm:

    yum install -y cdmi-storm

## Configure

Edit CDMI server configuration file ```config/application.yml```.
Edit StoRM plugin configuration files:

    /etc/cdmi-server/plugins/storm-properties.json
    /etc/cdmi-server/plugins/storm-capabilities.json

## Run

Run CDMI server:

    systemctl start cdmi-server.service