pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "http-event-pool"

include(
    "core",
    "annotations",
    "retrofit",
    "compose",
    "sample"
)
