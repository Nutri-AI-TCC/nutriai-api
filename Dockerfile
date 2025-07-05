# Estágio de build: Compila a aplicação Spring Boot
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw #
RUN ./mvnw dependency:go-offline
COPY src/ ./src/
RUN ./mvnw package -DskipTests

# Estágio de execução: Cria a imagem final com o JAR compilado
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT 8080
ENTRYPOINT ["java","-jar","app.jar"]