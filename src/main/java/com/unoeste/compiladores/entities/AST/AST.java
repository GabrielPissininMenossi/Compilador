package com.unoeste.compiladores.entities.AST;

import com.unoeste.compiladores.entities.TAC;
import com.unoeste.compiladores.entities.queue.FilaGenerica;

import java.util.ArrayList;
import java.util.List;

public class AST {
    private NoAST raiz;

    public AST(NoAST raiz) {
        this.raiz = raiz;
    }

    public AST() {
        this(null);
    }

    public NoAST getRaiz() {
        return raiz;
    }

    public void setRaiz(NoAST raiz) {
        this.raiz = raiz;
    }

    // Gerar os códigos de 3 endereços a partir da árvore já construída
    public List<TAC> generateThreeAddressCode()
    {
        if(raiz == null)
            return null;

        List<TAC> linhas = new ArrayList<>();

        // código aqui para a geração dos códigos de 3 endereços com a árvore já montada

        return linhas;
    }

    public void exibirArvoreNivel() {
        if (raiz == null) {
            System.out.println("A árvore está vazia.");
        }
        else
        {
            System.out.println(raiz.getValor());
            List<NoAST> filhos = raiz.getFilhos();

            for (int i = 0; i < filhos.size(); i++) {
                boolean isUltimo = (i == filhos.size() - 1);
                filhos.get(i).imprimirArvore("", isUltimo);
            }
        }
    }
}
