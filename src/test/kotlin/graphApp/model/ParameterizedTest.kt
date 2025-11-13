
import graphApp.model.graph.DirectedWeightedGraph
import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ParametrizedGraphTests {
    private val graph = DirectedWeightedGraph()

    @ParameterizedTest
    @CsvSource(
        "A, B, 2.5",
        "X, Y, 0.0",
        "M, N, 100.0"
    )
    fun `add multiple weighted edges parametrized`(from: String, to: String, weight: Double) {
        val vFrom = Vertex(from)
        val vTo = Vertex(to)
        graph.addVertex(vFrom)
        graph.addVertex(vTo)

        graph.addEdge(vFrom, vTo, weight)

        Assertions.assertEquals(weight, graph.getEdgeWeight(vFrom, vTo))
    }
}
