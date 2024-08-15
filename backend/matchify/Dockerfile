# Use an official Maven image as the base image
FROM maven:3.8.5-openjdk-17-slim AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package -DskipTests
# Use an official amazon JDK image as the base image
FROM amazoncorretto:17-al2023-jdk
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/*.jar /app/application.jar
# Expose port 8080 for the Spring Boot application
EXPOSE 8080
# Set the command to run the application
CMD ["java", "-jar", "application.jar"]