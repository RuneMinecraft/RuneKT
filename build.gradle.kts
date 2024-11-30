
plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.runemc"
version = "v0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    // Kotlin standard libraries (use a single version)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0") // Core stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0") // JDK 8 extensions

    // Kotlin Scripting Dependencies
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:2.1.0") // Updated

    // Kotlin Scripting Compiler (required for script compilation)
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.1.0")

    // Reflection library
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0") // Updated

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0") // Latest coroutine core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0") // Latest coroutine Android

    // MCCoroutine for Bukkit
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
