import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runTask)
}

group = "dev.remodded"
version = "1.0.4"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.opencollab.dev/main/") {
        name = "GeyserMC"
    }
}

dependencies {
    compileOnly(libs.velocity.api)
    compileOnly(libs.floodgate.api)

    // toml4j is deprecated in Velocity API and will be removed, so we ship our own (relocated) copy
    implementation(libs.toml4j) {
        exclude(group = "com.google.code.gson", module = "gson")
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks {

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }

    shadowJar {
        archiveBaseName.set("ReWhitelist")
        archiveClassifier.set("")

        relocate("com.moandjiezana.toml", "dev.remodded.rewhitelist.libs.toml4j")
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filter<ReplaceTokens>(
            "tokens" to mapOf("version" to project.version)
        )
    }
}
