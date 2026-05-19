package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.AST.AST;
import com.unoeste.compiladores.entities.AST.NoAST;
import com.unoeste.compiladores.entities.TAC;
import com.unoeste.compiladores.entities.stack.PilhaGenerica;

import java.util.ArrayList;
import java.util.List;

public class GeradorCodigoIntermediario {

    private List<TAC> instrucoes = new ArrayList<>();

    private int contadorTemp = 1;
    private int contadorLabel = 1;

    private String geraNomeVarTemp() {
        return "t" + (contadorTemp++);
    }

    private String geraNomeLabel() {
        return "L" + (contadorLabel++);
    }


    public List<TAC> gerar(AST ast) {
        instrucoes.clear();

        contadorTemp = 1;
        contadorLabel = 1;

        // Se não houver árvore ou raiz
        if (ast == null || ast.getRaiz() == null) {
            return instrucoes;
        }

        // Pilha principal para percorrer os comandos da AST
        PilhaGenerica<EstadoGeracao> pilhaComandos = new PilhaGenerica<>();

        // Empilha o primeiro frame com a raiz da árvore
        pilhaComandos.push(new EstadoGeracao(ast.getRaiz(), "VISITAR"));

        while (!pilhaComandos.isEmpty()) {
            EstadoGeracao frame = pilhaComandos.pop().getInfo();

            // Recupera o nó atual e o estado atual
            NoAST no = frame.getNo();
            String estado = frame.getEstado();

            // ESTADOS AUXILIARE para emitir apenas uma label/goto
            if (estado.equals("LABEL"))
                instrucoes.add(new TAC("label", "", "", frame.getLabel1()));
            else
            if (estado.equals("GOTO"))
                instrucoes.add(new TAC("goto", "", "", frame.getLabel1()));
            else
            {
                // Se não for estado auxiliar, então estamos visitando um nó real
                String valor = no.getValor();


                // BLOCO
                // O bloco apenas organiza a sequência de comandos.
                // Como a pilha é LIFO, empilhamos os filhos de trás para frente,
                // para que eles sejam processados na ordem correta.
                if (valor.equals("bloco")) {
                    List<NoAST> filhos = no.getFilhos();
                    for (int i = filhos.size() - 1; i >= 0; i--) {
                        pilhaComandos.push(new EstadoGeracao(filhos.get(i), "VISITAR"));
                    }
                }

                // ATRIBUIÇÃO

                // Exemplo:
                // =
                // ├── x
                // └── +
                //
                // Gera:
                // t1 = ...
                // x = t1
                else if (valor.equals("=")) {
                    String destino = no.getFilhoAt(0).getValor();

                    String resultado = gerarExpressao(no.getFilhoAt(1), destino);

                    // Se a expressão era só uma folha, fazer a atribuição direta
                    if (!resultado.equals(destino)) {
                        instrucoes.add(new TAC("=", resultado, "", destino));
                    }
                }

                // RETURN

                // Exemplo:
                // return
                // └── 0
                //
                // Gera:
                // return 0
                else if (valor.equals("return")) {
                    String retorno = gerarExpressao(no.getFilhoAt(0),"");
                    instrucoes.add(new TAC("return", retorno, "", ""));
                }


                // IF sem else:
                // ifFalse t1 goto L1
                // ... corpo if ...
                // L1:
                else if (valor.equals("if")) {
                    String condicao = gerarExpressao(no.getFilhoAt(0),"");

                    // IF sem ELSE
                    if (no.getFilhos().size() == 2) {
                        String labelFim = geraNomeLabel();

                        // Se a condição for falsa, pula pro final
                        instrucoes.add(new TAC("ifFalse", condicao, "", labelFim));

                        // Como a pilha é LIFO:
                        // primeiro empilha a label de fechamento
                        // depois empilha o corpo do if
                        // Assim o corpo será processado antes da label final
                        pilhaComandos.push(new EstadoGeracao(null, "LABEL", labelFim, ""));
                        pilhaComandos.push(new EstadoGeracao(no.getFilhoAt(1), "VISITAR"));
                    }


                    // ifFalse t1 goto Lelse
                    // ... corpo if ...
                    // goto Lfim
                    // Lelse:
                    // ... corpo else ...
                    // Lfim:
                    else if (no.getFilhos().size() == 3) {
                        String labelElse = geraNomeLabel();
                        String labelFim = geraNomeLabel();

                        // Se a condição for falsa, vai pro else
                        instrucoes.add(new TAC("ifFalse", condicao, "", labelElse));

                        NoAST noElse = no.getFilhoAt(2);

                        // Empilhamento na ordem inversa do que queremos executar:
                        // 1) label final
                        // 2) corpo else
                        // 3) label else
                        // 4) goto final
                        // 5) corpo if
                        pilhaComandos.push(new EstadoGeracao(null, "LABEL", labelFim, ""));

                        if (noElse.getValor().equals("else") && !noElse.getFilhos().isEmpty()) {
                            pilhaComandos.push(new EstadoGeracao(noElse.getFilhoAt(0), "VISITAR"));
                        } else {
                            pilhaComandos.push(new EstadoGeracao(noElse, "VISITAR"));
                        }

                        pilhaComandos.push(new EstadoGeracao(null, "LABEL", labelElse, ""));
                        pilhaComandos.push(new EstadoGeracao(null, "GOTO", labelFim, ""));
                        pilhaComandos.push(new EstadoGeracao(no.getFilhoAt(1), "VISITAR"));
                    }
                }

                // while (condição) corpo
                //
                // Gera:
                // L1:
                // t1 = condição
                // ifFalse t1 goto L2
                // ... corpo ...
                // goto L1
                // L2:
                else if (valor.equals("while")) {
                    String labelInicio = geraNomeLabel();//L1
                    String labelFim = geraNomeLabel();//L2

                    // Label de início do laço
                    instrucoes.add(new TAC("label", "", "", labelInicio));

                    // Gera o código da condição
                    String condicao = gerarExpressao(no.getFilhoAt(0),"");

                    // Se condição falsa, sai do laço
                    instrucoes.add(new TAC("ifFalse", condicao, "", labelFim));

                    // Empilha em ordem inversa:
                    // 1) label final
                    // 2) goto início
                    // 3) corpo do while
                    pilhaComandos.push(new EstadoGeracao(null, "LABEL", labelFim, ""));
                    pilhaComandos.push(new EstadoGeracao(null, "GOTO", labelInicio, ""));
                    pilhaComandos.push(new EstadoGeracao(no.getFilhoAt(1), "VISITAR"));
                }
            }
        }
        return instrucoes;
    }


    private String gerarExpressao(NoAST raizExpr, String destinoFinal) {
        // Pilhas para gerar a pós-ordem de forma iterativa
        PilhaGenerica<NoAST> pilha1 = new PilhaGenerica<>();
        PilhaGenerica<NoAST> pilha2 = new PilhaGenerica<>();

        // Pilha que guarda os valores intermediários da expressão
        PilhaGenerica<String> pilhaValores = new PilhaGenerica<>();

        // Começa pela raiz da expressão
        pilha1.push(raizExpr);

        // primeiro transformar a subárvore de expressão em pós-ordem
        while (!pilha1.isEmpty()) {
            NoAST atual = pilha1.pop().getInfo();
            pilha2.push(atual);

            // Empilha os filhos para depois processá-los antes do pai
            if (atual.getFilhos() != null) {
                for (NoAST filho : atual.getFilhos()) {
                    pilha1.push(filho);
                }
            }
        }

        //processar a pós-ordem
        while (!pilha2.isEmpty()) {
            NoAST no = pilha2.pop().getInfo();
            String valor = no.getValor();

            // Se for folha, só empilha o valor
            // Ex: número, identificador
            if (no.isFolha()) {
                pilhaValores.push(valor);
            }

            // Se for operador binário, desempilha dois operandos
            else if (isOperadorBinario(valor)) {
                String dir = pilhaValores.pop().getInfo();
                String esq = pilhaValores.pop().getInfo();

                // Se este nó é a raiz da expressão e existe um destino final,
                // usa o próprio destino em vez de criar temporária
                String resultado;
                if (no == raizExpr && destinoFinal != null && !destinoFinal.isEmpty()) {
                    resultado = destinoFinal;
                } else {
                    resultado = geraNomeVarTemp();
                }

                instrucoes.add(new TAC(valor, esq, dir, resultado));
                pilhaValores.push(resultado);
            }
        }

        //topo final da pilha é o resultado da expressão
        return pilhaValores.pop().getInfo();
    }

    private boolean isOperadorBinario(String valor) {
        return isOperadorAritmetico(valor) || isOperadorRelacional(valor) || isOperadorLogico(valor);
    }

    private boolean isOperadorAritmetico(String valor) {
        return valor.equals("+") || valor.equals("-") ||
                valor.equals("*") || valor.equals("/");
    }

    private boolean isOperadorRelacional(String valor) {
        return valor.equals(">") || valor.equals("<") ||
                valor.equals(">=") || valor.equals("<=") ||
                valor.equals("==") || valor.equals("!=");
    }

    private boolean isOperadorLogico(String valor) {
        return valor.equals("&&") || valor.equals("||");
    }




    public void imprimirInstrucoes() {
        for (TAC tac : instrucoes) {
            if (tac.getOperador().equals("label")) {
                System.out.println(tac.getResultado() + ":");
            }
            else if (tac.getOperador().equals("goto")) {
                System.out.println("goto " + tac.getResultado());
            }
            else if (tac.getOperador().equals("ifFalse")) {
                System.out.println("ifFalse " + tac.getOperandoEsq() + " goto " + tac.getResultado());
            }
            else if (tac.getOperador().equals("=")) {
                System.out.println(tac.getResultado() + " = " + tac.getOperandoEsq());
            }
            else if (tac.getOperador().equals("return")) {
                System.out.println("return " + tac.getOperandoEsq());
            }
            else {
                System.out.println(tac.getResultado() + " = " + tac.getOperandoEsq() + " " + tac.getOperador() + " " + tac.getOperandoDir());
            }
        }
    }
}
