package org.example.model;

import java.util.List;

public class Fornecedor {
    private int id;
    private final String nome;
    private final String cnpj;
    private final Endereco endereco;
    private final Integer numeroEndereco;
    private final String complementoEndereco;
    private final Double saldoAPagar;
    private final List<String> listaEmails;
    private final List<String> listaTelefones;

    public Fornecedor(List<String> listaTelefones, List<String> listaEmails, Double saldoAPagar, String complementoEndereco, Integer numeroEndereco, Endereco endereco, String cnpj, String nome) {
        this.listaTelefones = listaTelefones;
        this.listaEmails = listaEmails;
        this.saldoAPagar = saldoAPagar;
        this.complementoEndereco = complementoEndereco;
        this.numeroEndereco = numeroEndereco;
        this.endereco = endereco;
        this.cnpj = cnpj;
        this.nome = nome;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNome() {return nome;}
    public String getCnpj() {return cnpj;}
    public Endereco getEndereco() {return endereco;}
    public Integer getNumeroEndereco() {return numeroEndereco;}
    public String getComplementoEndereco() {return complementoEndereco;}
    public double getSaldoAPagar() {return saldoAPagar;}
    public List<String> getListaTelefones() {return listaTelefones;}
    public List<String> getListaEmails() {return listaEmails;}
}
