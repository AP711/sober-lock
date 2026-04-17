# Sober Lock ProGuard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# AdMob
-keep class com.google.android.gms.ads.** { *; }
# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
# Keep the app's main classes
-keep class com.ap711.soberlock.** { *; }