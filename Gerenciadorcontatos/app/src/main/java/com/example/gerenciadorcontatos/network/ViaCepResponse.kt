package com.example.gerenciadorcontatos.network

// DTO for ViaCEP API response.
// The "erro" field is only present when the CEP is not found.
// It may be a JSON boolean or a JSON string depending on the API version,
// so it is typed as Any? to safely handle both cases.
data class ViaCepResponse(
    val logradouro: String?,
    val bairro: String?,
    val localidade: String?,
    val uf: String?,
    val erro: Any? = null
)
