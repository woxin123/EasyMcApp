plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "top.mcwebsite.easymcapp.todoApp"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
//        register("release") {
//            isMinifyEnabled = false
//            proguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), File("proguard-rules.pro")))
//        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = libs.versions.kotlin.get()
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":todo:todo-data"))
    implementation(project(":todo:todo-common-ui-resources"))
    implementation(project(":todo:todo-compose-components"))
    implementation(project(":todo:todo-ui-task"))
    implementation(project(":todo:todo-ui-addtask"))
    implementation(project(":todo:todo-ui-choose_date_time"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.compose.material.material)
    implementation(libs.androidx.compose.material.iconsext)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.navigation.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.uiTestJunit)
    debugImplementation(libs.androidx.compose.ui.uiTooling)
}
