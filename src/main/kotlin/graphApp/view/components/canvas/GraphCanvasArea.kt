package graphApp.view.components.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import graphApp.model.graph.Edge
import graphApp.model.graph.Vertex
import graphApp.view.SelectionMode
import graphApp.view.components.Localization.tr
import graphApp.viewmodel.AlgorithmResult
import graphApp.viewmodel.GraphViewModel

@Composable
fun GraphCanvasArea(
    viewModel: GraphViewModel,
    mergeMode: Boolean,
    onMergeModeEnd: () -> Unit,
    dijkstraStart: Vertex?,
    onDijkstraStartSet: (Vertex?) -> Unit,
    dijkstraEnd: Vertex?,
    onDijkstraEndSet: (Vertex?) -> Unit,
    selectionMode: SelectionMode,
    onSelectionModeClear: () -> Unit,
    focusRequester: FocusRequester,
    onEdgeEditRequested: (Edge) -> Unit,
    modifier: Modifier = Modifier
) {
    var hoveredVertex by remember { mutableStateOf<Vertex?>(null) }
    var selectedVertex by remember { mutableStateOf<Vertex?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var isPanning by remember { mutableStateOf(false) }
    var mergeSourceVertex by remember { mutableStateOf<Vertex?>(null) }

    var vMode by remember { mutableStateOf(false) }
    var vFirstVertex by remember { mutableStateOf<Vertex?>(null) }
    var vHintVisible by remember { mutableStateOf(false) }
    var vHintMessage by remember { mutableStateOf("") }

    LaunchedEffect(mergeMode) {
        if (!mergeMode) mergeSourceVertex = null
    }

    Box(
        modifier = modifier
            .background(if (mergeMode) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent)
            .onSizeChanged { size ->
                viewModel.setCanvasSize(size.width.toFloat(), size.height.toFloat())
            }

            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        val graphX = (position.x - viewModel.offset.x) / viewModel.scale
                        val graphY = (position.y - viewModel.offset.y) / viewModel.scale
                        hoveredVertex = viewModel.findVertexAt(graphX, graphY)
                    }
                }
            }

            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Scroll) {
                            val scrollDelta = event.changes.first().scrollDelta.y
                            viewModel.handleZoom(scrollDelta * 0.05f)
                        }
                    }
                }
            }

            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    viewModel.handleZoom(zoom - 1)
                    viewModel.handlePan(pan.x, pan.y)
                }
            }

            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                        val vertex = viewModel.findVertexAt(graphX, graphY)
                        if (vertex != null) {
                            selectedVertex = vertex
                            isDragging = true
                        } else {
                            isPanning = true
                        }
                    },
                    onDrag = { _, dragAmount ->
                        if (isDragging) {
                            selectedVertex?.let { viewModel.moveVertex(it, dragAmount.x, dragAmount.y) }
                        } else if (isPanning) {
                            viewModel.handlePan(dragAmount.x, dragAmount.y)
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        isPanning = false
                        selectedVertex = null
                    }
                )
            }

            .onKeyEvent { event ->
                when {
                    event.key == Key.V && event.type == KeyEventType.KeyDown -> {
                        vMode = true
                        vHintVisible = true
                        vHintMessage = tr("v_mode_first")
                        true
                    }
                    event.key == Key.Escape && vMode -> {
                        vMode = false
                        vFirstVertex = null
                        vHintVisible = false
                        true
                    }
                    event.isCtrlPressed && event.key == Key.Z -> {
                        viewModel.undo()
                        true
                    }
                    event.isCtrlPressed && event.key == Key.Y -> {
                        viewModel.redo()
                        true
                    }
                    else -> false
                }
            }

            .pointerInput(vMode, mergeMode, selectionMode) {
                detectTapGestures(
                    onTap = { offset ->
                        focusRequester.requestFocus()

                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                        val vertex = viewModel.findVertexAt(graphX, graphY)

                        when (selectionMode) {
                            SelectionMode.DIJKSTRA_START -> {
                                onDijkstraStartSet(vertex)
                                onSelectionModeClear()
                            }
                            SelectionMode.DIJKSTRA_END -> {
                                onDijkstraEndSet(vertex)
                                onSelectionModeClear()
                            }
                            SelectionMode.NONE -> {}
                        }

                        if (vMode) {
                            if (vertex != null) {
                                if (vFirstVertex == null) {
                                    vFirstVertex = vertex
                                    vHintMessage = tr("v_mode_second")
                                } else if (vertex != vFirstVertex) {
                                    viewModel.addEdge(vFirstVertex!!, vertex)
                                    vMode = false
                                    vFirstVertex = null
                                    vHintVisible = false
                                } else {
                                    vFirstVertex = null
                                    vHintMessage = tr("v_mode_first_choose")
                                }
                            } else {
                                if (vFirstVertex != null) {
                                    vFirstVertex = null
                                    vHintMessage = tr("v_mode_first_choose")
                                } else {
                                    vMode = false
                                    vHintVisible = false
                                }
                            }
                        } else if (mergeMode) {
                            if (vertex != null) {
                                if (mergeSourceVertex == null) {
                                    mergeSourceVertex = vertex
                                } else if (vertex != mergeSourceVertex) {
                                    viewModel.addEdge(mergeSourceVertex!!, vertex)
                                    mergeSourceVertex = null
                                    onMergeModeEnd()
                                }
                            }
                        } else {
                            selectedVertex = vertex
                        }
                    },
                    onDoubleTap = { offset ->
                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                        viewModel.findEdgeNear(graphX, graphY, 1.8f)?.let(onEdgeEditRequested)
                    }
                )
            }

            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Release && event.buttons.isSecondaryPressed) {
                            val position = event.changes.first().position
                            val graphX = (position.x - viewModel.offset.x) / viewModel.scale
                            val graphY = (position.y - viewModel.offset.y) / viewModel.scale
                            viewModel.addVertexAtPosition(graphX, graphY)
                            event.changes.first().consume()
                        }
                    }
                }
            }

            .focusRequester(focusRequester)
            .onFocusChanged { }
            .focusable()
    ) {
        val graph = viewModel.graph.value
        GraphCanvas(
            vertices = graph?.positions ?: emptyMap(),
            edges = graph?.getAllEdges() ?: emptyList(),
            algorithmResult = viewModel.algorithmResult.value,
            selectedVertex = selectedVertex,
            hoveredVertex = hoveredVertex,
            firstVertexForMerge = vFirstVertex,
            scale = viewModel.scale,
            offset = viewModel.offset,
            dijkstraStart = dijkstraStart,
            dijkstraEnd = dijkstraEnd,
            settings = viewModel.graphSettings.value,
            modifier = Modifier.fillMaxSize()
        )

        if (vHintVisible) {
            Text(
                text = vHintMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp),
                color = Color.White,
                fontSize = 18.sp
            )
        }

        if (selectionMode != SelectionMode.NONE) {
            Text(
                text = when (selectionMode) {
                    SelectionMode.DIJKSTRA_START -> tr("start_vert")
                    SelectionMode.DIJKSTRA_END -> tr("end_vert")
                    SelectionMode.NONE -> ""
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp),
                color = Color.White,
                fontSize = 18.sp
            )
        }

        viewModel.algorithmResult.value?.let { result ->
            val message = when (result) {
                is AlgorithmResult.ShortestPath ->
                    tr("shortest_path_result").format(result.path.size, "%.2f".format(result.distance))
                is AlgorithmResult.ConnectedComponents ->
                    tr("strong_components_result").format(result.components.size)
                is AlgorithmResult.MessageResult -> result.message
            }
            Text(
                text = message,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp),
                color = Color.White,
                fontSize = 18.sp
            )
        }

        if (viewModel.showUndoRedoEffect.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green.copy(alpha = 0.2f))
            )
        }
    }
}
