/*
 * Production.java
 *
 * Created on 31 de Agosto de 2007, 15:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.syn.def.SyntacticTokenDefinition;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class Production {
    private boolean empty;
    private List<SyntacticTokenDefinition> tokens = new ArrayList();
    
    /** Creates a new instance of Production */
    public Production(boolean empty) {
        this.setIsEmpty(empty);
    }
 
    public Production() {
        this(false);
    }

    public int tokensCount(){
        return tokens.size();
    }
    
    public SyntacticTokenDefinition getToken(int index){
        return tokens.get(index);
    }
    
    public void addToken(SyntacticTokenDefinition token){
        tokens.add(token);
    }
    
    public void remove(int index){
        tokens.remove(index);
    }
    
    public void remove(SyntacticTokenDefinition token){
        tokens.remove(token);
    }

    public void clear(){
        tokens.clear();
    }
    
    public List<SyntacticTokenDefinition> getTokens(){
        return tokens;
    }
    
    public int compareTo(List<SyntacticTokenDefinition> tokens){
        int d;
        int i = 0;
        if(tokens == null) return -1;
        if(tokens.size() > this.tokensCount())
            d = this.tokensCount();
        else 
            d = tokens.size();
        while((i < d) && ((this.getToken(i) == tokens.get(i)) ||(this.getToken(i).toString().equals(tokens.get(i).toString()))))
            i++;
        return i-1;
    }
    
    public Production subproduction(int beginIndex){
        Production newProd = new Production();
        for(int i=beginIndex; i<this.tokensCount(); i++)
            newProd.addToken(this.getToken(i));
        return newProd;
    }
    
    public void prefix(int until,List<SyntacticTokenDefinition> listToPrefix){
        listToPrefix.clear();
        for(int i=0; i<=until; i++)
            listToPrefix.add(this.getToken(i));
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setIsEmpty(boolean empty) {
        this.empty = empty;
    }
    
    public String toString(){
        String ret = "";
        for(SyntacticTokenDefinition t : getTokens())
            ret += t + " ";
        return ret;        
    }
}
