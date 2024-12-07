package com.example.dopaminho
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

//class Notification (private val context: Context) {
//
//    companion object {
//        const val CANAL_ID = "meu_canal_id"
//        const val CANAL_NOME = "Canal de Notificação"
//        const val CANAL_DESCRICAO = "Descrição do canal de notificações"
//    }
//
//    /**
//     * Cria o canal de notificações. Deve ser chamado antes de enviar qualquer notificação.
//     * Necessário apenas para Android 8.0 (API 26) ou superior.
//     * NotificationManager.IMPORTANCE_HIGH/NotificationManager.IMPORTANCE_DEFAULT/NotificationManager.IMPORTANCE_LOW
//     * São 3 variáveis que podemos atribuir para val importancia para mudar comportamento da notificação
//     */
//    fun criarCanalNotificacao() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importancia = NotificationManager.IMPORTANCE_HIGH
//            val canal = NotificationChannel(CANAL_ID, CANAL_NOME, importancia).apply {
//                description = CANAL_DESCRICAO
//            }
//
//            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(canal)
//        }
//    }
//
//    /**
//     * Exibe uma notificação simples.
//     *
//     * @param titulo Título da notificação.
//     * @param conteudo Conteúdo da notificação.
//     * @param notificacaoId Identificador único da notificação.
//     */
//    fun mostrarNotificacao(titulo: String, conteudo: String, notificacaoId: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.e("Notificação", "Permissão de notificação não concedida. Operação ignorada.")
//            return
//        }
//
//        val builder = NotificationCompat.Builder(context, CANAL_ID)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(titulo)
//            .setContentText(conteudo)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(notificacaoId, builder.build())
//        }
//    }
//}
class Notification(private val context: Context) {

    companion object {
        const val CANAL_ID = "meu_canal_id"
        const val CANAL_NOME = "Canal de Notificação"
        const val CANAL_DESCRICAO = "Descrição do canal de notificações"
        const val PREFS_NAME = "NotificationPrefs" // Nome do SharedPreferences
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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
     * Verifica se a notificação já foi enviada.
     *
     * @param notificacaoId Identificador único da notificação.
     * @return `true` se já foi enviada, `false` caso contrário.
     */

    /**
     * Marca uma notificação como enviada.
     *
     * @param notificacaoId Identificador único da notificação.
     */
    private fun marcarNotificacaoComoEnviada(notificacaoId: Int) {
        sharedPreferences.edit().putBoolean(notificacaoId.toString(), true).apply()
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


        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(icone)
            .setContentTitle(titulo)
            .setContentText(conteudo)
            .setPriority(prioridade)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificacaoId, builder.build())
        }

    }

    /**
     * Exibe notificações específicas com base em condições.
     */
    fun exibirNotificacaoCondicional(notificacaoId: Int) {
        when  {

            notificacaoId == 1 -> mostrarNotificacao(
                titulo = "Meta Execedida",
                conteudo = "Seu dopaminho está com 70% de vida",
                notificacaoId = 1,
                icone = android.R.drawable.ic_dialog_alert,
                prioridade = NotificationCompat.PRIORITY_HIGH
            )

            notificacaoId == 2 -> mostrarNotificacao(
                titulo = "Você DOBROU sua meta de uso!",
                conteudo = "Seu dopaminho está com 50% de vida",
                notificacaoId = 2,
                prioridade = NotificationCompat.PRIORITY_HIGH
            )

            notificacaoId == 3 -> mostrarNotificacao(
                titulo = "META QUADRIPLICADA",
                conteudo = "Seu dopaminho está muito triste e doente",
                notificacaoId = 3,
                icone = android.R.drawable.ic_dialog_info,
                prioridade = NotificationCompat.PRIORITY_HIGH
            )

            else -> return
        }
    }

    /**
     * Reseta o estado de uma notificação enviada, para que possa ser enviada novamente.
     *
     * @param notificacaoId Identificador único da notificação.
     */


}