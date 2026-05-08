package com.example.gerenciadorcontatos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    val name: String,
    val initials: String,
    val email: String = "",
    val telefone: String = "",
    val nascimento: String = "",
    val cep: String = "",
    val bairro: String = "",
    val logradouro: String = "",
    val estado: String = "",
    val cidade: String = "",
    val numero: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
