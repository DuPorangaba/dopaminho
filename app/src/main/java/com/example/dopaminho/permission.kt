package com.example.dopaminho

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import android.content.Intent
import android.icu.util.Calendar
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import com.example.dopaminho.ui.theme.DopaminhoTheme
import android.view.View

fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
}

fun grantUsageStatsPermission(context: Context) {
    val sendIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    context.startActivity(sendIntent)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen() {
    val context = LocalContext.current

    DopaminhoTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Solicitando Permissão")
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp), // Add padding around the column
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between items
            ) {
                Text(
                    text = "Para o app funcionar, é necessário permitir que ele colete informações sobre o tempo de uso dos apps.\n" +
                            " Assim, é preciso dar essa permissão a este app"
                )

                Button(
                    onClick = { grantUsageStatsPermission(context) },
                    modifier = Modifier.fillMaxWidth(), // Make button full width
                ) {
                    Text("Dar Permissão")
                }
            }
        }
    }
}


fun getUsageStats() {
    val usageManager = getSystemService(Context, USAGE_STATS_SERVICE) as UsageStatsManager
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val startTime = calendar.timeInMillis
    val endTime = System.currentTimeMillis()

    val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

    for (usageStats in usageStatsList) {
        val packageName = usageStats.packageName
        val totalTime = usageStats.totalTimeInForeground

        println("App: $packageName, Time used: $totalTime ms\n")
    }
}