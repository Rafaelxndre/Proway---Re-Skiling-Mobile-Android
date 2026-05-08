package com.example.gerenciadorcontatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gerenciadorcontatos.ui.screens.ContactsListScreen
import com.example.gerenciadorcontatos.ui.screens.CreateContactScreen
import com.example.gerenciadorcontatos.ui.screens.EditContactScreen
import com.example.gerenciadorcontatos.ui.theme.GerenciadorContatosTheme
import com.example.gerenciadorcontatos.viewmodel.ContactViewModel
import com.example.gerenciadorcontatos.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ContactViewModel()
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
