import top.mcwebsite.easymcapp.build.BuildConfig

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = BuildConfig.COMPILE_SDK

    defaultConfig {
        minSdk = BuildConfig.MIN_SDK
        targetSdk = BuildConfig.TARGET_SDK
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":todo:todo-data"))
    implementation(project(":todo:todo-compose-components"))
    implementation(project(":todo:todo-common-ui-resources"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.material.iconsext)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
