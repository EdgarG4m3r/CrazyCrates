plugins {
    `java-library`
}

configurations { create("externalLibs") }

allprojects {
    apply(plugin = "java-library")

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")

        /**
         * Everything else we need.
         */
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}