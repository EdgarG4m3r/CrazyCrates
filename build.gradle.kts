plugins {
    `java-library`
}

rootProject.group = "me.badbones69.crazycrates"
rootProject.version = "${extra["plugin_version"]}"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"

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