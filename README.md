# Crazy Crates
The legacy source for CrazyCrates ( 1.8 -> 1.16.5 )

## Download: 
All versions labeled "Alpha" are legacy versions.
https://modrinth.com/plugin/crazycrates/versions

## Repository:
https://repo.crazycrew.us/#/releases

# Developer API

## Groovy
<details>
 <summary>
   Gradle (Groovy)
 </summary>

```gradle
repositories {
    maven {
        url = "https://repo.crazycrew.us/releases"
    }
}
```

```gradle
dependencies {
    compileOnly "me.badbones69.crazycrates:crazycrates:1.10.2.1"
}
```
</details>

## Kotlin
<details>
 <summary>
   Gradle (Kotlin)
 </summary>

```gradle
repositories {
    maven("https://repo.crazycrew.us/releases")
}
```

```gradle
dependencies {
    compileOnly("me.badbones69.crazycrates", "crazycrates", "1.10.2.1")
}
```
</details>

## Maven
<details>
 <summary>
   Maven
 </summary>

```xml
<repository>
  <id>crazycrew</id>
  <url>https://repo.crazycrew.us/releases</url>
</repository>
```

```xml
<dependency>
  <groupId>me.badbones69.crazycrates</groupId>
  <artifactId>crazycrates</artifactId>
  <version>1.10.2.1</version>
 </dependency>
```
</details>