package com.unoeste.compiladores.entities;

/**
 * TAC --> Three-Address Code
 *      Código de 3 endereços
 * */
public class TAC {
    private String operador;
    private String operandoEsq;
    private String operandoDir;
    private String resultado;

    public TAC(String operador, String operando1, String operando2, String resultado) {
        this.operador = operador;
        this.operandoEsq = operando1;
        this.operandoDir = operando2;
        this.resultado = resultado;
    }

    public TAC() {
        this("","","","");
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getOperando1() {
        return operandoEsq;
    }

    public void setOperando1(String operando1) {
        this.operandoEsq = operando1;
    }

    public String getOperando2() {
        return operandoDir;
    }

    public void setOperando2(String operando2) {
        this.operandoDir = operando2;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
