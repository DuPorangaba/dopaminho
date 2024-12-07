package com.example.dopaminho

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.HandlerThread
import android.os.Process
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.logging.Handler

val Context.dataStoreAppUsage: DataStore<Preferences> by preferencesDataStore(name = "appUsage")

data class AppUsageStat(
    val packageName: String,
    val labelName: String,
    val totalUsageTime: Long // Tempo de uso em segundos
) {
    companion object {
        fun listToJson(appsUsage: List<AppUsageStat>): String {
            val gson = Gson()
            return gson.toJson(appsUsage)
        }

        fun jsonToList(json: String): List<AppUsageStat> {
            val gson = Gson()
            val type = object : TypeToken<List<AppUsageStat>>() {}.type
            return gson.fromJson(json, type)
        }
    }
}


class appUsageRepository(context: Context) {
    private val dataStore = context.dataStoreAppUsage

    companion object {
        private val APP_USAGE_KEY = stringPreferencesKey("appUsage")
    }

    suspend fun saveAppUsage(appUsage: List<AppUsageStat>) {
        val appsJson = AppUsageStat.listToJson(appUsage)
        dataStore.edit { preferences ->
            preferences[APP_USAGE_KEY] = appsJson
        }
    }

    suspend fun loadAppUsage(): List<AppUsageStat> {
        val preferences = dataStore.data.first()
        val appsJson = preferences[APP_USAGE_KEY] ?: "[]"
        return AppUsageStat.jsonToList(appsJson)
    }

    fun loadAppUsageAsFlow(): Flow<List<AppUsageStat>> {
        return dataStore.data
            .map { preferences ->
                val appsJson = preferences[APP_USAGE_KEY] ?: "[]"
                AppUsageStat.jsonToList(appsJson)
            }
    }
}

class AppUsageManager {

    companion object {

        fun getAppUsageTime(context: Context, appName: String): Long {
            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000*60
            val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            //Procura o nome do aplicativo que o usuário passou e retorna o tempo de uso
            for (usageStats in usageStatsList) {
                val packageName = usageStats.packageName
                val appLabel = getApplicationLabel(packageName, context)
                val totalUsageTime = usageStats.totalTimeInForeground / 1000

                // Se o nome aplicativo corresponde com appName
                if (appLabel == appName) {
                    return totalUsageTime
                }
            }

            return 0
        }

        //Função que recebe o nome de um app e retorna um objeto AppUsageStat com o tempo
        fun getAppUsageStats(context: Context, appName: String): AppUsageStat {
            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000*60
            val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            //Procura o nome do aplicativo que o usuário passou e retorna o tempo de uso
            for (usageStats in usageStatsList) {
                val packageName = usageStats.packageName
                val appLabel = getApplicationLabel(packageName, context)
                val totalUsageTime = usageStats.totalTimeInForeground / 1000

                val app = AppUsageStat(packageName, appLabel, totalUsageTime)

                // Se o nome aplicativo corresponde com appName
                if (appLabel == appName) {
                    return app
                }
            }

            val appNone = AppUsageStat("NONE", "NONE", 0)
            return appNone
        }

        //função que irá atualizar o DataStore com os dados de getAppUsageStats de acordo com as metas definidas
        suspend fun createListAppsOnGoal(context: Context) {
            val goalRepository = GoalRepository(context)
            val appUsageRepository = appUsageRepository(context)

            val savedGoals = goalRepository.loadGoals()
            var savedApps = appUsageRepository.loadAppUsage().toMutableList()

            val goalPackageNames = savedGoals.map {it.labelApp}
            savedApps = savedApps.filter { it.packageName in goalPackageNames }.toMutableList()

            for (goal in savedGoals) {
                val newApp = getAppUsageStats(context,goal.labelApp)
                if(newApp.packageName != "NONE" ) {
                        savedApps.add(newApp)
                }
            }

            appUsageRepository.saveAppUsage(savedApps)

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
    private val handler = android.os.Handler()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val interval = 8000L

    private val runnable = object : Runnable {
        override fun run() {
            coroutineScope.launch {
                AppUsageManager.createListAppsOnGoal(this@UsageStatService)
                checkStatsGoals(this@UsageStatService)
                Log.d("ThreadCoroutine",  "Running on thread: ${Thread.currentThread().name}")
            }

            handler.postDelayed(this, interval)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       // startForegroundService()
        handler.post(runnable)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

//    private fun startForegroundService() {
//        val channelId = "UsageStatServiceChannel"
//        val channelName = "App Usage Tracking Service"
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_LOW
//            )
//
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(channel)
//        }
//
//        val notification: Notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Rastreamento de uso de apps")
//            .setContentText("Monitoramento o uso dos aplicativos em segundo plano")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .build()
//
//        startForeground(4, notification)
//    }
}

//override fun onCreate() {
//    val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
//    thread.start()
//    Log.d("onCreate()", "After service created")
//}
//
//override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//    Log.d("UsageStatService", "Serviço iniciado para obter uso de apps")
//
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            AppUsageManager.createListAppsOnGoal(applicationContext)
//            Log.d("UsageStatService", "Uso de apps atualizado com sucesso")
//
//        } catch (e: Exception) {
//            Log.d("UsageStatService", "Erro ao atualizar uso de apps", e)
//        } finally {
//            stopSelf(startId)
//        }
//    }
//    return START_NOT_STICKY
//}
//
//override fun onBind(intent: Intent): IBinder? = null

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
