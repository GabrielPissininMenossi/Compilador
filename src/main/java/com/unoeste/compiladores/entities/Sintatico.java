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
                    return Arrays.asList("int","main", "(", ")" ,"BLOCO");
                }
            break;
            case "BLOCO":
                if (categoria.equals("t_abreChave"))
                {
                    return Arrays.asList("{", "REPETICAO_COMANDO", "}");
                }
            break;
            case "REPETICAO_COMANDO":
                if (categoria.equals("t_void") || categoria.equals("t_char") || categoria.equals("t_int")
                        || categoria.equals("t_float") || categoria.equals("t_double") || categoria.equals("t_identificador")
                        || categoria.equals("t_if") || categoria.equals("t_while") || categoria.equals("t_return") || categoria.equals("t_abreChave"))
                {
                    return Arrays.asList("COMANDO", "REPETICAO_COMANDO");
                }
                else // follow
                if (categoria.equals("t_fechaChave"))
                {
                    return Arrays.asList("EPSILON");
                }
                break;
            case "COMANDO":
                if (categoria.equals("t_void") || categoria.equals("t_char") || categoria.equals("t_int")
                        || categoria.equals("t_float") || categoria.equals("t_double"))
                {
                    return Arrays.asList("DECLARACAOVARIAVEL");
                }
                else
                if (categoria.equals("t_identificador"))
                {
                    return Arrays.asList("DECLARACAOATRIBUICAO");
                }
                else
                if (categoria.equals("t_if"))
                {
                    return Arrays.asList("DECLARACAOSELECAO");
                }
                else
                if (categoria.equals("t_while"))
                {
                    return Arrays.asList("DECLARACAOITERACAO");
                }
                else
                if (categoria.equals("t_return"))
                {
                    return Arrays.asList("DECLARACAORETORNO");
                }
                else
                if (categoria.equals("t_abreChave"))
                {
                    return Arrays.asList("BLOCO");
                }
                break;
            case "DECLARACAOVARIAVEL":
                {
                    return Arrays.asList("TIPOVARIAVEL", "IDENTIFICADOR", "OPCAO_ATRIBUICAO", "REPETICAO_VARIAVEL", ";");
                }
            case "TIPOVARIAVEL":
                if (categoria.equals("t_void"))
                    return Arrays.asList("void");
                else
                if (categoria.equals("t_char"))
                    return Arrays.asList("char");
                else
                if (categoria.equals("t_int"))
                    return Arrays.asList("int");
                else
                if (categoria.equals("t_float"))
                    return Arrays.asList("float");
                else
                if (categoria.equals("t_double"))
                    return Arrays.asList("double");
                break;
            case "OPCAO_ATRIBUICAO":
                if (categoria.equals("t_igualAtribuicao"))
                {
                    return Arrays.asList("=", "EXPRESSAOLOGICA");
                }
                else // como é opcional
                if (categoria.equals("t_virgula") || categoria.equals("t_pontoVirgula"))
                    return Arrays.asList("EPSILON");
                break;
            case "REPETICAO_VARIAVEL":
                if (categoria.equals("t_virgula")) // first
                {
                    return Arrays.asList(",", "IDENTIFICADOR", "OPCAO_ATRIBUICAO", "REPETICAO_VARIAVEL");
                }
                else // follow
                if (categoria.equals("t_pontoVirgula"))
                {
                    return Arrays.asList("EPSILON");
                }
                break;
            case "DECLARACAOATRIBUICAO":
                return Arrays.asList("IDENTIFICADOR", "=", "EXPRESSAOLOGICA", ";");
            case "DECLARACAOSELECAO":
                if (categoria.equals("t_if"))
                {
                    return Arrays.asList("if", "(", "EXPRESSAOLOGICA", ")", "COMANDO", "OPCAO_ELSE");
                }
                break;
            case "OPCAO_ELSE":
                if (categoria.equals("t_else"))
                {
                    return Arrays.asList("else", "COMANDO");
                }
                else
                {
                    return Arrays.asList("EPSILON");
                }
            case "DECLARACAOITERACAO":
                if (categoria.equals("t_while"))
                {
                    return Arrays.asList("while", "(", "EXPRESSAOLOGICA", ")", "COMANDO");
                }
                break;
            case "DECLARACAORETORNO": // olha o first
                if (categoria.equals("t_return"))
                {
                    return Arrays.asList("return", "EXPRESSAOLOGICA");
                }
                break;
            case "EXPRESSAOLOGICA":
                return Arrays.asList("TERMOLOGICO", "REPETICAO_EXPRESSAOLOGICA");
            case "REPETICAO_EXPRESSAOLOGICA": // first
                if (categoria.equals("t_or"))
                {
                    return Arrays.asList("||", "TERMOLOGICO", "REPETICAO_EXPRESSAOLOGICA");
                }
                else
                {
                    return Arrays.asList("EPSILON");
                }
            case "TERMOLOGICO":
                return Arrays.asList("EXPRESSAORELACIONAL", "REPETICAO_TERMOLOGICO");
            case "REPETICAO_TERMOLOGICO":
                if (categoria.equals("t_and"))
                {
                    return Arrays.asList("&&", "EXPRESSAORELACIONAL", "REPETICAO_TERMOLOGICO");
                }
                else
                {
                    return Arrays.asList("EPSILON");
                }
            case "EXPRESSAORELACIONAL":
                return Arrays.asList("EXPRESSAOARITMETICA", "OPCAO_RELACIONAL");
            case "OPCAO_RELACIONAL":
                if (categoria.equals("t_maior") || categoria.equals("t_menor") || categoria.equals("t_menorIgual")
                    || categoria.equals("t_maiorIgual") || categoria.equals("t_igualComparacao") || categoria.equals("t_diferente"))
                {
                    return Arrays.asList("OPERADORRELACIONAL", "EXPRESSAOARITMETICA");
                }
                else
                {
                    return Arrays.asList("EPSILON");
                }
            case "OPERADORRELACIONAL":
                if (categoria.equals("t_maior"))
                    return Arrays.asList(">");
                else if (categoria.equals("t_menor"))
                    return Arrays.asList("<");
                else if (categoria.equals("t_maiorIgual"))
                    return Arrays.asList(">=");
                else if (categoria.equals("t_menorIgual"))
                    return Arrays.asList("<=");
                else if (categoria.equals("t_igualComparacao"))
                    return Arrays.asList("==");
                else if (categoria.equals("t_diferente"))
                    return Arrays.asList("!=");
                break;
            case "EXPRESSAOARITMETICA":
                return Arrays.asList("TERMO", "REPETICAO_EXPRESSAOARITMETICA");
            case "REPETICAO_EXPRESSAOARITMETICA": // first
                if (categoria.equals("t_adicao") || categoria.equals("t_subtracao"))
                    return Arrays.asList(token.getLexema(), "TERMO", "REPETICAO_EXPRESSAOARITMETICA");
                else
                    return Arrays.asList("EPSILON");
            case "TERMO":
                return Arrays.asList("VALOR", "REPETICAO_TERMO");
            case "REPETICAO_TERMO":
                if (categoria.equals("t_multiplicacao") || categoria.equals("t_divisao") || categoria.equals("t_resto"))
                    return Arrays.asList(token.getLexema(),"VALOR", "REPETICAO_TERMO");
                else
                    return Arrays.asList("EPSILON");
            case "VALOR":
                if (categoria.equals("t_numero"))
                    return Arrays.asList("NUMERO");
                else
                if (categoria.equals("t_identificador"))
                    return Arrays.asList("IDENTIFICADOR");
                else
                if (categoria.equals("t_abreParentese"))
                    return Arrays.asList("(", "EXPRESSAOLOGICA", ")");
                else
                if (categoria.equals("t_negacao"))
                    return Arrays.asList("!", "VALOR");
                break;
        }
        return null;
    }
    public void analisarSintatico()
    {
        int pos = 0;
        pilha.push("$");
        pilha.push("PROGRAMA"); // meu inicio
        tokenAtual = lexica.getToken(pos++);
        while (!pilha.isEmpty())
        {
            NoPilha noPilha = pilha.pop();
            String topo = noPilha.getString();
            if (tokenAtual != null)
            {
                if (!topo.equals("EPSILON")) // terminou uma estrutura/bloco qnd tem opcional ou repeticao
                {
                    if (topo.equals("$")) // a pilha acabou, mas ainda existem tokens
                    {
                        if (!tokenAtual.getToken().equals("$"))
                        {
                            //Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Token Inesperado: %s Após o Fim do Programa.\n", tokenAtual.getLinha(), tokenAtual.getColuna(), tokenAtual.getLexema()), tokenAtual.getLinha(), tokenAtual.getColuna());
                            //erroList.add(erro);
                        }

                    }
                    else
                    if (isTerminal(topo))
                    {
                        if (topo.equals(tokenAtual.getLexema()) || (topo.equals("IDENTIFICADOR") && tokenAtual.getToken().equals("t_identificador"))
                                || (topo.equals("NUMERO") && tokenAtual.getToken().equals("t_numero")))
                        {
                            tokenAtual = lexica.getToken(pos++);
                        }
                        else
                        {
                            Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Esperado: '%s', mas encontrado '%s'.\n",tokenAtual.getLinha(), tokenAtual.getColuna(), topo, tokenAtual.getLexema())
                                    ,tokenAtual.getLinha(), tokenAtual.getColuna());
                            erroList.add(erro);
                            // Modo Pânico
                            tokenAtual = lexica.getToken(pos++);
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
//            else // acabaram os tokens, mas a pilha ainda nao esta vazia
//            {
//                if (!topo.equals("EPSILON") && !topo.equals("$"))
//                {
//                    int linha = 1, coluna = 1;
//                    if (ultimoToken != null)
//                    {
//                        linha = ultimoToken.getLinha();
//                        coluna = ultimoToken.getColuna();
//                    }
//                    Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna: %d: Faltou fechar a estrutura: '%s'.\n", linha, coluna,topo), linha, coluna);
//                    erroList.add(erro);
//                    flag = false;
//                }
//            }
        }
    }

    private boolean isTerminal(String topo)
    {
        return topo.equals(topo.toLowerCase()) || topo.equals("IDENTIFICADOR") ||  topo.equals("NUMERO");
    }
}
