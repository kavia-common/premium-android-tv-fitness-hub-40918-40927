plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Keep kapt only if we truly need annotation processing (Room). We'll keep it but ensure it's stable.
    id("kotlin-kapt")
}

// Apply Google Services plugin only if google-services.json exists to avoid CI failures
val hasGoogleServicesJson = file("google-services.json").exists() ||
        file("src/google-services.json").exists() ||
        file("src/debug/google-services.json").exists() ||
        file("src/release/google-services.json").exists()

if (hasGoogleServicesJson) {
    apply(plugin = "com.google.gms.google-services")
}

// Resolve API base URL from -PapiBaseUrl property if provided; default to production URL
val apiBaseUrl: String = (project.findProperty("apiBaseUrl") as String?) ?: "https://api.example.com/"

android {
    namespace = "com.example.tv_app_frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tv_app_frontend"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields for API base and feature flags (can be overridden by flavors or CI)
        buildConfigField("String", "API_BASE_URL", "\"https://api.example.com/\"")
        buildConfigField("boolean", "FEATURE_TTS", "true")
        buildConfigField("boolean", "FEATURE_RECOMMENDATIONS", "true")
        buildConfigField("String", "GOOGLE_OAUTH_WEB_CLIENT_ID", "\"554965754520-27borongeqi1a3j06tacjolcbea2e91o.apps.googleusercontent.com\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
            isDebuggable = true
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    buildFeatures {
        // We use only ViewBinding; DataBinding disabled to avoid kapt/databinding processing.
        viewBinding = true
        dataBinding = false
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        // Keep CI-friendly lint; don't abort the whole build on debug variant.
        abortOnError = false
        warningsAsErrors = false
    }
}

// Kapt configuration to make processors robust in CI. If Room remains, this helps.
kapt {
    correctErrorTypes = true
    useBuildCache = true
}

dependencies {
    // Android TV Core
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.tvprovider:tvprovider:1.0.0")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Media3 ExoPlayer for video
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Glide transformations for rounded corners and blur
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // Google Sign In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Room (optional caching via minimal viable, we will use SharedPreferences primarily)
    // Keeping Room but ensure kapt works; if later not needed, remove both runtime/ktx and compiler.
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Preferences DataStore (for secure-ish token via EncryptedSharedPreferences fallback)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // WorkManager for recommendations refresh
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
