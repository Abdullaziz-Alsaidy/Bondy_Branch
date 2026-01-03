package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.presentation.screens.StatsCard1
import com.github.only52607.compose.window.dragFloatingWindow

@Composable
fun FloatingWindowContent(
    model: FloatingWindowViewModel,
    stats: BranchDailyStats?,
    isLoading: Boolean,
) {
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarSuccess by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val defaultWidth = 280.dp
    val defaultHeight = 240.dp
    val minWidth = 180.dp
    val minHeight = 160.dp
    val maxWidth = 420.dp
    val maxHeight = 520.dp
    val collapseWidth = 150.dp
    val collapseHeight = 130.dp

    var cardNumber by remember { mutableStateOf("") }
    var isCollapsed by remember { mutableStateOf(false) }
    var cardWidth by remember { mutableStateOf(defaultWidth) }
    var cardHeight by remember { mutableStateOf(defaultHeight) }
    var expandedWidth by remember { mutableStateOf(defaultWidth) }
    var expandedHeight by remember { mutableStateOf(defaultHeight) }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (message != null) {
            snackbarSuccess = uiState.snackbarSuccess
            Log.d("FloatingWindow", "Showing snackbar: $message")
            snackbarHostState.showSnackbar(message)
            model.consumeSnackbar()
        }
    }

    if (isCollapsed) {
        FloatingActionButton(
            modifier = Modifier.dragFloatingWindow(),
            onClick = {
                isCollapsed = false
                cardWidth = expandedWidth
                cardHeight = expandedHeight
            }
        ) {
            Icon(
                imageVector = Icons.Filled.PictureInPictureAlt,
                contentDescription = "Expand"
            )
        }
    } else {
        androidx.compose.foundation.layout.Box {
            StatsCard1(
                stats = stats,
                isLoading = isLoading || uiState.isSubmitting,
                modifier = Modifier,
                cardNumber = cardNumber,
                onCardNumberChange = { cardNumber = it },
                onCollapse = {
                    expandedWidth = cardWidth
                    expandedHeight = cardHeight
                    isCollapsed = true
                },
                onResize = { dx, dy ->
                    val newWidth = (cardWidth + with(density) { dx.toDp() })
                        .coerceIn(minWidth, maxWidth)
                    val newHeight = (cardHeight + with(density) { dy.toDp() })
                        .coerceIn(minHeight, maxHeight)
                    if (newWidth <= collapseWidth || newHeight <= collapseHeight) {
                        expandedWidth = cardWidth
                        expandedHeight = cardHeight
                        isCollapsed = true
                        return@StatsCard1
                    }
                    cardWidth = newWidth
                    cardHeight = newHeight
                    expandedWidth = newWidth
                    expandedHeight = newHeight
                },
                onSend = {
                    if (cardNumber.isNotBlank()) {
                        model.onSendClicked(cardNumber.trim())
                    } else {
                        Log.d("FloatingWindow", "Empty card number, ignoring send")
                    }
                    isCollapsed = true
                    Log.d("FloatingWindow", "Clicked (Content)")
                },
                width = cardWidth,
                height = cardHeight
            )
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    val containerColor = if (snackbarSuccess) {
                        Color(0xFF2E7D32)
                    } else {
                        Color(0xFFC62828)
                    }
                    Snackbar(
                        snackbarData = data,
                        containerColor = containerColor,
                        contentColor = Color.White,
                        actionColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    }

}
