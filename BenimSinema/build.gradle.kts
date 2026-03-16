import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

plugins {
    id("com.android.library")
    kotlin("android")
    // Cloudstream plugin'ini amiqo'nun kullandığı versiyonla eşliyoruz
    id("com.lagradost.cloudstream3.gradle")
}

android {
    namespace = "com.example"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Eklentiye özel ayarlar
configure<CloudstreamExtension> {
    // manifest.json dosyanın olduğu yer
    setMetadataResource(file("src/main/resources/manifest.json"))
}

dependencies {
    // Cloudstream temel kütüphaneleri
    val coreVersion = "3.0.0" 
    implementation("com.lagradost:cloudstream3:$coreVersion")
    
    // Kotlin ve diğer yardımcılar
    implementation(kotlin("stdlib"))
    implementation("com.github.jhy:jsoup:1.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
}
