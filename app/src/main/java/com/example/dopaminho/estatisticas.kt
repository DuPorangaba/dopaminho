package com.example.dopaminho

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext

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
        // Display the usage stats (this can be in a Text, for example)
        Column() {
            Text(text = "Usage Stats:")
            Text(text = usageStats)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EstatisticasScreenPreview(){
    EstatisticasScreen()
}