package graphApp.view.components.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import graphApp.model.graph.GraphSettings
import graphApp.model.graph.Position
import graphApp.model.graph.Vertex
import graphApp.viewmodel.AlgorithmResult
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.getVertexColor(
    vertex: Vertex,
    selectedVertex: Vertex?,
    hoveredVertex: Vertex?,
    firstVertexForMerge: Vertex?,
    algorithmResult: AlgorithmResult?,
    dijkstraStart: Vertex?,
    dijkstraEnd: Vertex?,
    baseColor: Color // Базовый цвет из настроек
): Color {
    return when {
        vertex == dijkstraStart -> Color.Green
        vertex == dijkstraEnd -> Color.Red
        algorithmResult is AlgorithmResult.ShortestPath && vertex in algorithmResult.path -> Color.Yellow
        vertex == selectedVertex -> Color.Magenta
        vertex == hoveredVertex -> Color.Cyan
        vertex == firstVertexForMerge -> Color.Blue
        else -> baseColor // Используем базовый цвет из настроек
    }
}

fun getVibrantColorForComponent(index: Int): Color {
    val colors = listOf(
        Color(0xFFFF5252), // Ярко-красный
        Color(0xFF448AFF), // Ярко-синий
        Color(0xFF4CAF50), // Ярко-зеленый
        Color(0xFFFFC107), // Ярко-янтарный
        Color(0xFFE040FB), // Фиолетовый
        Color(0xFF18FFFF), // Бирюзовый
        Color(0xFFFF4081), // Розовый
        Color(0xFFFF9800), // Оранжевый
        Color(0xFF7C4DFF), // Пурпурный
        Color(0xFF00BFA5)  // Морская волна
    )
    return colors[index % colors.size]
}

fun DrawScope.drawPathHighlight(
    path: List<Vertex>,
    positions: Map<Vertex, Position>
) {
    if (path.size < 2) return

    path.zipWithNext().forEach { (from, to) ->
        val start = positions[from]?.let { Offset(it.x, it.y) } ?: return@forEach
        val end = positions[to]?.let { Offset(it.x, it.y) } ?: return@forEach

        drawLine(
            color = Color(0xFF4CAF50),
            start = start,
            end = end,
            strokeWidth = 8f
        )
    }
}

fun DrawScope.drawVertex(
    vertex: Vertex,
    position: Position,
    textMeasurer: TextMeasurer,
    color: Color = Color.Blue,
    isHovered: Boolean = false,
    radius: Float
) {
    if (isHovered) {
        drawCircle(
            color = Color.Green.copy(alpha = 0.3f),
            radius = radius + 5f,
            center = Offset(position.x, position.y)
        )
    }

    drawCircle(
        color = color,
        radius = radius,
        center = Offset(position.x, position.y)
    )

    drawCircle(
        color = Color.White,
        radius = radius / 2,
        center = Offset(position.x, position.y)
    )

    val textLayout = textMeasurer.measure(vertex.toString())
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = position.x - textLayout.size.width / 2,
            y = position.y - textLayout.size.height / 2
        ),
        color = Color.Black
    )
}

fun DrawScope.drawEdge(
    textMeasurer: TextMeasurer,
    time: Float,
    start: Offset,
    end: Offset,
    isDirected: Boolean,
    weight: Double,
    settings: GraphSettings
) {
    val color = if (settings.animatedEdges) {
        getRGBColor(time, settings.animationSpeed)
    } else {
        settings.edgeColor // Статичный цвет если анимация выключена
    }
    val angle = atan2(end.y - start.y, end.x - start.x)
    val arrowOffset = 25f

    val adjustedEnd = Offset(
        end.x - arrowOffset * cos(angle),
        end.y - arrowOffset * sin(angle)
    )

    drawLine(
        color = color,
        start = start,
        end = adjustedEnd,
        strokeWidth = 3f
    )

    val weightText = "%.1f".format(weight)
    val textLayout = textMeasurer.measure(weightText)
    val midpoint = Offset((start.x + end.x) / 2, (start.y + end.y) / 2)

    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = (textLayout.size.height / 2 + 4).toFloat(),
        center = midpoint
    )

    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = midpoint.x - textLayout.size.width / 2,
            y = midpoint.y - textLayout.size.height / 2
        ),
        color = Color.Red
    )

    if (isDirected) {
        drawCustomArrow(
            center = adjustedEnd,
            angle = angle,
            size = settings.arrowSize, // Используем настройки размера
            color = settings.arrowColor, // Используем настройки цвета
            strokeWidth = settings.arrowStrokeWidth // Используем настройки толщины
        )
    }
}

private fun DrawScope.drawCustomArrow(
    center: Offset,
    angle: Float,
    size: Float,
    color: Color,
    strokeWidth : Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y)
        lineTo(
            center.x - size * cos(angle + (Math.PI / 6).toFloat()),
            center.y - size * sin(angle + (Math.PI / 6).toFloat())
        )
        moveTo(center.x, center.y)
        lineTo(
            center.x - size * cos(angle - (Math.PI / 6).toFloat()),
            center.y - size * sin(angle - (Math.PI / 6).toFloat())
        )
    }
    // Используем переданный цвет
    drawPath(path, color, style = Stroke(width = strokeWidth))
}

fun getRGBColor(time: Float, animationSpeed: Float): Color {
    val speedFactor = animationSpeed * 0.0005f
    val red = ((sin(time * speedFactor + 0) + 1) / 2 * 255).toInt()
    val green = ((sin(time * speedFactor + 2) + 1) / 2 * 255).toInt()
    val blue = ((sin(time * speedFactor + 4) + 1) / 2 * 255).toInt()

    return Color(red, green, blue)
}