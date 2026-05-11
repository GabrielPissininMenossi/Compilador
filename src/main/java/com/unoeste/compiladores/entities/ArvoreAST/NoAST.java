package com.unoeste.compiladores.entities.ArvoreAST;

import java.util.List;

public class NoAST {
    private List<NoGramatica> nos;

    public NoAST(List<NoGramatica> nos) {
        this.nos = nos;
    }

    public NoAST() {
        this(null);
    }

    public List<NoGramatica> getNos() {
        return nos;
    }

    public void setNos(List<NoGramatica> nos) {
        this.nos = nos;
    }

    public NoGramatica getNoAt(int pos) {
        if(pos>0 && pos<nos.size())
            return nos.get(pos);
        return null;
    }

    public void setNoAt(int pos, NoGramatica noGramatica) {
        if(pos>0 && pos<nos.size())
            nos.set(pos, noGramatica);
    }
}
