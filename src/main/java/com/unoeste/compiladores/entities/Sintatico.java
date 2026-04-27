package com.unoeste.compiladores.entities;

import com.unoeste.compiladores.entities.Pilhas.NoPilha;
import com.unoeste.compiladores.entities.Pilhas.Pilha;

import java.util.Arrays;
import java.util.List;

public class Sintatico
{
    private  Lexica lexica;
    private Pilha pilha;
    private  Token tokenAtual;
    private  List<Erro> erroList;
    private  int pos;
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
                if (categoria.equals("t_void") || categoria.equals("t_char") || categoria.equals("t_int") || categoria.equals("t_string")
                        || categoria.equals("t_float") || categoria.equals("t_double") || categoria.equals("t_identificador")
                        || categoria.equals("t_if") || categoria.equals("t_while") || categoria.equals("t_return") || categoria.equals("t_abreChave"))
                {
                    return Arrays.asList("COMANDO", "REPETICAO_COMANDO");
                }
                else // follow
                if (categoria.equals("t_fechaChave"))
                {
                    return Arrays.asList("$");
                }
                break;
            case "COMANDO":
                if (categoria.equals("t_void") || categoria.equals("t_char") || categoria.equals("t_int") || categoria.equals("t_string")
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
                    return Arrays.asList("char", "OPCAO_COLCHETE");
                else
                if (categoria.equals("t_string"))
                    return Arrays.asList("string");
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
            case "OPCAO_COLCHETE":
                if (categoria.equals("t_abreColchete"))
                {
                    return Arrays.asList("[", "]");
                }
                else
                {
                    return Arrays.asList("$");
                }

            case "OPCAO_ATRIBUICAO":
                if (categoria.equals("t_igualAtribuicao"))
                {
                    return Arrays.asList("=", "EXPRESSAOLOGICA");
                }
                else // como é opcional
                if (categoria.equals("t_virgula") || categoria.equals("t_pontoVirgula"))
                    return Arrays.asList("$");
                break;
            case "REPETICAO_VARIAVEL":
                if (categoria.equals("t_virgula")) // first
                {
                    return Arrays.asList(",", "IDENTIFICADOR", "OPCAO_ATRIBUICAO", "REPETICAO_VARIAVEL");
                }
                else // follow
                if (categoria.equals("t_pontoVirgula"))
                {
                    return Arrays.asList("$");
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
                    return Arrays.asList("$");
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
                    return Arrays.asList("return", "EXPRESSAOLOGICA", ";");
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
                    return Arrays.asList("$");
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
                    return Arrays.asList("$");
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
                    return Arrays.asList("$");
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
                    return Arrays.asList("$");
            case "TERMO":
                return Arrays.asList("VALOR", "REPETICAO_TERMO");
            case "REPETICAO_TERMO":
                if (categoria.equals("t_multiplicacao") || categoria.equals("t_divisao") || categoria.equals("t_resto"))
                    return Arrays.asList(token.getLexema(),"VALOR", "REPETICAO_TERMO");
                else
                    return Arrays.asList("$");
            case "VALOR":
                if (categoria.equals("t_numero"))
                    return Arrays.asList("NUMERO");
                else
                if (categoria.equals("t_identificador"))
                    return Arrays.asList("IDENTIFICADOR");
                else
                if (categoria.equals("t_cadeiaCaracterChar"))
                    return Arrays.asList("CARACTER");
                else
                if (categoria.equals("t_cadeiaCaracterString"))
                    return Arrays.asList("STRING");
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
        pos = 0;
        pilha.push("$");
        pilha.push("PROGRAMA"); // meu inicio
        tokenAtual = lexica.getToken(pos++);
        String estruturaAtual = "";
        while (!pilha.isEmpty())
        {
            NoPilha noPilha = pilha.pop();
            String topo = noPilha.getString();
            if (tokenAtual != null)
            {
                if (!topo.equals("$")) // terminou uma estrutura/bloco qnd tem opcional ou repeticao
                {
                    if (isTerminal(topo))
                    {
                        if (topo.equals(tokenAtual.getLexema()) || (topo.equals("IDENTIFICADOR") && tokenAtual.getToken().equals("t_identificador"))
                            || (topo.equals("NUMERO") && tokenAtual.getToken().equals("t_numero")) || (topo.equals("CARACTER") && tokenAtual.getToken().equals("t_cadeiaCaracterChar"))
                            || (topo.equals("STRING") && tokenAtual.getToken().equals("t_cadeiaCaracterString")))
                        {
                            if (topo.equals("while") || topo.equals("if") || topo.equals("else") || topo.equals("main"))
                                estruturaAtual = topo;
                            else
                            if (topo.equals("="))
                                estruturaAtual = "expressão de atribuição";
                            else
                            if (topo.equals("return"))
                                estruturaAtual = "declaração de retorno";
                            else
                            if (topo.equals("int") || topo.equals("float") || topo.equals("char") || topo.equals("double") || topo.equals("void"))
                                estruturaAtual = "declaração de variável";

                            tokenAtual = lexica.getToken(pos++);
                        }
                        else
                        {
                            Erro erro = getMensagemErroTerminal(topo, tokenAtual, estruturaAtual);
                            erroList.add(erro);
                            // Modo Pânico
                            tokenAtual = modoPanico();
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
                            tokenAtual = modoPanico();
                        }
                    }
                }

            }
        }
        if (tokenAtual != null && !tokenAtual.getToken().equals("$")) // acabou a pilha, mas ainda tem tokens
        {
            Erro erro = new Erro(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Token Inesperado: '%s' Após o Fim do Programa.\n", tokenAtual.getLinha(), tokenAtual.getColuna(), tokenAtual.getLexema()), tokenAtual.getLinha(), tokenAtual.getColuna());
            erroList.add(erro);
        }
        Erro erro = new Erro("", 0,0);

        int qtdeErros = obterQtdeErros();
        if (qtdeErros > 0)
            erro.setMensagem(String.format("[SUCESSO] Análise sintática concluída com %d erro(s) encontrado(s).\n", qtdeErros));
        else
            erro.setMensagem("[SUCESSO] Análise sintática concluída com 0 erro encontrado.\n");
        erroList.add(erro);
    }
    private int obterQtdeErros()
    {
        int qtdeErros = 0;
        if (!erroList.isEmpty())
        {
            int i = 0;
            while (i < erroList.size())
            {
                if (!erroList.get(i).getMensagem().startsWith("[ERRO LÉXICO]"))
                    qtdeErros++;
                i++;
            }
        }
        return qtdeErros;
    }
    private Erro getMensagemErroTerminal(String topo, Token tokenAtual, String estruturaAtual)
    {
        int linha = tokenAtual.getLinha(), coluna = tokenAtual.getColuna();
        Erro erro = new Erro("", linha, coluna);
        if (topo.equals(")"))
        {
            if (!estruturaAtual.isEmpty())
                erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Estrutura '%s' sem parêntese de fechamento '%s'.\n", linha, coluna, estruturaAtual, topo));
            else
                erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Estrutura sem parêntese de fechamento '%s'.\n", linha, coluna, topo));

        }
        else
        if (topo.equals("}"))
            erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Estrutura sem chave de fechamento '%s'.\n", linha, coluna, topo));
        else
        if (topo.equals(";"))
        {
            if (!estruturaAtual.isEmpty() && !estruturaAtual.equals("if") && !estruturaAtual.equals("while") && !estruturaAtual.equals("else") && !estruturaAtual.equals("main"))
                erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Esperado '%s' após %s, mas encontrado '%s'.\n", linha, coluna, topo, estruturaAtual,tokenAtual.getLexema()));
            else
                erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Esperado '%s', mas encontrado '%s'.\n", linha, coluna, topo, tokenAtual.getLexema()));

        }
        else
            erro.setMensagem(String.format("[ERRO SINTÁTICO] Linha %d, Coluna %d: Esperado '%s', mas encontrado '%s'.\n", linha, coluna, topo, tokenAtual.getLexema()));
        return erro;
    }
    private boolean isTerminal(String topo)
    {
        return topo.equals(topo.toLowerCase()) || topo.equals("IDENTIFICADOR") ||  topo.equals("NUMERO") || topo.equals("STRING") ||  topo.equals("CARACTER") ;
    }
    private Token modoPanico()
    {

        while (tokenAtual != null && !tokenAtual.getToken().equals("$") && !isTokenSincronizacao(tokenAtual))
        {
            tokenAtual = lexica.getToken(pos++);
        }

        return tokenAtual;
    }
    private boolean isTokenSincronizacao(Token token)
    {
        String categoria = token.getToken();
        if (categoria.equals("t_pontoVirgula") || categoria.equals("t_fechaChave") || categoria.equals("t_abreChave"))
        {
            return true;
        }
        if (categoria.equals("t_while") || categoria.equals("t_if") ||
            categoria.equals("t_int") || categoria.equals("t_void") ||
            categoria.equals("t_char") || categoria.equals("t_float") ||
            categoria.equals("t_double") || categoria.equals("t_return") || categoria.equals("t_else") || categoria.equals("t_string"))
        {
            return true;
        }

        return false;
    }
}
