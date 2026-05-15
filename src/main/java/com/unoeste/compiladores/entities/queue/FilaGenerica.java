package com.unoeste.compiladores.entities.queue;

public class FilaGenerica<T> {
    private NoFilaGenerica<T> ini;
    private NoFilaGenerica<T> fim;

    public FilaGenerica() {
        inicializar();
    }

    public void inicializar() {
        ini = null;
        fim = null;
    }

    // Insere no fim da fila
    public void enqueue(T elemento) {
        NoFilaGenerica<T> novo = new NoFilaGenerica<>(elemento, null);
        if (isEmpty()) {
            ini = novo;
            fim = novo;
        } else {
            fim.setProx(novo);
            fim = novo;
        }
    }

    // Remove do início da fila
    public NoFilaGenerica<T> dequeue() {
        if (isEmpty()) {
            return null; // ou lançar exceção
        }
        NoFilaGenerica<T> removido = ini;
        ini = ini.getProx();
        if (ini == null) { // se a fila ficou vazia
            fim = null;
        }
        removido.setProx(null); // boa prática: desconectar
        return removido;
    }

    public NoFilaGenerica<T> front() {
        return ini;
    }

    public boolean isEmpty() {
        return ini == null;
    }

}
