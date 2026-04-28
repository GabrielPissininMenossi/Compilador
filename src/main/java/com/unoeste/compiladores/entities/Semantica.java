package com.unoeste.compiladores.entities;

import com.unoeste.compiladores.entities.Pilhas.NoPilha;
import com.unoeste.compiladores.entities.Pilhas.Pilha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantica
{
    private Lexica lexica;
    private List<Erro> erroList;
    private List<String> list_tipos = new ArrayList<>(Arrays.asList("t_void", "t_char", "t_int", "t_float", "t_double"));
    private String tipoExpressao;
    public Semantica(Lexica lexica, List<Erro> erroList)
    {
        this.lexica = lexica;
        this.erroList = erroList;
        this.tipoExpressao = "";
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
                    Token tipo = buscarTipoVariavelInicializacao(indiceTokenIdentificador);
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
                        String valor2 = meuResolveExpressaoPosFixa(indiceTokenIdentificador);
                        if(id.getTipo().equals(tipoExpressao))
                        {
                           id.setValor(valor2);
                        }
                        else
                        {
                            //tratar os erros de tipos incorretos
                            //erro semântico -> tipo incorreto retornado
                            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' esperava tipo '%s', mas retornado '%s'.\n",
                                    id.getLinha(), id.getColuna(), id.getLexema(), id.getTipo(), tipoExpressao),
                                    id.getLinha(), id.getColuna());
                            erroList.add(erro);
                        }
                    }
                }
                else
                {
                    setarTipo(indiceTokenIdentificador);

                    Token id = lexica.getTokens().get(indiceTokenIdentificador);
                    if (isAtribuicao(indiceTokenIdentificador)) // int matheus = 10;
                    {
                        // Mudar o valor do identificador
                        String valor2 = meuResolveExpressaoPosFixa(indiceTokenIdentificador);
                        if(id.getTipo().equals(tipoExpressao) || (id.getTipo().equals("float") && tipoExpressao.equals("double")))
                        {
                            id.setValor(valor2);
                        }
                        else
                        {
                            //tratar os erros de tipos incorretos
                            //erro semântico -> tipo incorreto retornado
                            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' esperava tipo '%s', mas retornado '%s'.\n",
                                    id.getLinha(), id.getColuna(), id.getLexema(), id.getTipo(), tipoExpressao),
                                    id.getLinha(), id.getColuna());
                            erroList.add(erro);
                        }
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

        OrdenarListaErros();
    }

    private List<String> FormarExpressaoPosFixa(int indiceTokenIdentificador)
    {
        List<Token> expressaoInFixa = FormarExpressao(indiceTokenIdentificador);
        List<String> expressaoPosFixa = new ArrayList<>();
        Pilha pilha = new Pilha();
        int i = 0;
        while (i < expressaoInFixa.size())
        {
            String elemento = GetValorToken(expressaoInFixa.get(i++));
            switch (elemento)
            {
                case "+":
                    pilha.push(elemento);
                    break;
                case "-":
                    pilha.push(elemento);
                    break;
                case "*":
                    pilha.push(elemento);
                    break;
                case "/":
                    pilha.push(elemento);
                    break;
                case "%":
                    pilha.push(elemento);
                    break;
                case "(":
                    pilha.push(elemento);
                    break;
                case ")":

                    String aux;
                    do
                    {
                        aux = pilha.pop().getString();
                        if (!aux.equals("("))
                        {
                            expressaoPosFixa.add(aux);
                        }
                    } while (!pilha.isEmpty() && !aux.equals("("));

                    break;
                default: expressaoPosFixa.add(elemento);
            }
        }
        while (!pilha.isEmpty())
        {
            String aux = pilha.pop().getString();
            expressaoPosFixa.add(aux);
        }
        return expressaoPosFixa;

    }
    private String meuResolveExpressaoPosFixa(int indiceTokenIdentificador)
    {
        List<String> expressaoPosFixa = FormarExpressaoPosFixa(indiceTokenIdentificador);
        tipoExpressao = identificarTipoExpressaoPosFixa(expressaoPosFixa);
        Pilha pilha = new Pilha();
        String num1, num2;
        int i = 0;
        while (i < expressaoPosFixa.size())
        {
            String elemento = expressaoPosFixa.get(i++);

            switch (elemento)
            {
                case "+":
                     num2 = pilha.pop().getString();
                     num1 = pilha.pop().getString();
                     pilha.push(calculaExpressao3Op(num1, num2, "+"));
                     break;
                case "-":
                     num2 = pilha.pop().getString();
                     num1 = pilha.pop().getString();
                     pilha.push(calculaExpressao3Op(num1, num2, "-"));
                     break;
                case "/":
                    num2 = pilha.pop().getString();
                    num1 = pilha.pop().getString();
                    pilha.push(calculaExpressao3Op(num1, num2, "/"));
                     break;
                case "*":
                     num2 = pilha.pop().getString();
                     num1 = pilha.pop().getString();
                     pilha.push(calculaExpressao3Op(num1, num2, "*"));
                     break;
                case "%":
                     num2 = pilha.pop().getString();
                     num1 = pilha.pop().getString();
                     pilha.push(calculaExpressao3Op(num1, num2, "%"));
                     break;
                default: pilha.push(elemento);
            }
            
        }

        return pilha.pop().getString(); // int ou double ou float
    }

    private boolean isDouble(String string)
    {
        return string.contains(".");
    }

    private String identificarTipoExpressaoPosFixa(List<String> expressaoPosFixa)
    {
        for (int i = 0; i < expressaoPosFixa.size(); i++)
        {
            String aux = expressaoPosFixa.get(i);
            if(aux.contains(".")) // double, float
            {
                return "double";
            }

        }
        return "int";

    }

    private void setarTipo(int indiceTokenIdentificador)
    {
        Token tokenTipo = buscarTipoVariavelInicializacao(indiceTokenIdentificador);
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

    private Token buscarTipoVariavelInicializacao(int indiceTokenIdentificador)
    {
        int i = indiceTokenIdentificador;
        List<Token> tokenList = lexica.getTokens();

        while (i > 0 && !list_tipos.contains(tokenList.get(i).getToken()) &&
                !tokenList.get(i).getToken().equals("t_pontoVirgula") &&
                !tokenList.get(i).getToken().equals("t_abreChave") &&
                !tokenList.get(i).getToken().equals("t_fechaChave") &&
                !tokenList.get(i).getToken().equals("t_igualAtribuicao"))
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
            if (tokenValor.getToken().equals("t_identificador"))
            {
                token.setValor(buscarValorIdentificador(tokenValor));
            }
            else
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

    private List<Token> FormarExpressao(int indiceTokenIdentificador)
    {
        List<Token> expressaoPolonesa = new ArrayList<>();
        int i = indiceTokenIdentificador + 2;
        while(i < lexica.getTokens().size() && !lexica.getToken(i).getToken().equals("t_pontoVirgula"))
        {
            expressaoPolonesa.add(lexica.getToken(i));
            i++;
        }
        return expressaoPolonesa;
    }

    private String calculaExpressao3Op(String numero1, String numero2, String valorToken)
    {
        if (isDouble(numero1) || isDouble(numero2))
        {
            double num1 = Double.parseDouble(numero1);
            double num2 = Double.parseDouble(numero2);
            double res = 0.0;
            switch (valorToken)
            {
                case "+":
                    res = num1 + num2;
                    break;
                case "-":
                    res = num1 - num2;
                    break;
                case "*":
                    res = num1 * num2;
                    break;
                case "/":
                    res = num1 / num2;
                    break;
                case "%":
                    res = num1 % num2;
                    break;
            }
            return String.valueOf(res);
        }
        else
        {
            int num1 = Integer.parseInt(numero1);
            int num2 = Integer.parseInt(numero2);
            int res = 0;
            switch (valorToken)
            {
                case "+":
                    res = num1 + num2;
                    break;
                case "-":
                    res = num1 - num2;
                    break;
                case "*":
                    res = num1 * num2;
                    break;
                case "/":
                    res = num1 / num2;
                    break;
                case "%":
                    res = num1 % num2;
                    break;
            }
            return String.valueOf(res);
        }

    }

    private String buscarValorIdentificador(Token token)
    {
        List<Token> tokenList = lexica.getTokens();
        int pos = tokenList.indexOf(token);
        pos--;
        while (pos > 0 && !tokenList.get(pos).getLexema().equals(token.getLexema()))
            pos--;
        if (pos > 0)
        {
            return tokenList.get(pos).getValor();
        }
        return "";
    }
    private String GetValorToken(Token token)
    {
        if(token.getToken().equals("t_identificador"))
        {
            return buscarValorIdentificador(token);
        }

        return token.getLexema();
    }

    //maior linha pra menor
    private void OrdenarListaErros()
    {
        Erro aux;
        for (int i =0; i < erroList.size(); i++)
        {
            int j = i;
            while(j < erroList.size())
            {
               if(erroList.get(i).getLinha() <  erroList.get(j).getLinha())
               {
                   aux = erroList.get(i);
                   erroList.set(i, erroList.get(j));
                   erroList.set(j,aux);
               }
                j++;
            }
        }
    }
}
