# ---- Build stage ----
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app

# Copy only what we need for dependency caching first
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Prime Gradle cache (no tests here to keep image build predictable/fast)
RUN ./gradlew --no-daemon -q dependencies || true

# Copy sources and build
COPY src src
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Writable uploads directory inside container (override via env/volume)
ENV UPLOAD_DIR=/data/uploads

RUN mkdir -p /data/uploads

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

