# Stage 1: Build the app with Maven
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
# Copy the backend directory (contains pom.xml and src)
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]