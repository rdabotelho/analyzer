/*
 * Terminal.java
 *
 * Created on 31 de Agosto de 2007, 15:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn.def;

import com.rim.analyzer.*;
import com.rim.analyzer.syn.def.SyntacticTokenDefinition;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.syn.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class TerminalDefinition extends SyntacticTokenDefinition{
    
    private LexicalTokenDefinition tokenDefinition;
    private String lexema;

    /** Creates a new instance of Terminal */
    public TerminalDefinition(String lexema) {
        setLexema(lexema);
    }    

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
    
    public String toString(){
        if(!getLexema().equals(""))
            return getLexema();
        else
            return getTokenDefinition().getName();
    }

    public LexicalTokenDefinition getTokenDefinition() {
        return tokenDefinition;
    }

    public void setTokenDefinition(LexicalTokenDefinition tokenDefinition) {
        this.tokenDefinition = tokenDefinition;
    }

    public boolean isTerminal() {
        return true;
    }
    
    public boolean match(SyntacticMatcher token) {
        try {
            if(token.isTerminal()){
                if(!this.getLexema().equals("")){
                    if(getTokenDefinition().getLexicalAnalyzer().getAnalyzer().isCaseSensitive())
                        return this.getLexema().equals(token.getLexema()) && token.get(LexicalTokenDefinition.class) == this.getTokenDefinition();
                    else
                        return this.getLexema().equalsIgnoreCase(token.getLexema()) && token.get(LexicalTokenDefinition.class) == this.getTokenDefinition();
                }
                else
                    return token.get(LexicalTokenDefinition.class) == this.getTokenDefinition();
            }
            else
                return false;
        }
        catch(Exception e){
            return false;
        }
    }

    public boolean isEnd() {
        return false;
    }
    
    public boolean isEmpty(){
        return false;
    }    

    public <T> T get(Class<T> cls) {
        if(cls == TerminalDefinition.class)
            return (T)this;
        else if(cls == LexicalTokenDefinition.class)
            return (T)this.getTokenDefinition();
        else
            return null;
    }   
    
    public boolean isType(){
        return tokenDefinition.isType();
    }    

    public boolean isComputed(){
        return this.getTokenDefinition().isComputed();
    }
}
