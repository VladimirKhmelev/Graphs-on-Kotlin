package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import graphApp.model.graph.Vertex
import graphApp.view.components.Localization.tr

@Composable
fun AddEdgeDialog(
    vertices: List<Vertex>,
    onDismiss: () -> Unit,
    onConfirm: (from: Vertex, to: Vertex, weight: Double, isDirected: Boolean) -> Unit
) {
    var fromVertex by remember { mutableStateOf<Vertex?>(null) }
    var toVertex by remember { mutableStateOf<Vertex?>(null) }
    var weight by remember { mutableStateOf("1.0") }
    var isDirected by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("add_e")) },
        text = {
            Column {
                var fromExpanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { fromExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(fromVertex?.id ?: tr("start_vert"))
                    }
                    DropdownMenu(
                        expanded = fromExpanded,
                        onDismissRequest = { fromExpanded = false }
                    ) {
                        vertices.forEach { vertex ->
                            DropdownMenuItem(
                                text = { Text(vertex.id) },
                                onClick = {
                                    fromVertex = vertex
                                    fromExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                var toExpanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { toExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(toVertex?.id ?: tr("end_vert"))
                    }
                    DropdownMenu(
                        expanded = toExpanded,
                        onDismissRequest = { toExpanded = false }
                    ) {
                        vertices.forEach { vertex ->
                            DropdownMenuItem(
                                text = { Text(vertex.id) },
                                onClick = {
                                    toVertex = vertex
                                    toExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text(tr("dialog_weight")) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDirected,
                        onCheckedChange = { isDirected = it }
                    )
                    Text(tr("dialog_directed"))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weightValue = weight.toDoubleOrNull() ?: 1.0
                    onConfirm(fromVertex!!, toVertex!!, weightValue, isDirected)
                },
                enabled = fromVertex != null && toVertex != null && weight.isNotEmpty()
            ) {
                Text(tr("add"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(tr("cancel"))
            }
        }
    )
}
