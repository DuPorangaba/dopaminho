package com.example.dopaminho

import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dopaminho.ui.theme.DopaminhoTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.NavController
import com.example.dopaminho.ui.theme.AppTypography
import com.example.dopaminho.ui.theme.bodyFontFamily
import com.google.accompanist.drawablepainter.rememberDrawablePainter

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
    DopaminhoTheme {
        MainScreen()
    }

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
            PetTopBar(
                petName = petName,
                progress = progress,
                onEditPetName = { newName -> petName = newName },
                navController = navController // Passando o navController
            )

        },
        bottomBar = {
            Box(
                modifier = Modifier

            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp,
                        topEnd = 16.dp, // Apenas o canto superior direito arredondado
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp))
                ) {

                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(7.dp)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Icon(
                                        bitmap = ImageBitmap.imageResource(R.drawable.home_page),
                                        contentDescription = "Home page",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        },

                        selected = selectedTab == "inicio",
                        onClick = {
                            selectedTab = "inicio"
                            navController.navigate("inicio") {
                                popUpTo("inicio") { inclusive = true }
                            }
                        },

                        )
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(7.dp)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Icon(
                                        bitmap = ImageBitmap.imageResource(R.drawable.atividades),
                                        contentDescription = "Atividades",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        },

                        selected = selectedTab == "atividades",
                        onClick = {
                            selectedTab = "atividades"
                            navController.navigate("atividades") {
                                popUpTo("atividades") { inclusive = true }
                            }
                        },

                        )
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(7.dp)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(6.dp)
                                ) {
                                    Icon(
                                        bitmap = ImageBitmap.imageResource(R.drawable.estatisticas),
                                        contentDescription = "Estatísticas",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        },

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
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "inicio", Modifier.padding(innerPadding)) {
            composable("inicio") { InicioScreen() }
            composable("atividades") { AtividadesScreen() }
            composable("metas") { MetasScreen() }
            composable("estatisticas") { EstatisticasScreen() }
        }




    }
}

@Composable
fun InicioScreen() {
    Image(
        modifier = Modifier.clip(CircleShape),   //crops the image to circle shape
        painter = rememberDrawablePainter(
            drawable = getDrawable(
                LocalContext.current,
                R.drawable.dopaminho_piscando
            )
        ),
        contentDescription = "dopaminho piscando",
        contentScale = ContentScale.FillWidth,
    )
}



@Preview(showBackground = true, widthDp = 360, heightDp = 600)
@Composable
fun GreetingPreview() {
    DopaminhoTheme {
        MyApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetTopBar(petName: String, progress: Float, onEditPetName: (String) -> Unit, navController: NavController) {
    var isEditing by remember { mutableStateOf(false) }
    var newPetName by remember { mutableStateOf(TextFieldValue(petName)) }

    DopaminhoTheme {
        Column {

            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = petName,
                                //fontFamily = bodyFontFamily
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar nome do pet")
                            }
                        }
                        // Barra de progresso dentro da TopAppBar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .height(15.dp)
                                .clip(RoundedCornerShape(10.dp))
                                ,
                            color= MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("estatisticas") }) {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.config),
                            contentDescription = "Configurações",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(topStart = 0.dp,
                    topEnd = 0.dp, // Apenas o canto superior direito arredondado
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp))



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
}

