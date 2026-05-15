package com.unoeste.compiladores.entities.stacks;

public class PilhaGenerica<T> {
    private NoPilhaGenerico<T> topo;

    public PilhaGenerica() {
        inicializar();
    }

    public void inicializar() {
        topo = null;
    }

    public void push(T elemento) {
        // Cria o novo nó apontando para o antigo topo
        NoPilhaGenerico<T> novoNo = new NoPilhaGenerico<>(elemento, topo);
        topo = novoNo;
    }

    public NoPilhaGenerico<T> pop() {
        if (isEmpty()) {
            return null;
        }
        NoPilhaGenerico<T> aux = topo;
        topo = topo.getProx();

        return aux;
    }

    public NoPilhaGenerico<T> top() {
        return topo;
    }

    public boolean isEmpty() {
        return topo == null;
    }
}