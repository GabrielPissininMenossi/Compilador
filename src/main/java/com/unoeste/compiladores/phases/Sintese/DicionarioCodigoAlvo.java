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
    public static int contadorRestos = 0;

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
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA OPERAÇÃO ARITMÉTICA DE SOMA
     * */
    public static List<String> getSomaComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // 1.
        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA ESQUERDA
        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA ESQUERDA
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA DIREITA
        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA DIREITA
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        //realizar apenas soma de valores inteiros
        linhaTraduzida.append("addi R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // ARMAZENAR O RESULTADO NA VARIÁVEL
        linhaTraduzida.append("store R3, [").append(result).append("]\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA OPERAÇÃO ARITMÉTICA DE SUBTRAÇÃO
     * */
    public static List<String> getSubComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // LABEL DO RESULTADO
        if(!resultExists(result, codigoAlvo))
            linhas.add(result + ":\n");

        // =========================================================
        // OPERANDO DA ESQUERDA -> R1
        // =========================================================
        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // =========================================================
        // OPERANDO DA DIREITA -> R2
        // =========================================================
        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

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

        // REALIZAR SOMA APENAS COM VALORES INTEIROS
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

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA OPERAÇÃO ARITMÉTICA DE MULIPLICAÇÃO
     * */
    public static List<String> getMultComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

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
        linhas.add("move R0, R6\n"); // registrador correto para o teste do jump
        linhas.add("jmpLE R1<=R0, " + lblEsqNeg + "\n");

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
        linhas.add("move R0, R6\n"); // registrador correto para o teste do jump
        linhas.add("jmpLE R2<=R0, " + lblDirNeg + "\n");

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

        linhas.add("move R0, R2\n"); // registrador correto para o teste do jump
        linhas.add("jmpEQ R4=R0, " + lblFimLoop + "\n");

        linhas.add("addi R3, R3, R1\n");

        linhas.add("addi R4, R4, R5\n");

        linhas.add("jmp " + lblLoop + "\n");

        linhas.add(lblFimLoop + ":\n");

        // =========================================================
        // APLICAR SINAL FINAL
        // =========================================================
        linhas.add("load R0, 0\n"); // registrador correto para teste do jump
        linhas.add("jmpEQ R7=R0, " + lblResultadoPositivo + "\n");

        linhas.add("xor R6, R3, 11111111b\n");
        linhas.add("addi R3, R6, 1\n");

        linhas.add(lblResultadoPositivo + ":\n");

        // =========================================================
        // SALVAR RESULTADO
        // =========================================================
        linhas.add("store R3, [" + result + "]\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA OPERAÇÃO ARITMÉTICA DE DIVISÃO
     * */
    public static List<String> getDivComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        String seqDiv = getSequenciaDivisoes();

        String lblEsqNeg = "div_esq_neg_" + seqDiv;
        String lblEsqFim = "div_esq_fim_" + seqDiv;

        String lblDirNeg = "div_dir_neg_" + seqDiv;
        String lblDirFim = "div_dir_fim_" + seqDiv;

        String lblLoop = "div_loop_" + seqDiv;
        String lblLoopCorpo = "div_loop_corpo_" + seqDiv;
        String lblFimLoop = "div_fim_loop_" + seqDiv;

        String lblSinalPos = "div_sinal_pos_" + seqDiv;
        String lblDivZero = "div_zero_" + seqDiv;
        String lblFimDiv = "div_fim_" + seqDiv;

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
        if(!isNumero(operDir))
        {
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        }
        else
        {
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        }
        linhas.add(linhaTraduzida.toString());

        // =========================================================
        // CONSTANTES AUXILIARES
        // =========================================================
        linhas.add("load R6, -1\n"); // comparação com negativo

        // =========================================================
        // SINAL DO OPERANDO DA ESQUERDA
        // R3 = 0 -> positivo
        // R3 = 1 -> negativo
        // =========================================================
        linhas.add("move R0, R6\n"); // registrador correto para o teste do jump
        linhas.add("jmpLE R1<=R0, " + lblEsqNeg + "\n");
        linhas.add("move R3, 0\n");
        linhas.add("jmp " + lblEsqFim + "\n");

        linhas.add(lblEsqNeg + ":\n");
        linhas.add("move R3, 1\n");
        linhas.add("xor R7, R1, 11111111b\n");
        linhas.add("addi R1, R7, 1\n"); // R1 = abs(R1)

        linhas.add(lblEsqFim + ":\n");

        // =========================================================
        // SINAL DO OPERANDO DA DIREITA
        // R4 = 0 -> positivo
        // R4 = 1 -> negativo
        // =========================================================
        linhas.add("move R0, R6\n"); // registrador correto para o teste do jump
        linhas.add("jmpLE R2<=R0, " + lblDirNeg + "\n");
        linhas.add("move R4, 0\n");
        linhas.add("jmp " + lblDirFim + "\n");

        linhas.add(lblDirNeg + ":\n");
        linhas.add("move R4, 1\n");
        linhas.add("xor R7, R2, 11111111b\n");
        linhas.add("addi R2, R7, 1\n"); // R2 = abs(R2)

        linhas.add(lblDirFim + ":\n");

        // =========================================================
        // XOR DOS SINAIS
        // RF = 0 -> resultado positivo
        // RF = 1 -> resultado negativo
        // =========================================================
        linhas.add("xor RF, R3, R4\n");

        // =========================================================
        // DIVISÃO POR SUBTRAÇÕES/SOMAS SUCESSIVAS
        // =========================================================
        linhas.add("load R0, 0\n"); // registrador correto para teste do jump
        linhas.add("jmpEQ R2=R0, " + lblDivZero + "\n"); // divisão por zero

        linhas.add("load R3, 0\n"); // acumulador parcial
        linhas.add("load R5, 0\n"); // quociente
        linhas.add("load R6, 1\n"); // constante 1

        linhas.add(lblLoop + ":\n");
        linhas.add("addi R7, R3, R2\n");          // próximo acumulado = atual + divisor
        linhas.add("move R0, R1\n");
        linhas.add("jmpLE R7<=R0, " + lblLoopCorpo + "\n");
        linhas.add("jmp " + lblFimLoop + "\n");

        linhas.add(lblLoopCorpo + ":\n");
        linhas.add("move R3, R7\n");              // atualiza acumulado
        linhas.add("addi R5, R5, R6\n");          // quociente++
        linhas.add("jmp " + lblLoop + "\n");

        linhas.add(lblFimLoop + ":\n");

        // =========================================================
        // APLICAR SINAL FINAL
        // =========================================================
        linhas.add("load R0, 0\n");
        linhas.add("jmpEQ RF=R0, " + lblSinalPos + "\n");
        linhas.add("xor R7, R5, 11111111b\n");
        linhas.add("addi R5, R7, 1\n"); // R5 = -R5

        linhas.add(lblSinalPos + ":\n");
        linhas.add("store R5, [" + result + "]\n");
        linhas.add("jmp " + lblFimDiv + "\n");

        // =========================================================
        // DIVISÃO POR ZERO
        // =========================================================
        linhas.add(lblDivZero + ":\n");
        linhas.add("move R5, 0\n");
        linhas.add("store R5, [" + result + "]\n");

        linhas.add(lblFimDiv + ":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA OPERAÇÃO ARITMÉTICA DE RESTO DE DIVISÃO
     * */
    public static List<String> getRestoComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        String seqResto = getSequenciaRestos();

        String lblEsqNeg = "resto_esq_neg_" + seqResto;
        String lblEsqFim = "resto_esq_fim_" + seqResto;

        String lblDirNeg = "resto_dir_neg_" + seqResto;
        String lblDirFim = "resto_dir_fim_" + seqResto;

        String lblLoop = "resto_loop_" + seqResto;
        String lblCorpo = "resto_corpo_" + seqResto;
        String lblFim = "resto_fim_" + seqResto;

        String lblDivZero = "resto_div_zero_" + seqResto;
        String lblSalvarPos = "resto_salvar_pos_" + seqResto;

        // label do resultado
        if(!resultExists(result, codigoAlvo))
        {
            linhas.add(result + ":\n");
        }

        // operando esquerdo -> R1
        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // operando direito -> R2
        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // constante para testar negativo
        linhas.add("load R6, -1\n");

        // sinal do operando esquerdo: R3 = 0 positivo, 1 negativo
        linhas.add("move R0, R6\n"); // registrador correto para o teste do jump
        linhas.add("jmpLE R1<=R0, " + lblEsqNeg + "\n");
        linhas.add("move R3, 0\n");
        linhas.add("jmp " + lblEsqFim + "\n");
        linhas.add(lblEsqNeg + ":\n");
        linhas.add("move R3, 1\n");
        linhas.add("xor R7, R1, 11111111b\n");
        linhas.add("addi R1, R7, 1\n");
        linhas.add(lblEsqFim + ":\n");

        // sinal do operando direito: R4 = 0 positivo, 1 negativo
        linhas.add("move R0, R6\n"); // registrador correto para teste do jump
        linhas.add("jmpLE R2<=R0, " + lblDirNeg + "\n");
        linhas.add("move R4, 0\n");
        linhas.add("jmp " + lblDirFim + "\n");
        linhas.add(lblDirNeg + ":\n");
        linhas.add("move R4, 1\n");
        linhas.add("xor R7, R2, 11111111b\n");
        linhas.add("addi R2, R7, 1\n");
        linhas.add(lblDirFim + ":\n");

        // divisão por zero
        linhas.add("load R0, 0\n"); //registrador correto para teste do jump
        linhas.add("jmpEQ R2=R0, " + lblDivZero + "\n");

        // ---------------------------------------------------------
        // resto = dividend - (maior múltiplo de divisor <= dividend)
        // usando soma sucessiva
        // R5 = acumulador do múltiplo
        // R6 = 1
        // ---------------------------------------------------------
        linhas.add("load R5, 0\n");
        linhas.add("load R6, 1\n");

        linhas.add(lblLoop + ":\n");
        linhas.add("addi R7, R5, R2\n");                 // candidato = acumulador + divisor
        linhas.add("move R0, R1\n");
        linhas.add("jmpLE R7<=R0, " + lblCorpo + "\n");  // se candidato <= dividendo, continua
        linhas.add("jmp " + lblFim + "\n");

        linhas.add(lblCorpo + ":\n");
        linhas.add("move R5, R7\n");                     // atualiza acumulador
        linhas.add("jmp " + lblLoop + "\n");

        linhas.add(lblFim + ":\n");

        // Resto positivo: R1 - R5
        // faz isso por complemento de 2 em R5 e soma em R1
        linhas.add("xor R7, R5, 11111111b\n");
        linhas.add("addi R7, R7, 1\n");
        linhas.add("addi R5, R1, R7\n");                 // R5 = R1 - R5

        // sinal do resto: segue o sinal do operando esquerdo
        linhas.add("load R0, 0\n");
        linhas.add("jmpEQ R3=R0, " + lblSalvarPos + "\n");
        linhas.add("xor R7, R5, 11111111b\n");
        linhas.add("addi R5, R7, 1\n");

        linhas.add(lblSalvarPos + ":\n");
        linhas.add("store R5, [" + result + "]\n");
        linhas.add("jmp " + lblFim + "\n");

        // divisão por zero
        linhas.add(lblDivZero + ":\n");
        linhas.add("load R5, 0\n");
        linhas.add("store R5, [" + result + "]\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: ==
     * */
    public static List<String> getCompIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqIgual = getSequenciaIguais();

        linhas.add("jmpEQ R1=R0, igual"+seqIgual+"\n");
        linhas.add("naoIgual"+seqIgual+": store RA, ["+result+"]\n");
        linhas.add("jmp sairIgual"+seqIgual+"\n");
        linhas.add("igual"+seqIgual+": store RB, ["+result+"]\n");
        linhas.add("sairIgual"+seqIgual+":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: >
     * */
    public static List<String> getCompMaior(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMaior = getSequenciaMaiores();

        linhas.add("jmpLE R1<=R0, naoMaior"+seqMaior+"\n");
        linhas.add("maior"+seqMaior+": store RB, ["+result+"]\n");
        linhas.add("jmp sairMaior"+seqMaior+"\n");
        linhas.add("naoMaior"+seqMaior+": store RA, ["+result+"]\n");
        linhas.add("sairMaior"+seqMaior+":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: >=
     * */
    public static List<String> getCompMaiorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMaiorIgual = getSequenciaMaiorIguais();

        linhas.add("jmpEQ R1=R0, maiorIgualIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualNaoIgual: jmpLE R1<=R0, maiorIgualMenor"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualMaior"+seqMaiorIgual+": store RB, ["+result+"]\n");
        linhas.add("jmp sairMaiorIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualMenor"+seqMaiorIgual+": store RA, ["+result+"]\n");
        linhas.add("jmp sairMaiorIgual"+seqMaiorIgual+"\n");
        linhas.add("maiorIgualIgual"+seqMaiorIgual+": store RB, ["+result+"]\n");
        linhas.add("sairMaiorIgual"+seqMaiorIgual+":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: <
     * */
    public static List<String> getCompMenor(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMenor = getSequenciaMenores();

        linhas.add("jmpEQ R1=R0, naoMenorIgual"+seqMenor+"\n");
        linhas.add("menorNaoIgual: jmpLE R1<=R0, menor"+seqMenor+"\n");
        linhas.add("naoMenorMaior"+seqMenor+": store RA, ["+result+"]\n");
        linhas.add("jmp sairMenor"+seqMenor+"\n");
        linhas.add("menor"+seqMenor+": store RB, ["+result+"]\n");
        linhas.add("jmp sairMenor"+seqMenor+"\n");
        linhas.add("naoMenorIgual"+seqMenor+": store RB, ["+result+"]\n");
        linhas.add("sairMenor"+seqMenor+":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: <=
     * */
    public static List<String> getCompMenorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqMenorIgual = getSequenciaMenorIguais();

        linhas.add("jmpLE R1<=R0, menorOuIgual"+seqMenorIgual+"\n");
        linhas.add("naoMenorOuIgual"+seqMenorIgual+": store RA, ["+result+"]\n");
        linhas.add("jmp sairMenorIgual"+seqMenorIgual+"\n");
        linhas.add("menorOuIgual"+seqMenorIgual+": store RB, ["+result+"]\n");
        linhas.add("sairMenorIgual"+seqMenorIgual+":\n");

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UMA COMPARAÇÃO: !=
     * */
    public static List<String> getCompDiferente(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // definir os registradores para os valores de 0 e 1
        //      0 -> falso   -> RA
        //      1 -> verdade -> RB
        linhas.add("load RA, 0\n");
        linhas.add("load RB, 1\n");

        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        if(!isNumero(operDir))
            linhaTraduzida.append("load R0, [").append(operDir).append("]\n");
        else
            linhaTraduzida.append("load R0, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());

        String seqDiferente = getSequenciaDiferentes();

        linhas.add("jmpEQ R1=R0, naoDiferente"+seqDiferente+"\n");
        linhas.add("diferente"+seqDiferente+": store RB, ["+result+"]\n");
        linhas.add("jmp sairDiferente"+seqDiferente+"\n");
        linhas.add("naoDiferente"+seqDiferente+": store RA, ["+result+"]\n");
        linhas.add("sairDiferente"+seqDiferente+":\n");

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

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UM IF
     * */
    public static List<String> getIfFalse(String operEsq, String result)
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
        linhas.add("load R0, 0\n");
        linhaTraduzida.append("jmpEQ R1=R0, ").append(result).append("\n");
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
        linhas.add(result+":\n");

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
        StringBuilder linhaTraduzida;

        linhas.add("return:\n");
        linhaTraduzida = new StringBuilder();

        //carrego o valor de retorno
        if(isNumero(operEsq))
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        else
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        linhas.add(linhaTraduzida.toString());

        linhas.add("store R1, [return]\n");

        return linhas;
    }

    /**
     * RETORNA UM CONJUNTO DE INSTRUÇÕES PARA A OPERAÇÃO BINÁRIA AND
     * */
    public static List<String> getAndComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // 1.
        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA ESQUERDA
        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA ESQUERDA
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA DIREITA
        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA DIREITA
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        //realizar apenas soma de valores inteiros
        linhaTraduzida.append("and R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // ARMAZENAR O RESULTADO NA VARIÁVEL
        linhaTraduzida.append("store R3, [").append(result).append("]\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    /**
     * RETORNA UM CONJUNTO DE INSTRUÇÕES PARA A OPERAÇÃO BINÁRIA OR
     * */
    public static List<String> getOrComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // 1.
        // VERIFICO SE A VARIÁVEL DE RETORNO AINDA NÃO EXISTE
        if(!resultExists(result, codigoAlvo)) // SE NÃO EXISTIR EU CRIO UM LABEL PARA A VARIÁVEL DE DESTINO
            linhas.add(result + ":\n");

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA ESQUERDA
        if(!isNumero(operEsq))
            linhaTraduzida.append("load R1, [").append(operEsq).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA ESQUERDA
            linhaTraduzida.append("load R1, ").append(operEsq).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // VERIFICANDO SE É UM NÚMERO "CRU" OU SE É UMA VARIÁVEL -> OPERADOR DA DIREITA
        if(!isNumero(operDir))
            linhaTraduzida.append("load R2, [").append(operDir).append("]\n");
        else // É UM NÚMERO CRU -> OPERADOR DA DIREITA
            linhaTraduzida.append("load R2, ").append(operDir).append("\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        //realizar apenas soma de valores inteiros
        linhaTraduzida.append("or R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3

        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        // ARMAZENAR O RESULTADO NA VARIÁVEL
        linhaTraduzida.append("store R3, [").append(result).append("]\n");
        linhas.add(linhaTraduzida.toString());

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

    /**
     * MÉTODO QUE VERIFICA SE UMA DETERMINADA STRING É UM NÚMERO
     * */
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

    /**
     * PREENCHE UMA LISTA COM VALORES INTEIROS DE 0 ATÉ 9
     * */
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

    // GETS DE SEQUÊNCIA, PARA IDENTIFICAÇÃO ÚNICA NO CÓDIGO ASSEMBLY
    /**
     * RETORNA O PRÓXIMO ÍNDICE DE MULTIPLICAÇÕES
     * */
    public static String getSequenciaMultiplicacoes()
    {
        return "" + contadorMultiplicacoes++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE DIVISÕES
     * */
    public static String getSequenciaDivisoes()
    {
        return "" + contadorDivisores++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE IGUAIS
     * */
    public static String getSequenciaIguais()
    {
        return "" + contadorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE DIFERENTES
     * */
    public static String getSequenciaDiferentes()
    {
        return "" + contadorDiferentes++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MENORES-IGUAIS
     * */
    public static String getSequenciaMenorIguais()
    {
        return "" + contadorMenorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MAIORES-IGUAIS
     * */
    public static String getSequenciaMaiorIguais()
    {
        return "" + contadorMaiorIguais++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MAIORES
     * */
    public static String getSequenciaMaiores()
    {
        return "" + contadorMaiores++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE COMPARAÇÃO DE MENORES
     * */
    public static String getSequenciaMenores()
    {
        return "" + contadorMenores++;
    }

    /**
     * RETORNA O PRÓXIMO ÍNDICE DE RESTOS DE DIVISÃO
     * */
    public static String getSequenciaRestos()
    {
        return "" + contadorRestos++;
    }
}
