import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

abstract class BaseTestPlugin(private val id: String) : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        logger.lifecycle("Applying plugin '$id'")

        val amountTasks = providers.gradleProperty("amountTasks")
            .map(String::toInt)
            .getOrElse(1000)

        val amountLines = providers.gradleProperty("amountLines")
            .map(String::toInt)
            .orNull

        val tasksToMutate = providers.gradleProperty("mutations")
            .map(String::toInt)
            .getOrElse(0)
            .let { (1..it).map { Random.Default.nextInt(amountTasks) + 1 } }

        (1..amountTasks).forEach {
            val taskName = "plugin${id}Task$it"

            logger.lifecycle("Registering task '$taskName'")

            when (amountLines) {
                null -> tasks.register(taskName)
                else -> tasks.register<GenerateBigClassesTask>(taskName) {
                    linesCount.set(amountLines)
                    mutations.set(if (it in tasksToMutate) min((amountLines * .1f).roundToInt(), 1) else 0)
                }
            }.configure {
                logger.lifecycle("Configuring task '$taskName'")
                doFirst {
                    logger.lifecycle("Running task '$taskName'")
                }
            }
        }

        the<KotlinSourceSetContainer>().sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME) {
            kotlin.srcDir(tasks.withType<GenerateBigClassesTask>())
        }
    }

}
