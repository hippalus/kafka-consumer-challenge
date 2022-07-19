FROM maven:3.8.5-openjdk-17 as module-build

WORKDIR /module/kafka

COPY . .

RUN mv ./src/main/resources/application-env.properties ./src/main/resources/application.properties 

RUN mvn clean package -DskipTests

FROM openjdk:17.0.2-jdk-slim as production

USER root

RUN apt-get update -y && apt-get install -y jq curl

USER 1000

WORKDIR /app

COPY --from=module-build --chown=1000:1000 /module/kafka/target/kafka-consumer-challenge.jar ./kafka-consumer-challenge.jar

COPY docker/consumer/bin/entrypoint.sh /entrypoint.sh

CMD [ "/entrypoint.sh" ]

FROM production as development

USER root

COPY --from=module-build /usr/share/maven /usr/share/maven
COPY --from=module-build --chown=1000:1000 /module/kafka /development
COPY --from=module-build --chown=1000:1000 /root/.m2 /opt/jboss/.m2

RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

USER 1000