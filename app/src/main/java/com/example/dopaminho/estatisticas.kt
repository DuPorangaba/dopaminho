package com.example.dopaminho

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import androidx.work.WorkQuery

import com.example.dopaminho.ui.theme.DopaminhoTheme



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EstatisticasScreen() {
    val context = LocalContext.current

    // A state to hold the usage statistics
    var usageStats by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        usageStats = getUsageStats(context)
    }

    DopaminhoTheme {
        if (!hasUsageStatsPermission(context)) {
            PermissionScreen()
        } else {
            Column() {
                Text(text = "Usage Stats:")
                Text(text = usageStats)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EstatisticasScreenPreview(){
    EstatisticasScreen()
}