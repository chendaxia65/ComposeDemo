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

# 禁用特定优化（如内联可能导致崩溃的逻辑）[6](@ref)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn javax.annotation.**

# 排除测试代码
-dontnote junit.**
-dontwarn android.test.**
-dontwarn androidx.test.**

# 处理Kotlin特有编译规则
-keepclassmembers class ** {
    public *** component1();
    public *** copy(...);
}

# 保留ViewModel子类及构造方法
-keep class * extends androidx.lifecycle.ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# 保留泛型类型元数据（解决类型擦除问题）
-keepattributes Signature, Exceptions, InnerClasses
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations