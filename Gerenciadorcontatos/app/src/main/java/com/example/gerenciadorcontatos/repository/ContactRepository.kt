package com.example.gerenciadorcontatos.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.gerenciadorcontatos.database.ContactDao
import com.example.gerenciadorcontatos.model.Contact
import com.example.gerenciadorcontatos.model.ViaCepAddress
import com.example.gerenciadorcontatos.network.RetrofitInstance

class ContactRepository(private val contactDao: ContactDao) {
    val contacts = mutableStateListOf<Contact>()

    suspend fun loadAllContacts() {
        val allContacts = contactDao.getAllContactsList()
        contacts.clear()
        contacts.addAll(allContacts)
    }

    suspend fun add(contact: Contact) {
        contactDao.insertContact(contact)
        loadAllContacts()
    }

    suspend fun update(index: Int, contact: Contact) {
        if (index in contacts.indices) {
            val updated = contacts[index].copy(
                name = contact.name,
                initials = contact.initials,
                email = contact.email,
                telefone = contact.telefone,
                nascimento = contact.nascimento,
                cep = contact.cep,
                bairro = contact.bairro,
                logradouro = contact.logradouro,
                estado = contact.estado,
                cidade = contact.cidade,
                numero = contact.numero
            )
            contactDao.updateContact(updated)
            loadAllContacts()
        }
    }

    suspend fun delete(index: Int) {
        if (index in contacts.indices) {
            contactDao.deleteContact(contacts[index])
            loadAllContacts()
        }
    }

    suspend fun fetchAddress(cep: String): ViaCepAddress? = try {
        val response = RetrofitInstance.api.getAddress(cep)
        if (response.erro != null && response.erro != false) null
        else ViaCepAddress(
            bairro = response.bairro.orEmpty(),
            logradouro = response.logradouro.orEmpty(),
            estado = response.uf.orEmpty(),
            cidade = response.localidade.orEmpty()
        )
    } catch (e: Exception) {
        null
    }
}
