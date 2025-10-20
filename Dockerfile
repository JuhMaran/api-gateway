# ==========================================
# API GATEWAY - Dockerfile (LOCAL TESTS)
# ==========================================
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /app

# Copia pom.xml e dependências primeiro para cache eficiente
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte
COPY src ./src

# Gera o artefato
RUN mvn clean package -DskipTests

# =============================
# 🚀 Stage 2 — Runtime
# =============================
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

# Adiciona um usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Define variáveis de ambiente padrão
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SERVER_PORT=8080
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
ENV CORS_ALLOWED_ORIGINS=http://localhost:4200

# Health check básico
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health | grep '"status":"UP"' || exit 1

# Define o usuário de execução
USER spring

# Expondo porta
EXPOSE ${SERVER_PORT}

# Executa o app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]