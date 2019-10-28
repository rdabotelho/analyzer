/*
 * TypeInteger.java
 *
 * Created on 21 de Outubro de 2007, 11:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

/**
 *
 * @author Raimundo Botelho
 */
public class IntegerSupporter extends TypeSupporter<Integer> {
    
    /** Creates a new instance of TypeInteger */
    
    public IntegerSupporter(Integer value){
        setValue(value);
    }
    
    public String getTypeName() {
        return "integer";
    }
}
