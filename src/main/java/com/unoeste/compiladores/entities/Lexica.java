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
    private List<Character> numeros = new ArrayList<>(); //numeros válidos
    private List<Character> letras = new ArrayList<>();
    private List<String> opRelacional = new ArrayList<>(Arrays.asList("<", ">", "<=", ">=", "==", "!="));
    private List<String> tipos = new ArrayList<>(Arrays.asList("void", "char", "short", "int", "long", "float", "double"));
    private List<String> comandosReservados = new ArrayList<>(Arrays.asList("while", "if", "else"));
    private List<String> opMatematicos = new ArrayList<>(Arrays.asList("*", "+", "-", "/", "%", "!"));
    private List<Character> unitarios = new ArrayList<>(Arrays.asList('{', '}', ',', '=', ';', '.', '(', ')', '"'));
    private List<Character> especiais = new ArrayList<>(Arrays.asList('@', '#', '$'));

    private ObservableList<Token> tabelaSucessos;
    private TextArea textArea;

    private List<Erro> erroList =  new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();

    public void separarCadeias(String linha, int posLinha)
    {
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
                separarTokens(cadeia, posLinha, posColuna);
                cadeia = "";
            }
            i++;
        }
        if (!cadeia.isEmpty())
            separarTokens(cadeia, posLinha, posColuna);
    }
    private void separarTokens(String cadeia, int posLinha, int posColuna)
    {
        int i = 0;
        String token = "";
        int inicioToken = 0;

        while (i < cadeia.length())
        {
            char c = cadeia.charAt(i);

            if(!letras.contains(c) && !numeros.contains(c))
            {
                if (especiais.contains(c))
                {
                    Erro erro = new Erro(String.format("[ERRO LÉXICO] Caractere '%c' inválido na linha %d, coluna %d.\n", c, posLinha, posColuna), posLinha, posColuna + i);
                    erroList.add(erro);
                }
                else
                if (!token.isEmpty()) // apos uma sequencia de letras e numeros
                {

                    // adiciona (funcao para validar token)
                    addToken(token, posLinha, posColuna + inicioToken);
                    token = "";

                }
                inicioToken = i;
                token += c;

                // <= , >= , ==, !=
                if (cadeia.length() > i + 1 && cadeia.charAt(i + 1) == '=')
                {
                    token += cadeia.charAt(i + 1);
                    i++;

                }
                // adiciona (funcao para validar token)
                addToken(token, posLinha, posColuna + inicioToken);
                token = "";
            }
            else
            {
                if(token.isEmpty() && !primeiroDigitoValido(c)) //token inválido -> marcar a linha
                {
                    Erro erro = new Erro(String.format("[ERRO LÉXICO] Caractere '%c' inválido na linha %d, coluna %d.\n", c, posLinha, posColuna), posLinha, posColuna + i);
                    erroList.add(erro);
                }
                else
                {
                    if (token.isEmpty())
                        inicioToken = i;
                    token += c;
                }
            }

            i++;
        }

        if (!token.isEmpty())
            addToken(token, posLinha, posColuna + inicioToken);
    }

    public void limparListas()
    {
        textArea.clear();
        tokens.clear();
        erroList.clear();
        tabelaSucessos.clear();
    }
    private void addToken(String token, int linha, int coluna)
    {
        Token novoToken = new Token(verificarCategoria(token), token, linha, coluna);
        tokens.add(novoToken);
        tabelaSucessos.add(novoToken);

    }

    private String verificarCategoria(String token)
    {

        if(opRelacional.contains(token))
            return verificarSubRelacional(token);

        if(comandosReservados.contains(token))
            return verificarSubComandosReservados(token);

        if(tipos.contains(token))
            return verificarSubTipos(token);

        if(opMatematicos.contains(token))
            return verificarSubMatematicos(token);

        if(unitarios.contains(token.charAt(0)))
            return verificarSubUnitarios(token);

        if(isNumero(token))
            return "t_numero";

        if(isIdentificador(token))
            return "t_identificador";

        // erros vao vir aq

        return "";
    }

    private boolean isIdentificador(String token)
    {
        //primeiro dígito n pode ser número
        if(numeros.contains(token.charAt(0)))
            return false;

        //primeiro digito n pode ser underline
        if(token.charAt(0) == '_')
            return false;

        //n pode conter sinbolos especiais
        for(int i=0; i<token.length(); i++)
            if(especiais.contains(token.charAt(i)))
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
            if(numeros.contains(token.charAt(i)))
                quantNum++;

        return quantNum == token.length()-quantPonto;
    }

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
        if(token.equals("%")) return "t_resto";
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
    public void exibirLogErro(CodeArea codeArea)
    {
        int i = 0;
        boolean flag = false;
        while (i < erroList.size())
        {
            if (!flag)
            {
                codeArea.setParagraphStyle(erroList.get(i).getLinha() - 1,  Collections.singleton("erro-linha"));
                codeArea.currentParagraphProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                        if (!erroList.isEmpty())
                            if (erroList.get(0).getLinha() - 1 == t1)
                                codeArea.setParagraphStyle(erroList.get(0).getLinha() - 1, Collections.emptyList());
                    }
                });
                flag = true;
            }
            textArea.appendText(erroList.get(i).getMensagem());
            i++;
        }

    }
    //sub verificações


    private boolean primeiroDigitoValido(char c)
    {
        return !(c == '_');
    }

    public Lexica(ObservableList<Token> tabelaSucessos, TextArea textArea)
    {
        this.tabelaSucessos = tabelaSucessos;
        this.textArea = textArea;
        preencheListas();
    }





    private void preencheListas()
    {
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            numeros.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'A' até o 'Z'
        {
            char character = (char)(i+65);
            letras.add(character);
        }

        for(int i=0; i<26; i++) //vai inserir 'a' até o 'z'
        {
            char character = (char)(i+97);
            letras.add(character);
        }

        //adiciona o "underline" como uma exeção
        letras.add('_');
    }

}


