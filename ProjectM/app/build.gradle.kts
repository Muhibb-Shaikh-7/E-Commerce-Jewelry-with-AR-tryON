import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.majorproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.majorproject"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation("com.google.firebase:firebase-storage")
    implementation(libs.play.services.maps)
    implementation(libs.lottie)
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
    implementation("com.github.Spikeysanju:MotionToast:1.4")
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("com.github.wasabeef:blurry:3.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.navigation.ui.ktx)
    //implementation("com.paulrybitskyi.persistentsearchview:persistentsearchview:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("pub.devrel:easypermissions:2.0.1")
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("com.google.android.gms:play-services-wallet:19.4.0")
    implementation ("com.google.android.gms:play-services-ads-identifier:17.0.0")



}
