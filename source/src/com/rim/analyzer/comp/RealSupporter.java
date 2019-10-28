/*
 * FloatSupporter.java
 *
 * Created on 21 de Outubro de 2007, 11:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

/**
 *
 * @author Raimundo Botelho
 */
public class RealSupporter extends TypeSupporter<Float> {
    
    public RealSupporter(Float value){
        setValue(value);
    }
    
    public String getTypeName() {
        return "float";
    }
}
