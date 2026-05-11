package com.unoeste.compiladores.entities.ArvoreAST;

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
}
