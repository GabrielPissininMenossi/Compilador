package com.unoeste.compiladores.entities;

public class NoPilha
{
    private String string;
    private NoPilha prox;

    public NoPilha(String string, NoPilha prox) {
        this.string = string;
        this.prox = prox;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public NoPilha getProx() {
        return prox;
    }

    public void setProx(NoPilha prox) {
        this.prox = prox;
    }
}
