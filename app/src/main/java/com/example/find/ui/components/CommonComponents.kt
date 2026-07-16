package com.example.find.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.find.data.model.BenchType

fun benchTypeLabel(type: BenchType): String = when (type) {
    BenchType.WOOD -> "Madeira"
    BenchType.CEMENT -> "Cimento"
    BenchType.METAL -> "Metal"
    BenchType.STONE -> "Pedra"
    BenchType.OTHER -> "Outro"
}

@Composable
fun ErrorText(message: String?, modifier: Modifier = Modifier) {
    if (!message.isNullOrBlank()) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun SuccessText(message: String?, modifier: Modifier = Modifier) {
    if (!message.isNullOrBlank()) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}
