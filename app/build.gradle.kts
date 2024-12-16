plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    kotlin("plugin.serialization") version "2.1.0"
//    id("com.google.gms.google-services") version "4.4.2" apply false // Plugin Google Services

}

android {
    namespace = "com.isga.quran"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.isga.quran"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Read JSON
    implementation ("com.google.code.gson:gson:2.10.1")
    //RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    //Requests
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    //google
//    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.android.gms:play-services-auth:20.5.0")
//    implementation("com.google.android.gms:play-services-ads:23.6.0")

//    implementation(libs.mediation.test.suite)
//    implementation(libs.google.firebase.auth) // RecyclerView library
}