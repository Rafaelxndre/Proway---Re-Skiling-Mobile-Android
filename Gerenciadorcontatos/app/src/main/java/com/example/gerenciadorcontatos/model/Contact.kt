package com.example.gerenciadorcontatos.model

data class Contact(
    val name: String,
    val description: String = "",
    val initials: String,
    val email: String = "",
    val telefone: String = "",
    val nascimento: String = "",
    val cep: String = "",
    val bairro: String = "",
    val logradouro: String = "",
    val estado: String = "",
    val cidade: String = "",
    val numero: String = ""
)
