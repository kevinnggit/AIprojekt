# Java Backend Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven and build
RUN apt-get update && apt-get install -y maven && \
    mvn -B clean package && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Find the JAR file and run it
CMD ["sh", "-c", "java -jar target/*.jar"]
