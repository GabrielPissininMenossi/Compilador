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
    private List<String> tipos = new ArrayList<>(Arrays.asList("void", "char", "int", "float", "double"));
    private List<String> comandosReservados = new ArrayList<>(Arrays.asList("while", "if", "else", "return", "main"));
    private List<String> opMatematicos = new ArrayList<>(Arrays.asList("*", "+", "-", "/", "%", "!", "*=", "+=", "-=", "/=", "%="));
    private List<Character> unitarios = new ArrayList<>(Arrays.asList('{', '}', ',', '=', ';', '.', '(', ')'));
    private List<Character> especiais = new ArrayList<>(Arrays.asList('@', '#', '$'));

    private ObservableList<Token> tabelaSucessos;
    private TextArea textArea;

    private List<Erro> erroList =  new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();

    public Lexica(ObservableList<Token> tabelaSucessos, TextArea textArea)
    {
        this.tabelaSucessos = tabelaSucessos;
        this.textArea = textArea;
        preencheListas();
    }

    /**
     * O separarCadeias irá fazer um split pelos espaços em branco ' '
     * */
    public void separarCadeias(String linha, int posLinha)
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

            if(!letras.contains(c) && !numeros.contains(c) && c != '_')
            {
                if(c == '.' && isNumero(token))
                    token += c;
                else
                {
                    addToken(token, posLinha, posColuna + inicioToken);
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
                    addToken(token, posLinha, posColuna + inicioToken);
                    inicioToken = i;
                    token = "";
                }
            }
            else if(especiais.contains(c)) //especiais para darem erro
            {
                /**
                 * Character inválido detectado
                 * */
                Erro erro = new Erro(String.format("[ERRO LÉXICO] Token '%s' inválido na linha %d, coluna %d.\n", token, posLinha, posColuna + i), posLinha, posColuna + i);
                token = "";
                erroList.add(erro);
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
            addToken(token, posLinha, posColuna + inicioToken); //(i - token.length())+1
    }

    public void limparListas()
    {
        textArea.clear();
        tokens.clear();
        erroList.clear();
        tabelaSucessos.clear();
    }

    private boolean addToken(String token, int linha, int coluna)
    {
        if(token.isEmpty())
            return false;

        String categoria = verificarCategoria(token); // -> verifica se é um token válido
        if (!categoria.isEmpty())
        {
            // Add Tokens válidos
            Token novoToken = new Token(categoria, token, linha, coluna);
            tokens.add(novoToken);
            tabelaSucessos.add(novoToken);
            return true;
        }
        else
        {
            // Tratar tokens inválidos
            Erro erro = new Erro(String.format("[ERRO LÉXICO] Token '%s' inválido na linha %d, coluna %d.\n", token, linha, coluna), linha, coluna);
            erroList.add(erro);
            return false;
        }
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

        if(token.length() == 1 && unitarios.contains(token.charAt(0)))
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

    //sub verificações
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

    private String verificarColoracao(String categoria)
    {
        if (categoria.equals("t_void")  || categoria.equals("t_char") || categoria.equals("t_int")
            || categoria.equals("t_float") || categoria.equals("t_double") || categoria.equals("t_while")
            || categoria.equals("t_if") || categoria.equals("else") || categoria.equals("return") || categoria.equals("main")) // palavras reservadas
        {
            return "palavra-reservada";
        }
        else
        if (categoria.equals("t_menor")  || categoria.equals("t_maior") || categoria.equals("t_menorIgual")
            || categoria.equals("t_maiorIgual") || categoria.equals("t_igualComparacao") || categoria.equals("t_diferente")) // op relaciona
        {
            return "operador-relacional";
        }
        else
        if (categoria.equals("t_numero"))
        {
            return "numero";
        }
        else
        if (categoria.equals("t_identificador"))
        {
            return "identificador";
        }
        else
        if (categoria.equals("t_multiplicacao")  || categoria.equals("t_adicao") || categoria.equals("t_subtracao")
                || categoria.equals("t_divisao") || categoria.equals("t_resto") || categoria.equals("t_negacao")
                || categoria.equals("t_multiplicacaoIgual") || categoria.equals("t_adicaoIgual") || categoria.equals("t_subtracaoIgual")
                || categoria.equals("t_divisaoIgual") || categoria.equals("t_restoIgual")) // op relaciona
        {
            return "operador-matematico";
        }
        else
        {
            return "unitarios";
        }


    }
    public void coloracaoSintatica(CodeArea codeArea)
    {
        int i = 0;
        Token token;
        String categoria;

        //int tamanho = codeArea.getLength();
        //int linha, coluna, posInicial, posFinal;

        while (i < tokens.size())
        {
             token = tokens.get(i);
             categoria = verificarColoracao(token.getToken());

             colorirToken(token, categoria, codeArea); //-> retirar restante do código logo abaixo

            i++;
        }
    }

    public void colorirToken(Token token, String categoria, CodeArea codeArea)
    {
        // Pego a linha original do meu code area
        String linha = codeArea.getParagraph(token.getLinha()-1).getText(); //pegar a linha original

        // Calculo a incidência -> verificar quando possuo mais de 1 token igual na mesma linha
        int coluna = indiceInicial(token, linha);

        // Calculo a posicao inicial/final em relação ao codeArea
        int posInicial = codeArea.position(token.getLinha()-1, coluna).toOffset();
        int posFinal = codeArea.position(token.getLinha()-1, coluna+token.getLexema().length()).toOffset();

        if (categoria.equals("palavra-reservada"))
        {
            codeArea.setStyleClass(posInicial, posFinal, "palavra-reservada");
        }
        else
        if (categoria.equals("operador-relacional"))
        {
            codeArea.setStyleClass(posInicial, posFinal, "operador-relacional");
        }
        else
        if (categoria.equals("numero"))
        {
            codeArea.setStyleClass(posInicial, posFinal, "numero");
        }
        else
        if (categoria.equals("identificador"))
        {
            codeArea.setStyleClass(posInicial, posFinal, "identificador");
        }
        else
        if (categoria.equals("operador-matematico"))
        {
            codeArea.setStyleClass(posInicial, posFinal, "operador-matematico");
        }
        else
        {
            codeArea.setStyleClass(posInicial, posFinal, "unitarios");
        }
    }

    public int indiceInicial(Token token, String linha)
    {
        int colunaRelativa = 0, i;
        boolean flag = true;

        for(i = 0; i < linha.length() && flag; i++, colunaRelativa++)
            if(colunaRelativa == token.getColuna()-1)
                flag = false;
            else if(linha.charAt(i) == '\t')
                colunaRelativa += 7;

        return i-1;
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


