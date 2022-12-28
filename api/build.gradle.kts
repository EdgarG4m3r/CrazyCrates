dependencies {
    compileOnly("org.spigotmc", "spigot-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}