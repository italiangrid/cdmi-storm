# INDIGO-DataCloud CDMI StoRM plugin

The Java Service Provider implementation for CDMI StoRM storage back-end plugin.
Runnable within [INDIGO CDMI server](https://github.com/indigo-dc/CDMI).

## Build

```bash
mvn clean package
```

Build with all dependencies:

```bash
mvn -DdescriptorId=jar-with-dependencies assembly:single
```

## Run with Docker

Build cdmi-server image:

```bash
docker build -t cdmi-server docker/cdmi-server
```

Create a ```.env``` file with the following environment variables:

Name | Description
--- | ---
**YAML\_CONFIG\_FILE** | Path to application.yml file
**STORM\_CONFIG\_FILE** | Path to storm.properties file
**STORM\_CAPABILITIES\_FILE** | Path to storm-capabilities.json
**STORM\_BACKEND\_HOSTNAME** | Hostname of the extra host to add
**STORM\_BACKEND\_IPADDR** | IP addresso of the extra host to add
**EXTERNAL\_PORT** | Local port where CDMI server port will be map
**CDMI\_SERVER\_PORT** | CDMI server exposed port

Run docker-compose:

```bash
docker-compose up --build
```
