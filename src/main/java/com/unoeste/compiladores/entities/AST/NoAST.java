package com.unoeste.compiladores.entities.AST;

import java.util.ArrayList;
import java.util.List;

public class NoAST {
    // Explicação da estrutura utilizada
    /*
     * Se While
     *      [0] expressão lógica
     *      [1] bloco
     * */

    /*
     * Se If
     *      [0] expressão lógica
     *      [1] bloco if --> true/verdade
     *      [2] (se tiver) bloco else --> false/falso
     * */

    /*
     * Se operador (+, -, *, /)
     *      [0] operador da esquerda
     *      [1] operador da direita
     * */

    /*
     * Se for um nó folha (identicador, número)
     *      Se valor == String --> identificador
     *      Se valor == Numero --> numero
     * */
    private String valor;
    private List<NoAST> filhos;

    public NoAST(String valor, List<NoAST> filhos) {
        this.valor = valor;
        this.filhos = filhos;
    }

    public NoAST(String valor)
    {
        this.valor = valor;
        this.filhos = new ArrayList<>();
    }

    public NoAST() {
        this("");
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public List<NoAST> getFilhos() {
        return filhos;
    }

    public NoAST getFilhoAt(int pos)
    {
        if(pos>-1 && pos<filhos.size())
            return filhos.get(pos);
        return null;
    }

    public void setFilhos(List<NoAST> filhos) {
        this.filhos = filhos;
    }

    // Adição de um filho na AST
    public void addFilho(NoAST no)
    {
        filhos.add(no);
    }

    public boolean isFolha(){
        return filhos == null || filhos.isEmpty();
    }

    public void imprimirArvore(String prefixo, boolean isUltimo) {
        System.out.println(prefixo + (isUltimo ? "└── " : "├── ") + valor);

        String prefixoFilhos = prefixo + (isUltimo ? "    " : "│   ");

        for (int i = 0; i < filhos.size(); i++) {
            boolean ultimoFilho = (i == filhos.size() - 1);
            filhos.get(i).imprimirArvore(prefixoFilhos, ultimoFilho);
        }
    }
}
