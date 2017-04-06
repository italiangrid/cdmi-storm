FROM cdmi-server

ARG YAML_CONFIG_FILE
ARG STORM_CONFIG_FILE
ARG STORM_CAPABILITIES_FILE

ARG CDMI_SERVER_PORT

# build storm plugin jar with all dependencies
COPY . /cdmi-storm
WORKDIR /cdmi-storm
RUN mvn clean package
RUN mvn -DdescriptorId=jar-with-dependencies assembly:single
# copy plugin to JAVA external directory
RUN cp target/cdmi-storm-*-jar-with-dependencies.jar ${JAVA_HOME}/jre/lib/ext/cdmi-storm.jar

# copy configuration files into config/ directory
COPY ${YAML_CONFIG_FILE} /CDMI/config/
COPY ${STORM_CONFIG_FILE} /CDMI/config/storm.properties
COPY ${STORM_CAPABILITIES_FILE} /CDMI/config/storm-capabilities.json

EXPOSE ${CDMI_SERVER_PORT}

WORKDIR /CDMI

CMD java -Dstorm.configFile=/CDMI/config/storm.properties -Dstorm.capabilitiesFile=/CDMI/config/storm-capabilities.json -jar cdmi-server-1.1.jar