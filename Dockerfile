# Etapa 1: Build da aplicação
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime da aplicação
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Variáveis padrão (sobrescrevíveis)
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV APP_PORT=8080

EXPOSE ${APP_PORT}
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]