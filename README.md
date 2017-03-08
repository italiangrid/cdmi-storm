# StoRM CDMI Storage Interface Implementation

## Build

```bash
mvn clean package
```

Build with all dependencies:

```bash
mvn -DdescriptorId=jar-with-dependencies assembly:single
```

## Run with Docker

Build cdmi-server Docker image:

```bash
docker build -t cdmi-server docker/cdmi-server
```

Run docker-compose:

```bash
docker-compose up --build
```

The configuration file used is `docker/config/application.yml`
