plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Google Services Plugin
}

android {
    namespace = "com.example.fyp_clearcanvas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fyp_clearcanvas"
        minSdk = 26
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


    buildToolsVersion = "34.0.0"

    //META-INF/INDEX.LIST to prevent duplicate conflicts
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude ("META-INF/LICENSE")
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/NOTICE")
        exclude ("META-INF/LICENSE.txt")
        exclude ("META-INF/NOTICE.txt")
    }
}

dependencies {

    implementation ("androidx.gridlayout:gridlayout:1.0.0")

    // AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.1.0")) // BOM for consistent versions
    implementation("com.google.firebase:firebase-auth")                 // Authentication
    implementation("com.google.firebase:firebase-database")             // Realtime Database
    implementation("com.google.firebase:firebase-storage")              // Firebase Storage

    // CameraX dependencies
    implementation("androidx.camera:camera-core:1.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")
    implementation("androidx.camera:camera-extensions:1.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // OkHttp for Roboflow API requests
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Google ML
    implementation("com.google.mlkit:face-detection:16.1.3")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.activity)

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.google.firebase.bom)

    //JSoup
    implementation ("org.jsoup:jsoup:1.14.3")

    //Volley
    implementation ("com.android.volley:volley:1.2.1")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.play.services.analytics.impl)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    //MPAndroid
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


    // Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    implementation ("com.google.cloud:google-cloud-storage:2.47.0")
    implementation ("com.google.guava:guava:33.4.0-jre")


    implementation ("com.android.billingclient:billing:6.0.1")
}
