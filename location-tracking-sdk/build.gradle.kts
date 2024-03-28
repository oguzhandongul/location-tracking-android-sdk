plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.oguzhandongul.locationtrackingsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.security)
    implementation(libs.androidx.datastore)
    implementation(libs.material)
    implementation(libs.android.gms)
    implementation(libs.timber)

    implementation (libs.retrofit)
    implementation (libs.retrofit.converter)
    implementation (libs.retrofit.logger)

    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.test)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlin.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}