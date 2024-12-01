buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    // Apply application and library plugins here if necessary
    id("com.android.application") version "8.7.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

