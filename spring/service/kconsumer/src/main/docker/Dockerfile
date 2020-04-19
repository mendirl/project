FROM adoptopenjdk:14-jdk-hotspot-bionic

ARG APP_SLEEP
ARG SPRING_OUTPUT_ANSI_ENABLED

ENV SPRING_OUTPUT_ANSI_ENABLED=${SPRING_OUTPUT_ANSI_ENABLED}
ENV APP_SLEEP=${APP_SLEEP}

COPY entrypoint.sh /entrypoint.sh
RUN chmod 755 /entrypoint.sh

COPY app.conf /app.conf
RUN chmod 644 /app.conf

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar
RUN chmod 755 /app.jar

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "-c", "/entrypoint.sh"]
