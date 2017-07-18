# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wross/Developer/Android/Android SDK/tools/proguard/proguard-android.txt
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

#Appsee
-keep class com.appsee.** { *; }
-dontwarn com.appsee.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keepattributes SourceFile,LineNumberTable

#Remove Logcat calls
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#Butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepnames class * { @butterknife.Bind *; }

#Dagger
-dontwarn dagger.internal.codegen.**
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#Facebook
-keep class com.facebook.** { *; }
-keepattributes Signature

#Retrofit
-dontwarn rx.**
-dontwarn okio.**
-dontwarn retrofit.appengine.UrlFetchClient
-dontwarn retrofit2.**
-keepattributes Annotation
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }
-keepattributes Signature

#OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.okhttp3.**

#Stripe
-keep class com.stripe.** { *; }

#Crashlytics
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#GSON
-keepclassmembers class com.handy.portal.** {
    <fields>;
}
-dontwarn com.google.common.**
-keep class com.google.gson.** { *; }
-keepattributes EnclosingMethod

#Urban Airship
-keepnames class * implements android.os.Parcelable {
  public static final ** CREATOR;
}
-dontwarn com.amazon.device.messaging.**
-keepclassmembers class com.urbanairship.js.UAJavascriptInterface {
   public *;
}
-keep public class * extends com.urbanairship.Autopilot


-keep class com.jumio.** { *; }
-keep class jumio.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okio.**
-dontnote
-keep class net.sf.scuba.smartcards.IsoDepCardService {*;}
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.spongycastle.** {*;}
-keep class org.ejbca.** {*;}
-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn org.ejbca.**
-dontwarn org.spongycastle.**

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# Layer SDK
-keep class com.layer.** {*; }
-dontwarn com.layer.**
