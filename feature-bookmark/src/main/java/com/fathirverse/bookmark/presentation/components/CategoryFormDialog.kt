
package com.fathirverse.bookmark.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fathirverse.uikit.components.molecules.TextInputField
import com.fathirverse.uikit.components.organisms.AppDialog

@Composable
fun CategoryFormDialog(
    title: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember(initialName) {
        mutableStateOf(initialName)
    }

    AppDialog(
        title = title,
        onDismiss = onDismiss,
        confirmText = "Simpan",
        onConfirm = {
            onSave(name.trim())
        },
        confirmEnabled = name.isNotBlank(),
    ) {
        TextInputField(
            value = name,
            onValueChange = {
                name = it
            },
            label = "Nama kategori",
            singleLine = true,
        )
    }
}
