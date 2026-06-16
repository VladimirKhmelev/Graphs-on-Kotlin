package graphApp.view.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
    onHelp: () -> Unit,
    onNewWindow: () -> Unit,
    onCloseWindow: () -> Unit
) {
    TopAppBar(
        title = { Text(tr("app_title")) },
        navigationIcon = {
            Row {
                var windowMenuExpanded by remember { mutableStateOf(false) }
                IconButton(onClick = { windowMenuExpanded = true }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = windowMenuExpanded,
                    onDismissRequest = { windowMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tr("menu_new_window")) },
                        onClick = { windowMenuExpanded = false; onNewWindow() }
                    )
                    DropdownMenuItem(
                        text = { Text(tr("exit")) },
                        onClick = { windowMenuExpanded = false; onCloseWindow() }
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onLanguageToggle) {
                Text(if (currentLanguage == "en") "RU" else "EN")
            }
            Box {
                var fileMenuExpanded by remember { mutableStateOf(false) }
                IconButton(onClick = { fileMenuExpanded = true }) {
                    Icon(Icons.Filled.Folder, contentDescription = tr("button_open"))
                }
                DropdownMenu(
                    expanded = fileMenuExpanded,
                    onDismissRequest = { fileMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tr("file_open_json")) },
                        leadingIcon = { Icon(Icons.Filled.FileOpen, null) },
                        onClick = { fileMenuExpanded = false; onOpenFile() }
                    )
                    DropdownMenuItem(
                        text = { Text(tr("file_save_json")) },
                        leadingIcon = { Icon(Icons.Filled.Save, null) },
                        onClick = { fileMenuExpanded = false; onSaveFile() }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(tr("file_database")) },
                        leadingIcon = { Icon(Icons.Filled.Storage, null) },
                        onClick = { fileMenuExpanded = false; onDatabase() }
                    )
                }
            }
            IconButton(onClick = onHelp) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tr("button_help"))
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
