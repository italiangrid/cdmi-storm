# Developer guide

## How to build

The CDMI StoRM is a Java application.

To build it, you will need:

- Java 8
- Maven

Build command:

  mvn package

## Setting up the docker-based development environment

You will need:

- Docker 1.11.1
- Docker compose >= 1.7

Start the development/testing environment with the following command:

```bash
TAG=devel docker-compose up --build
```

Use TAG=devel to build and manually install cdmi-storm by manually move the jar file.
Use TAG=deploy to generate rpm and install it before launching the CDMI server.

The docker-compose.yml file requires that you set some environment variables
for it to run properly, mainly to provide StoRM Backend remote hostname, port 
and token and the list of supported VO with their relative stfnRoots.

Create a ```.env``` file with the following environment variables:

Name | Description
--- | ---
**TAG** | Switch between Dockerfiles. Values: devel, deploy.
**YAML\_CONFIG\_FILE** | Path to application.yml file
**STORM\_CONFIG\_FILE** | Path to storm.properties file
**STORM\_CAPABILITIES\_FILE** | Path to storm-capabilities.json
**STORM\_BACKEND\_HOSTNAME** | Hostname of the extra host to add
**STORM\_BACKEND\_IPADDR** | IP addresso of the extra host to add
**EXTERNAL\_PORT** | Local port where CDMI server port will be mapped
**CDMI\_SERVER\_PORT** | CDMI server exposed port

The setup also assumes that you have an entry in your DNS server (complex) or
/etc/hosts (simpler) that maps cdmi-server.local.io to the machine (or VM) where 
docker is running, e.g.:

```bash
$ cat /etc/hosts
...

192.168.99.100 cdmi-server.local.io
```
