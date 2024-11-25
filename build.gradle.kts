
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

        plugins {
            `java-library`
            id("io.papermc.paperweight.userdev") version "1.7.3"
            id("xyz.jpenilla.run-paper") version "2.3.1"
            id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
            id("com.gradleup.shadow") version "8.3.2"
        }

group = "net.runemc"
version = "0.1"
description = "Gruney"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    api(libs.org.reflections.reflections)
    compileOnly(libs.io.papermc.paper.paper.api)
    implementation("org.incendo", "cloud-paper", "2.0.0-beta.10")
    implementation("org.codehaus.groovy", "groovy-all", "4.0.15")
}

tasks {
    compileJava {
        options.release = 21
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveClassifier.set("")

        fun reloc(pkg: String) = relocate(pkg, "net.runemc.plugin.dependency.$pkg")

        reloc("org.incendo.cloud")
        reloc("io.leangen.geantyref")
    }

    reobfJar {
        dependsOn(shadowJar)

        val finalJar = file("S:/opt/mcsmanager/daemon/data/InstanceData/b4415dbc535e4829999b50802b3c009d/plugins/Gruney-0.1.jar")

        doLast {
            finalJar.parentFile.mkdirs()

            val builtJar = file("${buildDir}/libs/${project.name}-${version}.jar")

            if (builtJar.exists()) {
                copy {
                    from(builtJar)
                    into(finalJar.parentFile)
                    rename { finalJar.name }
                }
            } else {
                throw GradleException("Built JAR not found: $builtJar")
            }
        }

        outputs.upToDateWhen { false }
    }

    clean {
        doLast {
            delete(layout.buildDirectory)
        }
    }
}

bukkitPluginYaml {
    main = "net.runemc.plugin.Main"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("dank")
    apiVersion = "1.21"
}