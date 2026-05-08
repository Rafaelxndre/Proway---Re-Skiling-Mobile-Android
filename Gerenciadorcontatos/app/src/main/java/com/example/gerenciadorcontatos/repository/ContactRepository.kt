package com.example.gerenciadorcontatos.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.gerenciadorcontatos.database.ContactDao
import com.example.gerenciadorcontatos.model.Contact
import com.example.gerenciadorcontatos.model.ViaCepAddress
import com.example.gerenciadorcontatos.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {

    val contacts = mutableStateListOf<Contact>()
    private val TAG = "ContactRepository"
    
    fun getContactsFlow(): Flow<List<Contact>> = contactDao.getAllContacts()

    suspend fun loadAllContacts() {
        try {
            val allContacts = contactDao.getAllContactsList()
            contacts.clear()
            contacts.addAll(allContacts)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar contatos", e)
        }
    }

    suspend fun add(contact: Contact) {
        contactDao.insertContact(contact)
        loadAllContacts()
    }

    suspend fun update(index: Int, contact: Contact) {
        if (index in contacts.indices) {
            val contactToUpdate = contacts[index].copy(
                name = contact.name,
                description = contact.description,
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
            contactDao.updateContact(contactToUpdate)
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
        Log.d(TAG, "Buscando CEP: $cep")
        val response = RetrofitInstance.api.getAddress(cep)
        Log.d(TAG, "Response: $response")
        Log.d(TAG, "Erro flag: ${response.erro}")
        
        if (response.erro != null && response.erro != false) {
            Log.w(TAG, "CEP não encontrado: $cep")
            null
        } else {
            Log.d(TAG, "CEP encontrado com sucesso")
            ViaCepAddress(
                bairro = response.bairro.orEmpty(),
                logradouro = response.logradouro.orEmpty(),
                estado = response.uf.orEmpty(),
                cidade = response.localidade.orEmpty()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "Erro ao buscar CEP: $cep", e)
        null
    }
}
