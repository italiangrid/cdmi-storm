FROM maven:3.3.9-jdk-8

RUN mkdir -p /CDMI
RUN git clone https://github.com/indigo-dc/CDMI.git SOURCES

WORKDIR /SOURCES
RUN mvn clean package
RUN cp target/cdmi-server-*.jar /CDMI
WORKDIR /CDMI
RUN mkdir -p config