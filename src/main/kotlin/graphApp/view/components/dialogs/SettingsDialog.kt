package graphApp.view.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import graphApp.model.graph.GraphSettings
import graphApp.view.components.Localization.tr
import graphApp.viewmodel.GraphViewModel

@Composable
fun SettingsDialog(
    viewModel: GraphViewModel,
    settings: GraphSettings,
    onSettingsChange: (GraphSettings) -> Unit,
    onDismiss: () -> Unit
) {
    // Локальная копия настроек для редактирования
    var localSettings by remember {
        mutableStateOf(viewModel.graphSettings.value.copy())
    }

    var selectedTab by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("customize_settings")) },
        text = {
            Column {
                // Вкладки настроек
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(tr("vertices")) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(tr("edges")) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2},
                        text = { Text(tr("arrows"))}
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> VertexSettingsPanel(localSettings) { newSettings ->
                        localSettings = newSettings // Обновляем локальные настройки
                    }
                    1 -> EdgeSettingsPanel(localSettings) { newSettings ->
                        localSettings = newSettings
                    }
                    2 -> ArrowSettingsPanel(localSettings) { newSettings ->
                        localSettings = newSettings
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSettingsChange(localSettings) // Передаём обновлённые локальные настройки
                viewModel.updateSettings(localSettings) // Сохраняем в ViewModel
                onDismiss()
            }) {
                Text(tr("save"))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(tr("cancel"))
            }
        }
    )
}

@Composable
fun ArrowSettingsPanel(
    settings: GraphSettings,
    onSettingsChange: (GraphSettings) -> Unit
) {
    Column {
        Text(
            text = tr("arrow_settings"),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Настройка цвета
        Text(tr("arrow_color"), style = MaterialTheme.typography.titleMedium)
        ColorPicker(
            currentColor = settings.arrowColor,
            onColorChange = { newColor ->
                onSettingsChange(settings.copy(arrowColor = newColor))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Настройка размера
        Text(tr("arrow_size"))
        Slider(
            value = settings.arrowSize,
            onValueChange = { newSize ->
                onSettingsChange(settings.copy(arrowSize = newSize))
            },
            valueRange = 10f..25f,
            steps = 15
        )
        Text("${settings.arrowSize.toInt()}px")

        Spacer(modifier = Modifier.height(8.dp))

        // Настройка толщины
        Text(tr("arrow_thickness"))
        Slider(
            value = settings.arrowStrokeWidth,
            onValueChange = { newWidth ->
                onSettingsChange(settings.copy(arrowStrokeWidth = newWidth))
            },
            valueRange = 1f..5f,
            steps = 4
        )
        Text("${settings.arrowStrokeWidth.toInt()}px")
    }
}

@Composable
fun VertexSettingsPanel(
    settings: GraphSettings,
    onSettingsChange: (GraphSettings) -> Unit
) {
    Column {
        Text(tr("vertex_color"), style = MaterialTheme.typography.titleMedium)
        ColorPicker(
            currentColor = settings.vertexColor,
            onColorChange = { newColor ->
                onSettingsChange(settings.copy(vertexColor = newColor))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(tr("vertex_radius"))
        Slider(
            value = settings.vertexRadius,
            onValueChange = { newRadius ->
                onSettingsChange(settings.copy(vertexRadius = newRadius))
            },
            valueRange = 10f..50f,
            steps = 8
        )
        Text("${settings.vertexRadius.toInt()}px")
    }
}

@Composable
fun EdgeSettingsPanel(
    settings: GraphSettings,
    onSettingsChange: (GraphSettings) -> Unit
) {
    Column {
        // Переключатель анимации
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = settings.animatedEdges,
                onCheckedChange = { animated ->
                    onSettingsChange(settings.copy(animatedEdges = animated))
                }
            )
            Text(tr("animated_edges"))
        }

        // Цвет рёбер
        Text(tr("edge_color"), style = MaterialTheme.typography.titleMedium)
        ColorPicker(
            currentColor = settings.edgeColor,
            onColorChange = { newColor ->
                onSettingsChange(settings.copy(edgeColor = newColor))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ширина рёбер
        Text(tr("edge_width"))
        Slider(
            value = settings.edgeWidth,
            onValueChange = { newWidth ->
                onSettingsChange(settings.copy(edgeWidth = newWidth))
            },
            valueRange = 1f..10f,
            steps = 9
        )
        Text("${settings.edgeWidth.toInt()}px")

        // Скорость анимации (только если анимация включена)
        if (settings.animatedEdges) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(tr("animation_speed"), style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Speed,
                    contentDescription = tr("slow"),
                    modifier = Modifier.size(24.dp)
                )
                Slider(
                    value = settings.animationSpeed,
                    onValueChange = { newSpeed ->
                        onSettingsChange(settings.copy(animationSpeed = newSpeed))
                    },
                    valueRange = 0.1f..5f,
                    steps = 49,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.FastForward,
                    contentDescription = tr("fast"),
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = tr("current_speed").format(settings.animationSpeed),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ColorPicker(
    currentColor: Color,
    onColorChange: (Color) -> Unit
) {
    var red by remember { mutableStateOf((currentColor.red * 255).toInt()) }
    var green by remember { mutableStateOf((currentColor.green * 255).toInt()) }
    var blue by remember { mutableStateOf((currentColor.blue * 255).toInt()) }

    // Обновляем цвет при изменении ползунков
    LaunchedEffect(red, green, blue) {
        onColorChange(Color(red, green, blue))
    }

    // Предпросмотр цвета
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(red, green, blue))
    )

    Column {
        SliderRow(tr("red"), red, 0..255) { red = it }
        SliderRow(tr("green"), green, 0..255) { green = it }
        SliderRow(tr("blue"), blue, 0..255) { blue = it }
    }
}

@Composable
fun SliderRow(label: String, value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", modifier = Modifier.width(80.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            modifier = Modifier.weight(1f)
        )
        Text("$value", modifier = Modifier.width(40.dp))
    }
}