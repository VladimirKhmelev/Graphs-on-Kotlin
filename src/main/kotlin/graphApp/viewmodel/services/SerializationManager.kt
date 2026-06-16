package graphApp.viewmodel.services

import graphApp.model.graph.*
import graphApp.model.graph.serialization.SerializableGraph
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class ImportResult(val graph: Graph<Edge>, val maxVertexId: Int)

object SerializationManager {

    fun exportToJson(graph: Graph<Edge>?): String =
        Json.encodeToString(graph?.toSerializable())

    fun importFromJson(json: String): ImportResult? = try {
        val serialized = Json.decodeFromString<SerializableGraph>(json)
        val graph = Graph.fromSerializable(serialized)
        val maxId = graph.vertices.mapNotNull { it.id.removePrefix("V").toIntOrNull() }.maxOrNull() ?: 0
        ImportResult(graph, maxId)
    } catch (e: Exception) {
        println("Ошибка десериализации: ${e.message}")
        null
    }

    fun exportToCsv(graph: Graph<Edge>?): String {
        graph ?: return ""
        val sb = StringBuilder()
        sb.appendLine("type,id,x,y,source,target,weight,directed")
        graph.positions.forEach { (vertex, pos) ->
            sb.appendLine("vertex,${vertex.id},${pos.x},${pos.y}")
        }
        graph.edges.forEach { edge ->
            val directed = edge is DirectedEdge
            val weight = if (edge is WeightedEdge) edge.weight else 1.0
            sb.appendLine("edge,,,,${edge.from.id},${edge.to.id},$weight,$directed")
        }
        return sb.toString()
    }

    fun importFromCsv(csv: String): ImportResult? = try {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) null else run {
            val newGraph = Graph<Edge>()
            var maxId = 0

            lines.filter { it.startsWith("vertex") }.forEach { line ->
                val parts = line.split(",")
                if (parts.size >= 4) {
                    val id = parts[1]
                    val x = parts[2].toFloatOrNull() ?: 0f
                    val y = parts[3].toFloatOrNull() ?: 0f
                    val vertex = Vertex(id)
                    newGraph.addVertex(vertex)
                    newGraph.setPosition(vertex, x, y)
                    val idNum = id.removePrefix("V").toIntOrNull()
                    if (idNum != null && idNum > maxId) maxId = idNum
                }
            }

            lines.filter { it.startsWith("edge") }.forEach { line ->
                val parts = line.split(",")
                if (parts.size >= 8) {
                    val fromId = parts[4]
                    val toId = parts[5]
                    val weight = parts[6].toDoubleOrNull() ?: 1.0
                    val directed = parts[7].toBoolean()
                    val fromVertex = newGraph.vertices.find { it.id == fromId }
                    val toVertex = newGraph.vertices.find { it.id == toId }
                    if (fromVertex != null && toVertex != null) {
                        if (directed) newGraph.addEdge(DirectedWeightedEdge(fromVertex, toVertex, weight))
                        else newGraph.addEdge(WeightedEdge(fromVertex, toVertex, weight))
                    }
                }
            }
            ImportResult(newGraph, maxId)
        }
    } catch (e: Exception) {
        println("Ошибка импорта CSV: ${e.message}")
        null
    }
}
