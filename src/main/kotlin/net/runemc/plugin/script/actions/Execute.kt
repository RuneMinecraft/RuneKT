package net.runemc.plugin.script.actions

import net.runemc.plugin.script.ScriptManager
import org.bukkit.command.CommandSender
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvm.BasicJvmScriptEvaluator
import java.io.File
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvmhost.JvmScriptCompiler

suspend fun execute(scriptManager: ScriptManager, sender: CommandSender, scriptName: String) {
    val scriptFile: File? = scriptManager.getScript(scriptName)
    if (scriptFile == null || !scriptFile.exists() || !scriptFile.canRead()) {
        sender.sendMessage("Script '$scriptName' not found or is not readable.")
        return
    }

    try {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<Any> {
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
            }
        }
        val evaluationConfiguration = ScriptEvaluationConfiguration {
            jvm {
                baseClassLoader(this::class.java.classLoader)
            }
        }
        val scriptSource = FileScriptSource(scriptFile)
        val compiler = JvmScriptCompiler()
        val evaluator = BasicJvmScriptEvaluator()
        val compiledScriptResult = compiler.compilerProxy.compile(scriptSource, compilationConfiguration)

        if (compiledScriptResult is ResultWithDiagnostics.Failure) {
            val errorMessages = compiledScriptResult.reports.joinToString("\n") { it.message }
            sender.sendMessage("Failed to compile script: $errorMessages")
            return
        }

        val evaluationResult = evaluator(compiledScriptResult.valueOrThrow(), evaluationConfiguration)

        if (evaluationResult is ResultWithDiagnostics.Success) {
            sender.sendMessage("Script executed successfully: ${evaluationResult.value}")
        } else {
            val errorMessages = evaluationResult.reports.joinToString("\n") { it.message }
            sender.sendMessage("Script execution failed: $errorMessages")
        }
    } catch (e: Throwable) {
        sender.sendMessage("Error while executing script: ${e.message}")
        e.printStackTrace()
    }
}
