package com.unoeste.compiladores.entities;

import java.util.Arrays;
import java.util.List;

public class Sintatico
{
    private  Lexica lexica;
    private  Pilha pilha;
    private  Token tokenAtual;
    private  List<Erro> erroList;

    public Sintatico(Lexica lexica, List<Erro> erroList) {
        this.lexica = lexica;
        this.pilha = new Pilha();
        this.erroList = erroList;

    }

    private List<String> getProducoes(String topo, Token token)
    {
        String categoria = token.getToken();
        switch (topo)
        {
            case "PROGRAMA": // olha para o meu first
                if (categoria.equals("t_int"))
                {
                    return Arrays.asList("int","main");
                }
            break;


        }
        return null;
    }
    public void analisarSintatico()
    {
        int pos = 0;
        //pilha.push("$");
        pilha.push("PROGRAMA"); // meu inicio
        tokenAtual = lexica.getToken(pos++);
        while (!pilha.isEmpty())
        {
            NoPilha noPilha = pilha.pop();
            String topo = noPilha.getString();
            if (tokenAtual != null)
            {
                if (isTerminal(topo))
                {
                    if (topo.equals(tokenAtual.getLexema()))
                    {
                        tokenAtual = lexica.getToken(pos++);
                    }
                    else
                    {
                        Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Esperado: '%s', mas encontrado '%s'.\n",tokenAtual.getLinha(), tokenAtual.getColuna(), topo, tokenAtual.getLexema())
                                , tokenAtual.getLinha(), tokenAtual.getColuna());
                        erroList.add(erro);
                    }

                }
                else // Nao Terminal
                {
                    List<String> producoes = getProducoes(topo, tokenAtual);
                    if (producoes != null)
                    {
                        int i = producoes.size() - 1;
                        while (i >= 0)
                        {
                            pilha.push(producoes.get(i));
                            i--;
                        }
                    }
                    else
                    {
                        Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Inesperado '%s' na estrutura '%s'.\n",
                                tokenAtual.getLinha(), tokenAtual.getColuna(), tokenAtual.getLexema(), topo),
                                tokenAtual.getLinha(), tokenAtual.getColuna());
                        erroList.add(erro);
                    }
                }

            }

        }
    }

    private boolean isTerminal(String topo)
    {
        return topo.equals(topo.toLowerCase());
    }
}
