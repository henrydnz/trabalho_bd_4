package org.example.model;

public class Endereco {
    private final String estado;
    private final String siglaUF;
    private final String tipoLogradouro;
    private final String cidade;
    private final String rua;
    private final String bairro;
    private final String cep;


    public Endereco(String estado, String siglaUF, String tipoLogradouro, String cidade, String rua, String bairro, String cep) {
        this.estado = estado;
        this.siglaUF = siglaUF;
        this.tipoLogradouro = tipoLogradouro;
        this.cidade = cidade;
        this.rua = rua;
        this.bairro = bairro;
        this.cep = cep;
    }

    public String getEstado() {return estado;}
    public String getSiglaUF() {return siglaUF;}
    public String getTipoLogradouro() {return tipoLogradouro;}
    public String getCidade() {return cidade;}
    public String getRua() {return rua;}
    public String getBairro() {return bairro;}
    public String getCep() {return cep;}
}
