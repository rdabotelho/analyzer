/*
 * SyntacticMatcher.java
 *
 * Created on 25 de Setembro de 2007, 08:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.lex.def.LexicalTokenDefinition;

/**
 *
 * @author Administrador
 */
public interface SyntacticMatcher {
    public int getIndex();
    public boolean isTerminal();
    public boolean match(SyntacticMatcher token);
    public boolean isEnd();
    public boolean isEmpty();
    public <T> T get(Class<T> cls);
    public String getLexema();
    public boolean isType();
    public boolean isComputed();
}
