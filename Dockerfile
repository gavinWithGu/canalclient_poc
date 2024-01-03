ARG REPOSITORY=***
ARG IMAGE_NAME=openjdk
ARG IMAGE_TAG=8u171-jdk-alpine3.8
FROM ${REPOSITORY}/${IMAGE_NAME}:$IMAGE_TAG
LABEL maintainer="<guangyin.gu@jch.com>" \
      author="Gavin" \
      jch.cs.cloud.backendservice="kumamerge"


RUN mkdir -p /data/workspace
COPY target/*.jar /data/workspace/APP.jar
WORKDIR /data/workspace
EXPOSE 8030
ENV JAVA_OPTS="-server"
CMD java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar APP.jar
