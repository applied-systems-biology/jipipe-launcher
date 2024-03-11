#!/bin/bash

OPEN_JDK_URL=https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u402-b06/OpenJDK8U-jre_x64_linux_hotspot_8u402b06.tar.gz
APPIMAGE_TOOL_URL="https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
APPRUN_URL="https://github.com/AppImage/AppImageKit/releases/download/continuous/AppRun-x86_64"

# Delete the source JARs
rm ../../jipipe-launcher-app/target/*-sources.jar
rm ../../jipipe-launcher-app/target/original-*.jar

rm ../../jipipe-launcher-installer/target/*-sources.jar
rm ../../jipipe-launcher-installer/target/original-*.jar

# Prepare AppImageTool

if [ -e "appimagetool-x86_64.AppImage" ]; then
  echo "AppImageTool found"
else
  wget -O appimagetool-x86_64.AppImage "$APPIMAGE_TOOL_URL"
  chmod +x ./appimagetool-x86_64.AppImage
fi

# Prepare AppRun
if [ -e "JIPipeInstaller.AppDir/AppRun" ]; then
  echo "AppRun found"
else 
  wget -O JIPipeInstaller.AppDir/AppRun "$APPRUN_URL"
  chmod +x JIPipeInstaller.AppDir/AppRun
fi 

# JIPipe Boostrap (Installer)
chmod +x JIPipeInstaller.AppDir/usr/bin/jipipe-installer

rm JIPipeInstaller.AppImage
ARCH=x86_64 ./appimagetool-x86_64.AppImage JIPipeInstaller.AppDir JIPipeInstaller.AppImage
