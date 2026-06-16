package graphApp.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import graphApp.model.algorithms.AlgorithmType
import graphApp.model.algorithms.connectivity.Kosaraju
import graphApp.model.algorithms.layout.ForceAtlas2
import graphApp.model.algorithms.shortestpath.Dijkstra
import graphApp.model.graph.*
import graphApp.model.graph.repository.GraphRepository
import graphApp.model.graph.repository.SQLiteGraphRepository
import graphApp.view.components.Localization
import graphApp.view.components.dialogs.GraphType
import graphApp.viewmodel.services.GraphGeneratorService
import graphApp.viewmodel.services.SerializationManager
import kotlinx.coroutines.*
import kotlin.math.sqrt

class GraphViewModel {

    private val _graph = mutableStateOf<Graph<Edge>?>(null)
    val graph: State<Graph<Edge>?> get() = _graph

    val canUndo get() = _undoStack.isNotEmpty()
    val canRedo get() = _redoStack.isNotEmpty()
    private val _undoStack = mutableStateListOf<GraphState>()
    private val _redoStack = mutableStateListOf<GraphState>()

    private val _showUndoRedoEffect = mutableStateOf(false)
    val showUndoRedoEffect: State<Boolean> get() = _showUndoRedoEffect

    private val layoutAlgorithm = ForceAtlas2()

    val graphSettings = mutableStateOf(GraphSettings())

    var selectedStart by mutableStateOf<Vertex?>(null)
    var selectedEnd by mutableStateOf<Vertex?>(null)

    var scale by mutableStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    private var canvasSize = Offset(800f, 600f)

    fun setCanvasSize(width: Float, height: Float) {
        canvasSize = Offset(width, height)
    }

    private val _algorithmResult = mutableStateOf<AlgorithmResult?>(null)
    val algorithmResult: State<AlgorithmResult?> get() = _algorithmResult
    private var resultTimerJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var vertexCounter = 0

    private val dbRepository: GraphRepository = SQLiteGraphRepository()

    init {
        saveState()
    }

    fun updateSettings(newSettings: GraphSettings) {
        graphSettings.value = newSettings
        _graph.value = _graph.value?.copy()
    }

    fun setAlgorithmResult(result: AlgorithmResult?) {
        resultTimerJob?.cancel()
        _algorithmResult.value = result
        if (result != null) {
            resultTimerJob = coroutineScope.launch {
                delay(3000)
                _algorithmResult.value = null
            }
        }
    }

    private fun saveState() {
        _undoStack.add(GraphState(_graph.value?.copy(), vertexCounter, scale, offset))
        _redoStack.clear()
    }

    fun undo() {
        if (canUndo) {
            _redoStack.add(GraphState(_graph.value?.copy(), vertexCounter, scale, offset))
            restoreState(_undoStack.removeLast())
            flashUndoRedoEffect()
        }
    }

    fun redo() {
        if (canRedo) {
            _undoStack.add(GraphState(_graph.value?.copy(), vertexCounter, scale, offset))
            restoreState(_redoStack.removeLast())
            flashUndoRedoEffect()
        }
    }

    private fun flashUndoRedoEffect() {
        _showUndoRedoEffect.value = true
        coroutineScope.launch {
            delay(100)
            _showUndoRedoEffect.value = false
        }
    }

    private fun restoreState(state: GraphState) {
        _graph.value = state.graph
        vertexCounter = state.vertexCounter
        scale = state.scale
        offset = state.offset
    }

    private fun initializeGraphIfNeeded() {
        if (_graph.value == null) _graph.value = Graph()
    }

    private fun generateVertexId(): String {
        val existingIds = _graph.value?.vertices?.mapNotNull {
            it.id.removePrefix("V").toIntOrNull()
        }?.toMutableList() ?: mutableListOf()
        val newId = if (existingIds.isEmpty()) 1 else existingIds.max() + 1
        return "V$newId"
    }

    fun addVertexAtPosition(x: Float, y: Float) {
        saveState()
        initializeGraphIfNeeded()
        val vertex = Vertex(generateVertexId())
        _graph.value?.apply {
            addVertex(vertex)
            setPosition(vertex, x, y)
            coroutineScope.launch {
                for (i in 1..10) { delay(10); setPosition(vertex, x, y - i * 2f) }
                for (i in 10 downTo 0) { delay(10); setPosition(vertex, x, y - i * 2f) }
            }
            selectedStart = null
            selectedEnd = null
        }
    }

    fun moveVertex(vertex: Vertex, dx: Float, dy: Float) {
        _graph.value?.positions?.get(vertex)?.let { pos ->
            _graph.value?.setPosition(vertex, pos.x + dx / scale, pos.y + dy / scale)
            _graph.value = _graph.value?.copy()
        }
    }

    fun findVertexAt(x: Float, y: Float): Vertex? =
        _graph.value?.positions?.entries?.firstOrNull { (_, pos) ->
            val dx = pos.x - x; val dy = pos.y - y
            sqrt(dx * dx + dy * dy) < 20
        }?.key

    fun generateGraph(type: GraphType, vertexCount: Int, edgeProbability: Double, minWeight: Double, maxWeight: Double) {
        saveState()
        clearGraph()
        _graph.value = GraphGeneratorService.generate(type, vertexCount, edgeProbability, minWeight, maxWeight)
        centerAndScaleGraph()
    }

    fun addEdge(from: Vertex, to: Vertex, weight: Double = 1.0, isDirected: Boolean = false) {
        saveState()
        _graph.value?.let { graph ->
            val edge = if (isDirected) DirectedWeightedEdge(from, to, weight) else WeightedEdge(from, to, weight)
            if (!graph.edges.any { e -> e.from == from && e.to == to || (!isDirected && e.from == to && e.to == from) }) {
                graph.addEdge(edge)
            }
        }
    }

    fun clearGraph() {
        setAlgorithmResult(null)
        saveState()
        _graph.value = Graph()
        selectedStart = null
        selectedEnd = null
    }

    fun runAlgorithm(algorithm: AlgorithmType) {
        setAlgorithmResult(null)
        saveState()
        val currentGraph = _graph.value ?: return

        coroutineScope.launch(Dispatchers.Default) {
            try {
                val result = when (algorithm) {
                    AlgorithmType.DIJKSTRA -> handleDijkstra(currentGraph)
                    AlgorithmType.KOSARAJU -> handleKosaraju(currentGraph)
                    AlgorithmType.FORCEATLAS2 -> {
                        applyForceLayout()
                        AlgorithmResult.MessageResult(Localization.tr("layout_suc"))
                    }
                }
                withContext(Dispatchers.Main) { setAlgorithmResult(result) }
            } catch (_: Exception) {}
        }
    }

    private fun applyForceLayout() {
        _graph.value?.let { graph ->
            val tempGraph = graph.copy()
            layoutAlgorithm.applyLayout(tempGraph)
            tempGraph.positions.forEach { (vertex, position) ->
                graph.setPosition(vertex, position.x, position.y)
            }
            _graph.value = graph.copy()
        }
    }

    private fun centerAndScaleGraph() {
        val positions = _graph.value?.positions ?: return
        if (positions.isEmpty()) return

        var minX = Float.MAX_VALUE; var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE; var maxY = Float.MIN_VALUE

        positions.values.forEach { pos ->
            minX = minOf(minX, pos.x); maxX = maxOf(maxX, pos.x)
            minY = minOf(minY, pos.y); maxY = maxOf(maxY, pos.y)
        }

        val maxDim = maxOf(maxX - minX, maxY - minY, 1f)
        scale = minOf(1f, minOf(canvasSize.x, canvasSize.y) * 0.75f / maxDim)

        val centerX = (minX + maxX) / 2
        val centerY = (minY + maxY) / 2

        positions.forEach { (vertex, pos) ->
            _graph.value?.setPosition(vertex, (pos.x - centerX) * scale, (pos.y - centerY) * scale)
        }

        offset = Offset(canvasSize.x / 2, canvasSize.y / 2)
    }

    fun handlePan(dx: Float, dy: Float) {
        saveState()
        offset = Offset(offset.x + dx, offset.y + dy)
    }

    fun handleZoom(zoomDelta: Float) {
        saveState()
        scale = (scale * (1 + zoomDelta)).coerceIn(0.01f, 3f)
    }

    fun findEdgeNear(x: Float, y: Float, threshold: Float): Edge? {
        val graph = graph.value ?: return null
        val scaledThreshold = threshold / scale

        return graph.edges.minByOrNull { edge ->
            val fromPos = graph.positions[edge.from] ?: return@minByOrNull Float.MAX_VALUE
            val toPos = graph.positions[edge.to] ?: return@minByOrNull Float.MAX_VALUE
            distanceToLineSegment(x, y, fromPos.x, fromPos.y, toPos.x, toPos.y)
        }?.takeIf { edge ->
            val fromPos = graph.positions[edge.from]!!
            val toPos = graph.positions[edge.to]!!
            distanceToLineSegment(x, y, fromPos.x, fromPos.y, toPos.x, toPos.y) < scaledThreshold
        }
    }

    private fun distanceToLineSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val a = px - x1; val b = py - y1
        val c = x2 - x1; val d = y2 - y1
        val dot = a * c + b * d
        val lenSq = c * c + d * d
        val param = if (lenSq > 0) dot / lenSq else -1f
        val (xx, yy) = when {
            param < 0 -> x1 to y1
            param > 1 -> x2 to y2
            else -> (x1 + param * c) to (y1 + param * d)
        }
        val dx = px - xx; val dy = py - yy
        return sqrt(dx * dx + dy * dy)
    }

    fun updateEdge(oldEdge: Edge, newWeight: Double, isDirected: Boolean) {
        saveState()
        _graph.value?.let { graph ->
            graph.removeEdge(oldEdge)
            val newEdge = if (isDirected) DirectedWeightedEdge(oldEdge.from, oldEdge.to, newWeight)
                          else WeightedEdge(oldEdge.from, oldEdge.to, newWeight)
            graph.addEdge(newEdge)
            _graph.value = graph.copy()
        }
    }

    private fun handleDijkstra(currentGraph: Graph<Edge>): AlgorithmResult {
        if (selectedStart == null || selectedEnd == null)
            return AlgorithmResult.MessageResult("Select start and end vertices")
        val result = Dijkstra.findShortestPath(currentGraph, selectedStart!!, selectedEnd!!)
        return if (result != null)
            AlgorithmResult.ShortestPath(result.path, result.distance.coerceAtLeast(0.0))
        else
            AlgorithmResult.MessageResult(Localization.tr("path_not_found"))
    }

    private fun handleKosaraju(graph: Graph<Edge>): AlgorithmResult =
        AlgorithmResult.ConnectedComponents(Kosaraju.findStronglyConnectedComponents(graph))

    fun exportToJson(): String = SerializationManager.exportToJson(graph.value)

    fun importFromJson(json: String) {
        SerializationManager.importFromJson(json)?.let { result ->
            _graph.value = result.graph
            vertexCounter = result.maxVertexId
        }
    }

    fun exportToCsv(): String = SerializationManager.exportToCsv(graph.value)

    fun importFromCsv(csv: String) {
        SerializationManager.importFromCsv(csv)?.let { result ->
            _graph.value = result.graph
            vertexCounter = result.maxVertexId
            centerAndScaleGraph()
        }
    }

    fun saveToDatabase(name: String) {
        val g = graph.value ?: return
        try {
            @Suppress("UNCHECKED_CAST")
            dbRepository.save(g, name)
        } catch (e: Exception) {
            println("Ошибка сохранения в БД: ${e.message}")
        }
    }

    fun loadFromDatabase(name: String) {
        try {
            val loaded = dbRepository.load(name) ?: return
            _graph.value = loaded
            vertexCounter = loaded.vertices.mapNotNull { it.id.removePrefix("V").toIntOrNull() }.maxOrNull() ?: 0
            centerAndScaleGraph()
        } catch (e: Exception) {
            println("Ошибка загрузки из БД: ${e.message}")
        }
    }

    fun listDatabaseGraphs(): List<String> =
        try { dbRepository.listGraphs() } catch (_: Exception) { emptyList() }

    fun deleteFromDatabase(name: String) {
        try { dbRepository.delete(name) } catch (e: Exception) {
            println("Ошибка удаления из БД: ${e.message}")
        }
    }
}
