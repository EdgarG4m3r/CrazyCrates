dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "${project.extra["plugin_version"]}-R0.1-SNAPSHOT")
}