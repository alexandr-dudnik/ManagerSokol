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

#OkHttp3
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okhttp3.internal.platform.*
-dontnote okhttp3.**
-dontwarn okio.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.Conscrypt
-dontnote org.conscrypt.Conscrypt

#org.conscrypt
-dontwarn org.conscrypt.*
-keep class org.conscrypt.* { *; }
-keep interface org.conscrypt.* { *; }

