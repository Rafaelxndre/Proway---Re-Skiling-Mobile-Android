package com.example.gerenciadorcontatos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciadorcontatos.viewmodel.ContactViewModel
import com.example.gerenciadorcontatos.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(viewModel: ContactViewModel) {
    val contact = viewModel.selectedContact.value ?: return

    val scrollState = rememberScrollState()
    val name = remember { mutableStateOf(contact.name) }
    val email = remember { mutableStateOf(contact.email) }
    val telefone = remember { mutableStateOf(contact.telefone) }
    val nascimento = remember { mutableStateOf(contact.nascimento) }
    val cep = remember { mutableStateOf(contact.cep) }
    val bairro = remember { mutableStateOf(contact.bairro) }
    val logradouro = remember { mutableStateOf(contact.logradouro) }
    val estado = remember { mutableStateOf(contact.estado) }
    val cidade = remember { mutableStateOf(contact.cidade) }
    val numero = remember { mutableStateOf(contact.numero) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    // Fetch address only when CEP changes from the original value
    LaunchedEffect(cep.value) {
        val normalized = cep.value.filter { it.isDigit() }
        if (normalized.length == 8 && normalized != contact.cep) {
            viewModel.fetchAddress(normalized)
        }
    }

    // Apply fetched address to local form fields
    LaunchedEffect(viewModel.addressResult.value) {
        viewModel.addressResult.value?.let { address ->
            bairro.value = address.bairro
            logradouro.value = address.logradouro
            estado.value = address.estado
            cidade.value = address.cidade
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color(0xFF5A5A5A),
        unfocusedBorderColor = Color(0xFF3A3A3A),
        focusedLabelColor = Color(0xFFB5B5B5),
        unfocusedLabelColor = Color(0xFF8A8A8A),
        cursorColor = Color.White
    )

    val readOnlyColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFFB5B5B5),
        unfocusedTextColor = Color(0xFFB5B5B5),
        focusedBorderColor = Color(0xFF3A3A3A),
        unfocusedBorderColor = Color(0xFF3A3A3A),
        focusedLabelColor = Color(0xFF8A8A8A),
        unfocusedLabelColor = Color(0xFF8A8A8A)
    )

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Excluir contato", color = Color.White) },
            text = {
                Text(
                    "Deseja excluir ${contact.name}? Esta ação não pode ser desfeita.",
                    color = Color(0xFFB5B5B5)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                    viewModel.deleteContact()
                    viewModel.navigateTo(Screen.CONTACTS)
                }) {
                    Text("Excluir", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar", color = Color(0xFFB5B5B5))
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar contato", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.CONTACTS) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A0A))
            )
        },
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
                .padding(innerPadding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name.value, onValueChange = { name.value = it },
                label = { Text("Nome", color = Color(0xFFB5B5B5)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors
            )
            OutlinedTextField(
                value = email.value, onValueChange = { email.value = it },
                label = { Text("E-mail", color = Color(0xFFB5B5B5)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = fieldColors
            )
            OutlinedTextField(
                value = telefone.value, onValueChange = { telefone.value = it },
                label = { Text("Telefone", color = Color(0xFFB5B5B5)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = fieldColors
            )
            OutlinedTextField(
                value = nascimento.value, onValueChange = { nascimento.value = it },
                label = { Text("Nascimento", color = Color(0xFFB5B5B5)) },
                placeholder = { Text("dd/mm/aaaa", color = Color(0xFF8A8A8A)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors
            )
            OutlinedTextField(
                value = cep.value,
                onValueChange = { cep.value = it.filter { c -> c.isDigit() }.take(8) },
                label = { Text("CEP", color = Color(0xFFB5B5B5)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors
            )

            if (viewModel.cepError.value != null) {
                Text(
                    text = viewModel.cepError.value ?: "",
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )
            }

            OutlinedTextField(
                value = logradouro.value, onValueChange = {},
                label = { Text("Logradouro", color = Color(0xFFB5B5B5)) },
                readOnly = true, singleLine = true, modifier = Modifier.fillMaxWidth(),
                colors = readOnlyColors
            )
            OutlinedTextField(
                value = bairro.value, onValueChange = {},
                label = { Text("Bairro", color = Color(0xFFB5B5B5)) },
                readOnly = true, singleLine = true, modifier = Modifier.fillMaxWidth(),
                colors = readOnlyColors
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cidade.value, onValueChange = {},
                    label = { Text("Cidade", color = Color(0xFFB5B5B5)) },
                    readOnly = true, singleLine = true, modifier = Modifier.weight(1f),
                    colors = readOnlyColors
                )
                OutlinedTextField(
                    value = estado.value, onValueChange = {},
                    label = { Text("Estado", color = Color(0xFFB5B5B5)) },
                    readOnly = true, singleLine = true, modifier = Modifier.width(110.dp),
                    colors = readOnlyColors
                )
            }
            OutlinedTextField(
                value = numero.value, onValueChange = { numero.value = it },
                label = { Text("Numero", color = Color(0xFFB5B5B5)) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors
            )

            Button(
                onClick = {
                    viewModel.updateContact(
                        name.value.trim(), email.value.trim(), telefone.value.trim(),
                        nascimento.value.trim(), cep.value.trim(), bairro.value.trim(),
                        logradouro.value.trim(), estado.value.trim(), cidade.value.trim(),
                        numero.value.trim()
                    )
                    viewModel.navigateTo(Screen.CONTACTS)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                enabled =
                    name.value.trim().isNotEmpty() &&
                    email.value.trim().isNotEmpty() &&
                    telefone.value.trim().isNotEmpty() &&
                    nascimento.value.trim().isNotEmpty() &&
                    cep.value.length == 8 &&
                    numero.value.trim().isNotEmpty() &&
                    viewModel.cepError.value == null &&
                    !viewModel.isLoadingCep.value
            ) {
                Text("Salvar alterações")
            }

            OutlinedButton(
                onClick = { showDeleteDialog.value = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6B6B))
            ) {
                Text("Excluir contato")
            }
        }
    }
}
