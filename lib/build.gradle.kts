plugins {
    `java-library`
    kotlin
}

val apply = providers.gradleProperty("apply")
    .map(String::toBoolean)
    .getOrElse(true)

if (apply) {
    generatedPluginsIds.forEach {
        apply(plugin = it)
    }
}

repositories {
    mavenCentral()
}
