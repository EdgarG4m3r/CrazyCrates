plugins {
    `java-library`

    `maven-publish`

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    /**
     * Placeholders
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.mvdw-software.com/content/groups/public/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.codemc.io/repository/nms")
}

dependencies {
    implementation(project(":api"))

    implementation(project(":v1_8_R3"))
    implementation(project(":v1_12_R1"))
    implementation(project(":v1_16_R3"))
    implementation(project(":v1_17_R1"))
    implementation(project(":v1_18_R2"))

    implementation("de.tr7zw", "nbt-data-api", "2.11.1")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")
    implementation("org.jetbrains", "annotations", "23.0.0")

    compileOnly("org.spigotmc", "spigot-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.8")

    compileOnly("be.maximvdw", "MVdWPlaceholderAPI", "3.1.1-SNAPSHOT") {
        exclude(group = "org.spigotmc")
        exclude(group = "org.bukkit")
    }

    compileOnly("com.sainttx.holograms", "holograms", "2.12")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc")
        exclude(group = "org.bukkit")
    }
}