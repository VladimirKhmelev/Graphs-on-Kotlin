package graphApp.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import graphApp.model.graph.Vertex

class MainViewState {
    var darkTheme by mutableStateOf(false)
    var currentLanguage by mutableStateOf("en")
    var mergeMode by mutableStateOf(false)
    var dijkstraStart by mutableStateOf<Vertex?>(null)
    var dijkstraEnd by mutableStateOf<Vertex?>(null)
    var selectionMode by mutableStateOf<SelectionMode>(SelectionMode.NONE)
    val focusRequester = FocusRequester()
}
