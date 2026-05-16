package com.unoeste.compiladores.entities.stack;

public class NoPilhaGenerico<T> {
    private T info; // O dado agora é do tipo genérico T
    private NoPilhaGenerico<T> prox;

    public NoPilhaGenerico(T info, NoPilhaGenerico<T> prox) {
        this.info = info;
        this.prox = prox;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public NoPilhaGenerico<T> getProx() {
        return prox;
    }

    public void setProx(NoPilhaGenerico<T> prox) {
        this.prox = prox;
    }
}