FROM eclipse-temurin:23-jdk AS build
WORKDIR /app
COPY backend/.mvn ./.mvn
COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
