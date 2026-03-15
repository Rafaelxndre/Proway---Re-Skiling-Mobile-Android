package com.example.projeto_proway_01

import java.util.Scanner

fun main(args: Array<String>) {
    SistemaBancario.iniciar(args)
}

class SistemaBancario(
    var numeroConta: String,
    var titular: String,
    var saldo: Double
) {

    fun atualizarDados(novoTitular: String, novoSaldo: Double) {
        titular = novoTitular
        saldo = novoSaldo
    }

    companion object {
        private val contas = mutableListOf<SistemaBancario>()
        private val scanner = Scanner(System.`in`)
        private const val LIMITE_CONTAS = 10

        fun iniciar(args: Array<String>) {
            var opcao: Int

            do {
                exibirMenu()
                opcao = lerInteiro("Escolha uma opcao: ")

                when (opcao) {
                    1 -> cadastrar()
                    2 -> listar()
                    3 -> pesquisar()
                    4 -> alterar()
                    5 -> remover()
                    6 -> println("Sistema finalizado.")
                    else -> println("Opcao invalida. Tente novamente.")
                }
            } while (opcao != 6)
        }

        private fun exibirMenu() {
            println("\n╔═══════════════════════════════════════════╗")
            println("║       BANCO PROWAY - MENU PRINCIPAL       ║")
            println("╠═══════════════════════════════════════════╣")
            println("║ Contas cadastradas | ${contas.size.toString().padStart(2, '0')}/$LIMITE_CONTAS                ║")
            println("╠═══════════════════════════════════════════╣")
            println("║ 1 - Cadastrar conta                       ║")
            println("║ 2 - Listar contas                         ║")
            println("║ 3 - Pesquisar conta                       ║")
            println("║ 4 - Alterar conta                         ║")
            println("║ 5 - Remover conta                         ║")
            println("║ 6 - Finalizar sistema                     ║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun cadastrar() {
            imprimirTitulo("CADASTRAR CONTA")

            if (contas.size >= LIMITE_CONTAS) {
                imprimirAviso("Limite de $LIMITE_CONTAS contas atingido. Nao e possivel cadastrar novas contas.")
                return
            }

            val numero = lerNumeroConta("Numero da conta: ")

            if (contas.any { it.numeroConta == numero }) {
                imprimirErro("Ja existe uma conta com esse numero.")
                return
            }

            val titular = lerTitular("Nome do titular: ")
            val saldo = lerSaldo("Saldo inicial: ")

            contas.add(SistemaBancario(numero, titular, saldo))
            imprimirSucesso("Conta cadastrada com sucesso.")
        }

        private fun listar() {
            imprimirTitulo("LISTAR CONTAS")

            if (contas.isEmpty()) {
                imprimirAviso("Nenhuma conta cadastrada.")
                return
            }

            println("╔═══════════════════════════════════════════╗")
            println("║           CONTAS CADASTRADAS              ║")
            println("╠═══════════════════════════════════════════╣")

            contas.forEachIndexed { indice, conta ->
                println("║ ${indice + 1}. Conta ${conta.numeroConta.padEnd(10, ' ')} Titular ${conta.titular.padEnd(14, ' ')}║")
                println("║   Saldo: R$ ${"%.2f".format(conta.saldo).padEnd(30, ' ')}║")
                if (indice != contas.lastIndex) {
                    println("╠═══════════════════════════════════════════╣")
                }
            }

            println("╚═══════════════════════════════════════════╝")
        }

        private fun pesquisar() {
            imprimirTitulo("PESQUISAR CONTA")
            val numero = lerNumeroConta("Informe o numero da conta: ")
            val conta = contas.find { it.numeroConta == numero }

            conta?.let {
                imprimirConta("CONTA ENCONTRADA", it)
            } ?: run {
                imprimirErro("Conta nao encontrada.")
            }
        }

        private fun alterar() {
            imprimirTitulo("ALTERAR CONTA")
            val numero = lerNumeroConta("Informe o numero da conta para alterar: ")
            val conta = contas.find { it.numeroConta == numero }

            if (conta == null) {
                imprimirErro("Conta nao encontrada.")
                return
            }

            val novoTitular = lerTitular("Novo nome do titular: ")
            val novoSaldo = lerSaldo("Novo saldo: ")

            conta.atualizarDados(novoTitular, novoSaldo)

            imprimirSucesso("Conta alterada com sucesso.")
            imprimirConta("DADOS ATUALIZADOS", conta)
        }

        private fun remover() {
            imprimirTitulo("REMOVER CONTA")
            val numero = lerNumeroConta("Informe o numero da conta para remover: ")
            val removida = contas.removeIf { it.numeroConta == numero }

            if (removida) {
                imprimirSucesso("Conta removida com sucesso.")
            } else {
                imprimirErro("Conta nao encontrada.")
            }
        }

        private fun imprimirTitulo(titulo: String) {
            println("\n╔═══════════════════════════════════════════╗")
            println("║ ${titulo.padStart((titulo.length + 35) / 2, ' ').padEnd(41, ' ')} ║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun imprimirSucesso(mensagem: String) {
            println("╔═══════════════════════════════════════════╗")
            println("║ SUCESSO: ${mensagem.padEnd(31, ' ')}  ║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun imprimirErro(mensagem: String) {
            println("╔═══════════════════════════════════════════╗")
            println("║ ERRO: ${mensagem.padEnd(34, ' ')}║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun imprimirAviso(mensagem: String) {
            println("╔═══════════════════════════════════════════╗")
            println("║ AVISO: ${mensagem.padEnd(33, ' ')}║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun imprimirConta(titulo: String, conta: SistemaBancario) {
            println("╔═══════════════════════════════════════════╗")
            println("║ ${titulo.padStart((titulo.length + 35) / 2, ' ').padEnd(41, ' ')} ║")
            println("╠═══════════════════════════════════════════╣")
            println("║ Conta  : ${conta.numeroConta.padEnd(31, ' ')}  ║")
            println("║ Titular: ${conta.titular.padEnd(31, ' ')}  ║")
            println("║ Saldo  : R$ ${"%.2f".format(conta.saldo).padEnd(27, ' ')}   ║")
            println("╚═══════════════════════════════════════════╝")
        }

        private fun lerInteiro(mensagem: String): Int {
            while (true) {
                print(mensagem)
                val entrada = scanner.nextLine()
                val valor = entrada?.trim()?.toIntOrNull()
                if (valor != null) return valor
                println("Valor invalido. Digite um numero inteiro.")
            }
        }

        private fun lerDouble(mensagem: String): Double {
            while (true) {
                print(mensagem)
                val entrada = scanner.nextLine()
                val valor = entrada?.trim()?.replace(",", ".")?.toDoubleOrNull() ?: Double.NaN
                if (!valor.isNaN()) return valor
                println("Valor invalido. Digite um numero decimal.")
            }
        }

        private fun lerTexto(mensagem: String): String {
            while (true) {
                print(mensagem)
                val valor = scanner.nextLine().trim()
                if (valor.isNotEmpty()) return valor
                println("Texto invalido. Tente novamente.")
            }
        }

        private fun lerNumeroConta(mensagem: String): String {
            while (true) {
                val numero = lerTexto(mensagem)
                val somenteDigitos = numero.all { it.isDigit() }
                val tamanhoValido = numero.length in 4..10

                if (somenteDigitos && tamanhoValido) return numero
                println("Numero invalido. Use apenas digitos com tamanho entre 4 e 10 (zeros a esquerda sao permitidos).")
            }
        }

        private fun lerSaldo(mensagem: String): Double {
            while (true) {
                val saldo = lerDouble(mensagem)
                if (saldo >= 0.0) return saldo
                println("Saldo nao pode ser negativo.")
            }
        }

        private fun lerTitular(mensagem: String): String {
            while (true) {
                val titular = lerTexto(mensagem)
                val nomeValido = titular.length >= 3 && titular.all { it.isLetter() || it.isWhitespace() }

                if (nomeValido) return titular
                println("Titular invalido. Informe ao menos 3 letras e use apenas letras/espacos.")
            }
        }
    }
}