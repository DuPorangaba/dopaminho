package com.example.dopaminho
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notification (private val context: Context) {

    companion object {
        const val CANAL_ID = "meu_canal_id"
        const val CANAL_NOME = "Canal de Notificação"
        const val CANAL_DESCRICAO = "Descrição do canal de notificações"
    }

    /**
     * Cria o canal de notificações. Deve ser chamado antes de enviar qualquer notificação.
     * Necessário apenas para Android 8.0 (API 26) ou superior.
     * NotificationManager.IMPORTANCE_HIGH/NotificationManager.IMPORTANCE_DEFAULT/NotificationManager.IMPORTANCE_LOW
     * São 3 variáveis que podemos atribuir para val importancia para mudar comportamento da notificação
     */
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
     * Exibe uma notificação simples.
     *
     * @param titulo Título da notificação.
     * @param conteudo Conteúdo da notificação.
     * @param notificacaoId Identificador único da notificação.
     */
    fun mostrarNotificacao(titulo: String, conteudo: String, notificacaoId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Notificação", "Permissão de notificação não concedida. Operação ignorada.")
            return
        }

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(conteudo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificacaoId, builder.build())
        }
    }
}