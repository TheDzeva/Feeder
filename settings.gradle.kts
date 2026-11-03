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
        version("bouncycastle", "1.86")
        library("bcprov.jdk18on", "org.bouncycastle", "bcprov-jdk18on")
            .versionRef("bouncycastle")

        library("jdom2", "org.jdom", "jdom2").version("2.0.6.1")
    }
}
}

rootProject.name = "feeder"

include(":app")
