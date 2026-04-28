package com.unoeste.compiladores.phases;

import com.unoeste.compiladores.entities.Erro;
import com.unoeste.compiladores.stacks.Pilha;
import com.unoeste.compiladores.entities.Simbolo;
import com.unoeste.compiladores.entities.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantica
{
    private Lexica lexica;
    private List<Erro> erroList;
    private List<String> list_tipos = new ArrayList<>(Arrays.asList("t_void", "t_char", "t_int", "t_float", "t_double"));
    private String tipoExpressao;
    private List<Simbolo> tabelaSimbolos;

    public Semantica(Lexica lexica, List<Erro> erroList, List<Simbolo> tabelaSimbolos)
    {
        this.lexica = lexica;
        this.erroList = erroList;
        this.tipoExpressao = "";
        this.tabelaSimbolos = tabelaSimbolos;
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
                        // MUDAR O VALOR DO IDENTIFICADOR

                        // resolvo a expressão logo após o sinal de atribuição
                        String valorExpressao = meuResolveExpressaoPosFixa(indiceTokenIdentificador);

                        // recupero o símbolo da minha tabela com o respectivo lexema do token em questão
                        Simbolo simbolo = GetSimboloWithThisToken(id);

                        // se o tipo coincidir, então seto o novo valor
                        if(simbolo != null && simbolo.getTipo().equals(tipoExpressao))
                        {
                           simbolo.setValor(valorExpressao);
                        }
                        else
                        {
                            //erro semântico -> tipo incorreto retornado

                            assert simbolo != null; // --> garante que simbolo não pode ser nulo
                            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' esperava tipo '%s', mas retornado '%s'.\n",
                                    id.getLinha(), id.getColuna(), id.getLexema(), simbolo.getTipo(), tipoExpressao),
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
                        // MUDAR O VALOR DO IDENTIFICADOR

                        // resolvo a expressão logo após o sinal de atribuição
                        String valorExpressao = meuResolveExpressaoPosFixa(indiceTokenIdentificador);

                        // recupero o símbolo da minha tabela com o respectivo lexema do token em questão
                        Simbolo simbolo = GetSimboloWithThisToken(id);

                        if(simbolo != null && simbolo.getTipo().equals(tipoExpressao) || (simbolo.getTipo().equals("float") && tipoExpressao.equals("double")))
                        {
                            simbolo.setValor(valorExpressao);
                        }
                        else
                        {
                            //erro semântico -> tipo incorreto retornado
                            assert simbolo != null;
                            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' esperava tipo '%s', mas retornado '%s'.\n",
                                    id.getLinha(), id.getColuna(), id.getLexema(), simbolo.getTipo(), tipoExpressao),
                                    id.getLinha(), id.getColuna());
                            erroList.add(erro);
                        }
                    }
                }
            }

        } while (indiceTokenIdentificador != -1);

        for(Simbolo simbolo : tabelaSimbolos)
        {
            if(simbolo.getValor().isEmpty())
            {
                //erro semântico -> variável nunca utilizada
                Erro erro = new Erro(String.format("[ALERTA SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' nunca utilizado.\n",
                        simbolo.getToken().getLinha(), simbolo.getToken().getColuna(), simbolo.getToken().getLexema()),
                        simbolo.getToken().getLinha(), simbolo.getToken().getColuna());
                erroList.add(erro);
            }
        }

        OrdenarListaErros();
    }


    private int prioridade(String op)
    {
        //número maior igual a  maior prioridade
        if(op.equals("+") || op.equals("-"))
            return 1;
        else if(op.equals("*") || op.equals("/") || op.equals("%"))
            return 2;
        else
            return 0;//0 pois quando encontra ( não retira da pilha
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
                //igual if com ||
                case "+":
                case "-":
                case "*":
                case "/":
                case "%":
                    //Se o operador do topo for diferente de ( e tem prioridade maior ou igual, resolve ele primeiro
                    while (!pilha.isEmpty() && !pilha.top().getString().equals("(") &&
                            prioridade(pilha.top().getString()) >= prioridade(elemento))
                    {
                        expressaoPosFixa.add(pilha.pop().getString());
                    }
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

        if (tokenTipo != null)
        {
            //adicionar o token com o seu tipo na tabela de símbolos
            tabelaSimbolos.add(new Simbolo(tokenAtual, tokenTipo.getLexema(), ""));
        }
        else
        {
            Erro erro = new Erro(String.format("[ERRO SEMÂNTICO] Linha %d, Coluna %d: Identificador '%s' não declarado.\n",
                    tokenAtual.getLinha(), tokenAtual.getColuna(), tokenAtual.getLexema()),
                    tokenAtual.getLinha(), tokenAtual.getColuna());
            erroList.add(erro);
        }
    }

    private void setarValor(Token token, String valor)
    {
        int i=0;

        // buscando pelo token identificador na tabela de símbolos
        while(i < tabelaSimbolos.size() && !tabelaSimbolos.get(i).getToken().getLexema().equals(token.getLexema()))
            i++;

        // se achou
        if(i < tabelaSimbolos.size())
            tabelaSimbolos.get(i).setValor(valor);

        // não achou
    }

    private Simbolo GetSimboloWithThisToken(Token token)
    {
        int i=0;
        while(i < tabelaSimbolos.size() && !tabelaSimbolos.get(i).getToken().getLexema().equals(token.getLexema()))
            i++;

        // se achou
        if(i < tabelaSimbolos.size())
            return tabelaSimbolos.get(i);

        // não achou
        return null;
    }

    private boolean isDeclarado(int indiceTokenIdentificador)
    {
        if(tabelaSimbolos.isEmpty())
            return false;

        Token token = lexica.getToken(indiceTokenIdentificador);

        int pos = 0;
        while(pos < tabelaSimbolos.size() && !tabelaSimbolos.get(pos).getToken().getLexema().equals(token.getLexema()))
            pos++;

        // se ele não chegou no final e o tipo NÃO está vazio --> está declarado
        if(pos < tabelaSimbolos.size() && !tabelaSimbolos.get(pos).getTipo().isEmpty())
            return true;
        else
            return false;
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
        if (indiceTokenIdentificador + 1 < tokenList.size() && tokenList.get(indiceTokenIdentificador + 1).getToken().equals("t_igualAtribuicao"))
        {
            // verificar valor, dps
            Token token = tokenList.get(indiceTokenIdentificador);
            Token tokenValor = tokenList.get(indiceTokenIdentificador + 2);


            // esse if e else irá tratar o caso de quando existe apenas um token após o sinal de atribuição (não precisava ser implementado)
            if (tokenValor.getToken().equals("t_identificador"))
            {
                // pego o valor do próximo token
                String valor = buscarValorIdentificador(tokenValor);

                // seto o valor do "token" para o valor encontrado do "tokenValor"
                setarValor(token, valor);
            }
            else
                setarValor(token, tokenValor.getLexema());


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
        int i=0;

        // buscando pelo token identificador na tabela de símbolos
        while(i < tabelaSimbolos.size() && !tabelaSimbolos.get(i).getToken().getLexema().equals(token.getLexema()))
            i++;

        // se achou
        if(i < tabelaSimbolos.size())
            return tabelaSimbolos.get(i).getValor();

        // não achou
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
