# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/cc/.android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#Realm
# R8 Configuration for Realm
-keep @interface io.realm.annotations.RealmModule { *; }
-keep class io.realm.annotations.RealmModule { *; }

-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-keepnames public class * extends io.realm.RealmObject
-keep public class * extends io.realm.RealmObject { *; }
-keep class io.realm.** { *; }
