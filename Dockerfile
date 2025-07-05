# Estágio de build: Compila a aplicação Spring Boot
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ ./src/
RUN ./mvnw package -DskipTests

# Estágio de execução: Cria a imagem final com o JAR compilado
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Define a porta que o Cloud Run irá expor.
# O Spring Boot por padrão escuta na porta 8080, mas é bom ser explícito.
ENV PORT 8080

# Comando para executar a aplicação Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]