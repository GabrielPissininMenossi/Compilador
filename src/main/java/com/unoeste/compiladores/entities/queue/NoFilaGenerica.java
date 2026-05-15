package com.unoeste.compiladores.entities.queue;

public class NoFilaGenerica<T> {
    private T info;
    private NoFilaGenerica<T> prox;

    public NoFilaGenerica(T info, NoFilaGenerica<T> prox) {
        this.info = info;
        this.prox = prox;
    }

    public NoFilaGenerica() {
        this(null, null);
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public NoFilaGenerica<T> getProx() {
        return prox;
    }

    public void setProx(NoFilaGenerica<T> prox) {
        this.prox = prox;
    }
}
