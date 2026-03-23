package com.unoeste.compiladores.entities;

public class NaoTerminal extends Elemento
{
    private String nome;

    public NaoTerminal(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


}
