# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original repoDataSource file name.
#-renamesourcefileattribute SourceFile



# Can't find referenced class java.lang.invoke.LambdaMetafactory
-dontwarn java.lang.invoke.*


# OkHttp3
# See https://github.com/square/okio/issues/60
-dontwarn okhttp3.**
-dontwarn okio.**

# Dragger2
# That annotation is intentional there, it's used by ErrorProne. ErrorProne annotations have no use at runtime, so it's fine to ignore them in Proguard.
# https://github.com/google/dagger/issues/645
# https://github.com/google/dagger/pull/795
-dontwarn com.google.errorprone.annotations.*

# Glide
# See https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}