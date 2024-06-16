import java.util.Properties
import com.android.build.api.variant.FilterConfiguration.FilterType.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("androidx.navigation.safeargs.kotlin")
    id("com.github.alexfu.androidautoversion")
}

android {
    namespace = "io.github.leonidius20.lugat"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.leonidius20.lugat"
        minSdk = 21
        targetSdk = 34
        versionCode = androidAutoVersion.versionCode
        versionName = androidAutoVersion.versionName

        resourceConfigurations.addAll(listOf("en", "ru", "uk", "crh"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val keystoreFile = project.rootProject.file("android-keystore.jks")

    signingConfigs {
        create("with-production-signature") {
            storeFile = keystoreFile

            val secretPropertiesFile = project.rootProject.file("secrets.properties")
            if (!secretPropertiesFile.exists()) {
                // we are on github actions, get from env variables
                storePassword = System.getenv("SIGNATURE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("SIGNATURE_KEY_ALIAS")
                keyPassword = System.getenv("SIGNATURE_KEY_PASSWORD")
            } else {
                // we are on local machine, get from file
                storeFile = project.rootProject.file("android-keystore.jks")

                val secretProperties = Properties()
                secretProperties.load(secretPropertiesFile.inputStream())

                storePassword = secretProperties.getProperty("SIGNATURE_KEYSTORE_PASSWORD")
                keyAlias = secretProperties.getProperty("SIGNATURE_KEY_ALIAS")
                keyPassword = secretProperties.getProperty("SIGNATURE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("with-production-signature")
        }

        debug {
            // if there is no keystore file, use debug signature,
            // otherwise use production keystore
            /*signingConfig = if (!keystoreFile.exists())
                signingConfigs.getByName("debug")
            else
                signingConfigs.getByName("with-production-signature")*/
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            resourceConfigurations.addAll(listOf("en", "xxhdpi"))
            versionCode = 1
            versionName = "devbuild"
        }
        create("prod") {
            dimension = "environment"
            versionCode = androidAutoVersion.versionCode
            versionName = androidAutoVersion.versionName
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Making sure that apk variants get unique version codes
val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

androidComponents {
    beforeVariants { variantBuilder ->
        // not make the dev-release and prod-debug variants
        if (variantBuilder.productFlavors.contains("environment" to "prod") && variantBuilder.buildType == "debug") {
            variantBuilder.enable = false
        }

        if (variantBuilder.productFlavors.contains("environment" to "dev") && variantBuilder.buildType == "release") {
            variantBuilder.enable = false
        }
    }

    onVariants { variant ->
        variant.outputs.forEach { output ->
            val name = output.filters.find { it.filterType == ABI }?.identifier
            val baseAbiCode = abiCodes[name]
            // Because abiCodes.get() returns null for ABIs that are not mapped by ext.abiCodes,
            // the following code doesn't override the version code for universal APKs.
            // However, because you want universal APKs to have the lowest version code,
            // this outcome is desirable.
            if (baseAbiCode != null) {
                // Assigns the new version code to output.versionCode, which changes the version code
                // for only the output APK, not for the variant itself.
                output.versionCode.set(baseAbiCode * 1000 + (output.versionCode.get() ?: 0))
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // for robolectric tests
    testImplementation(libs.androidx.test.core)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.material.lists)

    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.mockito.kotlin)
   // testImplementation(libs.androidx.espresso.core)
  // testImplementation(libs.androidx.test.runner)
  //  testImplementation(libs.androidx.test.rules)

    testImplementation(libs.turbine)

    testImplementation(libs.robolectric)
    androidTestImplementation(libs.robolectric)
}