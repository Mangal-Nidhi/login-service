FROM openjdk:11-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} login.jar
ENTRYPOINT ["java","-jar","/login.jar"]