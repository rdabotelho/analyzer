/*
 * StringSupporter.java
 *
 * Created on 21 de Outubro de 2007, 11:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

/**
 *
 * @author Raimundo Botelho
 */
public class StringSupporter extends TypeSupporter<String>{
        
    /** Creates a new instance of StringSupporter */
    public StringSupporter(String value){
        setValue(value);
    }

    public String getTypeName() {
        return "string";        
    }       
}
