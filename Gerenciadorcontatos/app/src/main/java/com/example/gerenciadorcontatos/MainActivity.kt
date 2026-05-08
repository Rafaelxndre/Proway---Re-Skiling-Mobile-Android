package com.example.gerenciadorcontatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.gerenciadorcontatos.database.ContactDatabase
import com.example.gerenciadorcontatos.database.DatabaseInitializer
import com.example.gerenciadorcontatos.repository.ContactRepository
import com.example.gerenciadorcontatos.ui.screens.ContactsListScreen
import com.example.gerenciadorcontatos.ui.screens.CreateContactScreen
import com.example.gerenciadorcontatos.ui.screens.EditContactScreen
import com.example.gerenciadorcontatos.ui.theme.GerenciadorContatosTheme
import com.example.gerenciadorcontatos.viewmodel.ContactViewModel
import com.example.gerenciadorcontatos.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = ContactDatabase.getDatabase(this)
        val repository = ContactRepository(database.contactDao())
        val viewModel = ContactViewModel(repository)
        
        // Inicializa banco e carrega contatos
        lifecycleScope.launch {
            DatabaseInitializer.initializeDatabase(database)
            repository.loadAllContacts()
        }
        
        setContent {
            GerenciadorContatosTheme {
                when (viewModel.currentScreen.value) {
                    Screen.CONTACTS -> ContactsListScreen(viewModel)
                    Screen.CREATE_CONTACT -> CreateContactScreen(viewModel)
                    Screen.EDIT_CONTACT -> EditContactScreen(viewModel)
                }
            }
        }
    }
}
