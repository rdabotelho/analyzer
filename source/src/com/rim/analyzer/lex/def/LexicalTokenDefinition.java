/*
 * CustomToken.java
 *
 * Created on 17 de Setembro de 2007, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.lex.def;

import com.rim.analyzer.lex.LexicalAnalyzer;
import java.util.regex.Pattern;

/**
 *
 * @author Administrador
 */
public class LexicalTokenDefinition {
    
    private LexicalAnalyzer lexicalAnalyzer;
    private String name;
    private String begin;
    private String end;
    private Pattern pattern;
    private LexicalTokenType type;
    private boolean iscomputed;
    private boolean istype;
    private String trueValue;
    private String falseValue;    
    
    /** Creates a new instance of CustomToken */
    public LexicalTokenDefinition() {
    }

    public LexicalTokenDefinition(LexicalTokenType type,String name,Pattern pattern,String begin,String end,
            boolean iscomputed,boolean istype,String trueValue,String falseValue) {
        setType(type);
        setName(name);
        setPattern(pattern);
        setBegin(begin);
        setEnd(end);
        setIsComputed(iscomputed);
        setIsType(istype);
        setTrueValue(trueValue);
        setFalseValue(falseValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public LexicalTokenType getType() {
        return type;
    }

    public void setType(LexicalTokenType type) {
        this.type = type;
    }
    
    public String toString(){
        return this.name;
    }

    public void setIsComputed(boolean value){
        this.iscomputed = value;
    }
    
    public boolean isComputed(){
        return this.iscomputed;
    }
    
    public void setIsType(boolean value){
        this.istype = value;
    }
    
    public boolean isType(){
        return this.istype;
    }    

    public String getTrueValue() {
        return trueValue;
    }

    public void setTrueValue(String trueValue) {
        this.trueValue = trueValue;
    }

    public String getFalseValue() {
        return falseValue;
    }

    public void setFalseValue(String falseValue) {
        this.falseValue = falseValue;
    }

    public LexicalAnalyzer getLexicalAnalyzer() {
        return lexicalAnalyzer;
    }

    public void setLexicalAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
    }
}
