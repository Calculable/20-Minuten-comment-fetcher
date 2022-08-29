FROM maven:3-openjdk-17
ARG JAR_FILE=*.jar
COPY . .
RUN mvn package
ENTRYPOINT ["java", "-jar", "./target/newspaper-comment-to-database-fetcher-0.0.1-SNAPSHOT.jar"]