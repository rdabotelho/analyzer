/*
 * Empty.java
 *
 * Created on 1 de Setembro de 2007, 17:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn.def;

import com.rim.analyzer.*;

/**
 *
 * @author Raimundo Botelho
 */
public class EndDefinition extends TerminalDefinition {
    
    /** Creates a new instance of Empty */
    public EndDefinition() {
        super("$");
    }    
    
    public boolean isEnd(){
        return true;
    }        
    
    public boolean isComputed(){
        return false;
    }
}
