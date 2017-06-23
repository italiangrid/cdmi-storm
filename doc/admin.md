# Deployment and Administration guide

This is the INDIGO CDMI StoRM Backend Plugin deployment and administration guide.

## Prerequisites

INDIGO DataCloud CDMI server runs on RHEL7 platforms.
Our deployment tests have been done on CentOS7.

## Install

Install INDIGO repository:

    cd /etc/yum.repos.d
    wget http://repo.indigo-datacloud.eu/repos/2/indigo2.repo
    rpm --import http://repo.indigo-datacloud.eu/repository/RPM-GPG-KEY-indigodc

Install cdmi-storm:

    yum install -y cdmi-storm

## Configure

Edit CDMI server main configuration file by specifying storm as storage backend type:

    vim /var/lib/cdmi-server/config/application.yml

Change:

    cdmi:
      qos:
        backend:
          type: storm

Edit StoRM plugin configuration file:

    vim /etc/cdmi-server/plugins/storm-properties.json

Insert StoRM Backend info and the list of the Virtual File System for which CDMI server can negotiate QoS:

    {
        "backend": {
            "hostname": "storm.cnaf.infn.it",
            "port": 9998,
            "token": "mustbesecret"
        },
        "vfs": [
            {
                "voName": "test.vo",
                "readScope": "testvo:read",
                "recallScope": "testvo:recall",
                "iamGroup": "test.vo-users",
                "path": "/test.vo"
            }
        ]
    }

The backend properties that can be set are:

| **Property** | **Description** |
|:-------------|:----------------|
| **hostname** | StoRM Backend hostname to contact to retrieve file's metadata info and trigger file recalls from tape. |
| **port** | StoRM Backend REST interface port. Default: **9998**. |
| **token** | A secret token used to authorize REST requests. Use the value of the xmlrpc token used by the referenced StoRM instance (see [StoRM SysAdmin Guide](http://italiangrid.github.io/storm/documentation/sysadmin-guide/1.11.11/#GeneralYAIMvariables)). |

A list of the Virtual File Systems supported has to be specified with ```vfs```. 
Each element of the list has the following properties:

| **Property** | **Description** |
|:-------------|:----------------|
| **voName** | The name of the Virtual Organization associated to the VFS. |
| **readScope** | The OAuth scope that allows user to be authorized to get the status of a resource. |
| **recallScope** | The OAuth scope that allows user to be authorized to change the status of a resource. |
| **iamGroup** | The IAM group that allows user to be authorized both to get and to change the status of a resource. |
| **path** | The access point path from root to access the VFS. It must be the corresponding value of the StFN root for that VFS. |

## Run

Run CDMI server:

    systemctl start cdmi-server.service