#!/bin/bash
jarsigner -verbose -keystore bp-android.keystore "$1" bp-android
echo ""
echo ""
echo "Checking if APK is verified..."
jarsigner -verify "$1"
