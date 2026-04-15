package com.unoeste.compiladores.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantica
{
    private Lexica lexica;
    private List<Erro> erroList;
    private List<Token> visitados;
    private List<String> list_tipos = new ArrayList<>(Arrays.asList("t_void", "t_char", "t_int", "t_float", "t_double"));

    public Semantica(Lexica lexica, List<Erro> erroList)
    {
        this.lexica = lexica;
        this.erroList = erroList;

    }

    public void analisarSemantico()
    {
        // Verificacao de Tipos
        int pos = 0;
        int indiceTokenIdentificador;
        do
        {
            indiceTokenIdentificador = buscarPosTokenIdentificador(pos);
            pos = indiceTokenIdentificador + 1; // a partir do identificador encontrado
            if (indiceTokenIdentificador != -1)
            {
                if (isDeclaracao(indiceTokenIdentificador))
                {
                    if (isAtribuicao(indiceTokenIdentificador)) // int matheus = 10;
                    {


                    }
                    else // int matheus, x;
                    {

                    }
                }
                else
                if (isAtribuicao(indiceTokenIdentificador)) // matheus = 10;
                {

                }
                else // x = x + matheus;
                {

                }
                // primeiro caso: foi apenas declarado
                // segundo caso: foi apenas atribuido, mas ja declarado
                // teceiro caso: foi declarado e atribuido
                // quarto caso: esta sendo utilizado, porem ja declarado
            }


        } while (indiceTokenIdentificador != -1);

    }

    private boolean isDeclaracao(int indiceTokenIdentificador)
    {
        Token tokenTipo = buscarTipoVariavel(indiceTokenIdentificador);
        if (tokenTipo != null) // achei um token identificador com seu tipo declarado
        {
            Token tokenAtual = lexica.getToken(indiceTokenIdentificador);
            if (tokenAtual.getTipo().isEmpty())
            {
                tokenAtual.setTipo(tokenTipo.getLexema());
                return true;
            }

        }
        return false;
    }

    private Token buscarTipoVariavel(int indiceTokenIdentificador)
    {
        int i = indiceTokenIdentificador;
        List<Token> tokenList = lexica.getTokens();
        while (i > 0 && !list_tipos.contains(tokenList.get(i).getToken()))
            i--;
        if (i > 0) // achou um token
        {
            if (tokenList.get(i-1).getToken().equals("t_pontoVirgula") ||
                tokenList.get(i-1).getToken().equals("t_abreChave") ||
                tokenList.get(i-1).getToken().equals("t_fechaChave"))
            {
                return tokenList.get(i);
            }
        }
        return null;

    }

    private boolean isAtribuicao(int indiceTokenIdentificador)
    {

        List<Token> tokenList = lexica.getTokens();
        if (indiceTokenIdentificador + 1 < tokenList.size()  && tokenList.get(indiceTokenIdentificador + 1).getToken().equals("t_igualAtribuicao"))
        {
            // verificar valor, dps
            Token token = tokenList.get(indiceTokenIdentificador);
            Token tokenValor = tokenList.get(indiceTokenIdentificador + 2);
            token.setValor(tokenValor.getLexema());
            return true;
        }
        return false;

    }

    private int buscarPosTokenIdentificador(int pos)
    {
        List<Token> tokenList = lexica.getTokens();
        int i = pos;
        while (i < tokenList.size() && !tokenList.get(i).getToken().equals("t_identificador"))
            i++;
        if (i < tokenList.size())
            return i;
        return -1;

    }


}
