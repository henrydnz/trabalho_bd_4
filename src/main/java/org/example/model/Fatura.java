package org.example.model;

public class Fatura {
    private Integer numero;
    private Fornecedor fornecedor;
    private String nomeMotivo;
    private String dataLancamento;
    private String dataVencimento;
    private Double valor;

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getNomeMotivo() {
        return nomeMotivo;
    }

    public void setNomeMotivo(String nomeMotivo) {
        this.nomeMotivo = nomeMotivo;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
