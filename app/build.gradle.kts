plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.chatactivity"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatactivity"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.fatboyindustrial.gson-javatime-serialisers:gson-javatime-serialisers:1.1.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0") // Para logs*/
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    /* implementation(libs.androidx.security.state)
     implementation(libs.androidx.security.crypto)
     implementation(libs.androidx.security.crypto.v110alpha06) */


    // implementation (libs.gson)
    //   implementation (libs.converter.gson.v290)


    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.security:security-state:1.0.0-alpha04")
    implementation("androidx.security:security-crypto:1.0.0")

    //WebSocket
    implementation ("org.java-websocket:Java-WebSocket:1.5.2")
   // implementation ("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
}