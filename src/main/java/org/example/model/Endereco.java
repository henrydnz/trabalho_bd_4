package org.example.model;

public class Endereco {
    private String estado;
    private String siglaUF;
    private String tipoLogradouro;
    private String cidade;
    private String rua;
    private String bairro;
    private String cep;

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
    public void setEstado(String estado) {this.estado = estado;}\

    public String getSiglaUF() {
        return siglaUF;
    }

    public void setSiglaUF(String siglaUF) {
        this.siglaUF = siglaUF;
    }

    public String getTipoLogradouro() {return tipoLogradouro;}
    public void setTipoLogradouro(String tipoLogradouro) {this.tipoLogradouro = tipoLogradouro;}
    public String getCidade() {return cidade;}
    public void setCidade(String cidade) {this.cidade = cidade;}
    public String getRua() {return rua;}
    public void setRua(String rua) {this.rua = rua;}
    public String getBairro() {return bairro;}

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setBairro(String bairro) {this.bairro = bairro;

    }
}
