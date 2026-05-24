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

        //realizar a cópia das instruções originais
        for (TAC i : instrucoes)
            instrucoesOtimizadas.add(new TAC(i.getOperador(),i.getOperandoEsq(),i.getOperandoDir(), i.getResultado())); // Usando um construtor de cópia

        // PASSOS PARA SEREM SEGUIDOS:

        // 1 - Substituição de expressões algébricas por equivalentes mais simples
        subExpAlg();

        // 2 - Propagação de cópias
        propagacaoCopias();

        // 3 - Eliminação de subexpressões comuns
        elimSubExpComuns();

        // 4 - Eliminação de código redundante
        remNeverUsedLines();

        // 5 - Eliminação de desvios desnecessários
        remDesviosDesnecessarios();

        // 6 - Eliminar laços que sempre serão falsos
        elimLacosSempreFalsos();

        // 7 - Otimização de laços
        otimizacaoLacos();



        return instrucoesOtimizadas;
    }

    // ================= PARTES ======================================
    /**
     * // PARTE 1
     * SUBSTITUIR EXPRESSÕES ALGÉBRICAS POR OUTRAS EQUIVALENTES
     * */
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

    /**
     * // PARTE 2
     * REMOVE AS LINHAS QUE CONTÉM CÓPIAS
     * */
    private void propagacaoCopias()
    {
        int i = 0;
        while (i < instrucoesOtimizadas.size())
        {
            TAC copia = instrucoesOtimizadas.get(i);

            if (isAtribuicao(copia) && copia.getOperandoDir().isEmpty() && !isNumero(copia.getOperandoEsq()))
            {
                String destino = copia.getResultado();
                String origem = copia.getOperandoEsq();

                int j = i + 1;
                boolean parar = false;

                while (j < instrucoesOtimizadas.size() && !parar)
                {
                    TAC atual = instrucoesOtimizadas.get(j);

                    if (isRotulo(atual) || isDesvioCondicional(atual) || isDesvioIncondicional(atual) || isReturn(atual))
                    {
                        parar = true;
                    }
                    else
                    {
                        if (destino.equals(atual.getResultado()) || origem.equals(atual.getResultado()))
                        {
                            parar = true;
                        }
                        else
                        {
                            if (destino.equals(atual.getOperandoEsq()))
                                atual.setOperandoEsq(origem);

                            if (destino.equals(atual.getOperandoDir()))
                                atual.setOperandoDir(origem);
                        }
                    }

                    j++;
                }
            }

            i++;
        }
    }

    /**
     * // PARTE 3
     * REMOVE AS LINHAS QUE SÃO SUBEXPRESSÕES COMUNS
     * */
    private void elimSubExpComuns()
    {
        int i = 0;
        while (i < instrucoesOtimizadas.size())
        {
            TAC atual = instrucoesOtimizadas.get(i);

            if (isOperacaoAritmetica(atual) || isOperacaoRelacional(atual))
            {
                int j = 0;
                boolean substituiu = false;

                while (j < i && !substituiu)
                {
                    TAC anterior = instrucoesOtimizadas.get(j);

                    if (isOperacaoAritmetica(anterior) || isOperacaoRelacional(anterior))
                    {
                        boolean mesmaExpressao =
                                atual.getOperador().equals(anterior.getOperador()) &&
                                        atual.getOperandoEsq().equals(anterior.getOperandoEsq()) &&
                                        atual.getOperandoDir().equals(anterior.getOperandoDir());

                        if (mesmaExpressao)
                        {
                            boolean esqOk = !foiAlteradoEntre(anterior.getOperandoEsq(), j + 1, i - 1, instrucoesOtimizadas);
                            boolean dirOk = !foiAlteradoEntre(anterior.getOperandoDir(), j + 1, i - 1, instrucoesOtimizadas);

                            if (esqOk && dirOk)
                            {
                                atual.setOperador("=");
                                atual.setOperandoEsq(anterior.getResultado());
                                atual.setOperandoDir("");
                                substituiu = true;
                            }
                        }
                    }

                    j++;
                }
            }

            i++;
        }
    }

    /**
     * // PARTE 4
     * REMOVE AS LINHAS QUE NUNCA SÃO UTILIZADAS
     * */
    private void remNeverUsedLines()
    {
        for(int i=0; i<instrucoesOtimizadas.size(); i++)
            if(!isInicioIf(instrucoesOtimizadas,i) &&
                    !isDesvioCondicional(instrucoesOtimizadas.get(i)) &&
                    !isDesvioIncondicional(instrucoesOtimizadas.get(i)) &&
                    !isReturn(instrucoesOtimizadas.get(i)) &&
                    !isRotulo(instrucoesOtimizadas.get(i)) &&
                    isNeverUsed(i, instrucoesOtimizadas))
                instrucoesOtimizadas.remove(i);
    }

    /**
     * // PARTE 5
     * REMOVER DESVIOS DESNECESSÁRIOS -> GOTO APONTANDO PARA A LINHA LOGO ABAIXO
     * */
    private void remDesviosDesnecessarios()
    {
        List<Integer> linhas = linhasDesvIncoProxInst(instrucoesOtimizadas);

        for(Integer linha : linhas)
            instrucoesOtimizadas.remove(linha);
    }

    /**
     * // PARTE 6
     * OTIMIZA O CONTEÚDO DA EXPRESSÃO LÓGICA CASO SEJA CONSTANTE,
     *  E TAMBÉM OTIMIZA O CONTEÚDO DENTRO DO WHILE CASO O MESMO FOR CONSTANTE
     * */
    private void otimizacaoLacos()
    {
        int i = 0;

        while (i < instrucoesOtimizadas.size())
        {
            boolean ehWhile = isInicioWhile(instrucoesOtimizadas, i);

            if (ehWhile)
            {
                String labelInicio = instrucoesOtimizadas.get(i).getResultado();

                int j = i + 1;
                int linhaGotoVolta = -1;
                boolean achouGoto = false;

                while (j < instrucoesOtimizadas.size() && !achouGoto)
                {
                    TAC atual = instrucoesOtimizadas.get(j);

                    if (isDesvioIncondicional(atual) &&
                            atual.getResultado().equals(labelInicio))
                    {
                        linhaGotoVolta = j;
                        achouGoto = true;
                    }

                    j++;
                }

                if (linhaGotoVolta != -1)
                {
                    int k = i + 1;
                    boolean movido = false;

                    while (k < linhaGotoVolta && !movido)
                    {
                        TAC atual = instrucoesOtimizadas.get(k);

                        boolean candidata =
                                isOperacaoAritmetica(atual) ||
                                        isOperacaoRelacional(atual);

                        if (candidata)
                        {
                            String opEsq = atual.getOperandoEsq();
                            String opDir = atual.getOperandoDir();
                            String resultado = atual.getResultado();

                            boolean opEsqAlterado = foiAlteradoEntre(opEsq, k + 1, linhaGotoVolta - 1, instrucoesOtimizadas);
                            boolean opDirAlterado = foiAlteradoEntre(opDir, k + 1, linhaGotoVolta - 1, instrucoesOtimizadas);
                            boolean resultadoReatribuido = foiAlteradoEntre(resultado, k + 1, linhaGotoVolta - 1, instrucoesOtimizadas);

                            if (!opEsqAlterado && !opDirAlterado && !resultadoReatribuido)
                            {
                                TAC movida = new TAC(
                                        atual.getOperador(),
                                        atual.getOperandoEsq(),
                                        atual.getOperandoDir(),
                                        atual.getResultado()
                                );

                                instrucoesOtimizadas.add(i, movida);
                                instrucoesOtimizadas.remove(k + 1);
                                movido = true;
                            }
                        }

                        k++;
                    }
                }
            }

            i++;
        }
    }
    /**
     * // PARTE 7
     * ELIMINA LAÇOS QUE SÃO CONSTANTEMENTE FALSOS
     * */
    private void elimLacosSempreFalsos()
    {
        int i = 0;

        while (i < instrucoesOtimizadas.size())
        {
            TAC atual = instrucoesOtimizadas.get(i);
            boolean removerEstrutura = false;
            boolean ehWhile = false;
            boolean ehIf = false;

            int inicioRemocao = -1;
            int fimRemocao = -1;
            int linhaRotuloSaida = -1;

            if (isDesvioCondicional(atual))
            {
                boolean condicaoFalsa = false;
                String cond = atual.getOperandoEsq();

                if (isNumero(cond))
                {
                    condicaoFalsa = Double.parseDouble(cond) == 0;
                }
                else
                {
                    int posCond = i - 1;

                    if (posCond >= 0)
                    {
                        TAC instrucaoCond = instrucoesOtimizadas.get(posCond);

                        if (isOperacaoRelacional(instrucaoCond) &&
                                instrucaoCond.getResultado().equals(cond) &&
                                isNumero(instrucaoCond.getOperandoEsq()) &&
                                isNumero(instrucaoCond.getOperandoDir()))
                        {
                            double esq = Double.parseDouble(instrucaoCond.getOperandoEsq());
                            double dir = Double.parseDouble(instrucaoCond.getOperandoDir());
                            String op = instrucaoCond.getOperador();
                            boolean resultado = false;

                            if (op.equals(">"))
                                resultado = esq > dir;
                            else if (op.equals("<"))
                                resultado = esq < dir;
                            else if (op.equals(">="))
                                resultado = esq >= dir;
                            else if (op.equals("<="))
                                resultado = esq <= dir;
                            else if (op.equals("=="))
                                resultado = esq == dir;
                            else if (op.equals("!="))
                                resultado = esq != dir;

                            condicaoFalsa = !resultado;
                        }
                    }
                }

                if (condicaoFalsa)
                {
                    String labelSaida = atual.getResultado();

                    int j = i + 1;
                    boolean achouRotuloSaida = false;

                    while (j < instrucoesOtimizadas.size() && !achouRotuloSaida)
                    {
                        TAC candidata = instrucoesOtimizadas.get(j);

                        if (isRotulo(candidata) && candidata.getResultado().equals(labelSaida))
                        {
                            linhaRotuloSaida = j;
                            achouRotuloSaida = true;
                        }

                        j++;
                    }

                    if (linhaRotuloSaida != -1)
                    {
                        int k = i - 1;
                        boolean achouLabelInicio = false;
                        int linhaLabelInicio = -1;

                        while (k >= 0 && !achouLabelInicio)
                        {
                            TAC anterior = instrucoesOtimizadas.get(k);

                            if (isRotulo(anterior))
                            {
                                linhaLabelInicio = k;
                                achouLabelInicio = true;
                            }
                            else if (isDesvioCondicional(anterior) || isDesvioIncondicional(anterior) || isReturn(anterior))
                            {
                                k = -1;
                            }

                            k--;
                        }

                        if (linhaLabelInicio != -1)
                        {
                            String labelInicio = instrucoesOtimizadas.get(linhaLabelInicio).getResultado();

                            int m = i + 1;
                            int linhaGotoVolta = -1;
                            boolean achouGotoVolta = false;

                            while (m < linhaRotuloSaida && !achouGotoVolta)
                            {
                                TAC candidata = instrucoesOtimizadas.get(m);

                                if (isDesvioIncondicional(candidata) &&
                                        candidata.getResultado().equals(labelInicio))
                                {
                                    linhaGotoVolta = m;
                                    achouGotoVolta = true;
                                }

                                m++;
                            }

                            if (linhaGotoVolta != -1)
                            {
                                ehWhile = true;
                                removerEstrutura = true;
                                inicioRemocao = linhaLabelInicio;
                                fimRemocao = linhaGotoVolta;
                            }
                        }

                        if (!ehWhile)
                        {
                            ehIf = true;
                            removerEstrutura = true;

                            inicioRemocao = i;
                            fimRemocao = linhaRotuloSaida - 1;

                            if (i - 1 >= 0)
                            {
                                TAC possivelCond = instrucoesOtimizadas.get(i - 1);

                                if (isOperacaoRelacional(possivelCond) &&
                                        possivelCond.getResultado().equals(atual.getOperandoEsq()))
                                {
                                    inicioRemocao = i - 1;
                                }
                            }
                        }
                    }
                }
            }

            if (removerEstrutura && inicioRemocao >= 0 && fimRemocao >= inicioRemocao)
            {
                int qtdRemocoes = fimRemocao - inicioRemocao + 1;
                int c = 0;

                while (c < qtdRemocoes)
                {
                    instrucoesOtimizadas.remove(inicioRemocao);
                    c++;
                }
                // remover o label final
                instrucoesOtimizadas.remove(inicioRemocao);

                i = inicioRemocao;
                if (i < 0)
                    i = 0;
            }
            else
            {
                i++;
            }
        }
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
    /**
     * RETORNA BOOLEANO SE É O INÍCIO DE UM WHILE OU NÃO
     * */
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

    /**
     * RETORNA BOOLEANO SE É O INÍCIO DE UM IF OU NÃO
     * */
    private boolean isInicioIf(List<TAC> instrucoes, int pos)
    {
        if (isDesvioCondicional(instrucoes.get(pos)))
        {
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

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UM DESVIO CONDICIONAL OU NÃO
     * */
    private boolean isDesvioCondicional(TAC instrucao)
    {
        return instrucao.getOperador().equals("ifFalse");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UM DESVIO INCONDICIONAÇ OU NÃO
     * */
    private boolean isDesvioIncondicional(TAC instrucao)
    {
        return instrucao.getOperador().equals("goto");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UM RÓTULO OU NÃO
     * */
    private boolean isRotulo(TAC instrucao)
    {
        return instrucao.getOperador().equals("label");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UMA OPERAÇÃO DE RETURN OU NÃO
     * */
    private boolean isReturn(TAC instrucao)
    {
        return instrucao.getOperador().equals("return");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UMA OPERAÇÃO DE ATRIBUIÇÃO OU NÃO
     * */
    private boolean isAtribuicao(TAC instrucao)
    {
        return instrucao.getOperador().equals("=");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UMA OPERAÇÃO ARITMÉTICA OU NÃO
     * */
    private boolean isOperacaoAritmetica(TAC instrucao)
    {
        return instrucao.getOperador().equals("+")  ||
                instrucao.getOperador().equals("-") ||
                instrucao.getOperador().equals("*") ||
                instrucao.getOperador().equals("/");
    }

    /**
     * RETORNA BOOLEANO SE A INSTRUÇÃO É UMA OPERAÇÃO RELACIONAL OU NÃO
     * */
    private boolean isOperacaoRelacional(TAC instrucao)
    {
        return instrucao.getOperador().equals(">")   ||
                instrucao.getOperador().equals("<")  ||
                instrucao.getOperador().equals(">=") ||
                instrucao.getOperador().equals("<=") ||
                instrucao.getOperador().equals("==") ||
                instrucao.getOperador().equals("!=");
    }

    /**
     * EXIBE TODA A LISTA CONTENDO AS INSTRUÇÕES TAC (THREE ADDRESS CODE) OTIMIZADAS
     * */
    public void imprimirInstrucoesOtimizadas()
    {
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

    /**
     * RETORNA BOOLEANO INDICANDO SE A VARIÁVEL FOI ALTERADA OU NÃO DENTRO DE UM ESPAÇO INÍCIO E FIM
     * */
    private boolean foiAlteradoEntre(String variavel, int inicio, int fim, List<TAC> instrucoes)
    {
        if (variavel == null || variavel.isEmpty() || isNumero(variavel))
            return false;

        if (inicio < 0)
            inicio = 0;

        if (fim >= instrucoes.size())
            fim = instrucoes.size() - 1;

        int i = inicio;
        boolean alterado = false;

        while (i <= fim && !alterado)
        {
            if (variavel.equals(instrucoes.get(i).getResultado()))
                alterado = true;

            i++;
        }

        return alterado;
    }

    /**
     * RETORNA A QUANTIDADE DE VEZES QUE HOUVE UMA ATRIBUIÇÃO PARA UMA DETERMINADA VARIÁVEL EM UM
     *  DETERMINADO ESPAÇO DE INÍCIO E FIM
     * */
    private int quantidadeAtribuicoesNoIntervalo(String variavel, int inicio, int fim, List<TAC> instrucoes)
    {
        if (variavel == null || variavel.isEmpty())
            return 0;

        if (inicio < 0)
            inicio = 0;

        if (fim >= instrucoes.size())
            fim = instrucoes.size() - 1;

        int i = inicio;
        int qtde = 0;

        while (i <= fim)
        {
            if (variavel.equals(instrucoes.get(i).getResultado()))
                qtde++;

            i++;
        }

        return qtde;
    }

    /**
     * RETORNA BOOLEANO INDICANDO SE A CONDICAO SERÁ SEMPRE FALSA OU NÃO
     * */
    private boolean condicaoSempreFalsa(TAC ifFalse, List<TAC> instrucoes, int posCondicao)
    {
        if (posCondicao < 0 || posCondicao >= instrucoes.size())
            return false;

        TAC cond = instrucoes.get(posCondicao);

        boolean ehRelacional = isOperacaoRelacional(cond);
        boolean ambosNumeros = isNumero(cond.getOperandoEsq()) && isNumero(cond.getOperandoDir());

        if (ehRelacional && ambosNumeros)
        {
            double esq = Double.parseDouble(cond.getOperandoEsq());
            double dir = Double.parseDouble(cond.getOperandoDir());
            String op = cond.getOperador();

            boolean resultado;

            if (op.equals(">"))
                resultado = esq > dir;
            else if (op.equals("<"))
                resultado = esq < dir;
            else if (op.equals(">="))
                resultado = esq >= dir;
            else if (op.equals("<="))
                resultado = esq <= dir;
            else if (op.equals("=="))
                resultado = esq == dir;
            else if (op.equals("!="))
                resultado = esq != dir;
            else
                resultado = true;

            return !resultado;
        }

        return false;
    }

    /**
     * RETORNA BOOLEANO INDICANDO SE A STRING É UM NÚMERO VÁLIDO OU NÃO
     * */
    private boolean isNumero(String valor)
    {
        if (valor == null || valor.isEmpty())
            return false;

        return valor.matches("-?\\d+(\\.\\d+)?");
    }

}
