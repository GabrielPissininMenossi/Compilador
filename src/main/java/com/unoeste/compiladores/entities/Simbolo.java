package com.unoeste.compiladores.entities;

public class Simbolo {
    private Token token;
    private String tipo;
    private String valor;

    public Simbolo(Token token, String tipo, String valor) {
        this.token = token;
        this.tipo = tipo;
        this.valor = valor;
    }

    public Simbolo() {
        this(null,"","");
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
