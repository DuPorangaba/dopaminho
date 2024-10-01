package com.example.dopaminho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dopaminho.ui.theme.DopaminhoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DopaminhoTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    MainScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var petName by remember { mutableStateOf("Dopaminho") }
    var progress by remember { mutableStateOf(0.5f) }
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf("inicio") }

    Scaffold(
        topBar = {
            PetTopBar(petName = petName, progress = progress) { newName ->
                petName = newName
            }
        },
        bottomBar = {
            NavigationBar (

            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = selectedTab == "inicio",
                    onClick = {
                        selectedTab = "inicio"
                        navController.navigate("inicio") {
                            popUpTo("inicio") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Atividades") },
                    label = { Text("Atividades") },
                    selected = selectedTab == "atividades",
                    onClick = {
                        selectedTab = "atividades"
                        navController.navigate("atividades") {
                            popUpTo("atividades") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Edit, contentDescription = "Metas") },
                    label = { Text("Metas") },
                    selected = selectedTab == "metas",
                    onClick = {
                        selectedTab = "metas"
                        navController.navigate("metas") {
                            popUpTo("metas") { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "inicio", Modifier.padding(innerPadding)) {
            composable("inicio") { InicioScreen() }
            composable("atividades") { AtividadesScreen() }
            composable("metas") { MetasScreen() }
        }
    }
}

@Composable
fun InicioScreen() {
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
fun GreetingPreview() {
    DopaminhoTheme {
        MyApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetTopBar(petName: String, progress: Float, onEditPetName: (String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var newPetName by remember { mutableStateOf(TextFieldValue(petName)) }

    Column {
        TopAppBar(
            colors = topAppBarColors (
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title =  {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = petName)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar nome do pet")
                        }
                    }
                    // Barra de progresso dentro da TopAppBar
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth().padding(vertical = 4.dp)
                            .height(15.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            },
            actions = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
        if (isEditing) {
            AlertDialog(
                onDismissRequest = { isEditing = false },
                title = { Text("Editar nome do pet") },
                text = {
                    TextField(
                        value = newPetName,
                        onValueChange = { newPetName = it },
                        label = { Text("Nome do Pet") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEditPetName(newPetName.text)
                            isEditing = false
                        }
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

