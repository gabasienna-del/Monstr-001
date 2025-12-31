#!/bin/bash
echo "Инициализируем Gradle Wrapper..."
gradle wrapper --gradle-version 8.5 --distribution-type bin
chmod +x gradlew
