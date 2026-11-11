# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy source code
COPY src/ src/

# Compile all .java files into /app/out
RUN mkdir -p out && \
    find src/main/java -name "*.java" > sources.txt && \
    javac -d out @sources.txt

# Run stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy compiled classes
COPY --from=build /app/out/ classes/

# Data volume for task persistence
VOLUME /app/data

ENTRYPOINT ["java", "-cp", "classes", "taskreminder.Main"]
