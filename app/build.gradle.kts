/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}

fun String.asBuildConfigString(): String {
    val escaped = replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
}

fun org.gradle.api.Project.resolveLocalConfig(vararg keys: String, default: String = ""): String {
    for (key in keys) {
        providers.gradleProperty(key).orNull?.let { return it }
        providers.environmentVariable(key).orNull?.let { return it }
    }
    return default
}

android {
    namespace = "com.sevam.customer"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sevam.customer"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField(
            "String",
            "SUPABASE_URL",
            project.resolveLocalConfig("SUPABASE_URL", default = "").asBuildConfigString(),
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            project.resolveLocalConfig("SUPABASE_ANON_KEY", default = "").asBuildConfigString(),
        )
        buildConfigField(
            "String",
            "SUPABASE_SCHEMA",
            project.resolveLocalConfig("SUPABASE_SCHEMA", default = "public").asBuildConfigString(),
        )
        buildConfigField(
            "String",
            "SUPABASE_WORKER_LOCATIONS_TABLE",
            project.resolveLocalConfig("SUPABASE_WORKER_LOCATIONS_TABLE", default = "worker_locations").asBuildConfigString(),
        )

        testInstrumentationRunner = "com.sevam.customer.CustomTestRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.incremental" to "true"
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = true
            buildConfigField(
                "String",
                "SUPABASE_URL",
                project.resolveLocalConfig("SUPABASE_URL_DEBUG", "SUPABASE_URL", default = "").asBuildConfigString(),
            )
            buildConfigField(
                "String",
                "SUPABASE_ANON_KEY",
                project.resolveLocalConfig("SUPABASE_ANON_KEY_DEBUG", "SUPABASE_ANON_KEY", default = "").asBuildConfigString(),
            )
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField(
                "String",
                "SUPABASE_URL",
                project.resolveLocalConfig("SUPABASE_URL_RELEASE", "SUPABASE_URL", default = "").asBuildConfigString(),
            )
            buildConfigField(
                "String",
                "SUPABASE_ANON_KEY",
                project.resolveLocalConfig("SUPABASE_ANON_KEY_RELEASE", "SUPABASE_ANON_KEY", default = "").asBuildConfigString(),
            )
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguardTest-rules.pro")
        }
    }

    // Always show the result of every unit test, even if it passes.
    testOptions.unitTests {
        isIncludeAndroidResources = true

        all { test ->
            with(test) {
                testLogging {
                    events = setOf(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
                    )
                }
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        excludes += "META-INF/AL2.0"
        excludes += "META-INF/LGPL2.1"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
            freeCompilerArgs += "-opt-in=kotlin.Experimental"
        }
    }
}

/*
 Dependency versions are defined in the top level build.gradle file. This helps keeping track of
 all versions in a single place. This improves readability and helps managing project complexity.
 */
dependencies {

    // Sevam modules
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:security"))
    implementation(project(":core:ui"))
    implementation(project(":core:realtime"))
    implementation(project(":features:auth"))
    implementation(project(":features:home"))
    implementation(project(":features:bookings"))
    implementation(project(":features:tracking"))
    implementation(project(":features:payments"))
    implementation(project(":features:profile"))

    // App dependencies
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    implementation(libs.androidx.test.espresso.idling.resources)

    // Architecture Components
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    // Hilt
    implementation(libs.hilt.android.core)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Jetpack Compose
    val composeBom = platform(libs.androidx.compose.bom)

    implementation(libs.androidx.activity.compose)
    implementation(composeBom)
    implementation(libs.androidx.compose.foundation.core)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.accompanist.appcompat.theme)
    implementation(libs.accompanist.swiperefresh)

    debugImplementation(composeBom)
    debugImplementation(libs.androidx.compose.ui.tooling.core)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Dependencies for local unit tests
    testImplementation(composeBom)
    testImplementation(libs.junit4)
    testImplementation(libs.androidx.archcore.testing)
    testImplementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.androidx.test.espresso.contrib)
    testImplementation(libs.androidx.test.espresso.intents)
    testImplementation(libs.google.truth)
    testImplementation(libs.androidx.compose.ui.test.junit)

    // JVM tests - Hilt
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Dependencies for Android unit tests
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)

    // AndroidX Test - JVM testing
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.androidx.test.ext)
    testImplementation(libs.androidx.test.rules)
    testImplementation(project(":shared-test"))

    // AndroidX Test - Instrumented testing
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.archcore.testing)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.contrib)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.androidx.test.espresso.idling.resources)
    androidTestImplementation(libs.androidx.test.espresso.idling.concurrent)
    androidTestImplementation(project(":shared-test"))

    // AndroidX Test - Hilt testing
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
