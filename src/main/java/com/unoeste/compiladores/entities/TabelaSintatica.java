package com.unoeste.compiladores.entities;

import java.util.ArrayList;
import java.util.List;

public class TabelaSintatica
{
    List<NaoTerminal> listNaoTerminal = new ArrayList<>();

    public TabelaSintatica(List<NaoTerminal> listNaoTerminal) {
        this.listNaoTerminal = listNaoTerminal;

        criarEBNF();
    }

    private void criarEBNF()
    {
        List<Elemento> letras = new ArrayList<>();
        criarLetras(letras);
        NaoTerminal letra = new NaoTerminal("letra", letras);
        //letra = “a” | ... | “z” | “A” | ... | “Z”
    }

    private void criarLetras(List<Elemento> letras)
    {

    }
}
