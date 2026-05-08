package com.example.gerenciadorcontatos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciadorcontatos.ui.components.ContactItemWithDivider
import com.example.gerenciadorcontatos.ui.components.SearchBar
import com.example.gerenciadorcontatos.viewmodel.ContactViewModel
import com.example.gerenciadorcontatos.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsListScreen(viewModel: ContactViewModel) {
    val filteredContacts by remember {
        derivedStateOf {
            if (viewModel.searchQuery.value.isEmpty()) {
                viewModel.contacts.toList()
            } else {
                viewModel.contacts.filter {
                    it.name.contains(viewModel.searchQuery.value, ignoreCase = true)
                }
            }
        }
    }

    val groupedContacts by remember {
        derivedStateOf {
            filteredContacts
                .groupBy { it.name.first().uppercaseChar() }
                .toSortedMap()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Contatos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.resetCepState()
                        viewModel.navigateTo(Screen.CREATE_CONTACT)
                    }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Adicionar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
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
        ) {
            SearchBar(
                searchQuery = viewModel.searchQuery.value,
                onSearchChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                groupedContacts.forEach { (letter, contactsForLetter) ->
                    item {
                        Column {
                            Text(
                                letter.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF808080),
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
                            )
                            HorizontalDivider(
                                color = Color(0xFF2A2A2A),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    items(contactsForLetter.size) { index ->
                        val contact = contactsForLetter[index]
                        ContactItemWithDivider(
                            contact = contact,
                            showDivider = index < contactsForLetter.size - 1,
                            onClick = {
                                val globalIndex = viewModel.contacts.indexOf(contact)
                                viewModel.selectContact(contact, globalIndex)
                                viewModel.resetCepState()
                                viewModel.navigateTo(Screen.EDIT_CONTACT)
                            }
                        )
                    }
                }
            }
        }
    }
}
