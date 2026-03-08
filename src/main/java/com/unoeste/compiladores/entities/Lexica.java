package com.unoeste.compiladores.entities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.CodeArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lexica
{
    private List<Character> list_numeros = new ArrayList<>(); //numeros válidos
    private List<Character> list_letras = new ArrayList<>();

    private List<String> list_opRelacional = new ArrayList<>(Arrays.asList("<", ">", "<=", ">=", "==", "!="));
    private List<String> list_tipos = new ArrayList<>(Arrays.asList("void", "char", "int", "float", "double"));
    private List<String> list_comandosReservados = new ArrayList<>(Arrays.asList("while", "if", "else", "return", "main"));
    private List<String> list_opMatematicos = new ArrayList<>(Arrays.asList("*", "+", "-", "/", "%", "!", "*=", "+=", "-=", "/=", "%="));
    private List<Character> list_unitarios = new ArrayList<>(Arrays.asList('{', '}', ',', '=', ';', '.', '(', ')'));
    private List<Character> list_especiais = new ArrayList<>(Arrays.asList('@', '#', '$'));

    private List<Erro> list_erro =  new ArrayList<>();

    private List<Token> list_tokens = new ArrayList<>();

    private ObservableList<Token> tabelaSucessos;
    private TextArea logErro;

    public Lexica(ObservableList<Token> tabelaSucessos, TextArea textArea)
    {
        this.tabelaSucessos = tabelaSucessos;
        this.logErro = textArea;
        preencheListas();
    }

    /**
     * O separarCadeias irá fazer um split pelos espaços em branco ' '
     * */
    public void separarCadeias(String linha, int posLinha, List<Token> destino)
    {
        //apenas para identificar corretamente a coluna
        linha = linha.replaceAll("\t", "        ");
        int i = 0;
        String cadeia = "";
        int posColuna = 0;
        while (i < linha.length())
        {
            char c = linha.charAt(i);
            if (c != ' ')
            {
                if (cadeia.isEmpty())
                    posColuna = i + 1;
                cadeia = cadeia + c;
            }
            else
            {
                separarTokens(cadeia, posLinha, posColuna, destino);
                cadeia = "";
            }
            i++;
        }
        if (!cadeia.isEmpty())
            separarTokens(cadeia, posLinha, posColuna, destino);
    }

    private void separarTokens(String cadeia, int posLinha, int posColuna, List<Token> destino)
    {
        int i = 0;
        String token = "";
        int inicioToken = 0;

        while (i < cadeia.length())
        {
            char c = cadeia.charAt(i);

            if(!list_letras.contains(c) && !list_numeros.contains(c) && c != '_')
            {
                if(c == '.' && isNumero(token))
                    token += c;
                else
                {
                    addToken(token, posLinha, posColuna + inicioToken, destino);
                    token = "";
                    inicioToken = i;
                    token += c;
                    /**
                     * Se Verdade, então temos a certeza de que o próximo token não será: número, comando reservado, tipo e nem identificador
                     * */
                    if(c == '=' || c == '>' || c == '<' || c == '!' || c == '*' || c == '+' || c == '-' || c == '/' || c == '%')
                    {
                        if(i+1 < cadeia.length() && cadeia.charAt(i+1) == '=')
                        {
                            token += cadeia.charAt(i+1);
                            i++;
                        }
                    }
                    addToken(token, posLinha, posColuna + inicioToken, destino);
                    inicioToken = i;
                    token = "";
                }
            }
            else if(list_especiais.contains(c)) //especiais para darem erro
            {
                /**
                 * Character inválido detectado
                 * */
                Erro erro = new Erro(String.format("[ERRO LÉXICO] Token '%s' inválido na linha %d, coluna %d.\n", token, posLinha, posColuna + i), posLinha, posColuna + i);
                token = "";
                list_erro.add(erro);
            }
            else
            {
                if (token.isEmpty())
                    inicioToken = i;
                token += c;
            }

            i++;
        }

        if (!token.isEmpty())
            addToken(token, posLinha, posColuna + inicioToken, destino); //(i - token.length())+1
    }

    private boolean addToken(String token, int linha, int coluna, List<Token> destino)
    {
        if(token.isEmpty())
            return false;

        String categoria = verificarCategoria(token); // -> verifica se é um token válido
        if (!categoria.isEmpty())
        {
            // Add Tokens válidos
            Token novoToken = new Token(categoria, token, linha, coluna);

            destino.add(novoToken);
            /*list_tokens.add(novoToken);
            tabelaSucessos.add(novoToken);*/

            return true;
        }
        else
        {
            // Tratar tokens inválidos
            Erro erro = new Erro(String.format("[ERRO LÉXICO] Token '%s' inválido na linha %d, coluna %d.\n", token, linha, coluna), linha, coluna);
            list_erro.add(erro);
            return false;
        }
    }

    public void limparListas()
    {
        logErro.clear();
        list_tokens.clear();
        list_erro.clear();
        tabelaSucessos.clear();
    }

    // VERIFICAÇÕES -------------------------------------------------------------------------
    private String verificarSubUnitarios(String token)
    {
        if(token.equals("{")) return "t_abreChave";
        if(token.equals("}")) return "t_fechaChave";
        if(token.equals(",")) return "t_virgula";
        if(token.equals("=")) return "t_igualAtribuicao";
        if(token.equals(";")) return "t_pontoVirgula";
        if(token.equals(".")) return "t_ponto";
        if(token.equals("(")) return "t_abreParentese";
        if(token.equals(")")) return "t_fechaParentese";

        return "";
    }

    private String verificarSubMatematicos(String token)
    {
        if(token.equals("*")) return "t_multiplicacao";
        if(token.equals("+")) return "t_adicao";
        if(token.equals("-")) return "t_subtracao";
        if(token.equals("/")) return "t_divisao";
        if(token.equals("*=")) return "t_multiplicacaoIgual";
        if(token.equals("+=")) return "t_adicaoIgual";
        if(token.equals("-=")) return "t_subtracaoIgual";
        if(token.equals("/=")) return "t_divisaoIgual";
        if(token.equals("%=")) return "t_restoIgual";
        if(token.equals("!")) return "t_negacao";

        return "";
    }

    private String verificarSubTipos(String token)
    {
        if(token.equals("void")) return "t_void";
        if(token.equals("char")) return "t_char";
        if(token.equals("int")) return "t_int";
        if(token.equals("float")) return "t_float";
        if(token.equals("double")) return "t_double";

        return "";
    }

    private String verificarSubComandosReservados(String token)
    {
        if(token.equals("while")) return "t_while";
        if(token.equals("if")) return "t_if";
        if(token.equals("else")) return "t_else";
        if(token.equals("return")) return "t_return";
        if(token.equals("main")) return "t_main";

        return "";
    }

    private String verificarSubRelacional(String token)
    {
        if(token.equals(">")) return "t_maior";
        if(token.equals(">=")) return "t_maiorIgual";
        if(token.equals("<")) return "t_menor";
        if(token.equals("<=")) return "t_menorIgual";
        if(token.equals("==")) return "t_igualComparacao";
        if(token.equals("!=")) return "t_diferente";

        return "";
    }

    private String verificarCategoria(String token)
    {
        if(list_opRelacional.contains(token))
            return verificarSubRelacional(token);

        if(list_comandosReservados.contains(token))
            return verificarSubComandosReservados(token);

        if(list_tipos.contains(token))
            return verificarSubTipos(token);

        if(list_opMatematicos.contains(token))
            return verificarSubMatematicos(token);

        if(token.length() == 1 && list_unitarios.contains(token.charAt(0)))
            return verificarSubUnitarios(token);

        if(isNumero(token))
            return "t_numero";

        if(isIdentificador(token))
            return "t_identificador";

        // Todos os tokens que retornam vazios, são tokens inválidos
        return "";
    }

    private boolean isIdentificador(String token)
    {
        //primeiro dígito n pode ser número
        if(list_numeros.contains(token.charAt(0)))
            return false;

        //primeiro digito n pode ser underline
        if(token.charAt(0) == '_')
            return false;

        //n pode conter sinbolos especiais
        for(int i=0; i<token.length(); i++)
            if(list_especiais.contains(token.charAt(i)))
                return false;

        //se chegou aqui é valido
        return true;
    }

    private boolean isNumero(String token)
    {
        int quantPonto = 0, quantNum=0;

        for(int i=0; i<token.length(); i++)
            if (token.charAt(i) == '.')
                quantPonto++;

        if(quantPonto > 1) //mais de um ponto
            return false;

        for(int i=0; i<token.length(); i++)
            if(list_numeros.contains(token.charAt(i)))
                quantNum++;

        return quantNum == token.length()-quantPonto;
    }

    public void exibirLogErro(CodeArea codeArea)
    {
        int i = 0;
        boolean flag = false;
        while (i < list_erro.size())
        {
            if (!flag)
            {
                codeArea.setParagraphStyle(list_erro.get(i).getLinha() - 1,  Collections.singleton("erro-linha"));
                codeArea.multiPlainChanges().subscribe(change -> {

                    if (!list_erro.isEmpty()) {

                        int linhaErro = list_erro.get(0).getLinha() - 1;
                        int linhaAtual = codeArea.getCurrentParagraph();

                        if (linhaErro == linhaAtual) {
                            codeArea.setParagraphStyle(linhaErro, Collections.emptyList());
                        }
                    }
                });
                flag = true;
            }
            logErro.appendText(list_erro.get(i).getMensagem());
            i++;
        }
    }

    private void preencheListas()
    {
        // Adicionar valores nas Listas, utilizando como base para os indices a tabela Unicode
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            list_numeros.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'A' até o 'Z'
        {
            char character = (char)(i+65);
            list_letras.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'a' até o 'z'
        {
            char character = (char)(i+97);
            list_letras.add(character);
        }

        // Adiciona o "underline" como uma exeção
        list_letras.add('_');
    }

    public Token getToken(int pos)
    {
        return list_tokens.get(pos);
    }

    public List<Token> getTokens()
    {
        return list_tokens;
    }
}