dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "1.16.5-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        sourceCompatibility = "8"
        targetCompatibility = "8"
    }
}