plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    `java-library`
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
