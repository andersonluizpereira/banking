# Stage 1: Build the application
FROM maven:3.9.2-openjdk-21 as build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o arquivo pom.xml e baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar o código-fonte
COPY src ./src

# Empacotar a aplicação (skip tests para acelerar o build)
RUN mvn clean package -DskipTests

# Stage 2: Executar a aplicação
FROM openjdk:21-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado da etapa de build
COPY --from=build /app/target/banking-system-1.0.0.jar app.jar

# Expor a porta que a aplicação irá rodar
EXPOSE 8080

# Definir a entrada da aplicação
ENTRYPOINT ["java","-jar","app.jar"]
