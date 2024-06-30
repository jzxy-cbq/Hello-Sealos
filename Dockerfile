FROM bellsoft/liberica-openjre-alpine:17 AS layers
WORKDIR /application
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM bellsoft/liberica-openjre-alpine:17
VOLUME /tmp
RUN adduser -S spring-user
USER spring-user
COPY --from=layers /application/dependencies/ ./
COPY --from=layers /application/spring-boot-loader/ ./
COPY --from=layers /application/snapshot-dependencies/ ./
COPY --from=layers /application/application/ ./

ENV DB_USERNAME=YOUR_USERNAME
ENV DB_PASSWORD=YOUR_PWD
ENV DB_URL=YOUR_URL

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
