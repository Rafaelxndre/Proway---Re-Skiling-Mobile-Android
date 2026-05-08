package com.example.gerenciadorcontatos.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.gerenciadorcontatos.model.Contact
import com.example.gerenciadorcontatos.model.ViaCepAddress
import com.example.gerenciadorcontatos.network.RetrofitInstance

class ContactRepository {

    val contacts = mutableStateListOf(
        Contact("Rafael Alexandre", "", "RA"),
        Contact("Aldair Reis", "", "AR"),
        Contact("Amanda Lima", "", "AL"),
        Contact("Carol", "", "AM"),
        Contact("Ana", "", "AN"),
        Contact("Bruno Silva", "", "BS"),
        Contact("Beatriz Santos", "", "BS"),
        Contact("Carlos Mendes", "", "CM"),
        Contact("Camila Costa", "", "CC"),
        Contact("Fernando Oliveira", "", "FO"),
        Contact("Fernanda Rocha", "", "FR")
    )

    fun add(contact: Contact) {
        contacts.add(contact)
    }

    fun update(index: Int, contact: Contact) {
        if (index in contacts.indices) contacts[index] = contact
    }

    fun delete(index: Int) {
        if (index in contacts.indices) contacts.removeAt(index)
    }

    suspend fun fetchAddress(cep: String): ViaCepAddress? = runCatching {
        val response = RetrofitInstance.api.getAddress(cep)
        if (response.erro != null) {
            null
        } else {
            ViaCepAddress(
                bairro = response.bairro.orEmpty(),
                logradouro = response.logradouro.orEmpty(),
                estado = response.uf.orEmpty(),
                cidade = response.localidade.orEmpty()
            )
        }
    }.getOrNull()
}
