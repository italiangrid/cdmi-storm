FROM cdmi-server

ARG YAML_CONFIG_FILE

# build storm plugin jar with all dependencies
COPY . /storm-cdmi-spi
WORKDIR /storm-cdmi-spi
RUN mvn -DdescriptorId=jar-with-dependencies assembly:single
RUN cp target/storm-cdmi-spi-*-jar-with-dependencies.jar ${JAVA_HOME}/jre/lib/ext/storm-cdmi-spi.jar

# copy application.yml
COPY ${YAML_CONFIG_FILE} /CDMI/config/application.yml

WORKDIR /CDMI

CMD java -jar cdmi-server-1.1.jar