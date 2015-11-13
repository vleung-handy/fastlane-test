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

#Mixpanel
-dontwarn com.mixpanel.**

#Butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *; }

#Dagger
-dontwarn dagger.internal.codegen.**
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter

#Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#Facebook
-keep class com.facebook.** { *; }
-keepattributes Signature

#Retrofit
-dontwarn rx.**
-dontwarn okio.**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Annotation
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }
-keepattributes Signature

#OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#Newrelic
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses

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
