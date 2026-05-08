package com.example.gerenciadorcontatos.database

import com.example.gerenciadorcontatos.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {
    suspend fun initializeDatabase(database: ContactDatabase) {
        withContext(Dispatchers.IO) {
            try {
                val dao = database.contactDao()
                // Verifica se existe algum contato no banco
                val contactList = dao.getAllContactsList()
                
                // Se não existem contatos, adiciona dados iniciais
                if (contactList.isEmpty()) {
                    val initialContacts = listOf(
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
                    initialContacts.forEach { contact ->
                        dao.insertContact(contact)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
