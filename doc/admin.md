# Deployment and Administration guide

This is the INDIGO CDMI StoRM Backend Plugin deployment and administration guide.

## Run

Build a jar with all the dependencies:

    mvn -DdescriptorId=jar-with-dependencies assembly:single

Copy it into java external library directory:

    cp target/cdmi-storm-*-jar-with-dependencies.jar ${JAVA_HOME}/jre/lib/ext/cdmi-storm.jar


Clone [INDIGO CDMI server](https://github.com/indigo-dc/CDMI) project.

    git clone https://github.com/indigo-dc/CDMI; cd CDMI

Build CDMI server:

    mvn clean package

Edit configuration file ```config/application.yml```.
Copy StoRM configuration files:

    docker/config/storm-capabilities.json
    docker/config/storm-properties.json

into CDMI server ```.config``` directory.

Run CDMI server:

    java -Dstorm.configFile=./config/storm-properties.json -Dstorm.capabilitiesFile=./config/storm-capabilities.json -jar target/cdmi-server-1.2.jar
