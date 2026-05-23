package com.unoeste.compiladores.phases.Sintese;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dicionário de instruções, de valores TAC (Three Address Code) para instruções Simple Assembler
 * */
public class DicionarioCodigoAlvo {
    public static List<Character> list_numeros;
    public static int contadorMultiplicacoes = 0;
    public static int contadorDivisores = 0;
    public static int contadorIguais = 0;
    public static int contadorDiferentes = 0;
    public static int contadorMenorIguais = 0;
    public static int contadorMaiorIguais = 0;
    public static int contadorMaiores = 0;
    public static int contadorMenores = 0;

    public static void resetarValores(){
        preencheNumeros();
        contadorMultiplicacoes = 0;
        contadorDivisores = 0;
        contadorIguais = 0;
        contadorDiferentes = 0;
        contadorMenorIguais = 0;
        contadorMaiorIguais = 0;
        contadorMenores = 0;
    }

    /**
     * RETORNA AS INSTRUÇÕES PARA SE REALIZAR UMA SOMA COM DIFERENTES VARIÁVEIS E/OU NÚMEROS
     * */
    public static List<String> getSomaComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        boolean isDouble = false;
        String numero = "";
        preencheNumeros();

        // 1.
        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA ESQUERDA
        if(!isNumero(operEsq))
        {
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
            numero = getValorVariavel(operEsq, codigoAlvo);
        }
        else // É UM NÚMERO CRU -> OPERADOR DA ESQUERDA
        {
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
            numero = operEsq;
        }
        // VERIFICANDO SE O VALOR RECUPERADO DO OPERADOR É DOUBLE
        if(isDouble(numero))
            isDouble = true;
        numero = "";
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA DIREITA
        if(!isNumero(operDir))
        {
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
            numero = getValorVariavel(operDir, codigoAlvo);
        }
        else // É UM NÚMERO CRU -> OPERADOR DA DIREITA
        {
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
            numero = operDir;
        }
        if(isDouble(numero))
            isDouble = true;
        numero = "";
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // VERIFICANDO SE ALGUM OPERADOR POSSUI VALOR DOUBLE
        if(isDouble) // realizar adição de valores DOUBLE
        {
            linhaTraduzida.append("addf R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3
        }
        else // realizar adição de valores INTEIROS
            linhaTraduzida.append("addi R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // ARMAZENAR O RESULTADO NA VARIÁVEL
        linhaTraduzida.append("store R3, [").append(result).append("]\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    /**
     * RETORNA AS INSTRUÇÕES PARA SE REALIZAR UMA SUBTRAÇÃO COM DIFERENTES VARIÁVEIS E/OU NÚMEROS
     * */
    public static List<String> getSubComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        boolean isDouble = false;
        String numero = "";

        preencheNumeros();

        // LABEL DO RESULTADO
        if(!resultExists(result, codigoAlvo))
        {
            linhas.add(result + ":\n");
        }

        // =========================================================
        // OPERANDO DA ESQUERDA -> R1
        // =========================================================
        if(!isNumero(operEsq))
        {
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");

            numero = getValorVariavel(operEsq, codigoAlvo);
        }
        else
        {
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");

            numero = operEsq;
        }

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(isDouble(numero))
            isDouble = true;

        numero = "";

        // =========================================================
        // OPERANDO DA DIREITA -> R2
        // =========================================================
        if(!isNumero(operDir))
        {
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
            numero = getValorVariavel(operDir, codigoAlvo);
        }
        else
        {
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
            numero = operDir;
        }

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(isDouble(numero))
            isDouble = true;

        numero = "";

        // =========================================================
        // INVERTER O SINAL DE R2 EM TEMPO DE EXECUÇÃO
        // COMPLEMENTO DE 2:
        //
        // R2 = ~R2
        // R2 = R2 + 1
        // =========================================================

        // MÁSCARA COM TODOS OS BITS 1
        linhaTraduzida.append("load R4, 11111111b\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // CONSTANTE 1
        linhaTraduzida.append("load R5, 1\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // XOR -> INVERTE TODOS OS BITS
        linhaTraduzida.append("xor R6, R2, R4\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // SOMA 1 -> COMPLEMENTO DE 2
        linhaTraduzida.append("addi R2, R6, R5\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // =========================================================
        // SOMA FINAL
        // R3 = R1 + (-R2)
        // =========================================================
        if(isDouble)
            linhaTraduzida.append("addf R3, R1, R2\n");
        else
            linhaTraduzida.append("addi R3, R1, R2\n");

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // =========================================================
        // SALVAR RESULTADO
        // =========================================================
        linhaTraduzida.append("store R3, [").append(result).append("]\n");

        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    public static List<String> getMultComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        preencheNumeros();

        String seqMult = getSequenciaMultiplicacoes();

        String lblEsqNeg = "mult_esq_neg_" + seqMult;
        String lblEsqFim = "mult_esq_fim_" + seqMult;

        String lblDirNeg = "mult_dir_neg_" + seqMult;
        String lblDirFim = "mult_dir_fim_" + seqMult;

        String lblLoop = "mult_loop_" + seqMult;
        String lblFimLoop = "mult_fim_loop_" + seqMult;

        String lblResultadoPositivo = "mult_resultado_pos_" + seqMult;

        // =========================================================
        // LABEL DO RESULTADO
        // =========================================================
        if(!resultExists(result, codigoAlvo))
        {
            linhas.add(result + ":\n");
        }

        // =========================================================
        // OPERANDO DA ESQUERDA -> R1
        // =========================================================
        if(!isNumero(operEsq))
        {
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        }
        else
        {
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        }

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // =========================================================
        // OPERANDO DA DIREITA -> R2
        // =========================================================
        if(!isNumero(operDir)) {
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        }
        else {
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        }

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // =========================================================
        // CONSTANTES AUXILIARES
        // =========================================================
        linhas.add("load R6, -1\n"); // verificar negativo
        linhas.add("load R5, 1\n");  // incremento

        // =========================================================
        // VERIFICAR SINAL DO OPERANDO ESQUERDO
        // R3 = 0 -> positivo
        // R3 = 1 -> negativo
        // =========================================================
        linhas.add("jmpLE R1<=R6, " + lblEsqNeg + "\n");

        linhas.add("move R3, 0\n");
        linhas.add("jmp " + lblEsqFim + "\n");

        linhas.add(lblEsqNeg + ":\n");

        linhas.add("move R3, 1\n");

        // transformar em positivo
        linhas.add("xor R7, R1, 11111111b\n");
        linhas.add("addi R1, R7, 1\n");

        linhas.add(lblEsqFim + ":\n");

        // =========================================================
        // VERIFICAR SINAL DO OPERANDO DIREITO
        // R4 = 0 -> positivo
        // R4 = 1 -> negativo
        // =========================================================
        linhas.add("jmpLE R2<=R6, " + lblDirNeg + "\n");

        linhas.add("move R4, 0\n");
        linhas.add("jmp " + lblDirFim + "\n");

        linhas.add(lblDirNeg + ":\n");

        linhas.add("move R4, 1\n");

        // transformar em positivo
        linhas.add("xor R7, R2, 11111111b\n");
        linhas.add("addi R2, R7, 1\n");

        linhas.add(lblDirFim + ":\n");

        // =========================================================
        // XOR DOS SINAIS
        // R7:
        // 0 -> resultado positivo
        // 1 -> resultado negativo
        // =========================================================
        linhas.add("xor R7, R3, R4\n");

        // =========================================================
        // MULTIPLICAÇÃO POR SOMAS SUCESSIVAS
        // =========================================================

        // R3 -> acumulador
        // R4 -> contador

        linhas.add("load R3, 0\n");
        linhas.add("load R4, 0\n");

        linhas.add(lblLoop + ":\n");

        linhas.add("jmpEQ R4=R2, " + lblFimLoop + "\n");

        linhas.add("addi R3, R3, R1\n");

        linhas.add("addi R4, R4, R5\n");

        linhas.add("jmp " + lblLoop + "\n");

        linhas.add(lblFimLoop + ":\n");

        // =========================================================
        // APLICAR SINAL FINAL
        // =========================================================
        linhas.add("jmpEQ R7=0, " + lblResultadoPositivo + "\n");

        linhas.add("xor R6, R3, 11111111b\n");
        linhas.add("addi R3, R6, 1\n");

        linhas.add(lblResultadoPositivo + ":\n");

        // =========================================================
        // SALVAR RESULTADO
        // =========================================================
        linhas.add("store R3, [" + result + "]\n");

        return linhas;
    }

    public static List<String> getDivComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        preencheNumeros();

        // 1.
        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        String sufixo = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String rotuloLoop = "loopDiv_" + sufixo;
        String rotuloContinua = "continuaDiv_" + sufixo;
        String rotuloFim = "fimDiv_" + sufixo;
        String rotuloZero = "divZero_" + sufixo;

        linhas.add("load R3, 0\n");   // quociente
        linhas.add("load R4, 0\n");   // acumulador parcial
        linhas.add("load R5, 1\n");   // constante 1
        linhas.add("jmpEQ R2=0, " + rotuloZero + "\n");

        linhas.add(rotuloLoop + ":\n");
        linhas.add("addi R6, R4, R2\n");      // próximo acumulado = acumulado + divisor
        linhas.add("jmpLE R6<=R1, " + rotuloContinua + "\n");
        linhas.add("jmp " + rotuloFim + "\n");

        linhas.add(rotuloContinua + ":\n");
        linhas.add("move R4, R6\n");          // acumula o novo valor
        linhas.add("addi R3, R3, R5\n");      // quociente++
        linhas.add("jmp " + rotuloLoop + "\n");

        linhas.add(rotuloZero + ":\n");
        linhas.add("load R3, 0\n");           // divisão por zero: devolve 0
        linhas.add("jmp " + rotuloFim + "\n");

        linhas.add(rotuloFim + ":\n");
        linhas.add("store R3, [" + result + "]\n");

        return linhas;
    }

    public static List<String> getCompIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqIgual = getSequenciaIguais();

        linhas.add("jmpEQ R1=R2, igual"+seqIgual+"\n");
        linhas.add("naoIgual"+seqIgual+": store 0, ["+result+"]\n");
        linhas.add("jmp sairIgual"+seqIgual+"\n");
        linhas.add("igual"+seqIgual+": store 1, ["+result+"]\n");
        linhas.add("sairIgual"+seqIgual+"\n");

        return linhas;
    }

    public static List<String> getCompMaior(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMaior = getSequenciaMaiores();

        linhas.add("jmpLE R1<=R2, naoMaior"+seqMaior+"\n");
        linhas.add("maior"+seqMaior+": store 0, ["+result+"]\n");
        linhas.add("jmp sairMaior"+seqMaior+"\n");
        linhas.add("naoMaior"+seqMaior+": store 1, ["+result+"]\n");
        linhas.add("sairMaior"+seqMaior+"\n");

        return linhas;
    }

    public static List<String> getCompMaiorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMaiorIgual = getSequenciaMaiorIguais();

        linhas.add("jmpEQ R1=R2, maiorIgualIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualNaoIgual: jmpLE R1<=R2, maiorIgualMenor"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualMaior"+seqMaiorIgual+": store 1, ["+result+"]\n");
        linhas.add("jmp sairMaiorIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualMenor"+seqMaiorIgual+": store 0, ["+result+"]\n");
        linhas.add("jmp sairMaiorIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualIgual"+seqMaiorIgual+": store 1, ["+result+"]\n");
        linhas.add("sairMaiorIgual"+seqMaiorIgual+"\n");

        return linhas;
    }

    public static List<String> getCompMenor(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMenor = getSequenciaMenores();

        linhas.add("jmpEQ R1=R2, naoMenorIgual"+seqMenor+"\n");
        linhas.add("menorNaoIgual: jmpLE R1<=R2, menor"+seqMenor+"\n");
        linhas.add("naoMenorMaior"+seqMenor+": store 0, ["+result+"]\n");
        linhas.add("jmp sairMenor"+seqMenor+"\n");
        linhas.add("menor"+seqMenor+": store 1, ["+result+"]\n");
        linhas.add("jmp sairMenor"+seqMenor+"\n");
        linhas.add("naoMenorIgual"+seqMenor+": store 0, ["+result+"]\n");
        linhas.add("sairMenor"+seqMenor+"\n");

        return linhas;
    }

    public static List<String> getCompMenorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMenorIgual = getSequenciaMenorIguais();

        linhas.add("jmpLE R1<=R2, menorOuIgual"+seqMenorIgual+"\n");
        linhas.add("naoMenorOuIgual"+seqMenorIgual+": store 0, ["+result+"]\n");
        linhas.add("jmp sairMenorIgual"+seqMenorIgual+"\n");
        linhas.add("menorOuIgual"+seqMenorIgual+": store 1, ["+result+"]\n");
        linhas.add("sairMenorIgual"+seqMenorIgual+"\n");

        return linhas;
    }

    public static List<String> getCompDiferente(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqDiferente = getSequenciaDiferentes();

        linhas.add("jmpEQ R1=R2, diferente"+seqDiferente+"\n");
        linhas.add("naoDiferente"+seqDiferente+": store 1, ["+result+"]\n");
        linhas.add("jmp sairDiferente"+seqDiferente+"\n");
        linhas.add("diferente"+seqDiferente+": store 0, ["+result+"]\n");
        linhas.add("sairDiferente"+seqDiferente+"\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES PARA SER REALIZADO UMA ATRIBUIÇÃO.
     * Sendo:
     *      carregar o valor a ser passado (operEsq),
     *      move para o destino (result) o valor a ser passado (operEsq)
     * */
    public static List<String> getAtribuicao(String operEsq, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        if(isNumero(operEsq)) //mando o número diretamente
            linhaTraduzida.append("load R2, ").append(operEsq).append("\n");
        else //carrego a variável identificada pelo label no programa
            linhaTraduzida.append("load R2, [").append(operEsq).append("]\n");
        linhas.add(linhaTraduzida.toString());

        linhas.add("store R2, [" + result + "]\n");

        return linhas;
    }

    public static List<String> getIfFalse(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(isNumero(operEsq)) // valor direto
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        else // valor indireto -> pego de um label
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // formar o JUMP
        linhaTraduzida.append("jmpEQ R1=0, ").append(result).append("\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UM LABEL.
     * Sendo:
     *      label (result)
     * */
    public static List<String> getLabel(String result)
    {
        List<String> linhas = new  ArrayList<>();
        linhas.add(result + "\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTE PARA UM GOTO.
     * Sendo:
     *      jmp,
     *      destino do jmp (result)
     * */
    public static List<String> getGoto(String result)
    {
        List<String> linhas = new  ArrayList<>();
        linhas.add("jmp " + result + "\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTE PARA UM RETURN.
     * Sendo:
     *      load do valor na memória ou direto,
     *      criação de um label,
     *      store do valor no label de return
     * */
    public static List<String> getReturn(String operEsq)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        linhaTraduzida.append(getLabel("return")); //cria um label
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        //carrego o valor de retorno
        if(isNumero(operEsq))
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        else
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        linhaTraduzida.append(getLabel("return")); //cria um label
        linhas.add(linhaTraduzida.toString());

        linhas.add("store R1, [return]\n");

        return linhas;
    }

    /**
     * RETORNO DA ÚLTIMA INSTRUÇÃO DE UM CÓDIGO ASSEMBLER.
     * Sendo:
     *      halt
     * */
    public static String getInstrucaoFinal()
    {
        return "halt";
    }

    // FUNÇÕES AUXILIARES
    /**
     * Método que verifica na minha lista de codigo alvo de o result já foi declarado
     * */
    private static boolean resultExists(String result, List<String> codigoAlvo)
    {
        int i=0;
        while(i<codigoAlvo.size() && !codigoAlvo.get(i).startsWith(result))
            i++;
        return i<codigoAlvo.size();
    }

    public static boolean isNumero(String token)
    {
        if (!token.isEmpty())
        {
            int quantPonto = 0, quantNum=0;

            for(int i=0; i<token.length(); i++)
                if (token.charAt(i) == '.')
                    quantPonto++;

            if(quantPonto > 1) //mais de um ponto
                return false;

            for(int i=0; i<token.length(); i++)
                if(list_numeros.contains(token.charAt(i)))
                    quantNum++;

            return quantNum == token.length()-quantPonto;
        }
        return false;
    }

    public static void preencheNumeros()
    {
        list_numeros = new ArrayList<>();
        // Adicionar valores nas Listas, utilizando como base para os indices a tabela Unicode
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            list_numeros.add(character);
        }
    }

    /**
     * RETORNA O VALOR DA VARIÁVEL DE ACORDO COM O LABEL NO PROGRAMA ASSEMBLY.
     * SENDO:
     *      procura-se o label,
     *      retorna o valor que o label possui
     * */
    private static String getValorVariavel(String operando, List<String> codigoAlvo)
    {
        // andar e procurar o respectivo operando
        int i=0;
        while(i<codigoAlvo.size() && !codigoAlvo.get(i).startsWith(operando))
            i++;

        if(i<codigoAlvo.size())
            return codigoAlvo.get(i).substring(operando.length()).trim().replaceAll(" ","");

        return null;
    }

    /**
     * RETORNA SE O NÚMERO É DOUBLE OU NÃO
     * */
    private static boolean isDouble(String numero)
    {
        double valorDouble = Double.parseDouble(numero);
        int parteInteira = (int)valorDouble;
        return valorDouble-parteInteira != 0;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE MULTIPLICAÇÕES
     * */
    public static String getSequenciaMultiplicacoes()
    {
        return "multiplicacao"+ contadorMultiplicacoes++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE DIVISÕES
     * */
    public static String getSequenciaDivisoes()
    {
        return "divisao"+ contadorDivisores++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE IGUAIS
     * */
    public static String getSequenciaIguais()
    {
        return "igual"+ contadorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE DIFERENTES
     * */
    public static String getSequenciaDiferentes()
    {
        return "diferente"+ contadorDiferentes++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MENORES-IGUAIS
     * */
    public static String getSequenciaMenorIguais()
    {
        return "menorIguais"+ contadorMenorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MAIORES-IGUAIS
     * */
    public static String getSequenciaMaiorIguais()
    {
        return "maiorIgual"+ contadorMaiorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MAIORES
     * */
    public static String getSequenciaMaiores()
    {
        return "maior"+ contadorMaiores++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MENORES
     * */
    public static String getSequenciaMenores()
    {
        return "menor"+ contadorMenores++;
    }

    //    /**
//     * RETORNA SE A OPERAÇÃO DE MULTIPLICAÇÃO OU DIVISÃO SERÁ POSITIVA
//     * */
//    private static boolean isPositivoResultMultDiv(String operEsq, String operDir, List<String> codigoAlvo)
//    {
//        String numero1;
//        String numero2;
//
//        if(isNumero(operEsq)) {
//            numero1 = operEsq;
//        }
//        else {
//            numero1 = getValorVariavel(operEsq, codigoAlvo);
//        }
//
//        if(isNumero(operDir)) {
//            numero2 = operDir;
//        }
//        else{
//            numero2 = getValorVariavel(operDir, codigoAlvo);
//        }
//
//        String sinal = getSinalFinalMultDiv(numero1, numero2);
//
//        return sinal.equals("positivo");
//    }
//
//    /**
//     * RETORNA A REGRA DE SINAIS ENTRE DOIS NÚMEROS PASSADOS COMO STRING
//     * */
//    private static String getSinalFinalMultDiv(String numero1, String numero2){
//        if(numero1.charAt(0) == '-') // 1 é negativo
//            if(numero2.charAt(0) == '-') //2 é negativo
//                return "positivo"; // os 2 são negativos
//            else //2 é positivo
//                return "negativo"; // apenas 1 é negativo
//        else // 1 é positivo
//            if(numero2.charAt(0) == '-') //2 é negativo
//                return "negativo"; // apenas 1 é negativo
//            else //2 é positivo
//                return "positivo"; // os 2 são positivos
//    }
//
//    /**
//     * RETORNA A CONVERSÃO DE UM NÚMERO QUALQUER PARA POSITIVO
//     * */
//    private static String getNumeroPositivo(String numero)
//    {
//        if(numero.charAt(0) == '-')
//            return numero.substring(1); //converte para positivo e retorna
//        return numero; //já é positivo
//    }
//
//    /**
//     * RETORNA A CONVERSÃO DE UM NÚMERO QUALQUER PARA NEGATIVO
//     * */
//    private static String getNumeroNegativo(String numero)
//    {
//        if(numero.charAt(0) == '-')
//            return numero; //já é negativo
//        return "-"+numero; //converte para negativo e retorna
//    }
//
//    /**
//     * RETORNA UM NÚMERO INVERTIDO
//     * Sendo:
//     *      positivo --> negativo,
//     *      nagativo --> positivo
//     * */
//    private static String getValorVariavelSinalInvertido(String operando)
//    {
//        char sinal = operando.charAt(0);
//
//        if(sinal == '-') // se negativo --> retorno um valor positivo --> sem o primeiro character
//            return operando.substring(1);
//
//        return "-" + operando;
//    }
}
