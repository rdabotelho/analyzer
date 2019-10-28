/*
 * AcceptedToken.java
 *
 * Created on 19 de Setembro de 2007, 16:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.lex;

import com.rim.analyzer.*;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.lex.def.LexicalTokenType;
import com.rim.analyzer.syn.def.TerminalDefinition;

/**
 *
 * @author Administrador
 */
public class LexicalToken {
    
    private String lexema;
    private LexicalTokenDefinition definition;
    
    /** Creates a new instance of AcceptedToken */
    public LexicalToken(String lexema,LexicalTokenDefinition customToken) {
        String lex = lexema;
        if(customToken.getType() == LexicalTokenType.STRING){
            int begin = customToken.getBegin().length();
            int end = lex.length()-customToken.getEnd().length();
            lex = lex.substring(begin,end);            
        }    
        setLexema(lex);
        setDefinition(customToken);
    }

    public LexicalTokenDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(LexicalTokenDefinition customToken) {
        this.definition = customToken;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
    
    public String toString(){
        return "<"+definition.getName()+",'"+lexema+"'>";
    }    
}
