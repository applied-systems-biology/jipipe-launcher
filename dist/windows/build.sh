#!/bin/bash

OPEN_JDK_URL=https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u402-b06/OpenJDK8U-jre_x64_windows_hotspot_8u402b06.zip
LAUNCH4J_URL=https://kumisystems.dl.sourceforge.net/project/launch4j/launch4j-3/3.50/launch4j-3.50-linux-x64.tgz

# Delete the source JARs
rm ../../jipipe-launcher-app/target/*-sources.jar
rm ../../jipipe-launcher-app/target/original-*.jar

rm ../../jipipe-launcher-installer/target/*-sources.jar
rm ../../jipipe-launcher-installer/target/original-*.jar

# Prepare Launch4J
if [ -d "launch4j" ]; then
  echo "launch4j directory detected"
else
  echo "launch4j directory not detected. Downloading $LAUNCH4J_URL"
  wget -O launch4j.tar.gz "$LAUNCH4J_URL"
  tar -xvf launch4j.tar.gz
  rm launch4j.tar.gz
  dos2unix launch4j/launch4j
  dos2unix launch4j/launch4jc
fi

# Prepare JRE
if [ -d "jre" ]; then
  echo "JDK directory detected"
else
  echo "JDK directory not detected. Downloading $OPEN_JDK_URL"
  wget -O jre.zip "$OPEN_JDK_URL"
  unzip jre.zip
  mv jdk8u* jre
  rm jre.zip
fi

# # JIPipe Launcher
# LAUNCHER_APPDIR="$PWD/JIPipeLauncher.AppDir"
# mkdir -p "$LAUNCHER_APPDIR/usr/bin"
# cp -rv jre "$LAUNCHER_APPDIR/usr/bin/jre"
# cp -v jipipe.png "$LAUNCHER_APPDIR"
# cp -v jipipe-launcher.desktop "$LAUNCHER_APPDIR"
# cp -v jipipe-launcher "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher"
# cp -v ../../jipipe-launcher-app/target/*.jar "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher.jar"
# cp -v AppRun "$LAUNCHER_APPDIR"

# chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher"
# chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-launcher.jar"

# rm JIPipeLauncher.AppImage
# ./appimagetool-x86_64.AppImage JIPipeLauncher.AppDir JIPipeLauncher.AppImage

# # JIPipe Installer
# LAUNCHER_APPDIR="$PWD/JIPipeInstaller.AppDir"
# mkdir -p "$LAUNCHER_APPDIR/usr/bin"
# cp -rv jre "$LAUNCHER_APPDIR/usr/bin/jre"
# cp -v jipipe.png "$LAUNCHER_APPDIR"
# cp -v jipipe-installer.desktop "$LAUNCHER_APPDIR"
# cp -v jipipe-installer "$LAUNCHER_APPDIR/usr/bin/jipipe-installer"
# cp -v ../../jipipe-launcher-installer/target/*.jar "$LAUNCHER_APPDIR/usr/bin/jipipe-installer.jar"
# cp -v AppRun "$LAUNCHER_APPDIR"

# chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-installer"
# chmod +x "$LAUNCHER_APPDIR/usr/bin/jipipe-installer.jar"

# rm JIPipeInstaller.AppImage
# ./appimagetool-x86_64.AppImage JIPipeInstaller.AppDir JIPipeInstaller.AppImage