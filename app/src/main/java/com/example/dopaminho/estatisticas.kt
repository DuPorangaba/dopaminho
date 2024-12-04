package com.example.dopaminho

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.dopaminho.ui.theme.DopaminhoTheme
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun EstatisticasScreen() {
    val context = LocalContext.current
    val usageStatsRepository = remember { appUsageRepository(context) }
    var savedStats by remember { mutableStateOf<List<AppUsageStat>>(emptyList()) }

    // LaunchedEffect é usado para disparar a carga das estatísticas
    LaunchedEffect(Unit) {
        AppUsageManager.createListAppsOnGoal(context)
        savedStats = usageStatsRepository.loadAppUsage()
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

                if(savedStats.isEmpty()) {
                    Text(
                        text = "No usage stats available."
                    )
                } else {
                    savedStats.forEach { stat ->
                        Text(
                            text = "${stat.labelName}: ${stat.totalUsageTime} secs",
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                    }
                }
                // Exibe as estatísticas de uso, iterando sobre a lista de objetos AppUsageStat

            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun EstatisticasScreenPreview(){
    EstatisticasScreen()
}