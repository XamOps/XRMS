# STAGE 1: Build the application using Maven and JDK 17
FROM maven:3.9-eclipse-temurin-17 AS builder
LABEL stage=builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper files and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Fix permission issue
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the rest of your application's source code
COPY src ./src

# Package the application (skip tests for faster builds)
RUN ./mvnw package -DskipTests

# STAGE 2: Create the runtime image using JRE 17
FROM eclipse-temurin:17-jre-focal
# Set the working directory in the container

# Set the working directory in the container
WORKDIR /app