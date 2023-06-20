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
#-keepattributes Signature
#
#-keep class com.google.gson.reflect.TypeToken {
#    <init>();
#}
#-keep class com.lawmobile.body_cameras.x1.entities.** { *; }
#-keep class com.lawmobile.domain.entities.** { *; }
#-keep class java.net.** { *; }
#-keep class java.net.Socket.** { *; }
#-keep class java.net.Socket.** { *; }
#-keep class com.lawmobile.body_cameras.x1.entities.*
#-keep class com.lawmobile.domain.entities.*
#-keep class com.lawmobile.data.mappers.impl.** { *; }
#-keep class com.lawmobile.data.mappers.** { *; }
#-keep class com.lawmobile.data.repository.liveStreaming.** { *; }
#-keep class com.lawmobile.data.mappers.impl.*
#-keep class com.lawmobile.data.mappers.*
#-keep class com.lawmobile.data.repository.liveStreaming.*
#-keep class kotlin.Metadata
#-keep class com.lawmobile.body_cameras.utils.*
#-keep class com.lawmobile.body_cameras.utils.** { *; }
#
#-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#-dontwarn **
#-verbose
#-optimizations !code/simplification/arithmetic,!field/
#
#-keep public class * extends android.app.Activity {
#    public <methods>;
#}
#-keep public class * extends android.app.Fragment {
#    public <methods>;
#}
#-keep public class * extends androidx.fragment.app.Fragment {
#    public <methods>;
#}
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
## support design
#-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
#-keep interface android.support.design.** { *; }
#-keep public class android.support.design.R$* { *; }
#-dontwarn android.support.**
## Kotlinx
#-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
#-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }
#-keep class kotlinx.coroutines.CoroutineExceptionHandler { *; }
#-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }
#-keep class kotlin.Result { *; }
#-keepclassmembernames class kotlinx.** {
#    volatile <fields>;
#}
#-keep class com.google.android.material.** { *; }
#-keepattributes SourceFile,LineNumberTable
#-keep class com.google.gson.stream.** { *; }
## Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
## JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * extends com.google.gson.TypeAdapter
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer
## Prevent R8 from leaving Data object members always null
#-keepclassmembers,allowobfuscation class * {
#  @com.google.gson.annotations.SerializedName <fields>;
#}
## Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
#-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
#-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
#-keep class com.lawmobile.body_cameras.entities.** { <fields>; }
#-keep class com.shockwave.**
#-keep class com.shockwave.** { *; }
#-keepclassmembers class com.shockwave.** { *; }