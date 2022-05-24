import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.nio.file.Files
import javax.inject.Inject
import kotlin.random.Random
import org.gradle.kotlin.dsl.*

@CacheableTask
abstract class GenerateBigClassesTask : DefaultTask() {

    @get:Input
    abstract val linesCount: Property<Int>

    @get:Input
    abstract val mutations: Property<Int>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        linesCount.convention(1000)
        mutations.convention(0)
        outputDirectory.convention(project.layout.dir(project.provider { temporaryDir }))
    }

    @TaskAction
    fun generateFiles() {
        val file = outputDirectory.file("${this@GenerateBigClassesTask.name.capitalize()}.kt").get().asFile
        val lines = linesCount.get()
        val mutateLines = (1..mutations.get()).map { Random.Default.nextInt(lines) }

        Files.newBufferedWriter(file.toPath()).use { out ->
            out.appendLine("data class ${file.nameWithoutExtension}(")
            (1..lines).forEach {
                if (it in mutateLines) {
                    val mutateValue = Random.Default.nextFloat()

                    logger.lifecycle("field $it has been mutated: mutated$it = $mutateValue")
                    out.appendLine("    val mutated$it : Float = ${mutateValue}f,")

                } else {
                    out.appendLine("    val prop$it : String,")
                }
            }
            out.appendLine(")")
        }
    }

}
