package com.example.gerenciadorcontatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.gerenciadorcontatos.database.ContactDatabase
import com.example.gerenciadorcontatos.model.Contact
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
        
        lifecycleScope.launch {
            val dao = database.contactDao()
            if (dao.getAllContactsList().isEmpty()) {
                listOf(
                    Contact("Rafael Alexandre", "RA"),
                    Contact("Aldair Reis", "AR"),
                    Contact("Amanda Lima", "AL"),
                    Contact("Ana Silva", "AS"),
                    Contact("Bruno Costa", "BC"),
                    Contact("Beatriz Santos", "BS"),
                    Contact("Carlos Mendes", "CM"),
                    Contact("Camila Oliveira", "CO"),
                    Contact("Daniel Ferreira", "DF"),
                    Contact("Fernanda Rocha", "FR"),
                    Contact("Gabriel Souza", "GS")
                ).forEach { dao.insertContact(it) }
            }
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
