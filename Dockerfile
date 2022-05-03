FROM openjdk:11-jre-slim

LABEL NSD=support@nsd.no

ENV PROFILE=stage
ENV QDDT_DB_HOST=host.docker.internal
ENV QDDT_DB_NAME=qddt-dev

EXPOSE 5001
VOLUME /data/uploads-to-qddt/

COPY ./build/libs/qddt-1.0.jar /QDDT.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /QDDT.jar
