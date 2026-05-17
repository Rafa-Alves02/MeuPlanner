package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.util.List;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.repository.ContaRepository;

public class ContaService {

    private final ContaRepository contaRepo = new ContaRepository();

    public Conta criarConta(String nome, Conta.TipoConta tipo, BigDecimal saldoInicial) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome da conta não pode ser vazio!");
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo!");

        Conta conta = new Conta(nome, tipo, saldoInicial);
        contaRepo.salvar(conta);
        return conta;
    }

    public void atualizarConta(Conta conta) {
        if (conta.getId() == null)
            throw new IllegalArgumentException("Conta sem ID não pode ser atualizada!");
        contaRepo.atualizar(conta);
    }

    public void deletarConta(Long id) {
        contaRepo.deletar(id);
    }

    public List<Conta> listarContas() {
        return contaRepo.listarTodas();
    }

    public Conta buscarPorId(Long id) {
        return contaRepo.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada: " + id));
    }

    public BigDecimal saldoTotalGeral() {
        return contaRepo.listarTodas().stream()
                .map(Conta::getSaldoAtual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}