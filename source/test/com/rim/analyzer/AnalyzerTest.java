/*
 * AnalyzerTest.java
 * JUnit based test
 *
 * Created on 12 de Novembro de 2007, 14:47
 */

package com.rim.analyzer;

import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.lex.def.LexicalTokenType;
import java.util.List;
import junit.framework.*;
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
public class AnalyzerTest extends TestCase {
    
    private static Analyzer analyzer;
    
    public AnalyzerTest(String testName) {
        super(testName);
    }

    public void testCreateAnalyzer() throws Exception {
        String uri = new AnalyzerFactory().getClass().getResource("resources/expression-definition.xml").getPath();
        analyzer = AnalyzerFactory.createAnalyzer(uri);        
        assertNotNull(analyzer);
    }
    
    public void testScanner() throws Exception{
        StringBuffer code = new StringBuffer("3 + 4.2");
        analyzer.scanner(code);
        List<TokenHandler> tokensSequence = analyzer.getLexicalAnalyzer().getTokensSequence();
        assertTrue(tokensSequence.size() == 3 && 
                   tokensSequence.get(0).getHandle().getDefinition().getType() == LexicalTokenType.INTEGER &&
                   tokensSequence.get(1).getHandle().getDefinition().getType() == LexicalTokenType.ARITHMETIC_OPERATOR &&
                   tokensSequence.get(2).getHandle().getDefinition().getType() == LexicalTokenType.REAL);
    }
    
    public void testParse() throws Exception{
        analyzer.parse();
        assertTrue(true);
    }

    public void testCompute() throws Exception{
        TypeSupporter result = analyzer.compute();
        assertTrue(result.getValue().toString().equals("7.2"));
    }
}
