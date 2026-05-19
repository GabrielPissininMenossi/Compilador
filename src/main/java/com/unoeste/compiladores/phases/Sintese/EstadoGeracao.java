package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.AST.NoAST;

public class EstadoGeracao {
    private NoAST no;
    private String estado;
    private String label1;
    private String label2;

    public EstadoGeracao(NoAST no, String estado, String label1, String label2) {
        this.no = no;
        this.estado = estado;
        this.label1 = label1;
        this.label2 = label2;
    }

    public EstadoGeracao(NoAST no, String estado) {
        this(no, estado, "", "");
    }

    public NoAST getNo() {
        return no;
    }

    public String getEstado() {
        return estado;
    }

    public String getLabel1() {
        return label1;
    }

    public String getLabel2() {
        return label2;
    }
}
