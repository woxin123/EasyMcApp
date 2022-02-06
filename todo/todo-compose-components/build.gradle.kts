plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 26
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.ui.uiTooling)
    implementation(libs.androidx.compose.ui.uiViewBinding)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.accompanist.pager.pager)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.constraintlayout)
}
