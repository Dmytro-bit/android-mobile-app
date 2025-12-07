// settings.gradle.kts (Корневой файл)

// 1. pluginManagement (ДОЛЖЕН БЫТЬ ПЕРВЫМ)
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

// 2. plugins (объявление плагинов для проекта)
// Используем явные ID и версии плагинов (старый стиль)
plugins {
    id("com.android.application") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    // Объявление KSP
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

// 3. dependencyResolutionManagement
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "safeair"
include(":app")
