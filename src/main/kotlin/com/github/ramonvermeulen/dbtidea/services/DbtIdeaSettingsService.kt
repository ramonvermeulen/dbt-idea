package com.github.ramonvermeulen.dbtidea.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir

@State(
    name = "DbtIdeaSettings",
    storages = [Storage("DbtIdeaSettings.xml")],
)
@Service(Service.Level.PROJECT)
class DbtIdeaSettingsService(private var project: Project) : PersistentStateComponent<DbtIdeaSettingsService.State> {
    data class State(
        var dbtProjectDir: String = "",
        var dbtTargetDir: String = "",
    )
    companion object {
        const val DBT_DOCS_FILE = "index.html"
        const val DBT_MANIFEST_FILE = "manifest.json"
        const val DBT_CATALOG_FILE = "catalog.json"
    }
    var static = Companion

    private var state =
        State(
            dbtProjectDir = project.guessProjectDir()?.path ?: "",
            dbtTargetDir = "${project.guessProjectDir()?.path ?: ""}/target",
        )

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }
}
