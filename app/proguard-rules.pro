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
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep all data classes
-keep class * extends kotlin.coroutines.jvm.internal.SuspendLambda
-keep class kotlin.Metadata { *; }

# Hilt
-keep class * extends androidx.lifecycle.ViewModel
-keep class * implements dagger.hilt.EntryPoint
-keep class * implements dagger.hilt.InstallIn
-keep @dagger.hilt.InstallIn class *
-keep @dagger.hilt.DefineComponent class *

# Firebase
-keep class com.google.firebase.** { *; }
-keep class org.json.JSONObject { *; }

# Coil
-keep class coil.memory.MemoryCache { *; }
-keep class coil.disk.DiskCache { *; }
-keep class coil.fetch.Fetcher { *; }
-keep class coil.decode.Decoder { *; }
-keep class coil.request.ImageRequest { *; }
-keep class coil.request.ImageResult { *; }
