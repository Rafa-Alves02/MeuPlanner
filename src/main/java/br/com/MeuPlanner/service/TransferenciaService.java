package br.com.MeuPlanner.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.MeuPlanner.model.Conta;
import br.com.MeuPlanner.model.Transferencia;
import br.com.MeuPlanner.repository.TransferenciaRepository;

public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepo = new TransferenciaRepository();

    public Transferencia transferir(Conta origem, Conta destino,
                                    BigDecimal valor, String descricao) {
        if (origem.getId().equals(destino.getId()))
            throw new IllegalArgumentException("Conta de origem e destino não podem ser iguais!");
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor deve ser maior que zero!");
        if (origem.getSaldoAtual().compareTo(valor) < 0)
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem!");

        Transferencia transferencia = new Transferencia(origem, destino, valor, LocalDate.now(), descricao);
        transferenciaRepo.salvar(transferencia);
        return transferencia;
    }

    public List<Transferencia> listarPorConta(Long contaId) {
        return transferenciaRepo.listarPorConta(contaId);
    }
}