pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        //New
        maven { url = uri("https://www.jitpack.io" ) }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //New
        jcenter()
        maven { url = uri("https://www.jitpack.io" ) }
        maven { url = uri("https://maven.google.com" ) }
    }
}

rootProject.name = "Little Movie - Admin"
include(":app")