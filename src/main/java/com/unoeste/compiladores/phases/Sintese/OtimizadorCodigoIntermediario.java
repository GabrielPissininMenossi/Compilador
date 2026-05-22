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

        // andar cada instrução para realizar uma otimização ou não
        for(TAC instrucao : instrucoes)
        {
            if(existeOtimizacao(instrucao)) // verificação se a instrução possui possibilidade de otimização
            {
                // realiza a otimização
            }
            else
            {
                instrucoesOtimizadas.add(instrucao);
            }
        }

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

    // VERIFICAÇÃO SE UMA VARIÁVEL NÃO É UTILIZADA DEPOIS DE UMA DETERMINADA LINHA
    private boolean isNeverUsed(int linha, List<TAC> instrucoes)
    {

        return true; //provisório
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

    // RETORNO DE UMA LISTA DOS ÍNDICES DAS LINHAS CONTENDO CÓPIAS INUTILIZADAS (INÚTEIS)
    private List<Integer> linhasCopInut(List<TAC> instrucoes)
    {
        List<Integer> copias = new ArrayList<>();

        return copias;
    }

    // RETORNO DE UMA LISTA DOS ÍNDICES DAS LINHAS CONTENDO UM DESVIO INCONDICIONAL QUE APONTA DIRETAMENTE PARA A PRÓXIMA LINHA
    private List<Integer> linhasDesvIncoProxInst()
    {
        List<Integer> desvios = new ArrayList<>();

        return desvios;
    }

    // SUBSTITUIR EXPRESSÕES ALGÉBRICAS POR OUTRAS EQUIVALENTES
    private void subExpAlg()
    {
        for(TAC instrucao : instrucoesOtimizadas)
        {
            TAC novaInstrucao = getExpAlgEqui(instrucao); // aqui ele retora a expressão algébrica equivalente
            int indice = instrucoesOtimizadas.indexOf(novaInstrucao);

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
        TAC novaInstrucao = new TAC();

        // conseguir a expressão algébrica equivalente

        return novaInstrucao;
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
        return instrucao.getOperador().equals("+") || instrucao.getOperador().equals("-") ||
                instrucao.getOperador().equals("*") || instrucao.getOperador().equals("/");
    }

    private boolean isOperacaoRelacional(TAC instrucao)
    {
        return instrucao.getOperador().equals(">") || instrucao.getOperador().equals("<") ||
                instrucao.getOperador().equals(">=") || instrucao.getOperador().equals("<=") ||
                instrucao.getOperador().equals("==") || instrucao.getOperador().equals("!=");
    }
}
