package com.example.dopaminho

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

import com.example.dopaminho.ui.theme.DopaminhoTheme
import kotlinx.coroutines.delay
import kotlin.math.max

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun EstatisticasScreen() {

    val context = LocalContext.current

    val goalRepository = GoalRepository(context)
    val appUsageRepository = appUsageRepository(context)

    val goals by goalRepository.loadGoalsAsFlow().collectAsState(initial = emptyList())
    val stats by appUsageRepository.loadAppUsageAsFlow().collectAsState(initial = emptyList())

    // Combina as metas com as estatísticas de uso
    val combinedData = remember(goals, stats) {
        goals.mapNotNull {
            goal ->
            val appStat = stats.find { it.labelName == goal.labelApp }
            appStat?.let {
                Triple(
                    goal.labelApp,
                    (appStat.totalUsageTime - goal.usageTimeOnCreation),
                    goal.time
                )
            }
        }
    }

    DopaminhoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Tempo de Uso vs Metas",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF0054DD) //0054DD
            )

            if (goals.isEmpty() || stats.isEmpty()) {
                Text(
                    text = "No usage stats or goals available.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Espaço flexível para empurrar o gráfico para a parte inferior
                Spacer(modifier = Modifier.weight(1f))

                // Exibe o gráfico na parte inferior da tela
                BarChartComparison(
                    data = combinedData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(400.dp) // Altura do gráfico
                )
            }
        }
    }
}

@Composable
fun BarChartComparison(data: List<Triple<String, Long, Int>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        // Legenda das cores
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF618AD1)) // Azul-claro
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Uso atual", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4958B1)) // Azul-escuro
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Meta", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de barras
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxWidth = maxWidth
            val barWidth = maxWidth / (data.size * 2)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxValue = max(
                    data.map { it.second.toFloat() }.maxOrNull() ?: 0f,
                    data.map { it.third.toFloat() }.maxOrNull() ?: 0f
                )

                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 12.sp.toPx()
                }

                data.forEachIndexed { index, (label, usage, goal) ->
                    val usageHeight = size.height * (usage / maxValue)
                    val goalHeight = size.height * (goal / maxValue)
                    val barX = index * barWidth.toPx() * 2

                    // Desenho da barra de uso atual
                    drawRect(
                        color = Color(0xFF618AD1), // Azul-claro
                        topLeft = Offset(barX, size.height - usageHeight),
                        size = Size(barWidth.toPx(), usageHeight)
                    )

                    // Número sobre a barra de uso
                    drawContext.canvas.nativeCanvas.drawText(
                        usage.toString(),
                        barX + barWidth.toPx() / 2f,
                        size.height - usageHeight - 8.dp.toPx(),
                        paint
                    )

                    // Desenho da barra de meta
                    drawRect(
                        color = Color(0xFF4958B1), // Azul-escuro
                        topLeft = Offset(barX + barWidth.toPx(), size.height - goalHeight),
                        size = Size(barWidth.toPx(), goalHeight)
                    )

                    // Número sobre a barra de meta
                    drawContext.canvas.nativeCanvas.drawText(
                        goal.toString(),
                        barX + barWidth.toPx() * 1.5f,
                        size.height - goalHeight - 8.dp.toPx(),
                        paint
                    )

                    // Desenha o nome do aplicativo abaixo das barras
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        barX + barWidth.toPx(),
                        size.height + 20.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}


//fun BarChartComparison(data: List<Triple<String, Long, Int>>, modifier: Modifier = Modifier) {
//    BoxWithConstraints(modifier = modifier) {
//        val maxWidth = maxWidth
//        val barWidth = maxWidth / (data.size * 2) // Ajuste no espaço entre as barras
//
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            val maxValue = max(
//                data.map { it.second.toFloat() }.maxOrNull() ?: 0f,
//                data.map { it.third.toFloat() }.maxOrNull() ?: 0f
//            )
//
//            data.forEachIndexed { index, (label, usage, goal) ->
//                val usageHeight = size.height * (usage / maxValue)
//                val goalHeight = size.height * (goal / maxValue)
//                val barX = index * barWidth.toPx() * 2
//
//                // Desenho da barra de uso atual
//                drawRect(
//                    color = Color(0xFF618AD1) ,
//                    topLeft = Offset(barX, size.height - usageHeight),
//                    size = Size(barWidth.toPx(), usageHeight)
//                )
//
//                // Desenho da barra de meta
//                drawRect(
//                    color = Color(0xFF4958B1) ,
//                    topLeft = Offset(barX + barWidth.toPx(), size.height - goalHeight),
//                    size = Size(barWidth.toPx(), goalHeight)
//                )
//
//                // Desenha o nome do aplicativo abaixo das barras
//                drawContext.canvas.nativeCanvas.apply {
//                    drawText(
//                        label,
//                        barX + barWidth.toPx() / 2f,
//                        size.height + 20.dp.toPx(),
//                        android.graphics.Paint().apply {
//                            color = android.graphics.Color.BLACK
//                            textAlign = android.graphics.Paint.Align.CENTER
//                            textSize = 12.sp.toPx()
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

//fun EstatisticasScreen() {
//    val context = LocalContext.current
//    val usageStatsRepository = remember { appUsageRepository(context) }
//    val savedStats = usageStatsRepository.loadAppUsageAsFlow().collectAsState(initial = emptyList())
//
//    DopaminhoTheme {
//        if (!hasUsageStatsPermission(context)) {
//            PermissionScreen()
//        } else {
//            Column(modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//            ) {
//                Text(text = "Usage Stats:")
//
//                if(savedStats.value.isEmpty()) {
//                    Text(
//                        text = "No usage stats available."
//                    )
//                } else {
//                    savedStats.value.forEach { stat ->
//                        Text(
//                            text = "${stat.labelName}: ${stat.totalUsageTime} secs",
//                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
//                        )
//                    }
//                }
//
//            }
//        }
//    }
//
//}



@Preview(showBackground = true)
@Composable
fun EstatisticasScreenPreview(){
    EstatisticasScreen()
}