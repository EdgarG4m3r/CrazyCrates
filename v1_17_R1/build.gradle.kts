dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc", "spigot", "1.17.1-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}