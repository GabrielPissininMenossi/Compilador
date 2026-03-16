package com.unoeste.compiladores.entities;

import java.util.List;

public class NaoTerminal extends Elemento
{
    private String nome;
    private List<Elemento> producoes;
    private List<String> first;
    private List<String> follow;

    public NaoTerminal(String nome, List<Elemento> producoes) {
        this.nome = nome;
        this.producoes = producoes;
        this.first = first;
        this.follow = follow;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Elemento> getProducoes() {
        return producoes;
    }

    public void setProducoes(List<Elemento> producoes) {
        this.producoes = producoes;
    }

    public List<String> getFirst() {
        return first;
    }

    public void setFirst(List<String> first) {
        this.first = first;
    }

    public List<String> getFollow() {
        return follow;
    }

    public void setFollow(List<String> follow) {
        this.follow = follow;
    }

    private void calcularFirst()
    {

    }
    private void calcularFollow()
    {

    }
}
