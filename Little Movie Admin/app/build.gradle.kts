plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    //alias(libs.plugins.androidApplication)
    //alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.flatcode.littlemovieadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flatcode.littlemovieadmin"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")          //Shared Preference
    implementation(libs.androidx.activity)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //Layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.2.0")) //Firebase BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    //implementation("com.google.firebase:firebase-crashlytics-ktx")
    //Image
    implementation("de.hdodenhof:circleimageview:3.1.0")                //Circle Image
    implementation("com.github.bumptech.glide:glide:4.15.1")            //Glide Image
    //noinspection KaptUsageInsteadOfKsp
    kapt("com.github.bumptech.glide:compiler:4.15.1")                   //Glide Compiler
    implementation("com.balysv:material-ripple:1.0.2")                  //Ripple Effect
    implementation("jp.wasabeef:glide-transformations:4.3.0")           //Image Blur
    api("com.theartofdev.edmodo:android-image-cropper:2.8.0")           //Image Crop

    //Video Player
    implementation ("com.google.android.exoplayer:exoplayer:2.9.6")      //EXO Player
}