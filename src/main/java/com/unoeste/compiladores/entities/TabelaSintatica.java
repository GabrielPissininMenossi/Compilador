package com.unoeste.compiladores.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabelaSintatica
{
    private NaoTerminal programa = new NaoTerminal("programa");
    private NaoTerminal comando = new NaoTerminal("comando");
    private NaoTerminal bloco = new NaoTerminal("bloco");
    private NaoTerminal declaracaoVariavel = new NaoTerminal("declaracaoVariavel");

    private List<Elemento> producaoPrograma = new ArrayList<>(Arrays.asList(comando));


    public TabelaSintatica(List<NaoTerminal> listNaoTerminal) {

        criarEBNF();
    }

    private void criarEBNF()
    {
        List<Elemento> letras = new ArrayList<>();
        criarLetras(letras);
        //NaoTerminal letra = new NaoTerminal("letra", letras);
        //letra = “a” | ... | “z” | “A” | ... | “Z”
    }

    private void criarLetras(List<Elemento> letras)
    {

    }
}
