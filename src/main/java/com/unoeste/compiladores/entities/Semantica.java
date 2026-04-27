package com.unoeste.compiladores.entities;

import com.unoeste.compiladores.entities.Pilhas.Pilha;

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
                        // Mudar o valor do identificador
                        String tipoExpressao = "";
                        String valor = ResolveExpressao(indiceTokenIdentificador, tipoExpressao);
                        if(id.getTipo().equals(tipoExpressao))
                        {
                            id.setValor(valor);
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
                        String tipoExpressao = "";
                        String valor = ResolveExpressao(indiceTokenIdentificador, tipoExpressao);
                        if(id.getTipo().equals(tipoExpressao))
                        {
                            id.setValor(valor);
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

    private String ResolveExpressao(int indiceTokenIdentificador, String tipoExpressao)
    {
        List<Token> expressaoPolonesa = FormarExpressao(indiceTokenIdentificador);
        return ResolveExpressaoPolonesa(expressaoPolonesa, tipoExpressao);
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

    private String ResolveExpressaoPolonesa(List<Token> expressaoPolonesa, String tipoExpressao)
    {
        Pilha pilhaOperadores = new Pilha();
        Pilha pilhaNumeros = new Pilha();
        int i=0;

        pilhaNumeros.push(GetValorToken(expressaoPolonesa.get(i++)));
        while(!pilhaNumeros.isEmpty() && i < expressaoPolonesa.size())
        {
            String valorToken = GetValorToken(expressaoPolonesa.get(i++));
            if(isOperador(valorToken))
            {
                if(valorToken.equals("("))
                    pilhaOperadores.push("(");
                else if(valorToken.equals(")"))
                {
                    resolveAteAbreParenteses(pilhaNumeros, pilhaOperadores);
                }
                else if(temMaiorPrecedencia(valorToken, pilhaOperadores.top().getString()))
                {
                    pilhaOperadores.push(valorToken);
                }
                else if(temIgualOuMenorPrecedencia(valorToken, pilhaOperadores.top().getString()))
                {
                    if(!pilhaOperadores.top().getString().equals("("))
                    {
                        //realizar o cálculo com dois valores desempilhados da pilha de Operadores
                        String numero2 = pilhaNumeros.pop().getString();
                        String numero1 = pilhaNumeros.pop().getString();
                        String operador = pilhaOperadores.pop().getString();

                        //calcular a expressão com 3 operadores
                        pilhaNumeros.push(calculaExpressao3Op(numero1, numero2, operador)); // --> "operador" será o operador em questão
                    }

                    //empilho o operador que está chegando
                    pilhaOperadores.push(valorToken);
                }
            }
            else if(isNumero(valorToken))
                pilhaNumeros.push(valorToken);
        }
        resolveAteAcabar(pilhaNumeros, pilhaOperadores);

        return pilhaNumeros.pop().getString();
    }

    private boolean temMaiorPrecedencia(String operador, String operadorEmpilhado)
    {
        //FALTA TRATAR % e /

        // ( )
        // * / %
        // + -

        if(operador.equals("(") || operador.equals(")"))
        {
            if(operadorEmpilhado.equals("(") || operadorEmpilhado.equals(")"))
                return false;
            return true;
        }
        if(operador.equals("*") && (operadorEmpilhado.equals("+") || operadorEmpilhado.equals("-")))
            return true;

        return false;
    }

    private boolean temIgualOuMenorPrecedencia(String operador, String operadorEmpilhado)
    {
        // ( )
        // * / %
        // + -

        if(operadorEmpilhado.equals("*") || operadorEmpilhado.equals("/") || operadorEmpilhado.equals("%"))
        {
            return !operador.equals("(") || !operador.equals(")");
        }
        if(operadorEmpilhado.equals("+") || operadorEmpilhado.equals("-"))
        {
            return operador.equals("+") || operador.equals("-");
        }

        // -> os dois são '*'
        return true;
    }

    private String calculaExpressao3Op(String numero1, String numero2, String valorToken)
    {
        //desenvolver
        return "";
    }

    private void resolveAteAbreParenteses(Pilha pilhaNumeros, Pilha pilhaOperadores)
    {
        String operador = pilhaOperadores.pop().getString();
        while(!operador.equals("("))
        {
            //realizar o cálculo com dois valores desempilhados da pilha de Operadores
            String numero2 = pilhaNumeros.pop().getString();
            String numero1 = pilhaNumeros.pop().getString();

            //calcular a expressão com 3 operadores
            pilhaNumeros.push(calculaExpressao3Op(numero1, numero2, operador)); // --> "operador" será o operador em questão

            //desempilha o operador
            operador = pilhaOperadores.pop().getString();
        }
    }

    private void resolveAteAcabar(Pilha pilhaNumeros, Pilha pilhaOperadores)
    {
        if(!pilhaOperadores.isEmpty())
        {
            do{
                //realizar o cálculo com dois valores desempilhados da pilha de Operadores
                String numero2 = pilhaNumeros.pop().getString();
                String numero1 = pilhaNumeros.pop().getString();
                String operador = pilhaOperadores.pop().getString();

                //calcular a expressão com 3 operadores
                pilhaNumeros.push(calculaExpressao3Op(numero1, numero2, operador)); // --> "operador" será o operador em questão
            } while(!pilhaOperadores.isEmpty());
        }
    }

    private boolean isOperador(String token)
    {
        return switch (token) {
            case "+" -> true;
            case "-" -> true;
            case "*" -> true;
            case "/" -> true;
            case "%" -> true;
            default -> false;
        };
    }

    private boolean isNumero(String token)
    {
        return !switch (token) {
            case "+" -> true;
            case "-" -> true;
            case "*" -> true;
            case "/" -> true;
            case "%" -> true;
            case "(" -> true;
            case ")" -> true;
            default -> false;
        };
    }

    private String GetValorToken(Token token)
    {
        if(token.getToken().equals("t_identificador"))
            return token.getValor();
        return token.getLexema();
    }

    private void OrdenarListaErros()
    {
        // --> ordenar: erroList pelo
    }
}
