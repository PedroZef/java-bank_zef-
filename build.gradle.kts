plugins {
    id("java")
    id("application")
}

application {
    mainClass.set("br.com.zef.application.Main")
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "br.com.zef.application.Main"
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

group = "br.com.zef"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lombokVersion = "1.18.42"

dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
