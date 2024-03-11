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
if [ -e "AppRun" ]; then
  echo "AppRun found"
else 
  wget -O AppRun "$APPRUN_URL"
  chmod +x AppRun
fi 

# Prepare JRE
if [ -d "jre" ]; then
  echo "JDK directory detected"
else
  echo "JDK directory not detected. Downloading $OPEN_JDK_URL"
  wget -O jre.tar.gz "$OPEN_JDK_URL"
  tar -xvf jre.tar.gz
  mv jdk8u* jre
  rm jre.tar.gz
fi

# JIPipe Launcher
LAUNCHER_APPDIR="$PWD/JIPipeLauncher.AppDir"
mkdir -p "$LAUNCHER_APPDIR/usr/bin"
cp -rv jre "$LAUNCHER_APPDIR/usr/bin/jre"
cp -v jipipe.png "$LAUNCHER_APPDIR"
cp -v jipipe-launcher.desktop "$LAUNCHER_APPDIR"
cp -v jipipe-launcher "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher"
cp -v ../../jipipe-launcher-app/target/*.jar "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher.jar"
cp -v AppRun "$LAUNCHER_APPDIR"

chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher"
chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher.jar"

rm JIPipeLauncher.AppImage
./appimagetool-x86_64.AppImage JIPipeLauncher.AppDir JIPipeLauncher.AppImage

# JIPipe Installer
LAUNCHER_APPDIR="$PWD/JIPipeInstaller.AppDir"
mkdir -p "$LAUNCHER_APPDIR/usr/bin"
cp -rv jre "$LAUNCHER_APPDIR/usr/bin/jre"
cp -v jipipe.png "$LAUNCHER_APPDIR"
cp -v jipipe-installer.desktop "$LAUNCHER_APPDIR"
cp -v jipipe-installer "$LAUNCHER_APPDIR/usr/bin/jipipe-installer"
cp -v ../../jipipe-launcher-installer/target/*.jar "$LAUNCHER_APPDIR/usr/bin/jipipe-installer.jar"
cp -v AppRun "$LAUNCHER_APPDIR"

chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-installer"
chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-installer.jar"

rm JIPipeInstaller.AppImage
./appimagetool-x86_64.AppImage JIPipeInstaller.AppDir JIPipeInstaller.AppImage