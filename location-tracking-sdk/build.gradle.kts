import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.mavenPlugin)
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("io.github.oguzhandongul",version = "1.0.0")

    pom {
        name.set("Location Tracking SDK")
        description.set("Location Tracking Sample SDK for Assignment")
        inceptionYear.set("2024")
        url.set("https://github.com/oguzhandongul/location-tracking-android-sdk")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("oguzhandongul")
                name.set("Oguzhan Dongul")
                url.set("https://github.com/oguzhandongul/")
            }
        }
        scm {
            url.set("https://github.com/oguzhandongul/location-tracking-android-sdk")
            connection.set("scm:git:git@github.com:oguzhandongul/location-tracking-android-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com:oguzhandongul/location-tracking-android-sdk.git")
        }
    }
}

android {
    namespace = "io.github.oguzhandongul.locationtrackingsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

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
    implementation(libs.joda.time)

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