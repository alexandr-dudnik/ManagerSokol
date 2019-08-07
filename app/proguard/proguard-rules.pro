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

-printusage usage.txt
-printseeds seeds.txt

-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keep class com.sokolua.manager.data.** { *; }

-keep class java.** { *; }


-keepattributes InnerClasses
-keepattributes RuntimeVisibleAnnotations
-keepattributes AnnotationDefault
-keepattributes *Annotation*


-dontwarn java.lang.annotation.Annotation

-ignorewarnings