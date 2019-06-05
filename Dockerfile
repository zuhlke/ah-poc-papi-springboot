FROM maven:3.6.1-jdk-8 AS build
WORKDIR app
COPY pom.xml .

RUN mvn dependency:go-offline


COPY src src/
RUN mvn package

FROM build as dev
ENTRYPOINT ["mvn", "spring-boot:run"]

FROM build as test
ENTRYPOINT ["mvn", "test"]


FROM openjdk:8
COPY --from=build /app/target/*.jar /app/papi.jar
EXPOSE ${PORT:-8080}
CMD ["java","-jar","/app/papi.jar"]
