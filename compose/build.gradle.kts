plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "br.com.marconi"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinxCoroutines)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
