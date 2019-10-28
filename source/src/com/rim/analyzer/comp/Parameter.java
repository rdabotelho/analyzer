/*
 * Parameter.java
 *
 * Created on 27 de Outubro de 2007, 09:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.lex.def.LexicalTokenType;

/**
 *
 * @author Raimundo Botelho
 */
public interface Parameter {
    public TypeSupporter compute() throws ComputingException;  
    public TokenHandler getTokenHandler();
}
