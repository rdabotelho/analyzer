/*
 * Token.java
 *
 * Created on 31 de Agosto de 2007, 15:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn.def;

import com.rim.analyzer.syn.*;

/**
 *
 * @author Administrador
 */
public abstract class SyntacticTokenDefinition implements SyntacticMatcher {
    
    private int index;
    
    /** Creates a new instance of Token */
    public SyntacticTokenDefinition() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }  
    
    public abstract boolean isComputed();
}
