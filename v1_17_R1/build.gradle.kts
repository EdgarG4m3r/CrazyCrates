dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "1.17.1-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release.set(17)
    }
}