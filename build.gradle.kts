// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
    alias(libs.plugins.googleFirebaseCrashlytics) apply false

}

buildscript {
    repositories {
        maven("https://jitpack.io")
    }

    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")

        classpath("com.github.alexfu:androidautoversion:3.3.0")
    }
}