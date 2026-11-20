FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM tomcat:10.1-jdk21

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/target/taskmanager.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 10000

CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"10000\"/' /usr/local/tomcat/conf/server.xml && catalina.sh run"]
