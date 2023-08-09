FROM azul/zulu-openjdk-alpine:17 AS build

WORKDIR /project
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

COPY gradle.* gradlew ./
COPY gradle ./gradle

RUN ./gradlew --version

COPY . .

RUN ./gradlew installDist

FROM azul/zulu-openjdk-alpine:17-jre-headless AS runtime

WORKDIR /app

COPY --from=build /project/build/install/aussiebb-speedtest .

CMD ./bin/aussiebb-speedtest