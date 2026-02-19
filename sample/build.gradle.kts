plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "br.com.marconi"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotations"))
    implementation(project(":core"))
    implementation(project(":compose"))
    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("br.com.marconi.sample.MainKt")
}
