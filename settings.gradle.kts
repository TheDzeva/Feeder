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
            // 1. Declare the secure version
            version("bouncycastle", "1.86") 
            
            // 2. Define the library alias
            library("bcprov.jdk18on", "org.bouncycastle", "bcprov-jdk18on").versionRef("bouncycastle")
        }
    }
}

rootProject.name = "feeder"

include(":app")
