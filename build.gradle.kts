plugins {
    `java-library`
}

rootProject.group = "${extra["plugin_group"]}"
rootProject.version = "${extra["plugin_version"]}"
rootProject.description = "Add unlimited crates to your server with 10 different crate types to choose from!"

allprojects {
    apply(plugin = "java-library")

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        maven("https://jitpack.io")

        /** CrazyCrew **/
        maven("https://repo.crazycrew.us/private") {
            name = "crazycrew"
            //credentials(PasswordCredentials::class)
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }

        /**
         * Everything else we need.
         */
        mavenCentral()
    }
}