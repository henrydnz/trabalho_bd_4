package org.example.model;

import java.time.LocalDate;

public class Fatura {
    private final Integer numero;
    private final Fornecedor fornecedor;
    private final String nomeMotivo;
    private final LocalDate dataLancamento;
    private final LocalDate dataVencimento;
    private final Double valor;

    public Fatura(Integer numero, Fornecedor fornecedor, String nomeMotivo, LocalDate dataLancamento, LocalDate dataVencimento, Double valor) {
        this.numero = numero;
        this.fornecedor = fornecedor;
        this.nomeMotivo = nomeMotivo;
        this.dataLancamento = dataLancamento;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
    }

    public Integer getNumero() {return numero;}
    public Fornecedor getFornecedor() {return fornecedor;}
    public String getNomeMotivo() {return nomeMotivo;}
    public LocalDate getDataLancamento() {return dataLancamento;}
    public LocalDate getDataVencimento() {return dataVencimento;}
    public Double getValor() {return valor;}
}
