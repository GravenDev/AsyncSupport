FROM gradle:9.1.0-jdk17-corretto-al2023 AS builder

WORKDIR /build
COPY ./ /build

RUN gradle shadowjar

FROM amazoncorretto:17.0.19-alpine3.21

WORKDIR /home/GravenSupport
COPY --from=builder /build/build/libs/*-all.jar /GravenSupport.jar
VOLUME /home/GravenSupport/config.yml

ENTRYPOINT ["java","--enable-preview","-jar","/GravenSupport.jar"]