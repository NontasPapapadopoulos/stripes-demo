package com.example.stripesdemo.presentation.ui.composables


import android.view.KeyEvent.ACTION_UP
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable


fun Modifier.mapKeys(mappings: Map<Key, () -> Unit>, keyEventAction: Int = ACTION_UP): Modifier =
    inspectable(
        inspectorInfo = debugInspectorInfo {
            name = "onKeyEvent"
            mappings
            keyEventAction
        }
    ) {
        return@inspectable Modifier
            .onKeyEvent {
                val matchesAction = it.nativeKeyEvent.action == keyEventAction
                if (matchesAction) {
                    mappings[it.key]?.invoke()
                }
                return@onKeyEvent matchesAction && mappings.containsKey(it.key)
            }
    }
