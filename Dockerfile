#FROM quay.io/wildfly/wildfly-centos7

#ADD jaxws-calculator.war /opt/wildfly/standalone/deployments/

#FROM maven:3.6.0-jdk-11-slim AS build
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean package




#FROM tomcat:8.0-alpine


#ADD springboot.war /usr/local/tomcat/webapps/

#EXPOSE 8080

#CMD ["catalina.sh", "run"]

FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


