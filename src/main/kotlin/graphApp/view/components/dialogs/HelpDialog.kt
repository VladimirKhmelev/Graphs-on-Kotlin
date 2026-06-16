package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import graphApp.view.components.Localization.tr

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("help_title")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                HelpSection(tr("help_section_editing")) {
                    HelpItem(tr("help_rmb"))
                    HelpItem(tr("help_add_vertex"))
                    HelpItem(tr("help_move_vertex"))
                    HelpItem(tr("help_double_click"))
                }
                HelpSection(tr("help_section_connections")) {
                    HelpItem(tr("help_v_mode"))
                    HelpItem(tr("help_esc"))
                }
                HelpSection(tr("help_section_navigation")) {
                    HelpItem(tr("help_zoom"))
                    HelpItem(tr("help_undo_redo"))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}

@Composable
private fun HelpSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
    Column(modifier = Modifier.padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        content()
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun HelpItem(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium)
}
