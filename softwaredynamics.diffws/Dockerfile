# Version: 0.0.1
FROM openjdk:8-alpine
MAINTAINER Veit Frick "veit.frick@aau.at"
ARG version
COPY target/at.aau.softwaredynamics.diffws-${version}.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8080
