package br.com.MeuPlanner.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.MeuPlanner.model.Conta;

class ContaServiceTest {

    private final ContaService contaService = new ContaService();

    @Test
    void rejeitaNomeVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> contaService.criarConta("", Conta.TipoConta.CORRENTE, "Nubank", BigDecimal.TEN));
    }

    @Test
    void rejeitaNomeNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> contaService.criarConta(null, Conta.TipoConta.CORRENTE, "Nubank", BigDecimal.TEN));
    }

    @Test
    void rejeitaSaldoInicialNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> contaService.criarConta("Carteira", Conta.TipoConta.CARTEIRA, "Inter", new BigDecimal("-1")));
    }

    @Test
    void rejeitaAtualizacaoDeContaSemId() {
        Conta conta = new Conta("Poupança", Conta.TipoConta.POUPANCA, "Bradesco", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> contaService.atualizarConta(conta));
    }
}
