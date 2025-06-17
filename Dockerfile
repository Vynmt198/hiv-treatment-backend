FROM openjdk:25-ea-24-slim
COPY target/medical-0.0.1-SNAPSHOT.jar medical-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/medical-0.0.1-SNAPSHOT.jar"]