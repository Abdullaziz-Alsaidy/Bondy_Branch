package com.bondy.bondybranch.overlay


import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SystemAlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.only52607.compose.window.LocalFloatingWindow
import com.github.only52607.compose.window.dragFloatingWindow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bondy.bondybranch.data.model.BranchDailyStats
import com.bondy.bondybranch.presentation.screens.StatsCard
import com.bondy.bondybranch.presentation.screens.StatsCard1

@Composable
fun FloatingWindowContent(
    model: FloatingWindowViewModel = viewModel(),
    stats: BranchDailyStats?, isLoading: Boolean,


) {
    val floatingWindow = LocalFloatingWindow.current
    if (model.dialogVisible) {
        SystemAlertDialog(
            onDismissRequest = { model.dismissDialog() },
            confirmButton = {
                TextButton(onClick = { model.dismissDialog() }) {
                    Text(text = "OK")
                }
            },
            text = {
                Text(text = "This is a system dialog")
            }
        )
    }

    StatsCard1(
        stats = stats,
        isLoading = isLoading,
        modifier = Modifier.dragFloatingWindow(),
        call = {
            model.onSendClicked()
            Log.d("FloatingWindow", "Clicked (Content)")
        }
    )

}