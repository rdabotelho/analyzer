/*
 * SyntacticStack.java
 *
 * Created on 1 de Outubro de 2007, 11:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import java.util.Stack;

/**
 *
 * @author Administrador
 */
public class SyntacticStack extends Stack {
    
    /** Creates a new instance of SyntacticStack */
    public SyntacticStack() {
    }

    public SyntacticNode push(SyntacticNode item) {
        return (SyntacticNode)super.push(item);
    }

    public SyntacticNode pop() {
        return (SyntacticNode)super.pop();
    }
    
    public SyntacticNode top() {
        return (SyntacticNode)super.lastElement();
    }   
}
