package org.example.model;

import java.util.List;

public class Fornecedor {
    private String nome;
    private String cnpj;
    private Endereco endereco;
    private Integer numeroEndereco;
    private String complementoEndereco;
    private Double saldoAPagar;
    private List<String> listaEmails;
    private List<String> listaTelefones;

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

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getCnpj() {return cnpj;}
    public void setCnpj(String cnpj) {this.cnpj = cnpj;}
    public Endereco getEndereco() {return endereco;}
    public void setEndereco(Endereco endereco) {this.endereco = endereco;}
    public Integer getNumeroEndereco() {return numeroEndereco;}
    public void setNumeroEndereco(Integer numeroEndereco) {this.numeroEndereco = numeroEndereco;}
    public String getComplementoEndereco() {return complementoEndereco;}
    public void setComplementoEndereco(String complementoEndereco) {this.complementoEndereco = complementoEndereco;}
    public double getSaldoAPagar() {return saldoAPagar;}
    public void setSaldoAPagar(double saldoAPagar) {this.saldoAPagar = saldoAPagar;}
    public void setSaldoAPagar(Double saldoAPagar) {this.saldoAPagar = saldoAPagar;}
    public List<String> getListaTelefones() {return listaTelefones;}
    public void setListaTelefones(List<String> listaTelefones) {this.listaTelefones = listaTelefones;}
    public List<String> getListaEmails() {return listaEmails;}
    public void setListaEmails(List<String> listaEmails) {this.listaEmails = listaEmails;}
}
