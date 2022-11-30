FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /workspace/app
COPY src src
COPY gradle gradle
COPY build.gradle .
COPY gradlew .
COPY settings.gradle .
RUN --mount=type=cache,target=/root/.m2 ./gradlew build --console plain -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre-jammy
VOLUME /app
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "nl.abnamro.assessment.Application"]