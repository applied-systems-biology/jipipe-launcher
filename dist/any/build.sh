#!/bin/bash

LAUNCHER_JAR_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/jipipe-launcher.jar"
LAUNCHER_UPDATER_JAR_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/jipipe-launcher-updater.jar"

# Delete the source JARs
rm ../../jipipe-launcher-app/target/*-sources.jar
rm ../../jipipe-launcher-app/target/original-*.jar

rm ../../jipipe-launcher-updater/target/*-sources.jar
rm ../../jipipe-launcher-updater/target/original-*.jar

# Delete old release
rm boostrap.json
rm jipipe-launcher-updater.jar
rm jipipe-launcher.jar

# Copy new release
cp -v ../../jipipe-launcher-updater/target/*.jar jipipe-launcher-updater.jar
cp -v ../../jipipe-launcher-app/target/*.jar jipipe-launcher.jar

repo() {
    echo "$1" >> boostrap.json
}

repo "{"


SHA1=$(sha1sum jipipe-launcher.jar | cut -d " " -f 1)
repo "    \"launcher\": { \"url\": \"$LAUNCHER_JAR_URL\", \"sha1\": \"$SHA1\" }," 

SHA1=$(sha1sum jipipe-launcher-updater.jar | cut -d " " -f 1)
repo "    \"launcher-updater\": { \"url\": \"$LAUNCHER_UPDATER_JAR_URL\", \"sha1\": \"$SHA1\" }" 

repo "}"
