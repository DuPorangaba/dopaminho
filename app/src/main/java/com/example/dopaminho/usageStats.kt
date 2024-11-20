package com.example.dopaminho

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.HandlerThread
import android.os.Process
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData

data class AppUsageStat(
    val packageName: String,
    val labelName: String,
    val totalUsageTime: Long // Tempo de uso em segundos
)

class AppUsageManager {
    companion object {
        val AppUsageStatsList: MutableLiveData<MutableList<AppUsageStat>> = MutableLiveData(mutableListOf())

        fun getUsageStats(context: Context) {
            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000*60

            val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            val uniqueStats = mutableListOf<AppUsageStat>()

            // Usando um Set para garantir que os pacotes sejam únicos
            val seenPackages = mutableSetOf<String>()

            for (usageStats in usageStatsList) {
                val packageName = usageStats.packageName
                val appLabel = getApplicationLabel(packageName, context)
                val totalUsageTime = usageStats.totalTimeInForeground / 1000

                // Se o pacote já foi visto, ignoramos
                if (totalUsageTime > 0L && !seenPackages.contains(packageName)) {
                    seenPackages.add(packageName)
                    uniqueStats.add(AppUsageStat(packageName, appLabel, totalUsageTime))
                }
            }

            AppUsageStatsList.postValue(uniqueStats)
        }

        private fun getApplicationLabel(packageName: String, context: Context): String {
            val packageManager = context.packageManager
            try {
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                return packageManager.getApplicationLabel(applicationInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return packageName // Caso o nome não seja encontrado, retorna o nome do pacote
            }
        }


    }
}

class UsageStatService : Service() {
    override fun onCreate() {
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        Log.d("onCreate()", "After service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("UsageStatsService", "Serviço iniciado para obter uso de apps")
        AppUsageManager.getUsageStats(applicationContext)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null
}

// função padrão
//fun getUsageStats(context: Context): MutableList<AppUsageStat> {
//    val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//
//    val endTime = System.currentTimeMillis()
//    val startTime = endTime - 1000*60
//
//    val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
//
//    val uniqueStats = mutableListOf<AppUsageStat>()
//
//    // Usando um Set para garantir que os pacotes sejam únicos
//    val seenPackages = mutableSetOf<String>()
//
//    for (usageStats in usageStatsList) {
//        val packageName = usageStats.packageName
//        val totalUsageTime = usageStats.totalTimeInForeground / 1000
//
//        // Se o pacote já foi visto, ignoramos
//        if (totalUsageTime > 0L && !seenPackages.contains(packageName)) {
//            seenPackages.add(packageName)
//            uniqueStats.add(AppUsageStat(packageName, totalUsageTime))
//        }
//    }
//
//    return uniqueStats
//}
