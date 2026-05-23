package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.TAC;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigoAlvo {
    private List<String> codigoAlvo;
    private int contadorContinua;

    public GeradorCodigoAlvo(List<String> codigoAlvo) {
        this.codigoAlvo = codigoAlvo;
    }

    public GeradorCodigoAlvo() {
        this.codigoAlvo = new ArrayList<>();
        contadorContinua = 1;
    }

    public List<String> getCodigoAlvo(List<TAC> codigoIntermediarioOtimizado)
    {
        // limpar a lista por default
        codigoAlvo.clear();

        // para cada linha de TAC (three address code) é feita uma tradução para o assembly
        for(TAC instrucao : codigoIntermediarioOtimizado)
        {
            List<String> linhasAlvo = traduzirInstrucaoLinhaAlvo(instrucao); //traduzir a linha TAC para as linhas correspondentes do simple assembler

            // adicionar as instruções que foram traduzidas
            codigoAlvo.addAll(linhasAlvo);
        }
        codigoAlvo.add(DicionarioCodigoAlvo.getInstrucaoFinal());

        return codigoAlvo;
    }

    private List<String> traduzirInstrucaoLinhaAlvo(TAC instrucao)
    {
        List<String> instrucoesTraduzidas = new ArrayList<>();
        String operador = instrucao.getOperador();
        String operEsq = instrucao.getOperandoEsq();
        String operDir = instrucao.getOperandoDir();
        String result = instrucao.getResultado();

        switch (operador)
        {
            case "+":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getSomaComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "-": // vou ter que usar complemento de 2
                instrucoesTraduzidas = DicionarioCodigoAlvo.getSubComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "*": // somas sucessivas
                instrucoesTraduzidas = DicionarioCodigoAlvo.getMultComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "/": // subtracoes sucessivas
                instrucoesTraduzidas = DicionarioCodigoAlvo.getDivComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "==":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompIgual(operEsq, operDir, result, codigoAlvo);
                break;
            case ">":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompMaior(operEsq, operDir, result, codigoAlvo);
                break;
            case ">=":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompMaiorIgual(operEsq, operDir, result, codigoAlvo);
                break;
            case "<":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompMenor(operEsq, operDir, result, codigoAlvo);
                break;
            case "<=":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompMenorIgual(operEsq, operDir, result, codigoAlvo);
                break;
            case "!=":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getCompDiferente(operEsq, operDir, result, codigoAlvo);
                break;
            case "=":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getAtribuicao(operEsq, result, codigoAlvo);
                break;
            case "ifFalse":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getIfFalse(operEsq, operDir, result, codigoAlvo);
                break;
            case "label":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getLabel(result);
                break;
            case "goto":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getGoto(result);
                break;
            case "return":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getReturn(operEsq);
                break;
        }

        return instrucoesTraduzidas;
    }
}
