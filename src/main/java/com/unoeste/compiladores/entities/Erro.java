package com.unoeste.compiladores.entities;

public class Erro
{
    private String mensagem;
    private int linha;
    private int coluna;

    public Erro(String mensagem, int linha, int coluna) {

        this.mensagem = mensagem;
        this.linha = linha;
        this.coluna = coluna;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

}
