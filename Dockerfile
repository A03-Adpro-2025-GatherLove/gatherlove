# Use the eclipse-temurin image as the base
FROM eclipse-temurin:21

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

# Copy the source code
COPY src src

# Grant execute permission for the Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Expose the application port
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "build/libs/gatherlove-0.0.1-SNAPSHOT.jar"]