/*
 * ConsoleAppender.java
 *
 * Created on 2 de Novembro de 2007, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Raimundo Botelho
 */
public class ConsoleAppender implements Appender {
    
    /** Creates a new instance of ConsoleAppender */
    public ConsoleAppender() {
    }

    public void print(String s) {
        System.out.print(s);
    }

    public void println() {
        System.out.println();
    }

    public void println(String s) {
        System.out.println(s);
    }

    public String readln() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));        
        return reader.readLine();
    }
}
