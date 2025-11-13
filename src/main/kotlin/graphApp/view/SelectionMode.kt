package graphApp.view

// Для алгоритма Дейкстры

sealed class SelectionMode {
    data object NONE : SelectionMode()
    data object DIJKSTRA_START : SelectionMode()
    data object DIJKSTRA_END : SelectionMode()
}
