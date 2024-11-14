package com.example.dopaminho

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.dopaminho.ui.theme.DopaminhoTheme

@Composable
fun MetasScreen() {
    var savedGoals by remember { mutableStateOf(listOf<Goal>()) }
    var showDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            GoalCard(goal) { updatedGoal ->
                goalToEdit = updatedGoal // Define a meta a ser editada
                showDialog = true
            }
        }

        // Modal para adicionar ou editar nova meta
        if (showDialog) {
            AddGoalDialog(
                onDismiss = { showDialog = false },
                onAddGoal = { network, time, reason ->
                    if (goalToEdit != null) {
                        // Editar meta existente
                        savedGoals = savedGoals.map {
                            if (it == goalToEdit) Goal(network, time, reason) else it
                        }
                    } else {
                        // Adiciona nova meta
                        savedGoals = savedGoals + Goal(network, time, reason)
                    }
                    showDialog = false
                },
                existingGoal = goalToEdit // Passa a meta a ser editada
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAddGoal: (String, Int, String) -> Unit,
    existingGoal: Goal? = null // Recebe uma meta existente para edição
) {
    var selectedNetwork by remember { mutableStateOf(existingGoal?.network ?: "Facebook") }
    var usageTime by remember { mutableStateOf(existingGoal?.time?.toString() ?: "") }
    var reason by remember { mutableStateOf(existingGoal?.reason ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val socialNetworks = listOf("Facebook", "Instagram", "Twitter", "TikTok", "LinkedIn")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingGoal == null) "Adicionar Nova Meta" else "Editar Meta") },
        confirmButton = {
            Button(
                onClick = {
                    if (!usageTime.isNullOrEmpty() && !reason.isNullOrEmpty()) {
                        onAddGoal(selectedNetwork, usageTime!!.toInt(), reason)
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
                        value = selectedNetwork,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rede Social") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        socialNetworks.forEach { network ->
                            DropdownMenuItem(
                                text = { Text(network) },
                                onClick = {
                                    selectedNetwork = network
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
fun GoalCard(goal: Goal, onEdit: (Goal) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Rede Social: ${goal.network}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Tempo de Uso: ${goal.time} minutos", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Motivo: ${goal.reason}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onEdit(goal) }) {
                Text("Editar")
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
data class Goal(val network: String, val time: Int, val reason: String)
