# delete previous artifacts
rm -fR application-1.0.0
rm -fR application.jsa

# create application jar
mvn clean package -Dmaven.test.skip=true

# extract libs from jar
java -Djarmode=tools -jar application/target/application-1.0.0.jar extract

# dump cds with aot enabled onRefresh and exit
java -Dspring.aot.enabled=true -XX:ArchiveClassesAtExit=./application.jsa -Dspring.context.exit=onRefresh -jar application-1.0.0/application-1.0.0.jar


