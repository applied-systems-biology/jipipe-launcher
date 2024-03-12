#!/bin/bash

JRE_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/openjdk-8-macos.tar.gz"
UPDATER_URL="https://github.com/applied-systems-biology/jipipe-launcher/releases/download/current/jipipe-launcher.jar"

INSTANCE_DIR="$HOME/Library/Application Support/JIPipe Launcher/launcher"
JRE_EXE="$INSTANCE_DIR/jre/Contents/Home/bin/java"

if [ ! -e "$JRE_EXE" ] ; then
    # =================================================================
    echo "Removing old installations ... (instances are preserved)"
    if [ -e "$INSTANCE_DIR" ]; then
        rm -rvf "$INSTANCE_DIR" || exit 1
    fi

    # =================================================================
    echo "PROGRESS:10"
    echo "Creating in $INSTANCE_DIR ..."
    mkdir -p "$INSTANCE_DIR" || exit 1

    # =================================================================
    echo "PROGRESS:20"
    echo "Creating in $INSTANCE_DIR ... downloading JRE (Adoptium 8u402b06)"
    curl -sLo "$INSTANCE_DIR/jre.tar.gz" "$JRE_URL"

    # =================================================================
    echo "PROGRESS:60"
    echo "Creating in $INSTANCE_DIR ... extracting JRE (Adoptium 8u402b06)"
    OLD_DIR=$PWD
    cd "$INSTANCE_DIR" || exit 1
    tar -xvf jre.tar.gz
    rm jre.tar.gz
    cd "$OLD_DIR" || exit 1

fi

# =================================================================
echo "PROGRESS:80"
echo "Creating in $INSTANCE_DIR ... downloading launcher"

rm "$INSTANCE_DIR/jipipe-launcher.jar.tmp"
echo "Downloading newest launcher ..."
curl -sLo "$INSTANCE_DIR/jipipe-launcher.jar.tmp" "$UPDATER_URL"

if [ -e "$INSTANCE_DIR/jipipe-launcher.jar.tmp" ]; then
    rm "$INSTANCE_DIR/jipipe-launcher.jar"
    mv "$INSTANCE_DIR/jipipe-launcher.jar.tmp" "$INSTANCE_DIR/jipipe-launcher.jar"
fi

if [ ! -e "$INSTANCE_DIR/jipipe-launcher.jar" ]; then
    echo "ALERT:JIPipe Installer|Failed to download launcher!"
fi

echo "PROGRESS:100"
echo "Launcher is ready. Starting now."

"$JRE_EXE" -Xdock:icon="$PWD/jipipe.png" -jar "$INSTANCE_DIR/jipipe-launcher.jar" &
disown

echo "QUITAPP"
exit 0