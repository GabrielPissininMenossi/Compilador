package com.unoeste.compiladores.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantica
{
    private Lexica lexica;
    private Token tokenAtual;
    private List<Erro> erroList;
    private List<Token> visitados;
    private List<String> list_tipos = new ArrayList<>(Arrays.asList("t_void", "t_char", "t_int", "t_float", "t_double"));
    private int pos;
    public Semantica(Lexica lexica, List<Erro> erroList)
    {
        this.lexica = lexica;
        this.erroList = erroList;
        this.tokenAtual = null;
        this.pos = 0;
    }
    private int buscarIdentificador(int pos)
    {
        int aux = pos;
        tokenAtual =  lexica.getToken(aux);
        while (aux < lexica.getTokens().size() && !tokenAtual.getToken().equals("t_identificador"))
            tokenAtual = lexica.getToken(++aux);
        if (aux < lexica.getTokens().size())
            return aux;
        return -1;
    }

    public void analisarSemantico()
    {
        int posAux;
        do
        {
            posAux = buscarIdentificador(pos);
            pos = posAux + 1;
            if (posAux != -1) // achou um id
            {
                System.out.println("Token ID: "+tokenAtual.getLexema());
                int posInicializado = isInicializado(posAux);
                if (posInicializado != -1)
                {
                    if (isAnteriorTipo(posInicializado))
                    {
                        System.out.println("entrou: erro semantico");
                        if (isProximoAtribuicao(posInicializado))
                        {
                            // atualizar valor
                        }
                    }
                    else
                    {
                        // erro semantico

                    }

                }

            }



        }while (posAux != -1);

    }

    private boolean isProximoAtribuicao(int posInicializado)
    {
        return lexica.getToken(posInicializado + 1).getToken().equals("t_igualAtribuicao");
    }

    private boolean isAnteriorTipo(int posInicializado)
    {
        return list_tipos.contains(lexica.getToken(posInicializado - 1).getToken());
    }

    private int isInicializado(int pos)
    {
       int posAux = 0;
       while (posAux < pos && !lexica.getToken(posAux).getLexema().equals(tokenAtual.getLexema()))
           posAux++;
       if (posAux < pos)
           return posAux;
       return -1;
    }

}
