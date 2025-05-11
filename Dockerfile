FROM bellsoft/liberica-openjdk-debian:17 AS build
WORKDIR /workspace/app

# Copy Gradle wrapper and other necessary files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN apt-get update && apt-get install -y git

RUN ./gradlew build -x test

RUN mkdir -p target/extracted

RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination target/extracted

FROM bellsoft/liberica-openjre-debian:17
VOLUME /tmp
WORKDIR /app
ARG EXTRACTED=/workspace/app/target/extracted

# Copy over the unpacked application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./

ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]