/*
 * TypeSupporter.java
 *
 * Created on 21 de Outubro de 2007, 10:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

/**
 *
 * @author Raimundo Botelho
 */
public abstract class TypeSupporter<T> {
    
    private T value;
    
    /** Creates a new instance of TypeSupporter */
    public TypeSupporter() {
    }

    public Class getType(){
        return this.getClass();        
    }    
    
    public abstract String getTypeName();
    
    public T getValue(){
        return this.value;
    }
    
    public void setValue(T value){
        this.value = value;
    }

    public String toString() {
        return getValue().toString();
    }    
}
