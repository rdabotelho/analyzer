/*
 * LexicalToken.java
 *
 * Created on 16 de Setembro de 2007, 15:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.lex;

import com.rim.analyzer.*;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.syn.SyntacticMatcher;
import com.rim.analyzer.syn.def.TerminalDefinition;

/**
 *
 * @author Raimundo Botelho
 */
public class TokenHandler implements SyntacticMatcher{
    
    private int line;
    private int position;
    private int realPosition;
    private LexicalToken handle;
            
    /** Creates a new instance of LexicalToken */
    public TokenHandler(int line,int position,int realPosition,LexicalToken handle) {
        setLine(line);
        setPosition(position);
        setHandle(handle);
        setRealPosition(realPosition);
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LexicalToken getHandle() {
        return handle;
    }

    public void setHandle(LexicalToken handle) {
        this.handle = handle;
    }
    
    public String toString(){
        return this.handle.toString();
    }

    public int getIndex() {
        return 0;
    }
    
    public boolean isTerminal() {
        return true;
    }
    
    public boolean match(SyntacticMatcher token) {
        if(token.isTerminal()){
            if(!token.getLexema().equals("")){
                if(getHandle().getDefinition().getLexicalAnalyzer().getAnalyzer().isCaseSensitive())
                    return token.getLexema().equals(this.getLexema()) && this.getHandle().getDefinition() == token.get(LexicalTokenDefinition.class);
                else
                    return token.getLexema().equalsIgnoreCase(this.getLexema()) && this.getHandle().getDefinition() == token.get(LexicalTokenDefinition.class);                    
            }
            else
                return this.getHandle().getDefinition() == token.get(LexicalTokenDefinition.class);
        }
        else
            return false;
    }

    public boolean isEnd() {
        return false;
    }
    
    public boolean isEmpty(){
        return false;
    }    

    public <T> T get(Class<T> cls) {
        if(cls == TokenHandler.class)
            return (T)this;
        else if(cls == LexicalToken.class)
            return (T)this.getHandle() ;
        else if(cls == LexicalTokenDefinition.class)
            return (T)this.getHandle().getDefinition();
        else
            return null;
    }       
    
    public String getLexema(){
        return handle.getLexema();
    }
    
    public boolean isType(){
        return handle.getDefinition().isType();
    }        
    
    public boolean isComputed(){
        return handle.getDefinition().isComputed();
    }

    public int getRealPosition() {
        return realPosition;
    }

    public void setRealPosition(int realPosition) {
        this.realPosition = realPosition;
    }
}
