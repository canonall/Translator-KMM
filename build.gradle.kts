import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin

plugins {
    alias(libs.plugins.detekt)
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.tools.gradle.plugin)
        classpath(libs.sqldelight.gradle.plugin)
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.detekt.plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val detektFormatting = libs.detekt.formatting

subprojects {
    apply<DetektPlugin>()

    dependencies {
        detektPlugins(detektFormatting)
    }

    detekt {
        parallel = true
        ignoreFailures = true
        config = files("$rootDir/config/detekt/detekt.yml")
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "1.8"
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}