package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.presentation.screens.StatsCard1
import com.github.only52607.compose.window.dragFloatingWindow

@Composable
fun FloatingWindowContent(
    model: FloatingWindowViewModel,
    stats: BranchDailyStats?,
    isLoading: Boolean,
) {
    var cardNumber by remember { mutableStateOf("") }

    StatsCard1(
        stats = stats,
        isLoading = isLoading,
        modifier = Modifier.dragFloatingWindow(),
        cardNumber = cardNumber,
        onCardNumberChange = { cardNumber = it },
        onSend = {
            if (cardNumber.isNotBlank()) {
                model.onSendClicked(cardNumber.trim())
            }
            Log.d("FloatingWindow", "Clicked (Content)")
        }
    )

}
