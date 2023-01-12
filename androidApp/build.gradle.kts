plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.dagger.hilt.android.get().pluginId)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.canonal.translator.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.canonal.translator.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.canonal.translator.TestHiltRunner"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    // use ksp generated folder, used by compose destinations
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.compose)
    implementation(libs.compose.navigation)
    implementation(libs.coil.compose)

    implementation(libs.hilt.android)
    implementation(libs.compose.destinations)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    implementation(libs.ktor.android)

    androidTestImplementation(libs.testrunner)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.compose.testing)
    androidTestImplementation(libs.rules)
    debugImplementation(libs.compose.testing.manifest)

    kaptAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.testing)
}