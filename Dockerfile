FROM openjdk:11-jre-slim

FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD

COPY setup.sh /root/greenpolestockbroker/setup.sh
RUN chmod +x /root/greenpolestockbroker/setup.sh
RUN /root/greenpolestockbroker/setup.sh

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package -U -Dmaven.test.skip=true
RUN ls /build/target
RUN cp /build/target/greenpole-stockbroker-0.0.1-SNAPSHOT.jar /opt/greenpolestockbroker



WORKDIR /


COPY install.sh /root/greenpolestockbroker/install.sh
RUN chmod +x /root/greenpolestockbroker/install.sh
CMD  /root/greenpolestockbroker/install.sh