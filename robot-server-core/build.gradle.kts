version = rootProject.ext["publish_version"] as String

plugins {
    id("org.jetbrains.intellij.platform")
}

repositories {
    maven("https://repo.labs.intellij.net/intellij")

    intellijPlatform {
        defaultRepositories()
    }
}


dependencies {
    api(project(":remote-robot"))

    implementation("org.mozilla:rhino:1.7.15")
    implementation("org.assertj:assertj-swing:3.17.1")
    implementation("net.bytebuddy:byte-buddy-dep:1.15.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

    intellijPlatform {
        intellijIdeaCommunity("2024.1")
    }
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            untilBuild = provider { null }
        }
    }
}

publishing {
    publications {
        register("robotServerCoreJar", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = "robot-server-core"
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