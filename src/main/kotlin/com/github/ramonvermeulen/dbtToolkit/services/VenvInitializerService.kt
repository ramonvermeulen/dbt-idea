package com.github.ramonvermeulen.dbtToolkit.services

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.jetbrains.python.sdk.PythonSdkUtil
import org.apache.commons.lang3.SystemUtils
import java.nio.file.Files
import java.nio.file.Path

@Service(Service.Level.PROJECT)
class VenvInitializerService(private var project: Project) {
    private val loggingService = project.service<LoggingService>()
    private var venvDbtPath: String? = null

    private fun getDbtPath(venvPath: Path): Path {
        return if (SystemUtils.IS_OS_WINDOWS) {
            venvPath.resolve("Scripts/dbt.exe")
        } else {
            venvPath.resolve("dbt")
        }
    }

    fun initializeEnvironment() {
        val venvDir = getPythonVenvDir(project)
        if (venvDir == null) {
            loggingService.log("Python virtual environment not detected. Attempting to use a global dbt installation.\n\n", ConsoleViewContentType.ERROR_OUTPUT)
            return
        }

        loggingService.log("Detected Python virtual environment at: $venvDir\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val dbtPath = getDbtPath(Path.of(venvDir))

        if (!Files.exists(dbtPath)) {
            loggingService.log("dbt installation not found within the virtual environment. Please install dbt and restart your IDE.\n\n", ConsoleViewContentType.ERROR_OUTPUT)
            return
        }

        venvDbtPath = dbtPath.toString()
        loggingService.log("Located dbt installation within the virtual environment at: $venvDbtPath\n\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    private fun getPythonVenvDir(project: Project): String? {
        val projectSdk: Sdk? = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null && PythonSdkUtil.isVirtualEnv(projectSdk)) {
            return projectSdk.homePath
        }
        return null
    }

    fun getDbtPath(): String? {
        return venvDbtPath
    }
}