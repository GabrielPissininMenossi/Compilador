package com.unoeste.compiladores.entities;

public class Terminal extends Elemento{
    private String nome;

    public Terminal(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
