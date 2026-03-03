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
    private List<String> outros = new ArrayList<>(Arrays.asList("{", "}", ",", "=", ";", ".", "(", ")"));
    private List<Token> tokens = new ArrayList<>();

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
        while (i < cadeia.length())
        {



            i++;
        }
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


