package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import graphApp.view.components.Localization.tr
import java.awt.FileDialog
import java.io.File

@Composable
fun FrameWindowScope.FileSaveWithFormatDialog(onResult: (File?, String) -> Unit) {
    var selectedFormat by remember { mutableStateOf("json") }

    AlertDialog(
        onDismissRequest = { onResult(null, "") },
        title = { Text(tr("change_format")) },
        text = {
            Column {
                Text(tr("save_format"))
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" }
                    )
                    Text("JSON", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedFormat == "csv",
                        onClick = { selectedFormat = "csv" }
                    )
                    Text("CSV")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    FileDialog(window, tr("save_graph"), FileDialog.SAVE).apply {
                        directory = System.getProperty("user.home")
                        file = "graph.${selectedFormat}"
                        isVisible = true

                        val file = if (file != null) {
                            var fileName = file!!
                            if (!fileName.endsWith(".${selectedFormat}")) {
                                fileName += ".${selectedFormat}"
                            }
                            File(directory, fileName)
                        } else null

                        onResult(file, selectedFormat)
                    }
                }
            ) {
                Text(tr("button_save"))
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(null, "") }) {
                Text(tr("cancel"))
            }
        }
    )
}

@Composable
fun FrameWindowScope.FileOpenDialog(onResult: (File?) -> Unit) {
    AwtWindow(
        create = {
            FileDialog(window, tr("open_graph"), FileDialog.LOAD).apply {
                directory = System.getProperty("user.home")
                isMultipleMode = false
            }
        },
        dispose = FileDialog::dispose
    ) { dialog ->
        dialog.isVisible = true
        val file = if (dialog.file != null) File(dialog.directory, dialog.file) else null
        onResult(file)
    }
}

@Composable
fun OpenFormatDialog(
    onResult: (String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("json") }

    AlertDialog(
        onDismissRequest = { onResult("") },
        title = { Text(tr("change_format")) },
        text = {
            Column {
                Text(tr("open_graph_format"))
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" }
                    )
                    Text("JSON", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedFormat == "csv",
                        onClick = { selectedFormat = "csv" }
                    )
                    Text("CSV")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onResult(selectedFormat) }
            ) {
                Text(tr("open"))
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult("") }) {
                Text(tr("cancel"))
            }
        }
    )
}