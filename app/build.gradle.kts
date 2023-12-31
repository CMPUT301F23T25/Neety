plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.team25.neety"
    compileSdk = 34
    testOptions {
        animationsDisabled = true
    }

    defaultConfig {
        applicationId = "com.team25.neety"
        minSdk = 33
        targetSdk = 33
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

    compileOnly(files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"))

    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.2")
    implementation("androidx.navigation:navigation-ui:2.7.2")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    testImplementation("androidx.test:runner:1.4.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation ("org.mockito:mockito-core:3.12.4")
    testImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation ("org.mockito:mockito-core:3.12.4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")

    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.4.0")
}
//testImplementation 'androidx.test:runner:1.4.0'
//}
//testImplementation 'androidx.test.ext:junit:1.1.3'