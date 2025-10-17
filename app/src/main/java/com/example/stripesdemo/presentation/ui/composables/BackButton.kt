package com.example.stripesdemo.presentation.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.stripesdemo.presentation.extension.clickableOnce

@Composable
fun BackButton(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = {}) {
        Icon(
            Icons.Filled.ArrowBack,
            null,
            modifier = modifier.clickableOnce { navigateBack() }
        )
    }
}