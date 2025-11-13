package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import graphApp.model.graph.DirectedEdge
import graphApp.model.graph.Edge
import graphApp.view.components.Localization.tr


@Composable
fun EdgeEditDialog(
    edge: Edge,
    onDismiss: () -> Unit,
    onConfirm: (Double, Boolean) -> Unit
) {
    var weight by remember { mutableStateOf(edge.weight.toString()) }
    var isDirected by remember { mutableStateOf(edge is DirectedEdge) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("dialog_edit_edge")) },
        text = {
            Column {
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
                    onConfirm(weightValue, isDirected)
                }
            ) {
                Text(tr("dialog_save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(tr("dialog_cancel"))
            }
        }
    )
}