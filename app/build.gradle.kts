plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.servicebio.compose.application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.servicebio.compose.application"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        // 通用签名配置
        create("common") {
            // 哪个签名文件
            storeFile = file("joker_open_key")
            // 密钥别名
            keyAlias = "joker_open_key"
            // 密钥密码
            keyPassword = "joker123456"
            // 签名文件密码
            storePassword = "joker123456"

            // 启用所有签名方案以确保最大兼容性
            enableV1Signing = true  // JAR 签名 (Android 1.0+)
            enableV2Signing = true  // APK 签名 v2 (Android 7.0+)
            enableV3Signing = true  // APK 签名 v3 (Android 9.0+)
            enableV4Signing = true  // APK 签名 v4 (Android 11.0+)
        }
    }
    // 构建类型配置
    buildTypes {
        debug {
            // debug 模式下也使用正式签名配置 - 方便调试支付以及三方登录等功能
            signingConfig = signingConfigs["common"]
            // debug 模式下包名后缀
            applicationIdSuffix = ".debug"
        }

        release {
            signingConfig = signingConfigs["common"]
            // 是否启用代码压缩
            isMinifyEnabled = true
            // 资源压缩
            isShrinkResources = true
            isDebuggable = false
            // 配置ProGuard规则文件
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.lottie.compose)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}