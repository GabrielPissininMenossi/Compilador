package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.TAC;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigoAlvo {
    List<String> codigoAlvo;

    public GeradorCodigoAlvo(List<String> codigoAlvo) {
        this.codigoAlvo = codigoAlvo;
    }

    public GeradorCodigoAlvo() {
        this.codigoAlvo = new ArrayList<>();
    }

    public List<String> getCodigoAlvo(List<TAC> codigoIntermediarioOtimizado)
    {
        // limpar a lista por default
        codigoAlvo.clear();
        for(TAC instrucao : codigoIntermediarioOtimizado)
        {
            String linhaAlvo = traduzirInstrucaoLinhaAlvo(instrucao); //traduzir a linha TAC para uma linha do SimpSim
            codigoAlvo.add(linhaAlvo);
        }

        return codigoAlvo;
    }

    private String traduzirInstrucaoLinhaAlvo(TAC instrucao)
    {
        StringBuilder linhaTraduzida = new StringBuilder();
        String op = instrucao.getOperador();
        String esq = instrucao.getOperandoEsq();
        String dir = instrucao.getOperandoDir();
        String res = instrucao.getResultado();

        switch (op)
        {
            case "+":
                linhaTraduzida.append("    load R1, [").append(esq).append("]\n");
                linhaTraduzida.append("    load R2, [").append(dir).append("]\n");
                linhaTraduzida.append("    addi R3, R1, R2\n"); // adição entre dois registradores
                linhaTraduzida.append("    store R3, [").append(res).append("]\n"); // armazena o conteudo na memória de determinado registrador
                break;
            case "-": // vou ter que usar complemento de 2
                break;
            case "*": // somas sucessivas
                break;
            case "/": // subtracoes sucessivas
                break;
            case "==":
                break;
            case ">":
                break;
            case ">=":
                break;
            case "<":
                break;
            case "<=":
                break;
            case "!=":
                break;
            case "=":
                break;
            case "ifFalse":
                break;
            case "label":
                break;
            case "goto":
                break;
            case "return":
                break;
        }
        // aqui eu de fato traduzo a linha TAC

        return linhaTraduzida.toString();
    }
}
