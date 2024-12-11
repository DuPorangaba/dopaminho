package com.example.dopaminho
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notification(private val context: Context) {

    companion object {
        const val CANAL_ID = "meu_canal_id"
        const val CANAL_NOME = "Canal de Notificação"
        const val CANAL_DESCRICAO = "Descrição do canal de notificações"
    }

    fun criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CANAL_ID, CANAL_NOME, importancia).apply {
                description = CANAL_DESCRICAO
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * Exibe uma notificação configurável, se ainda não tiver sido enviada.
     *
     * @param titulo Título da notificação.
     * @param conteudo Conteúdo da notificação.
     * @param notificacaoId Identificador único da notificação.
     * @param icone Ícone da notificação (opcional).
     * @param prioridade Prioridade da notificação (opcional).
     */
    @SuppressLint("MissingPermission")
    fun mostrarNotificacao(
        titulo: String,
        conteudo: String,
        notificacaoId: Int,
        icone: Int = android.R.drawable.ic_dialog_info,
        prioridade: Int = NotificationCompat.PRIORITY_HIGH,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent para a notificação
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(icone)
            .setContentTitle(titulo)
            .setContentText(conteudo)
            .setPriority(prioridade)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificacaoId, builder.build())
        }

    }

}