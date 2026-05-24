package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.TAC;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigoAlvo {
    private List<String> codigoAlvo;

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
        DicionarioCodigoAlvo.resetarValores();

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
            case "-":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getSubComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "*":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getMultComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "/":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getDivComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "%":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getRestoComVariaveis(operEsq, operDir, result, codigoAlvo);
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
                instrucoesTraduzidas = DicionarioCodigoAlvo.getIfFalse(operEsq, result);
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
            case "&&":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getAndComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
            case "||":
                instrucoesTraduzidas = DicionarioCodigoAlvo.getOrComVariaveis(operEsq, operDir, result, codigoAlvo);
                break;
        }

        return instrucoesTraduzidas;
    }

    public void imprimirCodigoAlvo()
    {
        for(String codigoAssembly : codigoAlvo)
            System.out.print(codigoAssembly);
        System.out.println("\n");
    }
}
