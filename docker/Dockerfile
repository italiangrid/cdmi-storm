FROM centos:7

ARG YAML_CONFIG_FILE
ARG STORM_CONFIG_FILE
ARG STORM_CAPABILITIES_FILE
ARG DEBUG_OPTS

ARG CDMI_SERVER_PORT

RUN yum update -y
RUN yum install -y wget git maven java-1.8.0-openjdk-devel

#WORKDIR /etc/yum.repos.d
#RUN wget http://repo.indigo-datacloud.eu/repos/2/indigo2.repo
#RUN rpm --import http://repo.indigo-datacloud.eu/repository/RPM-GPG-KEY-indigodc
#RUN yum install -y cdmi-server

RUN wget https://ci.cloud.cnaf.infn.it/view/storm/job/CDMI/lastSuccessfulBuild/artifact/cdmi-server-1.2-1.fc25.x86_64.rpm
RUN yum localinstall -y cdmi-server-1.2-1.fc25.x86_64.rpm

# build storm plugin jar with all dependencies
COPY . /cdmi-storm
WORKDIR /cdmi-storm
RUN mvn clean package

# copy plugin to plugins directory
RUN cp target/cdmi-storm-*-jar-with-dependencies.jar /usr/lib/cdmi-server/plugins
RUN chown -R cdmi:cdmi /usr/lib/cdmi-server/plugins

EXPOSE ${CDMI_SERVER_PORT} 9999

USER cdmi

COPY ${YAML_CONFIG_FILE} /var/lib/cdmi-server/config/
COPY ${STORM_CONFIG_FILE} /etc/cdmi-server/plugins/storm-properties.json
COPY ${STORM_CAPABILITIES_FILE} /etc/cdmi-server/plugins/storm-capabilities.json

WORKDIR /var/lib/cdmi-server
ENV JAVA_OPTS "$DEBUG_OPTS -Dloader.path=/usr/lib/cdmi-server/plugins/"

CMD ./cdmi-server-1.2.jar