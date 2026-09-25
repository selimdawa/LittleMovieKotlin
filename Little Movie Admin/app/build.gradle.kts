plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.flatcode.littlemovieadmin"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.flatcode.littlemovieadmin"
        minSdk = 24
        targetSdk = 37
        versionCode = 6
        versionName = "1.30"

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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    //Layout
    implementation(libs.material)
    implementation(libs.multicolors)
    //Firebase
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    //Cloudinary
    implementation(libs.cloudinary.android)
    //Image
    implementation(libs.coil3)
    implementation(libs.coil3.network)
    api(libs.android.image.cropper)                     //Image Crop
    //MVVM
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    //Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    //Other
    implementation(libs.material.ripple)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.timber)
}
