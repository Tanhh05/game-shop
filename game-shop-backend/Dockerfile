FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -Dserver.address=0.0.0.0 -jar app.jar"]