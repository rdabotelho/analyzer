/*
 * NotTerminal.java
 *
 * Created on 31 de Agosto de 2007, 15:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn.def;

import com.rim.analyzer.*;
import com.rim.analyzer.syn.def.SyntacticTokenDefinition;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.syn.*;
import com.rim.analyzer.syn.Production;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class NotTerminalDefinition extends SyntacticTokenDefinition{
    
    private String name;
    private List<Production> productions;
    private String method;
            
    /** Creates a new instance of NotTerminal */
    public NotTerminalDefinition(String name) {
        productions = new ArrayList();
        setName(name);
    }

    public int productionsCount(){
        return getProductions().size();
    }
    
    public Production getProduction(int index){
        return getProductions().get(index);
    }
    
    public void addProduction(Production production){
        getProductions().add(production);
    }

    public void removeProduction(int index){
        getProductions().remove(index);
    }
    
    public void removeProduction(Production production){
        getProductions().remove(production);
    }
    
    public void clearProductions(){
        getProductions().clear();
    }
    
    public List<Production> getProductions(){
        return productions;
    }
    
    public void setProdutions(List<Production> productions){
        this.setProductions(productions);
    }
    
    public boolean hasEmpty(){
        for(Production p : productions)
            if(p.isEmpty())
                return true;
        return false; 
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }
    
    public String toString(){
        return getName();
    }
    
    public boolean isTerminal() {
        return false;
    }
    
    public boolean match(SyntacticMatcher token) {
        return !token.isTerminal() && ((NotTerminalDefinition)token.get(NotTerminalDefinition.class)).getName().equals(this.getName());
    }

    public boolean isEnd() {
        return false;
    }
    
    public boolean isEmpty() {
        return false;
    }

    public <T> T get(Class<T> cls) {
        if(cls == NotTerminalDefinition.class)
            return (T)this;
        else
            return null;
    }   
    
    public String getLexema(){
        return null;
    }
    
    public boolean isType(){
        return false;
    }

    public boolean isComputed(){
        return method != null;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
