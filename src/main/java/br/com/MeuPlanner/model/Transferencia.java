package br.com.MeuPlanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transferencia {

    private Long id;
    private Conta contaOrigem;
    private Conta contaDestino;
    private BigDecimal valor;
    private LocalDate dataTransferencia;
    private String descricao;

    public Transferencia() {}

    public Transferencia(Conta contaOrigem, Conta contaDestino,
                         BigDecimal valor, LocalDate dataTransferencia, String descricao) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.dataTransferencia = dataTransferencia;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Conta getContaOrigem() { return contaOrigem; }
    public void setContaOrigem(Conta contaOrigem) { this.contaOrigem = contaOrigem; }

    public Conta getContaDestino() { return contaDestino; }
    public void setContaDestino(Conta contaDestino) { this.contaDestino = contaDestino; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataTransferencia() { return dataTransferencia; }
    public void setDataTransferencia(LocalDate dataTransferencia) { this.dataTransferencia = dataTransferencia; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}