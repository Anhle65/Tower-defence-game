FROM ubuntu:latest
LABEL authors="ale155"

RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app

COPY app/build.gradle /usr/src/app/
COPY settings.gradle .
COPY gradlew .

RUN apt-get update &&  \
    apt-get install -y openjdk-21-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN chmod +x ./gradlew
COPY app .
RUN ./gradlew build --console=plain --quiet
CMD ["./gradlew", "run"]