plugins {
    id("org.jetbrains.intellij.platform")
}
val robotServerVersion = if (System.getenv("SNAPSHOT") == null) {
    rootProject.ext["publish_version"] as String
} else {
    "0.0.1-SNAPSHOT"
}
version = robotServerVersion

repositories {
    maven("https://repo.labs.intellij.net/intellij")
    intellijPlatform {
        defaultRepositories()
    }
}

configurations.runtimeClasspath {
    exclude("org.slf4j", "slf4j-api")
}

dependencies {
    api(project(":robot-server-core"))
    api(project(":test-recorder"))

    implementation(platform("io.ktor:ktor-bom:2.3.11"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-gson")
    implementation("io.ktor:ktor-server-default-headers")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

intellijPlatform {
//    buildSearchableOptions = false
    pluginConfiguration {
        ideaVersion {
            untilBuild = provider { null }
        }
    }
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks {
    runIde {
        systemProperty("robot-server.port", 8080)
//        systemProperty("robot.encryption.enabled", "true")
//        systemProperty("robot.encryption.password", "my super secret")
    }

    patchPluginXml {
        changeNotes.set("""
            api for robot inside Idea<br>
            <em>idea testing</em>
        """.trimIndent())
    }
}

publishing {
    publications {
        register("robotServerPlugin", MavenPublication::class) {
            artifact("build/distributions/robot-server-plugin-$robotServerVersion.zip")
            groupId = project.group as String
            artifactId = project.name
            version = robotServerVersion
        }
        register("robotServerJar", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "robot-server"
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}

configurations.all {
    resolutionStrategy {
        // 强制统一平台组件版本
        force("org.junit.platform:junit-platform-commons:1.10.0")
        force("org.junit.platform:junit-platform-engine:1.10.0")
        force("org.junit.platform:junit-platform-launcher:1.10.0")
        force("org.junit.jupiter:junit-jupiter-api:5.10.0")
        force("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    }
}