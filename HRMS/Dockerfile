# ======================
# STAGE 1: Build the application using Maven and JDK 17
# ======================
FROM maven:3.9-eclipse-temurin-17 AS builder
LABEL stage=builder

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and POM files first to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Pre-fetch dependencies to leverage Docker layer caching
RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copy the full source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests --no-transfer-progress

# ======================
# STAGE 2: Create the runtime image with JRE 17
# ======================
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Expose the application port
EXPOSE 8080

# Copy the packaged JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar


# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
