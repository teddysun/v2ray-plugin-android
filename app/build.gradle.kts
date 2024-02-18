import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.VariantOutput
import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Locale

plugins {
    id("com.android.application")
    kotlin("android")
}

val flavorRegex = "(assemble|generate)\\w*(Release|Debug)".toRegex()
val currentFlavor get() = gradle.startParameter.taskRequests.toString().let { task ->
    flavorRegex.find(task)?.groupValues?.get(2)?.toLowerCase(Locale.ROOT) ?: "debug".also {
        println("Warning: No match found for $task")
    }
}

android {
    ndkVersion = "26.2.11394342"
    namespace = "com.github.shadowsocks.plugin.v2ray"
    val javaVersion = JavaVersion.VERSION_17
    compileSdk = 34
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions.jvmTarget = javaVersion.toString()
    defaultConfig {
        applicationId = "com.github.shadowsocks.plugin.v2ray"
        minSdk = 23
        targetSdk = 34
        versionCode = 5013000
        versionName = "5.13.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }
    sourceSets.getByName("main") {
        jniLibs.setSrcDirs(jniLibs.srcDirs + files("$projectDir/build/go"))
    }
}

tasks.register<Exec>("goBuild") {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        println("Warning: Building on Windows is not supported")
    } else {
        executable("/bin/bash")
        args("go-build.bash", 23)
        environment("ANDROID_HOME", android.sdkDirectory)
        environment("ANDROID_NDK_HOME", android.ndkDirectory)
    }
}

tasks.configureEach {
    when (name) {
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders" -> dependsOn("goBuild")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", rootProject.extra.get("kotlinVersion").toString()))
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.github.shadowsocks:plugin:2.0.1")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)
if (currentFlavor == "release") android.applicationVariants.all {
    for (output in outputs) {
        abiCodes[(output as ApkVariantOutputImpl).getFilter(VariantOutput.ABI)]?.let { offset ->
            output.versionCodeOverride = versionCode + offset
        }
    }
}
