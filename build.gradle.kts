import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.ksp).apply(false)
    alias(libs.plugins.kotlin.parcelize).apply(false)
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.ktlint.gradle).apply(false)
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

val bouncyCastleLtsVersion = "2.73.12"

dependencies {
    implementation("org.bouncycastle:bcprov-lts8on:$bouncyCastleLtsVersion")
    implementation("org.bouncycastle:bcpkix-lts8on:$bouncyCastleLtsVersion")
}

