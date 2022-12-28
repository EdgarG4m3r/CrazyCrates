dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}