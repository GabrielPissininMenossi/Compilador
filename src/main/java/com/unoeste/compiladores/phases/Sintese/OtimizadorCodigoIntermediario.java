package com.unoeste.compiladores.phases.Sintese;

import com.unoeste.compiladores.entities.TAC;
import java.util.ArrayList;
import java.util.List;

public class OtimizadorCodigoIntermediario {
    private List<TAC> instrucoesOtimizadas = new ArrayList<>();

    public OtimizadorCodigoIntermediario(List<TAC> instrucoesOrimizadas) {
        this.instrucoesOtimizadas = instrucoesOrimizadas;
    }

    public OtimizadorCodigoIntermediario() {
        instrucoesOtimizadas = new ArrayList<>();
    }

    // RETORNAR A LISTA DE INSTRUÇÕES OTIMIZADAS A PARTIR DAS INSTRUÇÕES RECEBIDAS POR PARÂMETRO
    public List<TAC> otimizarInstrucoes(List<TAC> instrucoes) {
        // Limpar a lista por default
        instrucoesOtimizadas.clear();
        for (TAC i : instrucoes) { //realizar a cópia das instruções originais
            instrucoesOtimizadas.add(new TAC(i.getOperador(),i.getOperandoEsq(),i.getOperandoDir(), i.getResultado())); // Usando um construtor de cópia
        }

        // PASSOS PARA SEREM SEGUIDOS:
        // - Eliminação de subexpressões comuns                                  -> 1
        // - Eliminação de código redundante                                     -> 2
        // - Propagação de cópias                                                -> 3
        // - Eliminação de desvios desnecessários                                -> 4
        // - Substituição de expressões algébricas por equivalentes mais simples -> 5
        // - Otimização de laços                                                 -> 6

        // 1

        // 2
        remNeverUsedLines(instrucoesOtimizadas);

        // 3

        // 4
        remDesviosDesnecessarios(instrucoesOtimizadas);

        // 5
        subExpAlg(); //substitui as expressões algébricas por outras equivalentes

        // 6

        return instrucoesOtimizadas;
    }

    // VERIFICAÇÃO SE É POSSÍVEL REALIZAR ALGUMA OTIMIZAÇÃO NA INSTRUÇÃO
    private boolean existeOtimizacao(TAC instrucao)
    {

        return true; // provisório
    }

    // VERIFICAÇÃO SE O IF É SEMPRE FALSO -> recebe a linha onde se encontra o if
    private boolean ifIsAlwaysFalse(int linha, List<TAC> instrucoes)
    {

        return true; // provisório
    }

    // VERIFICAÇÃO SE O WHILE É SEMPRE FALSO -> recebe a linha onde se encontra o while
    private boolean whileAlwaysFalse(int linha, List<TAC> instrucoes)
    {

        return true; // provisório
    }

    // REMOVER AS LINHAS EM QUE VARIÁVEIS SÃO ATRIBUÍDAS E NUNCA MAIS USADAS NO CÓDIGO
    private void remNeverUsedLines(List<TAC> instrucoes)
    {
        for(int i=0; i<instrucoes.size(); i++)
            if(!isInicioIf(instrucoes,i) &&
                    !isDesvioCondicional(instrucoes.get(i)) &&
                    !isDesvioIncondicional(instrucoes.get(i)) &&
                    !isReturn(instrucoes.get(i)) &&
                    !isRotulo(instrucoes.get(i)) &&
                    isNeverUsed(i, instrucoes))
                instrucoes.remove(i);
    }

    // VERIFICAÇÃO SE UMA VARIÁVEL NÃO É UTILIZADA DEPOIS DE UMA DETERMINADA LINHA
    private boolean isNeverUsed(int linha, List<TAC> instrucoes)
    {
        for(int i = linha; i < instrucoes.size(); i++)
            if(instrucoes.get(linha).getResultado().equals(instrucoes.get(i).getOperandoDir()) ||
                    instrucoes.get(linha).getResultado().equals(instrucoes.get(i).getOperandoEsq()))
            {
                // se entrou no IF quer dizer que em algum ponto a variável é utilizada no código
                return false;
            }

        return true;
    }

    // VERIFICAÇÃO SE A VARIÁVEL NUNCA É EXECUTADA APÓS UMA DETERMINADA LINHA
    private boolean isNeverExecuted(int linha, List<TAC> instrucoes)
    {

        return true; //provisório
    }

    // VERIFICAÇÃO SE UMA EXPRESSÃO SE REPETE E OS SEUS VALORES DE RETORNO NÃO SE MODIFICAM
    private List<Integer> linhasRepetidas(int linha, List<TAC> instrucoes)
    {
        List<Integer> repetidas = new ArrayList<>();

        return repetidas;
    }

    // REMOVER DESVIOS DESNECESSÁRIOS -> GOTO APONTANDO PARA A LINHA LOGO ABAIXO
    private void remDesviosDesnecessarios(List<TAC> instrucoesOtimizadas)
    {
        List<Integer> linhas = linhasDesvIncoProxInst(instrucoesOtimizadas);

        for(Integer linha : linhas)
            instrucoesOtimizadas.remove(linha);
    }

    // RETORNO DE UMA LISTA DOS ÍNDICES DAS LINHAS CONTENDO UM DESVIO INCONDICIONAL QUE APONTA DIRETAMENTE PARA A PRÓXIMA LINHA
    private List<Integer> linhasDesvIncoProxInst(List<TAC> instrucoes)
    {
        List<Integer> desvios = new ArrayList<>();
        for(int i = 0; i < instrucoes.size(); i++)
        {
            if(isDesvioIncondicional(instrucoes.get(i)) &&
                    i+1<instrucoes.size() &&
                    instrucoes.get(i+1).getResultado().equals(instrucoes.get(i).getResultado())
            ) //verificar se o goto indica para a linha logo abaixo
            {
                desvios.add(i); //add o índice atual
            }
        }

        return desvios;
    }

    // SUBSTITUIR EXPRESSÕES ALGÉBRICAS POR OUTRAS EQUIVALENTES
    private void subExpAlg()
    {
        for(TAC instrucao : instrucoesOtimizadas)
        {
            TAC novaInstrucao = getExpAlgEqui(instrucao); // aqui ele retora a expressão algébrica equivalente
            int indice = instrucoesOtimizadas.indexOf(instrucao);

            // editar a instrução
            //instrucoesOtimizadas.set(indice, novaInstrucao);
            instrucoesOtimizadas.get(indice).setOperador(novaInstrucao.getOperador());
            instrucoesOtimizadas.get(indice).setOperandoDir(novaInstrucao.getOperandoDir());
            instrucoesOtimizadas.get(indice).setOperandoEsq(novaInstrucao.getOperandoEsq());
            instrucoesOtimizadas.get(indice).setResultado(novaInstrucao.getResultado());
        }
    }

    // RETORNAR UMA EXPRESSÃO ALGÉBRICA EQUIVALENTE A RECEBIDA POR PARÂMETRO
    private TAC getExpAlgEqui(TAC instrucao)
    {
        // conseguir a expressão algébrica equivalente
        if(instrucao.getOperador().equals("*"))
        {
            if(instrucao.getOperandoDir().equals("2")){
                return new TAC("+",instrucao.getOperandoEsq(),instrucao.getOperandoEsq(),instrucao.getResultado());
            }
            else if(instrucao.getOperandoEsq().equals("2")){
                return new TAC("+",instrucao.getOperandoDir(),instrucao.getOperandoDir(),instrucao.getResultado());
            }
            else if(instrucao.getOperandoDir().equals("1")){
                return new TAC("=",instrucao.getOperandoEsq(),"",instrucao.getResultado());
            }
            else if(instrucao.getOperandoEsq().equals("1")){
                return new TAC("=",instrucao.getOperandoDir(),"",instrucao.getResultado());
            }
            else if(instrucao.getOperandoDir().equals("0") || instrucao.getOperandoDir().equals("0")){
                return new TAC("=","0","",instrucao.getResultado());
            }
        }
        else if(instrucao.getOperador().equals("/"))
        {
            if(instrucao.getOperandoDir().equals("1")){
                return new TAC("=",instrucao.getOperandoEsq(),"",instrucao.getResultado());
            }
            else if(instrucao.getOperandoDir().equals("0") || instrucao.getOperandoEsq().equals("0")){
                return new TAC("=","0","",instrucao.getResultado());
            }
            else if (instrucao.getOperandoDir().equals("0.5")){
                return new TAC("+",instrucao.getOperandoEsq(),instrucao.getOperandoEsq(),instrucao.getResultado());
            }
        }
        else if(instrucao.getOperador().equals("+") || instrucao.getOperador().equals("-"))
        {
            if(instrucao.getOperandoDir().equals("0")){
                return new TAC("=",instrucao.getOperandoEsq(),"",instrucao.getResultado());
            }
            else if(instrucao.getOperandoEsq().equals("0")){
                return new TAC("=",instrucao.getOperandoDir(),"",instrucao.getResultado());
            }
        }
        return instrucao;
    }

    // VERIFICAÇÕES AUXILIARES
    private boolean isInicioWhile(List<TAC> instrucoes, int pos)
    {
        // 1. Verifica se a posição atual é um rótulo(label)
        if (isRotulo(instrucoes.get(pos))) {

            // Pega o nome do rótulo (ex: "L1")
            // Assumindo que o nome do label fica no campo de resultado ou operandoEsq
            String nomeLabel = instrucoes.get(pos).getResultado();

            // 2. Procura um goto mais pra frente que aponte de volta para esse rótulo
            for (int i = pos + 1; i < instrucoes.size(); i++) {
                TAC atual = instrucoes.get(i);

                if (isDesvioIncondicional(atual) && atual.getResultado().equals(nomeLabel)) {
                    return true; // Achou o salto de volta! É certeza que é um While.
                }
            }
        }
        return false; // É apenas um label de if/else comum
    }

    // L1
    // <
    // >
    // (ifFalse) while(x) seFalse -> L3
        //      L6
        //      >
        //      (ifFalse) while(z) seFalse -> L7
        //      ..
        //      ..
        //      goto L6
        //      L7
        //      <
        //      >
        //      (ifFalse) if(y) seFalse -> L2
        //      ..
        //      ..
        //      L2
    //  goto L1
    // L3
    private boolean isInicioIf(List<TAC> instrucoes, int pos)
    {
        if (isDesvioCondicional(instrucoes.get(pos))) {
            int i = pos-1;
            while(i>-1 &&
                    (isOperacaoRelacional(instrucoes.get(i)) ||
                    isOperacaoAritmetica(instrucoes.get(i)) ||
                    isAtribuicao(instrucoes.get(i)))
            )
                i--;

            if(i > -1) // parou antes do início do programa
                if(isRotulo(instrucoes.get(i)))
                {
                    TAC rotulo = instrucoes.get(i); //primeiro rótulo antes do ifFalse
                    int j = pos+1;
                    while(!instrucoes.get(pos).getResultado().equals(instrucoes.get(j).getResultado()))
                        j++;

                    if(isDesvioIncondicional(instrucoes.get(j-1))) // encontrou goto
                        if(instrucoes.get(j-1).getResultado().equals(rotulo.getResultado())) // while
                            return false;
                        else
                            return true;
                    else
                        return true;
                }
                else
                    return true;
            else
                return true;
        }
        return false;
    }

    private boolean isDesvioCondicional(TAC instrucao)
    {
        return instrucao.getOperador().equals("ifFalse");
    }

    private boolean isDesvioIncondicional(TAC instrucao)
    {
        return instrucao.getOperador().equals("goto");
    }

    private boolean isRotulo(TAC instrucao)
    {
        return instrucao.getOperador().equals("label");
    }

    private boolean isReturn(TAC instrucao)
    {
        return instrucao.getOperador().equals("return");
    }

    private boolean isAtribuicao(TAC instrucao)
    {
        return instrucao.getOperador().equals("=");
    }

    private boolean isOperacaoAritmetica(TAC instrucao)
    {
        return instrucao.getOperador().equals("+")  ||
                instrucao.getOperador().equals("-") ||
                instrucao.getOperador().equals("*") ||
                instrucao.getOperador().equals("/");
    }

    private boolean isOperacaoRelacional(TAC instrucao)
    {
        return instrucao.getOperador().equals(">")   ||
                instrucao.getOperador().equals("<")  ||
                instrucao.getOperador().equals(">=") ||
                instrucao.getOperador().equals("<=") ||
                instrucao.getOperador().equals("==") ||
                instrucao.getOperador().equals("!=");
    }

    public void imprimirInstrucoesOtimizadas() {
        for (TAC tac : instrucoesOtimizadas) {
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
