plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("edu.sc.seis.launch4j") version "2.5.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    mainClass.set("com.example.authapp.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dconsole.encoding=UTF-8"
    )
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.authapp.Launcher"
    }
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.txt")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    jvmArgs("-Dconsole.encoding=UTF-8")
}
tasks.withType<JavaExec>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    jvmArgs("-Dconsole.encoding=UTF-8")
}

launch4j {
    mainClassName = "com.example.authapp.Launcher"
    outfile = "PCPartsAggregator.exe"
    jarTask = tasks.jar.get()
    dontWrapJar = false
    jreMinVersion = "21"
}

tasks.named("distZip") { dependsOn(tasks.named("createExe")) }
tasks.named("distTar") { dependsOn(tasks.named("createExe")) }
tasks.named("startScripts") { dependsOn(tasks.named("createExe")) }

tasks.build {
    dependsOn(tasks.launch4j)
}
