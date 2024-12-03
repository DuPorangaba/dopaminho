package com.example.dopaminho

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

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
}

class AppUsageManager {

    companion object {

//        val AppUsageStatsList: MutableLiveData<MutableList<AppUsageStat>> = MutableLiveData(mutableListOf())
//
//        fun getUsageStats(context: Context) {
//            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//
//            val endTime = System.currentTimeMillis()
//            val startTime = endTime - 1000*60
//
//            val usageStatsList: List<UsageStats> = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
//
//            val uniqueStats = mutableListOf<AppUsageStat>()
//
//            // Usando um Set para garantir que os pacotes sejam únicos
//            val seenPackages = mutableSetOf<String>()
//
//            for (usageStats in usageStatsList) {
//                val packageName = usageStats.packageName
//                val appLabel = getApplicationLabel(packageName, context)
//                val totalUsageTime = usageStats.totalTimeInForeground / 1000
//
//                // Se o pacote já foi visto, ignoramos
//                if (totalUsageTime > 0L && !seenPackages.contains(packageName)) {
//                    seenPackages.add(packageName)
//                    uniqueStats.add(AppUsageStat(packageName, appLabel, totalUsageTime))
//
//                }
//            }
//
//            AppUsageStatsList.postValue(uniqueStats)
//        }

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
        @SuppressLint("UnrememberedMutableState")
        @Composable
        fun createListAppsOnGoal(context: Context) {
            val savedGoals by remember { mutableStateOf(listOf<Goal>()) }

            LaunchedEffect(Unit) {
                savedGoals = GoalRepository(context).loadGoals()
            }

            var savedApp by mutableStateOf(listOf<AppUsageStat>())

            LaunchedEffect(Unit) {
                savedApp = appUsageRepository(context).loadAppUsage()
            }

            for (goal in savedGoals) {
                val newApp = getAppUsageStats(context, goal.labelApp)
                if (newApp.packageName != "NONE") {
                    savedApp = savedApp + newApp
                }
            }
            appUsageRepository(context).saveAppUsage(savedApp)
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
        AppUsageManager.createListAppsOnGoal(applicationContext, )
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
