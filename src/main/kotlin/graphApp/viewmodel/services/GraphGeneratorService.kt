package graphApp.viewmodel.services

import graphApp.model.graph.*
import graphApp.view.components.dialogs.GraphType
import kotlin.math.min
import kotlin.random.Random

object GraphGeneratorService {

    fun generate(
        type: GraphType,
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ): Graph<Edge> = when (type) {
        GraphType.TREE -> generateTree(vertexCount, minWeight, maxWeight)
        GraphType.RANDOM -> generateRandomGraph(vertexCount, edgeProbability, minWeight, maxWeight)
        GraphType.WEIGHTED_GRAPH -> generateWeightedGraph(vertexCount, edgeProbability, minWeight, maxWeight)
    }

    private fun generateWeightedGraph(
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ): Graph<Edge> = Graph<Edge>().apply {
        repeat(vertexCount) { i ->
            val vertex = Vertex("V${i + 1}")
            addVertex(vertex)
            setPosition(vertex, Random.nextInt(100, 700).toFloat(), Random.nextInt(100, 600).toFloat())
        }
        val verticesList = vertices.toList()
        for (i in verticesList.indices) {
            for (j in i + 1 until verticesList.size) {
                if (Random.nextDouble() < edgeProbability) {
                    val weight = Random.nextDouble(minWeight, maxWeight)
                    addEdge(WeightedEdge(verticesList[i], verticesList[j], weight))
                    addEdge(WeightedEdge(verticesList[j], verticesList[i], weight))
                }
            }
        }
    }

    private fun generateTree(vertexCount: Int, minWeight: Double, maxWeight: Double): Graph<Edge> =
        Graph<Edge>().apply {
            repeat(vertexCount) { i ->
                val vertex = Vertex("V${i + 1}")
                addVertex(vertex)
                setPosition(vertex, Random.nextInt(100, 700).toFloat(), Random.nextInt(100, 600).toFloat())
            }
            val verticesList = vertices.toList()
            if (verticesList.isEmpty()) return@apply

            val root = verticesList.first()
            val visited = mutableSetOf(root)
            val queue = ArrayDeque<Vertex>().apply { add(root) }

            while (visited.size < verticesList.size) {
                val current = queue.removeFirstOrNull() ?: break
                val remaining = verticesList.size - visited.size
                val maxChildren = min(2, remaining)

                verticesList.filter { it !in visited }.shuffled().take((1..maxChildren).random())
                    .forEach { child ->
                        val weight = Random.nextDouble(minWeight, maxWeight)
                        val edge = if (Random.nextBoolean()) DirectedWeightedEdge(current, child, weight)
                                   else DirectedWeightedEdge(child, current, weight)
                        addEdge(edge)
                        visited.add(child)
                        queue.add(child)
                    }
            }

            verticesList.filter { it !in visited }.forEach { child ->
                val weight = Random.nextDouble(minWeight, maxWeight)
                val edge = if (Random.nextBoolean()) DirectedWeightedEdge(root, child, weight)
                           else DirectedWeightedEdge(child, root, weight)
                addEdge(edge)
            }
        }

    private fun generateRandomGraph(
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ): Graph<Edge> = Graph<Edge>().apply {
        repeat(vertexCount) { i ->
            val vertex = Vertex("V${i + 1}")
            addVertex(vertex)
            setPosition(vertex, Random.nextInt(100, 700).toFloat(), Random.nextInt(100, 600).toFloat())
        }
        val verticesList = vertices.toList()
        for (i in verticesList.indices) {
            for (j in verticesList.indices) {
                if (i != j && Random.nextDouble() < edgeProbability) {
                    addEdge(DirectedWeightedEdge(verticesList[i], verticesList[j], Random.nextDouble(minWeight, maxWeight)))
                }
            }
        }
    }
}
