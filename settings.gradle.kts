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
        maven { url = uri("https://jitpack.io") }
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            // Replicates ~> 1.85 (allows 1.85.x, rejects 1.86 and above)
            library("bouncycastle-prov", "org.bouncycastle:bcprov-jdk18on").version {
                strictly("[1.85, 1.86[") 
            }
        }
    }
}

rootProject.name = "feeder"

include(":app")
