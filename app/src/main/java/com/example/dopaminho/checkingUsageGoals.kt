package com.example.dopaminho

import android.content.Context

suspend fun checkStatsGoals(context: Context) {
    val goalRepository = GoalRepository(context)
    val appUsageRepository = appUsageRepository(context)

    val savedGoals = goalRepository.loadGoals()
    var savedApps = appUsageRepository.loadAppUsage()

    for (goal in savedGoals) {
        val app = savedApps.find {it.labelName == goal.labelApp}

        val appTimeGoal = goal.time

        if (app != null) {
            val appTimeUsage = app.totalUsageTime - goal.usageTimeOnCreation
            if (appTimeUsage > appTimeGoal) {
                BarraDeVida.perdeVida(5)
            }
        }

    }
}