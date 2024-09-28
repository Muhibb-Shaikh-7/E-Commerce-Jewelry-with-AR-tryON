import com.android.sdklib.computeReleaseNameAndDetails

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding =true

    }
    viewBinding{
        enable=true
    }
    dataBinding{
        enable=true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.lottie)
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
    implementation("com.github.Spikeysanju:MotionToast:1.4")
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation("com.github.wasabeef:blurry:3.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}





