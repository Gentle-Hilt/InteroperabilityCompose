@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "gentle.hilt.interop"
    compileSdk = 33

    defaultConfig {
        applicationId = "gentle.hilt.interop"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "gentle.hilt.interop.androidTestUtil.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/NOTICE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
// Core
    implementation(libs.core.ktx)
    implementation(libs.core.activity)
    implementation(libs.appcompat)
    implementation(libs.material.design)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // Convenience
    implementation(libs.hilt)
    implementation(libs.legacy.support.v4) // https://issuetracker.google.com/issues/179057202 still waiting
    kapt(libs.hilt.compiler)
    implementation(libs.timber)
    implementation(libs.logger)
    // Internet
    implementation(libs.retrofit)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.codegen)
    implementation(libs.moshi.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.paging)
    // Storage
    implementation(libs.datastore.preferences)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room)
    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines)
    // Compose
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.animation)
    implementation(libs.compose.text)
    implementation(libs.compose.material)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.coil)
    implementation(libs.compose.paging)

    // Local tests
    testImplementation(testLibs.test.core)
    testImplementation(testLibs.test.core.ktx)
    testImplementation(testLibs.junit)
    testImplementation(testLibs.junit.ext)
    testImplementation(testLibs.arch.core)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.android)
    testImplementation(testLibs.truth)
    testImplementation(testLibs.coroutines)
    testImplementation(testLibs.robolectric)
    testImplementation(testLibs.hilt)
    debugImplementation(testLibs.hilt)
    debugImplementation(testLibs.runner)
    kaptAndroidTest(testLibs.hilt.compiler)
    kaptTest(testLibs.hilt.compiler)

    // Other
    implementation(libs.splash.screen)
    implementation(testLibs.kotlin.reflect)
    testImplementation(testLibs.kotlin.reflect)
}
