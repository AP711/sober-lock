#!/bin/bash

echo "=== Waiting for emulator ==="
adb wait-for-device
sleep 8
adb shell input keyevent 82
sleep 3

echo "=== Installing APK ==="
adb install -r app/build/outputs/apk/debug/app-debug.apk
sleep 2

echo "=== Screenshot: before launch ==="
adb shell screencap -p /sdcard/00_before.png
adb pull /sdcard/00_before.png 00_before.png

echo "=== Clearing logcat ==="
adb logcat -c
sleep 1

echo "=== Launching app ==="
adb shell am start -W -n com.ap711.soberlock/.MainActivity
sleep 8

echo "=== Capturing logcat ==="
adb logcat -d > logcat_full.txt
grep -i "soberlock\|fatal\|exception\|error" logcat_full.txt | tail -50 > logcat_filtered.txt || true
cat logcat_filtered.txt

echo "=== Screenshot: after launch ==="
adb shell screencap -p /sdcard/01_after_launch.png
adb pull /sdcard/01_after_launch.png 01_after_launch.png

echo "=== Process check ==="
adb shell ps -A | grep soberlock || echo "NO APP PROCESS - CRASHED"

echo "=== Window state ==="
adb shell dumpsys window displays | grep -E "mCurrentFocus|soberlock" | head -5 || true

echo "=== Done - check artifacts for screenshots and logcat ==="
