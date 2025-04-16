# Use a Maven image to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory inside container
WORKDIR /app

# Copy the entire project
COPY . .

# Package the application with dependencies
RUN mvn clean package

# Use a smaller JDK image for runtime
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the fat jar (with dependencies) from build stage
COPY --from=build /app/target/opium-dataframe-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Copy any additional resources if needed for the demo
COPY --from=build /app/src/test/java/fr/project/demo/demo.csv ./demo.csv

# Run the demo class
ENTRYPOINT ["java", "-cp", "app.jar:.", "fr.project.demo.Demo"]

