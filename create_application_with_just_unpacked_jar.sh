# delete previous artifacts
rm -fR application-1.0.0
rm -fR application.jsa

# create application jar
mvn clean package -Dmaven.test.skip=true

# extract libs from jar
java -Djarmode=tools -jar application/target/application-1.0.0.jar extract


echo "created application with just unpacked jar"

