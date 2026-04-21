#!/bin/bash

echo "=== Waiting for emulator ==="
adb wait-for-device
adb shell input keyevent 82
sleep 5

echo "=== Installing APK ==="
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "=== Screenshot: launcher (before launch) ==="
adb shell screencap -p /sdcard/00_launcher.png
adb pull /sdcard/00_launcher.png 00_launcher.png

echo "=== Clearing logcat before launch ==="
adb logcat -c

echo "=== Launching app ==="
adb shell am start -n com.ap711.soberlock/.MainActivity
sleep 8

echo "=== Capturing logcat immediately after launch ==="
adb logcat -d > full_logcat.txt
grep -E "AndroidRuntime|FATAL EXCEPTION|Process.*soberlock|Error|Exception" full_logcat.txt | tail -40 > crash_logcat.txt || true
echo "--- Crash logcat ---"
cat crash_logcat.txt

echo "=== Screenshot: after launch attempt ==="
adb shell screencap -p /sdcard/01_after_launch.png
adb pull /sdcard/01_after_launch.png 01_after_launch.png

echo "=== Window focus check ==="
adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp' > focus.txt
cat focus.txt

echo "=== Package activity check ==="
adb shell dumpsys activity activities | grep -E "soberlock|Resumed|Paused" | head -10

echo "=== Check if app process is running ==="
adb shell ps | grep soberlock || echo "APP PROCESS NOT FOUND - DEFINITELY CRASHED"

echo "=== All diagnostics captured ==="
