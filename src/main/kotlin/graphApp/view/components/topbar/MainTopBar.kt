package graphApp.view.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import graphApp.view.components.Localization.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    darkTheme: Boolean,
    onThemeToggle: () -> Unit,
    currentLanguage: String,
    onLanguageToggle: () -> Unit,
    onOpenFile: () -> Unit,
    onSaveFile: () -> Unit,
    onDatabase: () -> Unit,
    canUndo: Boolean,
    onUndo: () -> Unit,
    canRedo: Boolean,
    onRedo: () -> Unit,
    onNewWindow: () -> Unit,
    onCloseWindow: () -> Unit
) {
    TopAppBar(
        title = { Text(tr("app_title")) },
        navigationIcon = {
            Row {
                var windowMenuExpanded by remember { mutableStateOf(false) }
                IconButton(onClick = { windowMenuExpanded = true }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Window menu")
                }
                DropdownMenu(
                    expanded = windowMenuExpanded,
                    onDismissRequest = { windowMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tr("menu_new_window")) },
                        onClick = {
                            windowMenuExpanded = false
                            onNewWindow()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(tr("exit")) },
                        onClick = {
                            windowMenuExpanded = false
                            onCloseWindow()
                        }
                    )
                }
            }
        },
        actions = {
            var editMenuExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = onLanguageToggle) {
                Text(if (currentLanguage == "en") "RU" else "EN")
            }
            IconButton(onClick = onOpenFile) {
                Icon(Icons.Filled.Folder, contentDescription = "Open file")
            }
            IconButton(onClick = onSaveFile) {
                Icon(Icons.Filled.Save, "Save")
            }
            IconButton(onClick = onDatabase) {
                Icon(Icons.Filled.Storage, contentDescription = tr("button_database"))
            }
            Box {
                IconButton(onClick = { editMenuExpanded = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                DropdownMenu(
                    expanded = editMenuExpanded,
                    onDismissRequest = { editMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tr("button_undo")) },
                        onClick = {
                            onUndo()
                            editMenuExpanded = false
                        },
                        enabled = canUndo
                    )
                    DropdownMenuItem(
                        text = { Text(tr("button_redo")) },
                        onClick = {
                            onRedo()
                            editMenuExpanded = false
                        },
                        enabled = canRedo
                    )
                }
            }
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }
    )
}
