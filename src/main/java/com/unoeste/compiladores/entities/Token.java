package com.unoeste.compiladores.entities;

import org.fxmisc.richtext.CodeArea;

public class Token {
    private String token;
    private String lexema;
    private int linha;
    private int coluna;

    public Token(String token, String lexema, int linha, int coluna)
    {
        this.token = token;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Token() {
        this("","",0,0);
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getLexema()
    {
        return lexema;
    }

    public void setLexema(String lexema)
    {
        this.lexema = lexema;
    }

    public int indiceInicialToken(String linha)
    {
        int colunaRelativa = 0, i;
        boolean flag = true;

        for(i = 0; i < linha.length() && flag; i++, colunaRelativa++)
            if(colunaRelativa == this.coluna-1)
                flag = false;
            else if(linha.charAt(i) == '\t')
                colunaRelativa += 7;

        return i-1;
    }

    public void colorirToken(String categoria, CodeArea codeArea)
    {
        // Pego a linha no code area
        String linha = codeArea.getParagraph(this.linha-1).getText(); //pegar a linha original

        // Encontra a coluna exata do token
        int coluna = indiceInicialToken(linha);

        // Calculo a posicao inicial/final em relação ao codeArea
        int posInicial = codeArea.position(this.linha-1, coluna).toOffset();
        int posFinal = codeArea.position(this.linha-1, coluna+this.lexema.length()).toOffset();

        if (categoria.equals("palavra-reservada"))
            codeArea.setStyleClass(posInicial, posFinal, "palavra-reservada");
        else
        if (categoria.equals("operador-relacional"))
            codeArea.setStyleClass(posInicial, posFinal, "operador-relacional");
        else
        if (categoria.equals("numero"))
            codeArea.setStyleClass(posInicial, posFinal, "numero");
        else
        if (categoria.equals("identificador"))
            codeArea.setStyleClass(posInicial, posFinal, "identificador");
        else
        if (categoria.equals("operador-matematico"))
            codeArea.setStyleClass(posInicial, posFinal, "operador-matematico");
        else
            codeArea.setStyleClass(posInicial, posFinal, "unitarios");
    }

    public String verificarColoracaoToken()
    {
        if (this.token.equals("t_void")  || this.token.equals("t_char") || this.token.equals("t_int")
                || this.token.equals("t_float") || this.token.equals("t_double") || this.token.equals("t_while")
                || this.token.equals("t_if") || this.token.equals("else") || this.token.equals("return") || this.token.equals("main")) // palavras reservadas
        {
            return "palavra-reservada";
        }
        else
        if (this.token.equals("t_menor")  || this.token.equals("t_maior") || this.token.equals("t_menorIgual")
                || this.token.equals("t_maiorIgual") || this.token.equals("t_igualComparacao") || this.token.equals("t_diferente")) // op relaciona
        {
            return "operador-relacional";
        }
        else
        if (this.token.equals("t_numero"))
        {
            return "numero";
        }
        else
        if (this.token.equals("t_identificador"))
        {
            return "identificador";
        }
        else
        if (this.token.equals("t_multiplicacao")  || this.token.equals("t_adicao") || this.token.equals("t_subtracao")
                || this.token.equals("t_divisao") || this.token.equals("t_resto") || this.token.equals("t_negacao")
                || this.token.equals("t_multiplicacaoIgual") || this.token.equals("t_adicaoIgual") || this.token.equals("t_subtracaoIgual")
                || this.token.equals("t_divisaoIgual") || this.token.equals("t_restoIgual")) // op relaciona
        {
            return "operador-matematico";
        }
        else
        {
            return "unitarios";
        }
    }
}
