FROM openjdk:17-jdk-alpine

WORKDIR /app

# Cài curl
RUN apk add --no-cache curl

# Copy Maven Wrapper trước
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build
RUN ./mvnw clean package -DskipTests

EXPOSE 8080
CMD ["java", "-jar", "target/cinema-system-1.0-SNAPSHOT.jar"]