dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "1.16.5-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}