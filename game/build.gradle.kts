val javaVersion: String = JavaVersion.VERSION_16.toString()

val appMainClass: String = "gargoyle.xenox.Xenox"
val appModule: String = "Xenox"
val appName: String = "Xenox"

val os: org.gradle.internal.os.OperatingSystem = org.gradle.internal.os.OperatingSystem.current()
val appInstallerType: String = "msi"
val appIconIco: String = "game/src/main/resources/gargoyle/xenox/icon.ico"
val appIconPng: String = "game/src/main/resources/gargoyle/xenox/icon.gif"
val appCopyright: String = "Lure of Chaos"
val appVendor: String = "Lure of Chaos"

group = "gargoyle.xenox"
version = "1.0"
description = "xenox"

plugins {
    java
    application
    kotlin("jvm") version ("1.6.21")

    id("org.javamodularity.moduleplugin") version ("1.8.11")
    id("org.beryx.jlink") version ("2.25.0")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation(kotlin("stdlib-jdk7"))
        implementation(kotlin("stdlib-jdk8"))
    }
    implementation(platform(kotlin("bom")))

    implementation(project(":util"))

    implementation(project(":services"))
    implementation(project(":campaign"))
}

tasks.compileJava {
    modularity.inferModulePath.set(true)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    options.encoding = Charsets.UTF_8.toString()
}

tasks.compileKotlin {
    destinationDirectory.set(tasks.compileJava.get().destinationDirectory)
    targetCompatibility = javaVersion
    kotlinOptions {
        jvmTarget = javaVersion
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.build {
    dependsOn += tasks.jpackage
}

application {
    mainClass.set(appMainClass)
    mainModule.set(appModule)
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = appName
        mainClass.set(appMainClass)
    }
    jpackage {
        installerType = appInstallerType
        installerName = appName
        appVersion = project.version.toString()
        if (os.isWindows) {
            icon = rootProject.file(appIconIco).path
            installerOptions = listOf(
                "--description", rootProject.description,
                "--copyright", appCopyright,
                "--vendor", appVendor,
                "--win-dir-chooser",
                "--win-menu",
                "--win-per-user-install",
                "--win-shortcut"
            )
        }
        if (os.isLinux) {
            icon = rootProject.file(appIconPng).path
            installerOptions = listOf(
                "--description", rootProject.description,
                "--copyright", appCopyright,
                "--vendor", appVendor,
                "--linux-shortcut"
            )
        }
        if (os.isMacOsX) {
            icon = rootProject.file(appIconPng).path
        }
    }
}
