dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.code.gson:gson:2.10.1")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation(project(":remote-robot"))
    testImplementation(project(":remote-fixtures"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("commons-io:commons-io:2.16.1")
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("ideLauncher", MavenPublication::class) {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = rootProject.ext["publish_version"] as String
            val sourcesJar by tasks.getting(Jar::class)
            artifact(sourcesJar)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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