/*
 * NovoClass.java
 *
 * Created on 31 de Agosto de 2007, 14:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer;

import com.rim.analyzer.comp.ComputingException;
import com.rim.analyzer.comp.ComputingSupporter;
import com.rim.analyzer.comp.TypeSupporter;
import com.rim.analyzer.lex.LexicalAnalyzer;
import com.rim.analyzer.lex.LexicalException;
import com.rim.analyzer.syn.SyntacticAnalyzer;
import com.rim.analyzer.syn.SyntacticException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author Administrador
 */
public class Analyzer {
                
    private String languageName;
    private String languageDescription;
    private String languageVersion;
    private boolean caseSensitive;
    private Appender appender;
    private SyntacticAnalyzer syntacticAnalizador; 
    private LexicalAnalyzer lexicalAnalyzer;    
    private ComputingSupporter computingSupporter;
    
    /** Creates a new instance of NovoClass */
    public Analyzer(Appender appender) {
        setAppender(appender);
    }
    
    public void setAppender(Appender appender){
        if(appender != null)
            this.appender = appender;
    }
    
    public Appender getAppender(){
        return this.appender;
    }

    public Analyzer(ComputingSupporter computingSupporter) {
        setComputingSupporter(computingSupporter);
    }

    public ComputingSupporter getComputingSupporter(){
        return this.computingSupporter;
    }
    
    public void setComputingSupporter(ComputingSupporter computingSupporter){
        this.computingSupporter = computingSupporter;        
        this.computingSupporter.setAnalyzer(this);
    }

    public SyntacticAnalyzer getSyntacticAnalizador() {
        return syntacticAnalizador;
    }

    public void setSyntacticAnalizador(SyntacticAnalyzer syntacticAnalizador) {
        this.syntacticAnalizador = syntacticAnalizador;
        this.syntacticAnalizador.setAnalyzer(this);
    }

    public void scanner(String uri) throws FileNotFoundException, IOException, LexicalException{
        getLexicalAnalyzer().scanner(uri);        
    }

    public void scanner(File file) throws FileNotFoundException, IOException, LexicalException{
        getLexicalAnalyzer().scanner(file);        
    }
    
    public void scanner(Reader reader) throws FileNotFoundException, IOException, LexicalException{
        getLexicalAnalyzer().scanner(reader);        
    }
    
    public void scanner(CharSequence source) throws FileNotFoundException, IOException, LexicalException{
        getLexicalAnalyzer().scanner(source);        
    }
    
    public void parse() throws SyntacticException{
        getSyntacticAnalizador().parse(getLexicalAnalyzer().getTokensSequence(),getLexicalAnalyzer().getTokensTable());        
    }
    
    public TypeSupporter compute() throws ComputingException{
        return getSyntacticAnalizador().getSyntacticTree().compute();
    }
    
    public LexicalAnalyzer getLexicalAnalyzer() {
        return lexicalAnalyzer;
    }

    public void setLexicalAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.lexicalAnalyzer.setAnalyzer(this);
    }
    
    public void viewTokensSequence(boolean view) {
        getLexicalAnalyzer().viewTokensSequence(view);
    }
    
    public void viewContextFreeGrammar(boolean view) {
        getSyntacticAnalizador().getContextFreeGrammar().viewContextFreeGrammar(view);
    }    
    
    public void viewSyntacticTable(boolean view){
        getSyntacticAnalizador().getContextFreeGrammar().viewSyntacticTable(view);
    }    

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageDescription() {
        return languageDescription;
    }

    public void setLanguageDescription(String languageDescription) {
        this.languageDescription = languageDescription;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(String languageVersion) {
        this.languageVersion = languageVersion;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}
