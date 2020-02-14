package de.itemis.mps.gradle

import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import java.io.File
import java.lang.RuntimeException

data class Plugin(
        var id: String,
        var path: String
)

data class Macro(
        var name: String,
        var value: String
)

open class BasePluginExtensions {
    lateinit var mpsConfig: Configuration
    var mpsLocation: File? = null
    var plugins: List<Plugin> = emptyList()
    var pluginLocation: File? = null
    var macros: List<Macro> = emptyList()
    var projectLocation: File? = null
    var debug = false
    var javaExec: File? = null
}

fun validateDefaultJvm(){
    val minimumRequiredVersion = 11
    val actualVersion = Integer.parseInt(System.getProperty("java.version"))
    if (actualVersion < minimumRequiredVersion)
        throw RuntimeException("Minimum required JVM version not met: Detected version: " + actualVersion + " Minimum required version: " + minimumRequiredVersion)
}

fun argsFromBaseExtension(extensions: BasePluginExtensions): MutableList<String> {
    val pluginLocation = if (extensions.pluginLocation != null) {
        sequenceOf("--plugin-location=${extensions.pluginLocation!!.absolutePath}")
    } else {
        emptySequence()
    }


    val projectLocation = extensions.projectLocation ?: throw GradleException("No project path set")
    val prj = sequenceOf("--project=${projectLocation.absolutePath}")

    return sequenceOf(pluginLocation,
            extensions.plugins.map { "--plugin=${it.id}::${it.path}" }.asSequence(),
            extensions.macros.map { "--macro=${it.name}::${it.value}" }.asSequence(),
            prj).flatten().toMutableList()
}