package com.example.dopaminho

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay

//@Composable
//fun AtividadesScreen() {
//    var activities by remember { mutableStateOf(listOf("Correr", "Ler", "Estudar")) }
//    var newActivity by remember { mutableStateOf("") }
//    var selectedActivity by remember { mutableStateOf("") }
//    var selectedTime by remember { mutableStateOf(0) }
//    var timeInputVisible by remember { mutableStateOf(false) }
//    var isTiming by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Text(text = "Atividades", style = MaterialTheme.typography.headlineMedium)
//
//        // Listagem de atividades como cards
//        LazyColumn {
//            items(activities) { activity ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                        .clickable {
//                            selectedActivity = activity
//                            timeInputVisible = true
//                        },
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(text = activity, style = MaterialTheme.typography.titleMedium)
//                        if (selectedActivity == activity && timeInputVisible) {
//                            TextField(
//                                value = selectedTime.toString(),
//                                onValueChange = { time ->
//                                    selectedTime = time.toIntOrNull() ?: 0
//                                },
//                                label = { Text("Tempo (minutos)") },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            Button(onClick = {
//                                isTiming = true
//                                timeInputVisible = false // Oculta o campo de entrada
//                            }) {
//                                Text("Iniciar Temporizador")
//                            }
//                        }
//                        if (isTiming && selectedActivity == activity) {
//                            Timer(selectedTime * 60) // Converte minutos para segundos
//                        }
//                    }
//                }
//            }
//        }
//
//        // Campo para adicionar nova atividade
//        TextField(
//            value = newActivity,
//            onValueChange = { newActivity = it },
//            label = { Text("Adicionar nova atividade") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Button(onClick = {
//            if (newActivity.isNotBlank()) {
//                activities = activities + newActivity
//                newActivity = "" // Limpa o campo após adicionar
//            }
//        }) {
//            Text("Adicionar")
//        }
//    }
//}
//
//@Composable
//fun Timer(seconds: Int) {
//    var remainingTime by remember { mutableStateOf(seconds) }
//    var timerRunning by remember { mutableStateOf(true) }
//
//    LaunchedEffect(timerRunning) {
//        while (timerRunning && remainingTime > 0) {
//            delay(1000)
//            remainingTime -= 1
//        }
//        if (remainingTime == 0) {
//            timerRunning = false
//        }
//    }
//
//    if (remainingTime > 0) {
//        Text(text = "Tempo restante: ${remainingTime / 60}m ${remainingTime % 60}s")
//    } else {
//        Text(text = "Tempo esgotado!", color = MaterialTheme.colorScheme.error)
//    }
//}

@Composable
fun AtividadesScreen() {
    var activities by remember { mutableStateOf(listOf("Correr", "Ler", "Estudar")) }
    var newActivity by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(0) }
    var timeInputVisible by remember { mutableStateOf(false) }
    var isTiming by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Atividades", style = MaterialTheme.typography.headlineMedium)

        // Listagem de atividades como cards
        LazyColumn {
            items(activities) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (!isTiming || selectedActivity != activity) {
                                selectedActivity = activity
                                timeInputVisible = true
                            }
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = activity, style = MaterialTheme.typography.titleMedium)
                        if (selectedActivity == activity && timeInputVisible) {
                            TextField(
                                value = if (selectedTime > 0) selectedTime.toString() else "",
                                onValueChange = { time ->
                                    selectedTime = time.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                },
                                label = { Text("Tempo (segundos)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Button(
                                onClick = {
                                    if (selectedTime > 0) {
                                        isTiming = true
                                        timeInputVisible = false // Oculta o campo de entrada
                                    }
                                },
                                enabled = selectedTime > 0
                            ) {
                                Text("Iniciar Temporizador")
                            }
                        }
                        if (isTiming && selectedActivity == activity) {
                            if (activity == "Correr") {
                                Image(  //Exibe imagem baseado em image resource
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(400.dp), // Define o tamanho da imagem
                                    painter = rememberDrawablePainter(
                                        drawable = getDrawable(
                                            LocalContext.current,
                                            R.drawable.dopaminho_andando
                                        )
                                    ),
                                    contentDescription = "Imagem do Dopaminho",
                                    contentScale = ContentScale.FillWidth,

                                    )
                            } else if (activity == "Ler") {
                                Image(  //Exibe imagem baseado em image resource
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(400.dp), // Define o tamanho da imagem
                                    painter = rememberDrawablePainter(
                                        drawable = getDrawable(
                                            LocalContext.current,
                                            R.drawable.dopaminho_lendo
                                        )
                                    ),
                                    contentDescription = "Imagem do Dopaminho",
                                    contentScale = ContentScale.FillWidth,

                                    )
                            }
                            Timer(selectedTime) {
                                isTiming = false
                                BarraDeVida.ganhaVida(5)
                            }
                        }
                    }
                }
            }
        }

        // Campo para adicionar nova atividade
        TextField(
            value = newActivity,
            onValueChange = { newActivity = it },
            label = { Text("Adicionar nova atividade") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            if (newActivity.isNotBlank()) {
                activities = activities + newActivity
                newActivity = "" // Limpa o campo após adicionar
            }
        }) {
            Text("Adicionar")
        }
    }
}

@Composable
fun Timer(seconds: Int, onTimerEnd: () -> Unit) {
    var remainingTime by remember { mutableStateOf(seconds) }
    var timerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        }
        if (remainingTime == 0) {
            timerRunning = false
            onTimerEnd()
        }
    }

    if (remainingTime > 0) {
        Text(text = "Tempo restante: ${remainingTime / 60}m ${remainingTime % 60}s")
    } else {
        Text(text = "Tempo esgotado!", color = MaterialTheme.colorScheme.error)
    }
}


@Preview(showBackground = true)
@Composable
fun AtividadesScreenPreview(){
    AtividadesScreen()
}