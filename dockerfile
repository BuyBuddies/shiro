# --- Build Stage ---
# Use the Azul Zulu OpenJDK 23 base image to match your local dev environment.
FROM azul/zulu-openjdk:23-jdk as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests
RUN ./mvnw package -DskipTests


# --- Final Stage ---
# Use the slim Azul Zulu JRE-only image for the final container.
FROM azul/zulu-openjdk:23-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]