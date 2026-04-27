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
                if (isDeclarado(indiceTokenIdentificador))
                {
                    Token tipo = buscarTipoVariavel(indiceTokenIdentificador);
                    Token id = lexica.getTokens().get(indiceTokenIdentificador);

                    if(tipo != null)
                    {
                        //erro semântico -> variável já declarada
                        Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' já declarado.\n",
                                id.getLinha(), id.getColuna(), id.getLexema()),
                                id.getLinha(), id.getColuna());
                        erroList.add(erro);
                    }

                    if (isAtribuicao(indiceTokenIdentificador)) // matheus = 10;
                    {
                        // Mudar o valor do identificador

                        // Verificar se a expressão retorna o tipo correto da variável
                    }
                }
                else
                {
                    setarTipo(indiceTokenIdentificador);

                    if (isAtribuicao(indiceTokenIdentificador)) // int matheus = 10;
                    {
                        // Mudar o valor do identificador

                        // Verificar se a expressão retorna o tipo correto da variável
                    }
                }
            }

        } while (indiceTokenIdentificador != -1);

        // Verificação de Não Utilização
        // Reconhecer identificadores sem valores
        for(Token token : lexica.getTokens())
        {
            if(token.getToken().equals("t_identificador") && token.getValor().isEmpty())
            {
                //erro semântico -> variável nunca utilizada
                Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' nunca utilizado.\n",
                        token.getLinha(), token.getColuna(), token.getLexema()),
                        token.getLinha(), token.getColuna());
                erroList.add(erro);
            }
        }
    }

    private void setarTipo(int indiceTokenIdentificador)
    {
        Token tokenTipo = buscarTipoVariavel(indiceTokenIdentificador);
        Token tokenAtual = lexica.getToken(indiceTokenIdentificador);

        if (tokenTipo != null && tokenAtual.getTipo().isEmpty())
        {
            tokenAtual.setTipo(tokenTipo.getLexema());
        }
        else
        {
            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' não declarado.\n",
                    tokenAtual.getLinha(), tokenAtual.getColuna(), tokenAtual.getLexema()),
                    tokenAtual.getLinha(), tokenAtual.getColuna());
            erroList.add(erro);
        }
    }

    private boolean isDeclarado(int indiceTokenIdentificador)
    {
        List<Token> tokens = lexica.getTokens();
        Token token = lexica.getToken(indiceTokenIdentificador);

        int pos = 0;
        while(pos < tokens.size() && !tokens.get(pos).getLexema().equals(token.getLexema()))
            pos ++;

        if(pos < tokens.size() && tokens.get(pos).getTipo().isEmpty()) //achei
        {
            return false;
        }
        else
        {
            token.setTipo(tokens.get(pos).getTipo());
            token.setValor(tokens.get(pos).getValor());

            return true;
        }
    }

    private Token buscarTipoVariavel(int indiceTokenIdentificador)
    {
        int i = indiceTokenIdentificador;
        List<Token> tokenList = lexica.getTokens();

        while (i > 0 && !list_tipos.contains(tokenList.get(i).getToken()) &&
                !tokenList.get(i).getToken().equals("t_pontoVirgula") &&
                !tokenList.get(i).getToken().equals("t_abreChave") &&
                !tokenList.get(i).getToken().equals("t_fechaChave"))
            i--;

        if (i > 0 && list_tipos.contains(tokenList.get(i).getToken())) // achou um token
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
