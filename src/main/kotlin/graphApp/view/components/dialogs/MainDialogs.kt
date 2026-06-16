package graphApp.view.components.dialogs

import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import graphApp.model.algorithms.AlgorithmType
import graphApp.model.graph.Vertex
import graphApp.view.components.Localization
import graphApp.viewmodel.AlgorithmResult
import graphApp.viewmodel.GraphViewModel
import java.io.File

@Composable
fun FrameWindowScope.MainDialogs(
    state: MainDialogsState,
    viewModel: GraphViewModel,
    dijkstraStart: Vertex?,
    dijkstraEnd: Vertex?
) {
    // Внутренние состояния цепочки открытия файла
    var showOpenFormatDialog by remember { mutableStateOf(false) }
    var showFileOpenDialog by remember { mutableStateOf(false) }
    var openDialogShown by remember { mutableStateOf(false) }
    var formatForOpenDialog by remember { mutableStateOf("") }

    if (state.showSaveFormatDialog) {
        FileSaveWithFormatDialog { file, format ->
            if (file != null && format.isNotEmpty()) {
                val content = when (format) {
                    "json" -> viewModel.exportToJson()
                    "csv" -> viewModel.exportToCsv()
                    else -> ""
                }
                file.writeText(content)
            }
            state.showSaveFormatDialog = false
        }
    }

    if (state.showOpenDialog && !openDialogShown) {
        openDialogShown = true
        showOpenFormatDialog = true
        state.showOpenDialog = false
    }

    if (showOpenFormatDialog) {
        OpenFormatDialog { format ->
            showOpenFormatDialog = false
            if (format.isNotEmpty()) {
                formatForOpenDialog = format
                showFileOpenDialog = true
            } else {
                openDialogShown = false
            }
        }
    }

    if (showFileOpenDialog) {
        FileOpenDialog { file: File? ->
            showFileOpenDialog = false
            openDialogShown = false
            file?.let {
                try {
                    val content = it.readText()
                    when (formatForOpenDialog) {
                        "json" -> viewModel.importFromJson(content)
                        "csv" -> viewModel.importFromCsv(content)
                    }
                } catch (e: Exception) {
                    println("Ошибка загрузки: ${e.message}")
                }
            }
        }
    }

    if (state.showGenerateDialog) {
        GenerateGraphDialog(
            onGenerate = { type, vertexCount, edgeProbability, minWeight, maxWeight ->
                viewModel.generateGraph(type, vertexCount, edgeProbability, minWeight, maxWeight)
                state.showGenerateDialog = false
            },
            onDismiss = { state.showGenerateDialog = false }
        )
    }

    if (state.showEdgeDialog) {
        val vertices = viewModel.graph.value?.vertices?.toList() ?: emptyList()
        AddEdgeDialog(
            vertices = vertices,
            onDismiss = { state.showEdgeDialog = false },
            onConfirm = { from, to, weight, isDirected ->
                if (isDirected) {
                    viewModel.addEdge(from, to, weight, true)
                } else {
                    viewModel.addEdge(from, to, weight)
                    viewModel.addEdge(to, from, weight)
                }
                state.showEdgeDialog = false
            }
        )
    }

    val edge = state.selectedEdge
    if (state.showEdgeEditDialog && edge != null) {
        EdgeEditDialog(
            edge = edge,
            onDismiss = {
                state.showEdgeEditDialog = false
                state.selectedEdge = null
            },
            onConfirm = { newWeight, isDirected ->
                viewModel.updateEdge(edge, newWeight, isDirected)
                state.showEdgeEditDialog = false
                state.selectedEdge = null
            }
        )
    }

    if (state.showAlgorithmDialog) {
        AlgorithmDialog(
            onDismiss = { state.showAlgorithmDialog = false },
            onAlgorithmSelected = { algorithm ->
                when (algorithm) {
                    AlgorithmType.DIJKSTRA -> {
                        if (dijkstraStart == null || dijkstraEnd == null) {
                            viewModel.setAlgorithmResult(
                                AlgorithmResult.MessageResult(
                                    Localization.tr("dijkstra_vertices_not_selected")
                                )
                            )
                        } else {
                            viewModel.selectedStart = dijkstraStart
                            viewModel.selectedEnd = dijkstraEnd
                            viewModel.runAlgorithm(algorithm)
                        }
                    }
                    else -> viewModel.runAlgorithm(algorithm)
                }
                state.showAlgorithmDialog = false
            }
        )
    }

    if (state.showDatabaseDialog) {
        DatabaseDialog(
            viewModel = viewModel,
            onDismiss = { state.showDatabaseDialog = false }
        )
    }

    if (state.showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onSettingsChange = { viewModel.updateSettings(it) },
            onDismiss = { state.showSettingsDialog = false }
        )
    }

    if (state.showHelpDialog) {
        HelpDialog(onDismiss = { state.showHelpDialog = false })
    }
}
