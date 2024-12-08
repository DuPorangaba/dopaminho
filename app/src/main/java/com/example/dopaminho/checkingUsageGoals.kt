package com.example.dopaminho

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat

suspend fun checkStatsGoals(context: Context) {
    val goalRepository = GoalRepository(context)
    val appUsageRepository = appUsageRepository(context)

    val savedGoals = goalRepository.loadGoals()
    var savedApps = appUsageRepository.loadAppUsage()
    val  notification = Notification(context)
    notification.criarCanalNotificacao()


    for (goal in savedGoals) {
        var app = savedApps.find { it.labelName == goal.labelApp }

        val appTimeGoal = goal.time

        if (app != null) {
            val appTimeUsage = app.totalUsageTime - goal.usageTimeOnCreation

                if(appTimeUsage > appTimeGoal * 3 && BarraDeVida.vidaAtual > 30)  {
                    BarraDeVida.vidaAtual = 30.0
                    notification.mostrarNotificacao(
                        titulo = "META TRIPLICADA",
                        conteudo = "Seu dopaminho está muito triste e doente",
                        notificacaoId = 3,
                        icone = R.drawable.ic_dopa_doente,
                        prioridade = NotificationCompat.PRIORITY_HIGH
                    )
                    Log.d("CheckStats", "appTimeUsage > appTimeGoal * 4 && BarraDeVida.vidaAtual > 30")
                }
                    if (appTimeUsage > appTimeGoal * 2 && BarraDeVida.vidaAtual > 50)  {
                    BarraDeVida.vidaAtual = 50.0
                    notification.mostrarNotificacao(
                        titulo = "Você DOBROU sua meta de uso!",
                        conteudo = "Seu dopaminho está com 50% de vida",
                        notificacaoId = 2,
                        icone = R.drawable.ic_dopa_triste,
                        prioridade = NotificationCompat.PRIORITY_HIGH
                    )
                        Log.d("CheckStats", "appTimeUsage > appTimeGoal * 2 && BarraDeVida.vidaAtual > 50")
                }

                    if (appTimeUsage > appTimeGoal && BarraDeVida.vidaAtual > 70)  {
                    BarraDeVida.vidaAtual = 70.0
                    notification.mostrarNotificacao(
                        titulo = "Você excedeu sua meta!",
                        conteudo = "Seu dopaminho está com 70% de vida",
                        notificacaoId = 1,
                        icone = R.drawable.ic_dopa_neutro,
                        prioridade = NotificationCompat.PRIORITY_HIGH
                    )
                        Log.d("CheckStats", "appTimeUsage > appTimeGoal && BarraDeVida.vidaAtual > 70")
                }



            }
            //se execede = 70
            //se dobra = 50
            //se quadruplica = 30


    }
}


