#backend
FROM openjdk:19-jre-slim
EXPOSE 5770
ARG JAR_FILE=target/sk-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]