# delete previous artifacts
rm -fR application-1.0.0
rm -fR application.jsa
rm -fR app-classes.list

# create application jar
mvn -Dspring.aot.enabled=true clean package -Dmaven.test.skip=true

java -Djarmode=tools -jar application/target/application-1.0.0.jar extract

# dump classlist
java  -XX:DumpLoadedClassList=./app-classes.list -Dspring.context.exit=onRefresh -jar application-1.0.0/application-1.0.0.jar

java -Dspring.aot.enabled=true -Xshare:dump -XX:SharedClassListFile=./app-classes.list -XX:SharedArchiveFile=./application.jsa

