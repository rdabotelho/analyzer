/*
 * SyntacticAnalizador.java
 *
 * Created on 31 de Agosto de 2007, 15:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.Analyzer;
import com.rim.analyzer.lex.LexicalToken;
import com.rim.analyzer.lex.TokenHandler;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class SyntacticAnalyzer {
    
    private Analyzer analyzer;
    private ContextFreeGrammar contextFreeGrammar;
    private SyntacticTree syntacticTree;

    /** Creates a new instance of SyntacticAnalizador */
    public SyntacticAnalyzer() {
        syntacticTree = new SyntacticTree(this);
    }           
    
    public void parse(List<TokenHandler> tokensSequence,List<LexicalToken> tokensTable) throws SyntacticException{
        getContextFreeGrammar().parse(tokensSequence,tokensTable);
    }
    
    public ContextFreeGrammar getContextFreeGrammar() {
        return contextFreeGrammar;
    }

    public void setContextFreeGrammar(ContextFreeGrammar contextFreeGrammar) {
        this.contextFreeGrammar = contextFreeGrammar;
        this.contextFreeGrammar.setSyntacticAnalyzer(this);
    }    

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }    

    public SyntacticTree getSyntacticTree() {
        return syntacticTree;
    }
}
