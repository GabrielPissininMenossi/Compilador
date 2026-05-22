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

    public TAC(String operador, String operandoEsq, String operandoDir, String resultado) {
        this.operador = operador;
        this.operandoEsq = operandoEsq;
        this.operandoDir = operandoDir;
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

    public String getOperandoEsq() {
        return operandoEsq;
    }

    public void setOperandoEsq(String operando1) {
        this.operandoEsq = operando1;
    }

    public String getOperandoDir() {
        return operandoDir;
    }

    public void setOperandoDir(String operando2) {
        this.operandoDir = operando2;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
