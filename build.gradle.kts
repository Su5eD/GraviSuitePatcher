import net.minecraftforge.gradle.common.util.RunConfig
import wtf.gofancy.fancygradle.script.extensions.curse
import wtf.gofancy.fancygradle.script.extensions.deobf
import java.time.LocalDateTime

plugins {
    java
    idea
    id("net.minecraftforge.gradle") version "5.1.+"
    id("wtf.gofancy.fancygradle") version "1.1.0-0"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

version = "1.0.0"
group = "mods.su5ed"

minecraft {
    mappings("stable", "39-1.12")

    runs {
        val config = Action<RunConfig> {
            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP,COREMODLOG",
                    "forge.logging.console.level" to "debug"
                )
            )
            workingDirectory = project.file("run").canonicalPath
            source(sourceSets["main"])
        }

        create("client", config)
        create("server", config)
    }
}

fancyGradle {
    patches {
        resources
        coremods
        codeChickenLib
        asm
    }
}

repositories {
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
    }
    maven {
        name = "IC2"
        url = uri("https://maven.ic2.player.to/")
    }
    maven("https://su5ed.jfrog.io/artifactory/maven")
    mavenLocal()
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "1.12.2-14.23.5.2855")
    
    implementation("dev.su5ed.koremods:script:0.0.23")
    implementation("dev.su5ed.koremods:launchwrapper:0.0.23")
    implementation(group = "dev.su5ed", name = "koffee", version = "8.1.5")
    implementation(fg.deobf(group = "net.industrial-craft", name = "industrialcraft-2", version = "2.8.220-ex112"))
    implementation(fg.deobf(curse(mod = "gravitation-suite", projectId = 253590, fileId = 2700845)))
}

tasks {
    jar {
        manifest { 
            attributes(
                "Specification-Title" to "gravisuitespatch",
                "Specification-Vendor" to "su5ed",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "su5ed",
                "Implementation-Timestamp" to LocalDateTime.now()
            )
        }
    }
}
