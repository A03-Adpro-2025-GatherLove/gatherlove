#!/bin/bash
# Force Gradle to use Java 21 instead of Java 24
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
echo "Using Java at: $JAVA_HOME"
java -version
echo "Running Gradle command: ./gradlew $@"
./gradlew "$@"
