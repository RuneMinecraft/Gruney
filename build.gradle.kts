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
description = "Plugin Description"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    api("org.reflections:reflections:0.9.12")
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation ("org.graalvm.js:js:23.0.5")
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
    authors.add("Runemc")
    apiVersion = "1.21"
}

tasks.build {
    dependsOn("shadowJar")
    finalizedBy("reobfJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
