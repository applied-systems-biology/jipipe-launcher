#!/bin/bash

LAUNCHER_LINUX_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/JIPipeLauncher.AppImage"
INSTALLER_LINUX_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/JIPipeInstaller.AppImage"

pushd linux || exit 1
./build.sh
popd || exit 1

rm -rv release
mkdir release

repo() {
    echo "$1" >> release/boostrap.json
}

repo "{"

# Copy linux
cp linux/JIPipeLauncher.AppImage release
cp linux/JIPipeInstaller.AppImage release

SHA1=$(sha1sum linux/JIPipeLauncher.AppImage | cut -d " " -f 1)
repo "    \"launcher-linux\": { \"url\": \"$LAUNCHER_LINUX_URL\", \"sha1\": \"$SHA1\" }," 

SHA1=$(sha1sum linux/JIPipeInstaller.AppImage | cut -d " " -f 1)
repo "    \"installer-linux\": { \"url\": \"$INSTALLER_LINUX_URL\", \"sha1\": \"$SHA1\" }" 

repo "}"
