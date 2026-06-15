package graphApp.model.graph.repository

import graphApp.model.graph.Edge
import graphApp.model.graph.Graph

interface GraphRepository {
    fun save(graph: Graph<Edge>, name: String)
    fun load(name: String): Graph<Edge>?
    fun listGraphs(): List<String>
    fun delete(name: String)
}
