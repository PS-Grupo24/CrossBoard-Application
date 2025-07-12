FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

EXPOSE 8080


COPY server/build/libs/server-all.jar ./app.jar

COPY composeApp/build/dist/wasmJs/productionExecutable/ ./static/

ENTRYPOINT ["java", "-jar", "app.jar"]