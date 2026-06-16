package graphApp.view.components.dialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import graphApp.model.graph.Edge

class MainDialogsState {
    var showAlgorithmDialog by mutableStateOf(false)
    var showEdgeDialog by mutableStateOf(false)
    var showSaveFormatDialog by mutableStateOf(false)
    var showOpenDialog by mutableStateOf(false)
    var showGenerateDialog by mutableStateOf(false)
    var showHelpDialog by mutableStateOf(false)
    var showSettingsDialog by mutableStateOf(false)
    var showDatabaseDialog by mutableStateOf(false)
    var showEdgeEditDialog by mutableStateOf(false)
    var selectedEdge by mutableStateOf<Edge?>(null)
}
