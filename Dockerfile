FROM openjdk:8
ADD ./target/primalounas.backend-0.0.1-SNAPSHOT.jar /usr/src/primalounas.backend-0.0.1-SNAPSHOT.jar
WORKDIR usr/src
ENTRYPOINT ["java","-jar", "primalounas.backend-0.0.1-SNAPSHOT.jar"]
