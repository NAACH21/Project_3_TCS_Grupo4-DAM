plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.project_3_tcs_grupo4_dam"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.project_3_tcs_grupo4_dam"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Campo para configurar fácilmente la URL del backend (usar comillas)
        // Por defecto apunta al emulador (10.0.2.2). Cambia esto para probar en dispositivo físico (p.ej. "http://192.168.1.10:8080/")
        buildConfigField("String", "BACKEND_URL", "\"http://10.0.2.2:8080/\"")
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Catálogo (mantén todo lo posible aquí)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)


    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")
    // Permite usar el `viewModel()` composable function
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Tests / tooling
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Retrofit + converters + logging  (retrofit estaba duplicado)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coroutines + ViewModel (unificado: antes 1.7.3 y 1.8.1 / 2.7.0 y 2.8.6)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

    // Activity KTX extra (no es duplicado)
    implementation("androidx.activity:activity-ktx:1.9.0")
}
