dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")
}