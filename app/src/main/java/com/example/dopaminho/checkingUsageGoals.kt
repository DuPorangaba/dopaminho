package com.example.dopaminho

import android.app.Notification
import android.content.Context

suspend fun checkStatsGoals(context: Context) {
    val goalRepository = GoalRepository(context)
    val appUsageRepository = appUsageRepository(context)

    val savedGoals = goalRepository.loadGoals()
    var savedApps = appUsageRepository.loadAppUsage()
    val  notification = Notification(context)
    notification.criarCanalNotificacao()


    for (goal in savedGoals) {
        val app = savedApps.find {it.labelName == goal.labelApp}

        val appTimeGoal = goal.time

        if (app != null) {
            val appTimeUsage = app.totalUsageTime - goal.usageTimeOnCreation
            //se execede = 70
            //se dobra = 50
            //se quadruplica = 30
            if (appTimeUsage > appTimeGoal) {
                //BarraDeVida.perdeVida(5)

            }
            notification.exibirNotificacaoCondicional(BarraDeVida)

        }

    }
}


