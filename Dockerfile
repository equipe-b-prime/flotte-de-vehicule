FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests
CMD ["sh", "-c", "java -jar target/*.jar"]
