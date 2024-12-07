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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/Tera-Group/One-Loyalty-Data-Android")
            name = "Github"
            credentials {
                username = "Tera-Group"
                password = "ghp_GnQGHzsKB9NcW2Hnzzzzz" //Todo: This need to correct token
            }
        }
    }
}

rootProject.name = "Check SDK"
include(":app")
 