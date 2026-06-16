package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import graphApp.view.components.Localization.tr
import graphApp.viewmodel.GraphViewModel

@Composable
fun DatabaseDialog(
    viewModel: GraphViewModel,
    onDismiss: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    var saveName by remember { mutableStateOf("") }
    var savedGraphs by remember { mutableStateOf(viewModel.listDatabaseGraphs()) }
    var saveError by remember { mutableStateOf("") }

    fun refresh() { savedGraphs = viewModel.listDatabaseGraphs() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("db_title")) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tab) {
                    Tab(selected = tab == 0, onClick = { tab = 0 }) {
                        Text(tr("db_save"), modifier = Modifier.padding(vertical = 12.dp))
                    }
                    Tab(selected = tab == 1, onClick = { tab = 1; refresh() }) {
                        Text(tr("db_load"), modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                when (tab) {
                    0 -> {
                        OutlinedTextField(
                            value = saveName,
                            onValueChange = { saveName = it; saveError = "" },
                            label = { Text(tr("db_name")) },
                            isError = saveError.isNotEmpty(),
                            supportingText = if (saveError.isNotEmpty()) {
                                { Text(saveError) }
                            } else null,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val name = saveName.trim()
                                if (name.isEmpty()) {
                                    saveError = tr("db_name_empty")
                                } else {
                                    viewModel.saveToDatabase(name)
                                    saveName = ""
                                    saveError = ""
                                    refresh()
                                    tab = 1
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tr("db_save"))
                        }
                    }
                    1 -> {
                        if (savedGraphs.isEmpty()) {
                            Text(
                                text = tr("db_empty"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                items(savedGraphs) { name ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = name,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        TextButton(onClick = {
                                            viewModel.loadFromDatabase(name)
                                            onDismiss()
                                        }) {
                                            Text(tr("db_load"))
                                        }
                                        IconButton(onClick = {
                                            viewModel.deleteFromDatabase(name)
                                            refresh()
                                        }) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = tr("db_delete"),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(tr("cancel")) }
        }
    )
}
