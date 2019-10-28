/*
 * Appender.java
 *
 * Created on 2 de Novembro de 2007, 17:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer;

import java.io.IOException;

/**
 *
 * @author Raimundo Botelho
 */
public interface Appender {
    public void print(String s);
    public void println();
    public void println(String s);
    public String readln() throws IOException;  
}
