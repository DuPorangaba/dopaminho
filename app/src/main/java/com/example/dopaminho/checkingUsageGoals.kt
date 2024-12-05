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
            if (appTimeUsage > appTimeGoal) {
                BarraDeVida.perdeVida(5)
            }
            if (BarraDeVida.vidaAtual < 70) {
                notification.mostrarNotificacao(
                    titulo = "Meta Excedida",
                    conteudo = "Seu dopaminho está ficando triste :/",
                    notificacaoId = 1
                )
            }
        }

    }
}


