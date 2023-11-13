mvn clean
mvn install
rm -rf target || true
mvn -B package --file pom.xml
java -jar target/demo-1.0.jar 1