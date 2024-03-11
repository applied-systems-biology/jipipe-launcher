#!/bin/bash

OPEN_JDK_URL=https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u402-b06/OpenJDK8U-jre_x64_windows_hotspot_8u402b06.zip
WARP_PACKER_URL=https://github.com/dgiagio/warp/releases/download/v0.3.0/linux-x64.warp-packer
RESOURCE_HACKER_URL=https://www.angusj.com/resourcehacker/resource_hacker.zip

# Delete the source JARs
rm ../../jipipe-launcher-app/target/*-sources.jar
rm ../../jipipe-launcher-app/target/original-*.jar

rm ../../jipipe-launcher-installer/target/*-sources.jar
rm ../../jipipe-launcher-installer/target/original-*.jar

# Prepare warp-packer
if [ -e "warp-packer" ]; then
  echo "warp-packer detected"
else
  echo "warp-packer not detected. Downloading $WARP_PACKER_URL"
  wget -O warp-packer "$WARP_PACKER_URL"
  chmod +x warp-packer
fi

# Prepare resource hacker
if [ -e "ResourceHacker.exe" ]; then
  echo "ResourceHacker.exe detected"
else
  echo "ResourceHacker.exe not detected. Downloading $RESOURCE_HACKER_URL"
  wget -O resource_hacker.zip "$RESOURCE_HACKER_URL"
  unzip -d resource_hacker resource_hacker.zip
  cp resource_hacker/ResourceHacker.exe .
  rm -rvf resource_hacker resource_hacker.zip
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

# JIPipe Launcher
rm -rvf bundle
mkdir bundle
cp -rv jre bundle/jre
cp -v jipipe-launcher.cmd bundle/run.cmd
cp -v ../../jipipe-launcher-app/target/*.jar bundle/jipipe-launcher.jar
unix2dos bundle/run.cmd
./warp-packer --arch windows-x64 --input_dir bundle --exec run.cmd --output JIPipeLauncher.exe

rm JIPipeLauncher.original.exe
mv JIPipeLauncher.exe JIPipeLauncher.original.exe
wine64 ResourceHacker.exe -open JIPipeLauncher.original.exe -save JIPipeLauncher.exe -action addskip -res jipipe.ico -mask ICONGROUP,MAINICON,

exit

# JIPipe Installer
rm -rvf bundle
mkdir bundle
cp -rv jre bundle/jre
cp -v jipipe-installer.cmd bundle/run.cmd
cp -v ../../jipipe-launcher-installer/target/*.jar bundle/jipipe-installer.jar
unix2dos bundle/run.cmd
./warp-packer --arch windows-x64 --input_dir bundle --exec run.cmd --output JIPipeInstaller.exe

rm JIPipeInstaller.original.exe
mv JIPipeInstaller.exe JIPipeInstaller.original.exe
wine64 ResourceHacker.exe -open JIPipeInstaller.original.exe -save JIPipeInstaller.exe -action addskip -res jipipe.ico -mask ICONGROUP,MAINICON,

# Delete the bundles
rm -rvf bundle


