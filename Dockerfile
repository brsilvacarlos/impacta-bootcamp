# Use uma imagem base com Maven e JDK
FROM maven:3.9-amazoncorretto-21-alpine as build

# Defina o diretório de trabalho no contêiner
WORKDIR /app

# Copie o arquivo pom.xml e o diretório src para o contêiner
COPY pom.xml /app
COPY src /app/src

# Execute o comando Maven para compilar o projeto e gerar o JAR
RUN mvn clean package -DskipTests

# Agora, crie uma imagem menor contendo apenas o JAR
FROM openjdk:21-slim

# Defina o diretório de trabalho no contêiner
WORKDIR /app

# Copie o JAR gerado no estágio anterior para a imagem final
COPY --from=build /app/target/prova-conceito-0.0.1-SNAPSHOT.jar /app/prova-conceito.jar

# Exponha a porta que a aplicação usará
EXPOSE 8080

# Comando para rodar a aplicação quando o contêiner for iniciado
ENTRYPOINT ["java", "-jar", "prova-conceito.jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]
