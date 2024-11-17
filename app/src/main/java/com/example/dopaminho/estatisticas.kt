package com.example.dopaminho

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.dopaminho.ui.theme.DopaminhoTheme



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun EstatisticasScreen() {
    val context = LocalContext.current
    // Usamos uma lista mutável para armazenar as estatísticas de uso
    val usageStatsList = remember { mutableStateListOf<AppUsageStat>() }

    // LaunchedEffect é usado para disparar a carga das estatísticas
    LaunchedEffect(context) {
        // Atualiza a lista de estatísticas de uso
        usageStatsList.clear()  // Limpa a lista antes de adicionar novas estatísticas
        usageStatsList.addAll(getUsageStats(context))
    }

    DopaminhoTheme {
        if (!hasUsageStatsPermission(context)) {
            PermissionScreen()
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Usage Stats:")
                // Exibe as estatísticas de uso, iterando sobre a lista de objetos AppUsageStat
                usageStatsList.forEach { stat ->
                    Text(
                        text = "${stat.packageName}: ${stat.totalUsageTime} secs",
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun EstatisticasScreenPreview(){
    EstatisticasScreen()
}