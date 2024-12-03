package com.example.dopaminho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dopaminho.ui.theme.DopaminhoTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay
import java.util.logging.Handler

//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            DopaminhoTheme {
//                MyApp()
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        setContent {
//            DopaminhoTheme {
//                if (!hasUsageStatsPermission(this)) {
//                    PermissionScreen()
//                } else {
//                    MyApp()
//                    AlarmReceiver.setAlarm(this)
//                }
//            }
//        }
//    }
//}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificacao = Notification(this)
        notificacao.criarCanalNotificacao()

        notificacao.mostrarNotificacao(
            titulo = "Teste Direto",
            conteudo = "oi.",
            notificacaoId = 1
        )
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
                if (!hasUsageStatsPermission(this) || !hasNotificationPermission(this)) {
                    // Se qualquer permissão não foi concedida, exibe a tela de permissões
                    PermissionScreen()
                } else {
                    // Se ambas as permissões foram concedidas, exibe o app
                    MyApp()
                    AlarmReceiver.setAlarm(this)
                }
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


@Composable
fun MainScreen() {
    var petName by remember { mutableStateOf("Dopaminho") }
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf("inicio") }




    Scaffold(

        topBar = {
            PetTopBar(
                petName = petName,
                onEditPetName = { newName -> petName = newName },
                navController = navController // Passando o navController
            )


        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer

            ) {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp, // Apenas o canto superior direito arredondado
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Garante que o conteúdo não sobreponha a barra de navegação
            contentAlignment = Alignment.Center // Centraliza todo o conteúdo no Box
        ) {
            // Navegação entre telas
            NavHost(navController, startDestination = "inicio", Modifier.fillMaxSize()) {
                composable("inicio") { InicioScreen() }
                composable("atividades") { AtividadesScreen() }
                composable("metas") { MetasScreen() }
                composable("estatisticas") { EstatisticasScreen() }
            }




        }
    }
}

@Composable
fun InicioScreen() {
    //Tentando puxar as metas
    val context = LocalContext.current
    val goalRepository = remember { GoalRepository(context) }
    var savedGoals by remember { mutableStateOf(listOf<Goal>()) }

    LaunchedEffect(Unit) {
        savedGoals = goalRepository.loadGoals()
    }

    LaunchedEffect(savedGoals) {
        goalRepository.saveGoals(savedGoals)
    }
    //Tentando puxar tempo de uso
    val usageStatsList by AppUsageManager.AppUsageStatsList.observeAsState(mutableListOf())

    LaunchedEffect(Unit) {
        while(true) {
            AppUsageManager.getUsageStats(context)
            delay(1000)
        }

    }
    // Exibe o Dopaminho centralizado
    var vida by remember { mutableDoubleStateOf(BarraDeVida.vidaAtual) }
    //Utiliza Lauched effects para que rode a função em loop a cada 1 segundo e atualiza vida com base no objeto Vida
    LaunchedEffect(Unit) {
        while (true) {
            vida= BarraDeVida.vidaAtual
            delay(1000)


        }
    }
    Column {
        //ImageResource recebe variavel vida que é mutável então consegue se modificar ao longo da execução do programa
        var imageResource = remember(vida) {
            when {
                vida > 70 -> R.drawable.dopaminho_piscando // Acima de 70%: Imagem feliz
                else -> R.drawable.dopaminho_neutro // Entre 30% e 70%: Imagem neutra
            }
        }
        Image(  //Exibe imagem baseado em image resource
            modifier = Modifier
                .clip(CircleShape)
                .size(240.dp), // Define o tamanho da imagem
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
        Button(onClick = {BarraDeVida.vidaAtual=100.0}) {
            Text("Recuperar vida")
        }
    }
    //Exibindo metas na tela
    Column {
        Column {
            savedGoals.forEach { goal ->
                Text("Meta: ${goal.labelApp} = ${goal.time} min")
            }
        }
        //Exibindo tempo de uso na tela
        Column(
            modifier = Modifier
        ) {
            usageStatsList.forEach { stat ->
                Text(
                    text = "${stat.labelName}: ${stat.totalUsageTime} secs",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
        }
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ){
            savedGoals.forEach{goal->
                usageStatsList.forEach{stat->
                    if(goal.labelApp==stat.labelName){
                        Text("Esta na meta: ${stat.labelName}")
                        if(goal.time * 60  < stat.totalUsageTime){
                            Text("Fora da meta: ${goal.time} min | Uso: ${stat.totalUsageTime/60} min")
                        }
                        else{
                            Text("Dentro da meta: ${goal.time} min | Uso: ${stat.totalUsageTime/60} min")
                        }
                    }


                }
            }
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
object BarraDeVida { //Objeto barra de vida, não armazenou na memória
    var vidaAtual = 100.00

    fun perdeVida(dano:Long){
        vidaAtual -= dano
        if(vidaAtual<0 ) vidaAtual= 0.0
    }


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetTopBar(petName: String, onEditPetName: (String) -> Unit, navController: NavController) {
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
                        // Barra de progresso dentro da TopAppBar
                        //Loop que roda função não composable que perde vida a cada 0.1 e atualiza variavel vida que é mutável de forma que atualiza
                        //Text
                        LaunchedEffect(Unit) {
                            while (true) {
                                BarraDeVida.perdeVida(1)
                                vida= BarraDeVida.vidaAtual
                                delay(100)


                            }
                        }
                        Text("Vida: $vida / 100")
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
