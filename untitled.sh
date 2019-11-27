#!/bin/sh
rm build/libs/function.zip
rm build/libs/state-machine-0.2.1-SNAPSHOT
rm build/libs/state-machine-0.2.1-SNAPSHOT.jar

./gradlew shadowJar
/home/kim/Downloads/graalvm-ce-java8-19.3.0/bin/native-image --no-server -jar build/libs/state-machine-0.2.1-SNAPSHOT.jar
mv state-machine-0.2.1-SNAPSHOT build/libs/state-machine-0.2.1-SNAPSHOT
zip -j build/libs/function.zip build/libs/bootstrap build/libs/state-machine-0.2.1-SNAPSHOT
aws lambda update-function-code --function-name test3-state-machine-graalvm8 --zip-file fileb://build/libs/function.zip
aws lambda invoke --function-name test3-state-machine-graalvm8 output.txt
cat output.txt