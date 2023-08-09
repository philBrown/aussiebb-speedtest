FROM gradle:8.2.1-jdk17 AS build

WORKDIR /project

#COPY gradle.* gradlew ./
#COPY gradle ./gradle

#RUN ./gradlew --version

COPY . .

RUN gradle installDist

FROM azul/zulu-openjdk-alpine:17-jre-headless AS runtime

WORKDIR /app

COPY --from=build /project/build/install/aussiebb-speedtest .

CMD ./bin/aussiebb-speedtest