package com.unoeste.compiladores.phases.Sintese;

import java.util.ArrayList;
import java.util.List;

/**
 * Dicionário de instruções, de valores TAC (Three Address Code) para instruções Simple Assembler
 * */
public class DicionarioCodigoAlvo {
    public static List<Character> list_numeros;
    public static int contadorContinua = 0;

    /**
     * RETORNA AS INSTRUÇÕES PARA SE REALIZAR UMA SOMA COM DIFERENTES VARIÁVEIS
     * */
    public static List<String> getSomaComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        preencheNumeros();

        if(!resultExists(result, codigoAlvo))
        {
            // criar um label para o resultado final
            linhaTraduzida.append(result).append(":").append("\n");
            linhas.add(linhaTraduzida.toString());
            linhaTraduzida = new StringBuilder();
        }

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

        if(isDouble(operDir) || isDouble(operEsq)) //realizar adição de valores double
        {
            linhaTraduzida.append("addf R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3
        }
        else //realizar adição de valores inteiros
            linhaTraduzida.append("addi R3, R1, R2\n"); // adição entre dois registradores -> armazenar em R3
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();
        linhaTraduzida.append("store R3, [").append(result).append("]\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    public static List<String> getSubComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        preencheNumeros();

        if(!resultExists(result, codigoAlvo))
        {
            // criar um label para o resultado final
            linhaTraduzida.append(result).append(":").append("\n");
            linhas.add(linhaTraduzida.toString());
            linhaTraduzida = new StringBuilder();
        }

        return linhas;
    }

    public static List<String> getMultComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        preencheNumeros();

        if(!resultExists(result, codigoAlvo))
        {
            // criar um label para o resultado final
            linhaTraduzida.append(result).append(":").append("\n");
            linhas.add(linhaTraduzida.toString());
            linhaTraduzida = new StringBuilder();
        }

        return linhas;
    }

    public static List<String> getDivComVariaveis(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();
        preencheNumeros();

        if(!resultExists(result, codigoAlvo))
        {
            // criar um label para o resultado final
            linhaTraduzida.append(result).append(":").append("\n");
            linhas.add(linhaTraduzida.toString());
            linhaTraduzida = new StringBuilder();
        }

        return linhas;
    }

    /**
     * Método que verifica na minha lista de codigo alvo de o result já foi declarado
     * */
    private static boolean resultExists(String result, List<String> codigoAlvo)
    {

        return true; //provisório
    }

    public static List<String> getCompIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    public static List<String> getCompMaior(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    public static List<String> getCompMaiorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    public static List<String> getCompMenor(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    public static List<String> getCompMenorIgual(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    public static List<String> getCompDiferente(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES PARA SER REALIZADO UMA ATRIBUIÇÃO.
     * Sendo:
     *      carregar o valor a ser passado (operEsq),
     *      move para o destino (result) o valor a ser passado (operEsq)
     * */
    public static List<String> getAtribuicao(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        linhaTraduzida.append("load R2, [").append(operEsq).append("]\n");
        linhas.add(linhaTraduzida.toString());
        linhaTraduzida = new StringBuilder();

        linhaTraduzida.append("move ").append("[").append(result).append("], ").append("R2\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    public static List<String> getIfFalse(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTES A UM LABEL.
     * Sendo:
     *      label (result)
     * */
    public static List<String> getLabel(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        linhaTraduzida.append(result).append("\n");
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    /**
     * RETORNA O CONJUNTO DE INSTRUÇÕES EQUIVALENTE PARA UM GOTO.
     * Sendo:
     *      jmp,
     *      destino do jmp (result)
     * */
    public static List<String> getGoto(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        linhaTraduzida.append("jmp ").append(result).append("\n"); // RESULT SERÁ O LOCAL DE DESTINO DO JUMP
        linhas.add(linhaTraduzida.toString());

        return linhas;
    }

    public static List<String> getReturn(String operEsq, String operDir, String result, List<String> codigoAlvo)
    {
        List<String> linhas = new  ArrayList<>();
        StringBuilder linhaTraduzida = new StringBuilder();

        // lógica aqui

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

    private static String getValorVariavel(String operador, List<String> codigoAlvo)
    {
        // andar e procurar o respectivo operEsq
        int i=0;
        while(i<codigoAlvo.size() && !codigoAlvo.get(i).startsWith(operador))
            i++;

        if(i<codigoAlvo.size())
            return codigoAlvo.get(i).substring(operador.length());

        return null;
    }

    private static boolean isDouble(String numero)
    {
        double valorDouble = Double.parseDouble(numero);
        int parteInteira = (int)valorDouble;
        return valorDouble-parteInteira > 0;
    }

    public static String getSequenciaContinua()
    {
        return "continua"+contadorContinua++;
    }
}
