package com.unoeste.compiladores.stacks;

public class Pilha
{
    private NoPilha topo;

    public Pilha() {
        inicializar();
    }

    public void inicializar()
    {
        topo = null;
    }

    public void push(String string)
    {
        NoPilha noPilha = new NoPilha(string, null);
        if (topo == null)
        {
            topo = noPilha;
        }
        else
        {
            noPilha.setProx(topo);
            topo = noPilha;
        }
    }

    public NoPilha pop()
    {
        NoPilha aux;
        aux = topo;
        topo = topo.getProx();
        return aux;
    }

    public NoPilha top()
    {
        return topo;
    }

    public boolean isEmpty()
    {
        return topo == null;
    }
}
