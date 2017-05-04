# User Guide

## Milestones

StoRM Backend can serve multiple Virtual Organizations.
As first implementation, StoRM CDMI plugin is designed to serve one and only Virtual Organization.
Users those want to know the QoS of a resource needs to own ```<vo-name>_read``` scope.
Users those want to manipulate QoS of a resource needs to own ```<vo-name>_write``` scope.

## Configure plugin

StoRM CDMI plugin needs the following configuration files:

* ```storm-properties.json```
* ```storm-capabilities.json```

Within cdmi-storm installation a template for each of them is created 
into the CDMI service plugins configuration directory ```/etc/cdmi-server/plugins```.

### storm-properties.json

It's the plugin configuration file. 
It contains info about StoRM Backend to contact and which VO has to be served.

| Variable name | Description |
|---------------|-------------|
| *backend.hostname* | StoRM Backend hostname to contact |
| *backend.port* | StoRM Backend REST port. Default: *9998* |
| *backend.token* | The XMLRPC token value already used by StoRM Backend |
| *organization.name* | The Virtual Organization served |
| *organization.paths* | List of stfn root paths to serve for that VO. The list of VO's storage area in brief. |

Example:

```
{
    "backend": {
        "hostname": "storm-backend.cnaf.infn.it}",
        "port": 9998,
        "token": "my-secret-token"
    },
    "organization": {
        "name": "test.vo",
        "paths": [
            "/test.vo"
        ]
    }
}
```

### storm-capabilities.json

It's the QoS profiles configuration. Here you can manipulate the metadata returned for each QoS profile.
Handle it with care.
