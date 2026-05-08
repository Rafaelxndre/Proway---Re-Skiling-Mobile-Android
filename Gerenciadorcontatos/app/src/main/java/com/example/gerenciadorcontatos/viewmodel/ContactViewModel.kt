package com.example.gerenciadorcontatos.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciadorcontatos.model.Contact
import com.example.gerenciadorcontatos.model.ViaCepAddress
import com.example.gerenciadorcontatos.repository.ContactRepository
import kotlinx.coroutines.launch

enum class Screen { CONTACTS, CREATE_CONTACT, EDIT_CONTACT }

class ContactViewModel(
    private val repository: ContactRepository = ContactRepository()
) : ViewModel() {

    val contacts get() = repository.contacts

    val currentScreen = mutableStateOf(Screen.CONTACTS)
    val searchQuery = mutableStateOf("")
    val selectedContact = mutableStateOf<Contact?>(null)
    val selectedContactIndex = mutableStateOf(-1)

    val addressResult = mutableStateOf<ViaCepAddress?>(null)
    val isLoadingCep = mutableStateOf(false)
    val cepError = mutableStateOf<String?>(null)

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun selectContact(contact: Contact, index: Int) {
        selectedContact.value = contact
        selectedContactIndex.value = index
    }

    fun addContact(
        name: String, email: String, telefone: String, nascimento: String,
        cep: String, bairro: String, logradouro: String, estado: String,
        cidade: String, numero: String
    ) {
        repository.add(
            Contact(
                name = name, initials = buildInitials(name),
                email = email, telefone = telefone, nascimento = nascimento,
                cep = cep, bairro = bairro, logradouro = logradouro,
                estado = estado, cidade = cidade, numero = numero
            )
        )
    }

    fun updateContact(
        name: String, email: String, telefone: String, nascimento: String,
        cep: String, bairro: String, logradouro: String, estado: String,
        cidade: String, numero: String
    ) {
        val base = selectedContact.value ?: return
        repository.update(
            selectedContactIndex.value,
            base.copy(
                name = name, initials = buildInitials(name),
                email = email, telefone = telefone, nascimento = nascimento,
                cep = cep, bairro = bairro, logradouro = logradouro,
                estado = estado, cidade = cidade, numero = numero
            )
        )
    }

    fun deleteContact() {
        repository.delete(selectedContactIndex.value)
        selectedContact.value = null
        selectedContactIndex.value = -1
    }

    fun fetchAddress(cep: String) {
        viewModelScope.launch {
            isLoadingCep.value = true
            cepError.value = null
            val result = repository.fetchAddress(cep)
            isLoadingCep.value = false
            if (result != null) {
                addressResult.value = result
            } else {
                addressResult.value = null
                cepError.value = "CEP não encontrado"
            }
        }
    }

    fun resetCepState() {
        addressResult.value = null
        isLoadingCep.value = false
        cepError.value = null
    }

    private fun buildInitials(name: String): String {
        val words = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            words.isEmpty() -> "?"
            words.size == 1 -> words[0].take(2).uppercase()
            else -> "${words[0].first()}${words[1].first()}".uppercase()
        }
    }
}
