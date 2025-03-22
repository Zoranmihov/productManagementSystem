# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Give executable permissions to mvnw BEFORE using it
RUN chmod +x mvnw

# Download dependencies (will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Grant full permissions to all files (only if really necessary)
RUN chmod -R 777 /app

# Package the application
RUN ./mvnw clean package -DskipTests

# Debugging step to list contents of target directory
RUN ls -l target

# Optionally, uncomment the following line to run the application with hot reloading for development
ENTRYPOINT ["./mvnw", "spring-boot:run"]
