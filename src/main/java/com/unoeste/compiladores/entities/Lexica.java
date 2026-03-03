package com.unoeste.compiladores.entities;

import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexica
{
    private List<Character> numeros = new ArrayList<>(); //numeros válidos
    private List<Character> letras = new ArrayList<>();
    private List<String> opRelacional = new ArrayList<>(Arrays.asList("<", ">", "<=", ">=", "==", "!="));
    private List<String> tipos = new ArrayList<>(Arrays.asList("void", "char", "short", "int", "long", "float", "double"));
    private List<String> comandosReservados = new ArrayList<>(Arrays.asList("while", "if", "else"));
    private List<String> opMatematicos = new ArrayList<>(Arrays.asList("*", "+", "-", "/", "%", "!"));
    private List<Character> unitarios = new ArrayList<>(Arrays.asList('{', '}', ',', '=', ';', '.', '(', ')', '"'));
    private List<Token> tokens = new ArrayList<>();
    private List<Character> especiais = new ArrayList<>(Arrays.asList('@', '#', '$'));

    //exemplo de execução no main
//    for(linha in linhas) // a partir do token, definir a cor
//    {
//        lexica.separaTokens(linha);
//
//    }
//    lexica.getTokens()

    public Token getToken(int pos)
    {
        return tokens.get(pos);
    }

    //separar os tokens
    public void separarCadeias(String linha)
    {
        int i = 0;
        String cadeia = "";
        while (i < linha.length())
        {
            char c = linha.charAt(i);
            if (c != ' ')
            {
                cadeia = cadeia + c;
            }
            else
            {
                separarTokens(cadeia);
                cadeia = "";
            }


            i++;
        }
        if (!cadeia.isEmpty())
        {
            separarTokens(cadeia);
        }
    }
    private void separarTokens(String cadeia)
    {
        int i = 0;
        String token = "";
        while (i < cadeia.length())
        {
            char c = cadeia.charAt(i);

            if(!letras.contains(c) && !numeros.contains(c))
            {
                if (!token.isEmpty())
                {
                    // adiciona (funcao para validar token)
                    addToken(token);
                    System.out.println(token);
                    token = "";

                }
                token += c;

                // <= , >= , ==, !=
                if (cadeia.length() > i + 1 && cadeia.charAt(i + 1) == '=')
                {
                    token += cadeia.charAt(i + 1);
                    i++;

                }
                // adiciona (funcao para validar token)
                addToken(token);
                System.out.println(token);
                token = "";
            }
            else
            {
                if(token.isEmpty() && !primeiroDigitoValido(c)) //token inválido -> marcar a linha
                {
                    System.out.println("Caracter Invalido: "+c);
                }
                else
                {
                    token += c;
                }
            }

            i++;
        }
        if (!token.isEmpty())
        {
            addToken(token);
            System.out.println(token); // adiciona (funcao para validar token)
        }
    }

    private void addToken(String token)
    {
        Token novoToken = new Token(verificarCategoria(token), token);
        tokens.add(novoToken);
    }

    public String verificarCategoria(String token)
    {
        if(opRelacional.contains(token))
            verificarSubRelacional(token);

//        if(opRelacional.contains(token))
//            verificarSubRelacional(token);
//
//        if(opRelacional.contains(token))
//            verificarSubRelacional(token);
//
//        if(opRelacional.contains(token))
//            verificarSubRelacional(token);
//
        return "";
    }

    //sub verificações
    public String verificarSubRelacional(String token)
    {
        if(token.equals(">")) return "t_maior";
        if(token.equals(">=")) return "t_maiorIgual";
        if(token.equals("<")) return "t_menor";
        if(token.equals("<=")) return "t_menorIgual";
        if(token.equals("==")) return "t_igual";
        if(token.equals("!=")) return "t_diferente";

        return "";
    }

    public boolean primeiroDigitoValido(char c)
    {
        if(c == '_')
            return false;

        return !(c >= 65 && c <= 90); //primeira letra é maiúscula
    }

    public Lexica()
    {
        preencheListas();
    }

    private boolean isLetra(Character character)
    {
        return letras.contains(character);
    }

    private boolean isNumero(Character character)
    {
        return numeros.contains(character);
    }

    private boolean isOpRelacional(String string)
    {
        return opRelacional.contains(string);
    }

    private boolean isComandosReservados(String string)
    {
        return comandosReservados.contains(string);
    }

    private boolean isOpMatematico(String string)
    {
        return opMatematicos.contains(string);
    }

    private void preencheListas()
    {
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            numeros.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'A' até o 'Z'
        {
            char character = (char)(i+65);
            letras.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'a' até o 'z'
        {
            char character = (char)(i+97);
            letras.add(character);
        }

        //adiciona o "underline" como uma exeção
        letras.add('_');
    }
}


