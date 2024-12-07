package com.example.dopaminho

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import android.content.Intent
import android.content.UriPermission
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dopaminho.ui.theme.DopaminhoTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
}

fun grantUsageStatsPermission(context: Context) {
    val sendIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    context.startActivity(sendIntent)
}

fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true // Permissão não é necessária em versões anteriores
    }
}


fun grantNotificationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PermissionScreen() {
//    val context = LocalContext.current
//    var showPermissionDialog by remember { mutableStateOf(true) }
//
//        DopaminhoTheme {
//            AlertDialog(
//                onDismissRequest = { showPermissionDialog = false },
//                title = {
//                    Text("Solicitando Permissão")
//                },
//                text = {
//                    Text(
//                        text = "Para o app funcionar, é necessário permitir que ele colete informações sobre o tempo de uso dos apps.\n" +
//                                "Assim, é preciso dar essa permissão a este app."
//                    )
//                },
//                confirmButton = {
//                    Button(
//                        onClick = {
//                            // Chama a função grantUsageStatsPermission quando o botão for clicado
//                            grantUsageStatsPermission(context)
//                            showPermissionDialog = false // Fecha o diálogo após a ação
//                        }
//                    ) {
//                        Text("Dar Permissão")
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = { showPermissionDialog = false }) {
//                        Text("Cancelar")
//                    }
//                }
//            )
//        }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(true) }
    var notificationPermissionGranted by remember { mutableStateOf(hasNotificationPermission(context)) }

    DopaminhoTheme {
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = {
                    Text("Solicitando Permissões")
                },
                text = {
                    Text(
                        text = "Para o app funcionar corretamente, é necessário conceder as seguintes permissões:\n" +
                                "- Permissão de uso dos aplicativos\n" +
                                "- Permissão para enviar notificações"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Solicita permissão de uso de estatísticas
                            grantUsageStatsPermission(context)

                            // Verifica e solicita permissão de notificações, se necessário
                            if (!notificationPermissionGranted) {
                                grantNotificationPermission(context)
                            }

                            showPermissionDialog = false // Fecha o diálogo após a ação
                        }
                    ) {
                        Text("Dar Permissões")
                    }
                },
                dismissButton = {
                    Button(onClick = { showPermissionDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

