package graphApp.model.graph

import androidx.compose.ui.graphics.Color

data class GraphSettings(
    var arrowColor: Color = Color.Red,
    var arrowSize: Float = 15f, // Размер в пикселях
    var arrowStrokeWidth: Float = 3f, // Толщина обводки
    var vertexColor: Color = Color.Blue,
    var edgeColor: Color = Color.Black,
    var vertexRadius: Float = 25f,
    var edgeWidth: Float = 3f,
    var animatedEdges: Boolean = true,
    var animationSpeed: Float = 1.0f
)