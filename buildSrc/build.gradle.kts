plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val amountOfPlugins = providers.gradleProperty("amountPlugins")
    .map(String::toInt)
    .getOrElse(100)

val pluginsToGenerate = (1..amountOfPlugins)

gradlePlugin {
    pluginsToGenerate.forEach {
        plugins.create("$it") {
            id = "test$it"
            implementationClass = "TestPlugin$it"
        }
    }
}

val classesDir = layout.buildDirectory.dir("generated/classes")
val generatePlugins = tasks.register("generateTestPlugins") {
    outputs.dir(classesDir)

    doLast {
        pluginsToGenerate.forEach {
            classesDir.get().file("TestPlugin$it.kt").asFile.writeText("""
                class TestPlugin$it : BaseTestPlugin("$it")
            """.trimIndent())
        }

        classesDir.get().file("GeneratedPlugins.kt").asFile.writeText(gradlePlugin.plugins.joinToString(
            prefix = "val generatedPluginsIds = arrayOf(",
            separator = ",",
            postfix = "\n)"
        ) { "\n    \"${it.id}\"" })
    }
}

kotlin.sourceSets.main.configure {
    kotlin.srcDir(generatePlugins.map { classesDir })
}
