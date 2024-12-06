package com.example.dopaminho

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
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
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay

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

    override fun onStart() {
        super.onStart()
        setContent {
            DopaminhoTheme {
                if (!hasUsageStatsPermission(this)) {
                    PermissionScreen()
                } else {
                    MyApp()
                    startAppUsageService()
                    //AlarmReceiver.setAlarm(this)
                }
            }
        }
    }

    private fun startAppUsageService() {
        val intent = Intent(this, UsageStatService::class.java)
        this.startService(intent)
    }
}



@Composable
fun MyApp() {
    DopaminhoTheme {
        MainScreen()
    }

}


@Composable
fun MainScreen() {
    var petName by remember { mutableStateOf("Dopaminho") }
    var progress by remember { mutableFloatStateOf(0.5f) }
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
            NavigationBar (
                containerColor= MaterialTheme.colorScheme.primaryContainer

            ) {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(
                        RoundedCornerShape(topStart = 16.dp,
                        topEnd = 16.dp, // Apenas o canto superior direito arredondado
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp)
                    )
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
                                        bitmap = ImageBitmap.imageResource(R.drawable.config),
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
    var vida by remember { mutableDoubleStateOf(BarraDeVida.vidaAtual) }
    LaunchedEffect(Unit) {
        while(true){
            vida = BarraDeVida.vidaAtual
            delay(1000)
        }
    }
    Column {
        //ImageResource recebe variavel vida que é mutável então consegue se modificar ao longo da execução do programa
        var imageResource = remember(vida) {
            when {
                vida > 70 -> R.drawable.dopaminho_piscando // Acima de 70%: Imagem feliz
                vida > 50 -> R.drawable.dopaminho_neutro
                vida > 30 -> R.drawable.dopaminho_meio_triste
                else -> R.drawable.dopaminho_muito_triste
            }
        }
        Image(  //Exibe imagem baseado em image resource
            modifier = Modifier
                .clip(CircleShape)
                .size(500.dp), // Define o tamanho da imagem
            painter = rememberDrawablePainter(
                drawable = getDrawable(
                    LocalContext.current,
                    imageResource
                )
            ),
            contentDescription = "Imagem do Dopaminho",
            contentScale = ContentScale.FillWidth,

            )
        //Botão que restaura vida para 100, mudando valor da variavel dentro do objetco Barra de Vida
        Button(onClick = {BarraDeVida.vidaAtual=100.0},
            modifier= Modifier.align(Alignment.CenterHorizontally)) {
            Text("Recuperar vida")
        }
        Button(
            onClick={BarraDeVida.perdeVida(10)},
            modifier= Modifier.align(Alignment.CenterHorizontally)){
            Text("Perde 10 de vida")
        }
    }
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
    var vida by remember { mutableDoubleStateOf(BarraDeVida.vidaAtual) } //utilizando remember para refletir mudanças na interface, que não estava funcionando com object e Lauched Effects

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
                        LaunchedEffect(Unit) {
                            while (true) {
                                //BarraDeVida.perdeVida(1)
                                vida= BarraDeVida.vidaAtual
                                delay(100)
                            }
                        }
                        Text("Vida: $vida / 100.0")
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("estatisticas") }) {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.estatisticas),
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

