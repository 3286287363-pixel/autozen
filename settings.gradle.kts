pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AutoZen"
include(":app")
include(":feature-dashboard")
include(":feature-trip")
include(":feature-weather")
include(":core-data")
include(":core-network")
include(":core-obd")
include(":core-ui")
