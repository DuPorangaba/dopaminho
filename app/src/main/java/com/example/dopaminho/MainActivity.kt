package com.example.dopaminho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dopaminho.ui.theme.DopaminhoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DopaminhoTheme {
                    MyApp(
                    )
                }
            }
        }
    }

@Composable
fun MyApp(){
    MainScreen()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var presses by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Dopaminho")
                },

                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                ElevatedButton(
                    onClick = {/*Deve levar à página de atividades*/},
                    modifier = Modifier.padding(horizontal = 10.dp).weight(1f)
                ) {
                    Text(("Atividades"))

                }
                ElevatedButton(
                    onClick = {/*Deve levar à página de estatísticas*/},
                    modifier = Modifier.padding(horizontal = 10.dp).weight(1f)
                ) {
                    Text(("Estatísticas"))

                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++/*Deveria abrir tela se sobreposição*/ }) {
                Text("Definir Metas", modifier=Modifier.padding(5.dp))

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text =
                """
                    Botões são clicáveis, falta levá-los às páginas corretas, e o botão de menu
                    não foi definido o que faz.
                    Para navegar para outras telas, pode ser implementado o NavHost,
                    ou fazer de forma burra e simplesmente rodar partes diferentes do código 
                    dependendo se o botão foi clicado ou não

                    You have pressed the floating action button $presses times.
                """.trimIndent(),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
fun GreetingPreview() {
    DopaminhoTheme {
        MyApp()
    }
}