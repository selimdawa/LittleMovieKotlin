plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.flatcode.littlemovie"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.flatcode.littlemovie"
        minSdk = 24
        targetSdk = 37
        versionCode = 9
        versionName = "1.35"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.datastore.preferences)   //DataStore
    //Layout
    implementation(libs.material)
    implementation(libs.multicolors)
    //Image
    implementation(libs.coil)                           //Coil Image
    api(libs.android.image.cropper)                     //Image Crop
    implementation(libs.autoimageslider)                //Slider Show
    //Firebase
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    //Other's
    implementation(libs.material.ripple)                //Ripple Effect
    implementation(libs.nafisbottomnav)                 //Nafis Bottom Navigation
    implementation (libs.exoplayer)                     //EXO Video Player

    // MVVM & Lifecycle components (ViewModel, StateFlow)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Timber Logging
    implementation(libs.timber)

    // Jetpack Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}