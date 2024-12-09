package com.example.dopaminho

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dopaminho.ui.theme.DopaminhoTheme
import kotlinx.coroutines.flow.first
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStoreGoals: DataStore<Preferences> by preferencesDataStore(name = "goals")

class GoalRepository(context: Context) {
    private val dataStore = context.dataStoreGoals

    companion object {
        private val GOALS_KEY = stringPreferencesKey("goals")
    }

    suspend fun saveGoals(goals: List<Goal>) {
        val goalJson = Goal.listToJson(goals)
        dataStore.edit { preferences ->
            preferences[GOALS_KEY] = goalJson
        }
    }

    suspend fun loadGoals(): List<Goal> {
        val preferences = dataStore.data.first()
        val goalsJson = preferences[GOALS_KEY] ?: "[]"
        return Goal.jsonToList(goalsJson)
    }

    fun loadGoalsAsFlow(): Flow<List<Goal>> {
        return dataStore.data
            .map { preferences ->
                val goalsJson = preferences[GOALS_KEY] ?: "[]"
                Goal.jsonToList(goalsJson)
            }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAddGoal: (String, Int, String) -> Unit,
    context: Context,
    existingGoal: Goal? = null // Recebe uma meta existente para edição
) {
    //listando os aplicativos instalados existentes
    val installedApps = remember {
        context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            },
            PackageManager.MATCH_ALL
        ).map {it.activityInfo.loadLabel(context.packageManager).toString()}.sorted()
    }

    //vendo as metas existentes
    val savedGoals = remember { mutableStateOf(listOf<Goal>()) }
    LaunchedEffect(Unit) {
        savedGoals.value = GoalRepository(context).loadGoals()
    }

    //filtrando os apps instalados de acordo com as metas existentes, para que não existam duplicatas
    val filteredApps = installedApps.filter {
        app -> !savedGoals.value.any {it.labelApp == app}
    }

    var selectedApp by remember { mutableStateOf(existingGoal?.labelApp ?: "Instagram") }
    var usageTime by remember { mutableStateOf(existingGoal?.time?.toString() ?: "") }
    var reason by remember { mutableStateOf(existingGoal?.reason ?: "") }
    var expanded by remember { mutableStateOf(false) }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingGoal == null) "Adicionar Nova Meta" else "Editar Meta") },
        confirmButton = {
            Button(
                onClick = {
                    if (!usageTime.isNullOrEmpty() && !reason.isNullOrEmpty()) {
                        onAddGoal(selectedApp, usageTime!!.toInt(), reason)
                        usageTime = ""
                        reason = ""
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                Text("Escolha a Rede Social:")
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedApp,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rede Social") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredApps.forEach { labelApp ->
                            DropdownMenuItem(
                                text = { Text(labelApp) },
                                onClick = {
                                    selectedApp = labelApp
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text("Defina o Tempo de Uso (em minutos):")
                TextField(
                    value = usageTime ?: "",
                    onValueChange = { usageTime = it },
                    label = { Text("Ex: 30") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Motivo:")
                TextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Por que você quer isso?") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun MetasScreen() {
    val context = LocalContext.current
    val goalRepository = remember { GoalRepository(context) }

    var savedGoals by remember { mutableStateOf(listOf<Goal>()) }
    var showDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }

    LaunchedEffect(Unit) {
        savedGoals = goalRepository.loadGoals()
    }

    LaunchedEffect(savedGoals) {
        goalRepository.saveGoals(savedGoals)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                goalToEdit = null // Define que estamos criando uma nova meta
                showDialog = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Adicionar Meta")
        }

        // Exibe os cards com as metas salvas
        Spacer(modifier = Modifier.height(16.dp))
        savedGoals.forEach { goal ->
            GoalCard(
                goal = goal,
                onEdit = { updatedGoal ->
                    goalToEdit = updatedGoal // Define a meta a ser editada
                    showDialog = true
                },
                onRemove = { goalToRemove ->
                    // Remover a meta diretamente
                    savedGoals = savedGoals.filterNot { it == goalToRemove }
                }
            )
        }


        // Modal para adicionar ou editar nova meta
        if (showDialog) {
            AddGoalDialog(
                onDismiss = { showDialog = false },
                onAddGoal = { labelApp, time, reason ->
                    //pega a quantidade de tempo usada até de criar a meta
                    val usageStatsOnCreation = AppUsageManager.getAppUsageTime(context, labelApp)

                    val newGoal = Goal(labelApp, time, reason, usageStatsOnCreation)
                    savedGoals = if (goalToEdit != null) {
                        // Editar meta existente
                        savedGoals.map {
                            if (it == goalToEdit) Goal(labelApp, time, reason, usageStatsOnCreation) else it
                        }
                    } else {
                        // Adiciona nova meta
                        savedGoals + newGoal
                    }

                    showDialog = false
                },
                context,
                existingGoal = goalToEdit // Passa a meta a ser editada
            )
        }
    }
}

@Composable
fun GoalCard(goal: Goal, onEdit: (Goal) -> Unit, onRemove: (Goal) ->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Rede Social: ${goal.labelApp}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Tempo de Uso: ${goal.time} minutos", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Motivo: ${goal.reason}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onEdit(goal) }) {
                Text("Editar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onRemove(goal) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Remover", color = MaterialTheme.colorScheme.onError)
            }

        }
    }
}

@Preview
    (showBackground = true, widthDp = 320, heightDp = 480)
@Composable
fun MetasScreenPreview() {
    DopaminhoTheme {
        MetasScreen()
    }
}
// Data class para representar uma meta
data class Goal(
    val labelApp: String, 
    val time: Int, 
    val reason: String,
    var usageTimeOnCreation: Long) {
    companion object {
        fun listToJson(goals: List<Goal>): String {
            val gson = Gson()
            return gson.toJson(goals)
        }

        fun jsonToList(json: String): List<Goal> {
            val gson = Gson()
            val type = object : TypeToken<List<Goal>>() {}.type
            return gson.fromJson(json, type)

        }
    }
}
