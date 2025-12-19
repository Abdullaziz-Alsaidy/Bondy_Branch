package com.bondy.bondybranch.overlay

import android.content.Context
import androidx.compose.runtime.*
import com.github.only52607.compose.window.ComposeFloatingWindow

@Composable
fun rememberFloatingWindow(
    appContext: Context,
    content: @Composable () -> Unit
): ComposeFloatingWindow {
    // Create ONCE
    val window = remember(appContext) {
        ComposeFloatingWindow(appContext).apply {
            setContent { content() }
        }
    }

    // Ensure cleanup when leaving screen
    DisposableEffect(window) {
        onDispose { runCatching { window.hide() } }
    }

    return window
}
