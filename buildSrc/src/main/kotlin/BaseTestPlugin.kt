import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BaseTestPlugin(private val id: String) : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        logger.lifecycle("Applying plugin '$id'")

        val amount = providers.gradleProperty("amountTasks")
            .map(String::toInt)
            .getOrElse(1000)

        (1..amount).forEach {
            val taskName = "plugin${id}Task$it"

            logger.lifecycle("Registering task '$taskName'")
            tasks.register(taskName) {
                logger.lifecycle("Configuring task '$taskName'")
                doLast {
                    logger.lifecycle("Running task '$taskName'")
                }
            }
        }
    }

}
