// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.github.ben-manes.versions") version "0.51.0"
}

buildscript {
    val kotlinVersion = "1.9.22"
    extra.set("kotlinVersion", kotlinVersion)

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.51.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
