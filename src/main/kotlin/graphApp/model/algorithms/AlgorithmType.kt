package graphApp.model.algorithms

import graphApp.view.components.Localization.tr

enum class AlgorithmType(val displayName: String) {
    DIJKSTRA(tr("dij")),
    FORCEATLAS2(tr("for")),
    KOSARAJU(tr("kos"))
}