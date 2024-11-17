package com.example.dopaminho

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData

data class AppUsageStat(
    val packageName: String,
    val totalUsageTime: Long // Tempo de uso em segundos
)

fun getUsageStats(context: Context): MutableList<AppUsageStat> {
    val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val calendar = Calendar.getInstance()

    val endTime = System.currentTimeMillis()
    val startTime = endTime - 1000*60

    val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

    val uniqueStats = mutableListOf<AppUsageStat>()

    // Usando um Set para garantir que os pacotes sejam únicos
    val seenPackages = mutableSetOf<String>()

    for (usageStats in usageStatsList) {
        val packageName = usageStats.packageName
        val totalUsageTime = usageStats.totalTimeInForeground / 1000

        // Se o pacote já foi visto, ignoramos
        if (totalUsageTime > 0L && !seenPackages.contains(packageName)) {
            seenPackages.add(packageName)
            uniqueStats.add(AppUsageStat(packageName, totalUsageTime))
        }
    }

    return uniqueStats
}
