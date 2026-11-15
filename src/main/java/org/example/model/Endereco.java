package org.example.model;

public class Endereco {
    private String estado;
    private String tipoLogradouro;
    private String cidade;
    private String rua;
    private String bairro;

    public Endereco(String estado, String tipoLogradouro, String cidade, String rua, String bairro) {
        this.estado = estado;
        this.tipoLogradouro = tipoLogradouro;
        this.cidade = cidade;
        this.rua = rua;
        this.bairro = bairro;
    }

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}
    public String getTipoLogradouro() {return tipoLogradouro;}
    public void setTipoLogradouro(String tipoLogradouro) {this.tipoLogradouro = tipoLogradouro;}
    public String getCidade() {return cidade;}
    public void setCidade(String cidade) {this.cidade = cidade;}
    public String getRua() {return rua;}
    public void setRua(String rua) {this.rua = rua;}
    public String getBairro() {return bairro;}
    public void setBairro(String bairro) {this.bairro = bairro;}
}
