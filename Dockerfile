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

# --- INÍCIO DAS ALTERAÇÕES PARA A WALLET ---

# 1. Cria um diretório para a wallet dentro da imagem final
RUN mkdir -p /app/wallet

# 2. Copia os arquivos da wallet do seu diretório local 'wallet/'
# para o diretório '/app/wallet/' dentro da imagem
COPY wallet/ /app/wallet/

# 3. Define a variável de ambiente TNS_ADMIN
# Isso informa ao driver JDBC da Oracle onde encontrar os arquivos da wallet
ENV TNS_ADMIN=/app/wallet



ENV PORT=8080
ENTRYPOINT ["java","-jar","app.jar"]