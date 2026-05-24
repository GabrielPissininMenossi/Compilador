package com.unoeste.compiladores.entities.AST;

import java.util.List;

public class AST {
    private NoAST raiz;

    public AST(NoAST raiz) {
        this.raiz = raiz;
    }

    public AST() {
        this(null);
    }

    public NoAST getRaiz() {
        return raiz;
    }

    public void setRaiz(NoAST raiz) {
        this.raiz = raiz;
    }

    public void exibirArvoreNivel() {
        if (raiz == null) {
            System.out.println("A árvore está vazia.");
        }
        else
        {
            System.out.println(raiz.getValor());
            List<NoAST> filhos = raiz.getFilhos();

            for (int i = 0; i < filhos.size(); i++) {
                boolean isUltimo = (i == filhos.size() - 1);
                filhos.get(i).imprimirArvore("", isUltimo);
            }
        }
    }
}
