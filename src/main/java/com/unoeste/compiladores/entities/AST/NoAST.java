package com.unoeste.compiladores.entities.AST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoAST {
    // Explicação da estrutura utilizada
    /*
     * Se While
     *      [0] expressão lógica
     *      [1] bloco
     * */

    /*
     * Se If
     *      [0] expressão lógica
     *      [1] bloco if --> true/verdade
     *      [2] (se tiver) bloco else --> false/falso
     * */

    /*
     * Se operador (+, -, *, /)
     *      [0] operador da esquerda
     *      [1] operador da direita
     * */

    /*
     * Se for um nó folha (identicador, número)
     *      Se valor == String --> identificador
     *      Se valor == Numero --> numero
     * */
    private String valor;
    private List<NoAST> filhos;

    public NoAST(String valor, List<NoAST> filhos) {
        this.valor = valor;
        this.filhos = filhos;
    }

    public NoAST(String valor)
    {
        this.valor = valor;
        this.filhos = new ArrayList<>();
    }

    public NoAST() {
        this("");
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public List<NoAST> getFilhos() {
        return filhos;
    }

    public NoAST getFilhoAt(int pos)
    {
        if(pos>-1 && pos<filhos.size())
            return filhos.get(pos);
        return null;
    }

    public void setFilhos(List<NoAST> filhos) {
        this.filhos = filhos;
    }

    // Adição de um filho na AST
    public void addFilho(NoAST no)
    {
        filhos.add(no);
    }

    public void addFilhos(List<NoAST> noses)
    {
        filhos.addAll(noses);
    }

    // Limpar todos os filhos do Nó
    public void limparFilhos()
    {
        filhos.clear();
    }

    // Verificações lógicas
    private boolean isNumero(String token)
    {
        List<Character> list_numeros = new ArrayList<>();
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            list_numeros.add(character);
        }
        if (!token.isEmpty())
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
        return false;
    }

    private boolean isIdentificador(String token)
    {
        List<Character> list_numeros = new ArrayList<>();
        for(int i=0; i<10; i++) //vai inserir '0' até o '9'
        {
            char character = (char)(i+48);
            list_numeros.add(character);
        }
        List<Character> list_especiais = new ArrayList<>(Arrays.asList('@', '#', '$'));

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

    private boolean isOperador()
    {
        List<String> list_operadores = new ArrayList<>(Arrays.asList("*", "+", "-", "/", "%"));

        return list_operadores.contains(valor);
    }

    public boolean isFolha(){
        return filhos == null || filhos.isEmpty();
    }

    public void imprimirArvore(String prefixo, boolean isUltimo) {
        System.out.println(prefixo + (isUltimo ? "└── " : "├── ") + valor);

        String prefixoFilhos = prefixo + (isUltimo ? "    " : "│   ");

        for (int i = 0; i < filhos.size(); i++) {
            boolean ultimoFilho = (i == filhos.size() - 1);
            filhos.get(i).imprimirArvore(prefixoFilhos, ultimoFilho);
        }
    }
}
