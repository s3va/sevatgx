# == COMMON ==

# Keep native methods
-keepclasseswithmembers class * {
  native <methods>;
}
# Keep inflated classes
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet, int);
}
# Keep items annotated with @Keep
-keep @androidx.annotation.Keep public class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
# Keep metadata for crashes
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# == TELEGRAM X ==

# Keep native bridge
-keep class org.thunderdog.challegram.N { *; }
-keep class org.thunderdog.challegram.N$* { *; }
-keepclassmembers class org.thunderdog.challegram.N { *; }
# Keep log
-keep class org.thunderdog.challegram.Log
-keepclassmembers class org.thunderdog.challegram.Log { *; }
# Keep all related to VoIP
-keep class org.thunderdog.challegram.voip.**
-keepclassmembers class org.thunderdog.challegram.voip.** { *; }
# Keep sync services
-keep class org.thunderdog.challegram.sync.**

# == THIRDPARTY ==

# MP4Parser
-keep class * implements com.coremedia.iso.boxes.Box { *; }

# https://github.com/leolin310148/ShortcutBadger/blob/master/ShortcutBadger/proguard-rules.pro
-keep class me.leolin.shortcutbadger.impl.** {
  <init>(...);
}

# https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
# https://github.com/square/okio/blob/master/okio/src/jvmMain/resources/META-INF/proguard/okio.pro
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.ConscryptHostnameVerifier

# TODO remove once fixed in Android Gradle Plugin
-dontoptimize