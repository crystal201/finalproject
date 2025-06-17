FROM openjdk:17-jdk-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
EXPOSE 8080
CMD ["./mvnw", "spring-boot:run", "-DskipTests"]