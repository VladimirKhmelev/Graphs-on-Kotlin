package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import graphApp.view.components.Localization.tr

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("help")) },
        text = {
            Column {
                Text(tr("help_v_mode"))
                Text(tr("help_esc"))
                Text(tr("help_add_vertex"))
                Text(tr("help_move_vertex"))
                Text(tr("help_zoom"))
                Text(tr("help_undo_redo"))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
