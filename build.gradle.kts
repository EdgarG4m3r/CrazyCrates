plugins {
    `java-library`
}

project.group = "me.badbones69.crazycrates"
project.version = "${extra["plugin_version"]}"

allprojects {
    apply(plugin = "java-library")

    repositories {
        /**
         * Spigot Team
         */
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        maven("https://jitpack.io")

        /**
         * Everything else we need.
         */
        mavenCentral()
        mavenLocal()
    }
}