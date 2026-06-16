package graphApp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import graphApp.view.components.Localization
import graphApp.view.components.canvas.GraphCanvasArea
import graphApp.view.components.dialogs.MainDialogs
import graphApp.view.components.dialogs.MainDialogsState
import graphApp.view.components.panels.ControlPanel
import graphApp.view.components.panels.ZoomControls
import graphApp.view.components.topbar.MainTopBar
import graphApp.view.themes.MyAppTheme
import graphApp.viewmodel.GraphViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrameWindowScope.MainView(
    viewModel: GraphViewModel,
    onNewWindow: () -> Unit,
    onCloseWindow: () -> Unit
) {
    val s = remember { MainViewState() }
    val dialogs = remember { MainDialogsState() }

    MainDialogs(
        state = dialogs,
        viewModel = viewModel,
        dijkstraStart = s.dijkstraStart,
        dijkstraEnd = s.dijkstraEnd
    )

    MyAppTheme(darkTheme = s.darkTheme) {
        Scaffold(
            topBar = {
                MainTopBar(
                    darkTheme = s.darkTheme,
                    onThemeToggle = { s.darkTheme = !s.darkTheme },
                    currentLanguage = s.currentLanguage,
                    onLanguageToggle = {
                        s.currentLanguage = if (s.currentLanguage == "en") "ru" else "en"
                        Localization.setLanguage(s.currentLanguage)
                    },
                    onOpenFile = { dialogs.showOpenDialog = true },
                    onSaveFile = { dialogs.showSaveFormatDialog = true },
                    onDatabase = { dialogs.showDatabaseDialog = true },
                    canUndo = viewModel.canUndo,
                    onUndo = { viewModel.undo() },
                    canRedo = viewModel.canRedo,
                    onRedo = { viewModel.redo() },
                    onNewWindow = onNewWindow,
                    onCloseWindow = onCloseWindow
                )
            },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    GraphCanvasArea(
                        viewModel = viewModel,
                        mergeMode = s.mergeMode,
                        onMergeModeEnd = { s.mergeMode = false },
                        dijkstraStart = s.dijkstraStart,
                        onDijkstraStartSet = { s.dijkstraStart = it },
                        dijkstraEnd = s.dijkstraEnd,
                        onDijkstraEndSet = { s.dijkstraEnd = it },
                        selectionMode = s.selectionMode,
                        onSelectionModeClear = { s.selectionMode = SelectionMode.NONE },
                        focusRequester = s.focusRequester,
                        onEdgeEditRequested = { edge ->
                            dialogs.selectedEdge = edge
                            dialogs.showEdgeEditDialog = true
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    ControlPanel(
                        onAddVertex = { viewModel.addVertexAtPosition(0f, 0f) },
                        onAddEdge = { dialogs.showEdgeDialog = true },
                        onAlgorithms = { dialogs.showAlgorithmDialog = true },
                        onClear = { viewModel.clearGraph() },
                        onMerge = { s.mergeMode = !s.mergeMode },
                        onGenerate = { dialogs.showGenerateDialog = true },
                        onHelp = { dialogs.showHelpDialog = true },
                        onUndo = { viewModel.undo() },
                        onRedo = { viewModel.redo() },
                        canUndo = viewModel.canUndo,
                        canRedo = viewModel.canRedo,
                        onSetDijkstraStart = { s.selectionMode = SelectionMode.DIJKSTRA_START },
                        onSetDijkstraEnd = { s.selectionMode = SelectionMode.DIJKSTRA_END },
                        dijkstraStart = s.dijkstraStart,
                        dijkstraEnd = s.dijkstraEnd,
                        onCustomize = { dialogs.showSettingsDialog = true },
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )

                    ZoomControls(
                        scale = viewModel.scale,
                        onZoomIn = { viewModel.handleZoom(0.1f) },
                        onZoomOut = { viewModel.handleZoom(-0.1f) },
                        onReset = {
                            viewModel.scale = 1f
                            viewModel.offset = Offset.Zero
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        )
    }
}
