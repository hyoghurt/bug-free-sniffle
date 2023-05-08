FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

COPY tracker-main tracker-main
COPY tracker-security tracker-security
COPY tracker-controller tracker-controller
COPY tracker-dto tracker-dto
COPY tracker-dao tracker-dao
COPY tracker-service tracker-service

RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/tracker-main/target/app.jar app.jar
RUN addgroup appusers && adduser -S appuser -G appusers
USER appuser:appusers
ENTRYPOINT ["java","-jar","/app/app.jar"]
