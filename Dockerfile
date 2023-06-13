FROM maven:3.9.1-eclipse-temurin-17-alpine as build
WORKDIR /app

COPY pom.xml .

COPY tracker-main tracker-main
COPY tracker-security tracker-security
COPY tracker-controller tracker-controller
COPY tracker-dto tracker-dto
COPY tracker-dao tracker-dao
COPY tracker-service tracker-service

RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/tracker-main/target/app.jar app.jar
RUN mkdir data
RUN addgroup appusers && adduser -S appuser -G appusers
RUN chown -R appuser:appusers /app
USER appuser:appusers
ENTRYPOINT ["java","-jar","/app/app.jar"]
