FROM bellsoft/liberica-openjdk-alpine:21.0.4-9-cds as reduced_optimizer

WORKDIR /app

COPY live-kitchen/application/target/*.jar myapp.jar

RUN java -Djarmode=tools -jar myapp.jar extract


FROM bellsoft/liberica-openjdk-alpine:21.0.4-9-cds

ARG DATASOURCE_URL=changeme
ARG CHEF_SERVER_URL=changeme

ENV DATASOURCE_URL=$DATASOURCE_URL
ENV CHEF_SERVER_URL=$CHEF_SERVER_URL

RUN echo "database url ${DATASOURCE_URL}"
RUN echo "chef server url ${CHEF_SERVER_URL}"

ENTRYPOINT ["java","-Dspring.aot.enabled=true","-Dspring.datasource.url=${DATASOURCE_URL}","-Dchefserver.host=${CHEF_SERVER_URL}", "-jar", "myapp/myapp.jar"]

COPY --from=reduced_optimizer /app ./

RUN java -Dspring.aot.enabled=true  -Dspring.datasource.url=${DATASOURCE_URL} -Dchefserver.host=${CHEF_SERVER_URL} -Dspring.context.exit=onRefresh -jar myapp/myapp.jar
