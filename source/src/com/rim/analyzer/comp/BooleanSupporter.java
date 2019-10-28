/*
 * BooleanSupporter.java
 *
 * Created on 21 de Outubro de 2007, 11:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

import com.rim.analyzer.lex.LexicalConsts;

/**
 *
 * @author Raimundo Botelho
 */
public class BooleanSupporter extends TypeSupporter<Boolean> {
    
    public BooleanSupporter(Boolean value){
        setValue(value);
    }

    public String getTypeName() {
        return "boolean";
    }
    
    public String toString(){
        if(getValue())
            return LexicalConsts.TRUE_VALUE;
        else
            return LexicalConsts.FALSE_VALUE;
    }
}
