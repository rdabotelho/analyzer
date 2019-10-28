/*
 * LexiconAnalyzer.java
 *
 * Created on 14 de Setembro de 2007, 17:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.lex;

import com.rim.analyzer.*;
import com.rim.analyzer.lex.LexicalToken;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.lex.def.LexicalTokenType;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrador
 */
public class LexicalAnalyzer {
    
    private Analyzer analyzer;
    private Pattern newLinePattern;
    private Pattern pattern;            
    private Matcher matcher;
    private CharSequence source;
    private int position;
    private int line;
    private int linePosition;
    
    private List<LexicalTokenDefinition> tokensDefinitions;
    private List<TokenHandler> tokensSequence;
    private List<LexicalToken> tokensTable;
    
    private boolean viewTokensSequence;
    
    /** Creates a new instance of LexiconAnalyzer */
    public LexicalAnalyzer() {
        tokensDefinitions = new ArrayList();
        tokensSequence = new LinkedList();
        tokensTable = new LinkedList();
        viewTokensSequence(false);
    }
    
    private void beginScanner(CharSequence src){
        getTokensSequence().clear();
        getTokensTable().clear();
        source = src;
        StringBuffer regExpr = new StringBuffer();
        for(LexicalTokenDefinition ct : tokensDefinitions)
            regExpr.append("("+ct.getPattern().pattern()+")|");
        regExpr.append("(\\s)");
        if(!getAnalyzer().isCaseSensitive())
            pattern = Pattern.compile(regExpr.toString(),Pattern.DOTALL+Pattern.CASE_INSENSITIVE);
        else
            pattern = Pattern.compile(regExpr.toString(),Pattern.DOTALL);
        newLinePattern = Pattern.compile("(\\n)",Pattern.DOTALL);
        matcher = pattern.matcher(source);        
        position = 0;
        line = 1;
        linePosition = 1;
    }
    
    private LexicalToken findIdentify(String lexema){
        for(LexicalToken lexToken : tokensTable)
            if(lexToken.getLexema().equals(lexema))
                return lexToken;
        return null;
    }
    
    private LexicalTokenDefinition findTokenDefinitionByPattern(String lexema){
        for(LexicalTokenDefinition ct : tokensDefinitions)       
            if(ct.getPattern().matcher(lexema).matches())
                return ct;
        return null;
    }
    
    private TokenHandler nextToken() throws LexicalException{
        String lexema;
        while(matcher.find()){
            if(position != matcher.start())
                throw new LexicalException("Token not defined (line: "+line+", position: "+linePosition+")");
            lexema = matcher.group();
            Matcher newLineMather = newLinePattern.matcher(lexema);
            while(newLineMather.find()){
                line++;
                linePosition = 1;
            }                
            position = matcher.end();
            if(!lexema.equals("\r") && !lexema.equals("\n")){
                linePosition += lexema.length();
                if(!lexema.equals("") && !lexema.equals(" ") && !lexema.equals("\t")){
                    LexicalTokenDefinition tokenDef = findTokenDefinitionByPattern(lexema);
                    LexicalToken lexToken = null;
                    if(tokenDef.getType() == LexicalTokenType.IDENTIFIER)
                        lexToken = findIdentify(lexema);
                    if(lexToken == null){                    
                        lexToken = new LexicalToken(lexema,tokenDef);
                        getTokensTable().add(lexToken);
                    }    
                    TokenHandler tokenHandler = new TokenHandler(line,linePosition-lexema.length(),position,lexToken);
                    return tokenHandler;
                }
            }    
        }
        if(position == source.length())
            return null;
        else
            throw new LexicalException("Token not defined (line: "+line+", position: "+linePosition+")");
    }

    private void error(String msg,TokenHandler token) throws LexicalException{
        throw new LexicalException(msg+" (Line: "+token.getLine()+", Position: "+token.getPosition()+")");
    }
    
    public void scanner(String uri) throws FileNotFoundException, IOException, LexicalException{
        File file = new File(uri);
        scanner(file);
    }
    
    public void scanner(File file) throws FileNotFoundException, IOException, LexicalException{
        FileReader fileReader = new FileReader(file);
        scanner(fileReader);
    }
    
    public void scanner(Reader reader) throws IOException,  LexicalException{
        StringBuffer buffer = new StringBuffer();
        int c;
        while((c = reader.read()) != -1)
            buffer.append((char)c);
        scanner(buffer);
    }

    public void scanner(CharSequence source) throws LexicalException{        
        showInfo();
        beginScanner(source);
        TokenHandler tokenHandler;
        while((tokenHandler = nextToken()) != null)
            if(tokenHandler.getHandle().getDefinition().getType() != LexicalTokenType.COMMENT)
                getTokensSequence().add(tokenHandler); 
        if(viewTokensSequence)
            showTokensSequence();
    }
    
    private void showInfo(){        
        analyzer.getAppender().println("\nGeneric Grammatical Analyzer - Version 1.0");
        analyzer.getAppender().println("Students:");
        analyzer.getAppender().println("\tRaimundo Botelho");
        analyzer.getAppender().println("\tIsrael França");
        analyzer.getAppender().println("\tMichael Salzer");
        analyzer.getAppender().println("Instructor:");
        analyzer.getAppender().println("\tOtávio Noura\n");
    }
    
    private void showTokensSequence(){
        analyzer.getAppender().println("\n---TOKENS SEQUENCES---\n");            
        for(TokenHandler th : tokensSequence)
            analyzer.getAppender().print(th.getHandle().toString());
        analyzer.getAppender().println();                                    
    }
    
    public List<LexicalTokenDefinition> getTokensDefinitions() {
        return tokensDefinitions;
    }

    public LexicalTokenDefinition findTokenDefinition(String name){
        for(LexicalTokenDefinition td : tokensDefinitions)
            if(td.getName().equals(name))
                return td;
        return null;
    }

    public void viewTokensSequence(boolean view) {
        this.viewTokensSequence = view;
    }

    public List<TokenHandler> getTokensSequence() {
        return tokensSequence;
    }

    public List<LexicalToken> getTokensTable() {
        return tokensTable;
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }    
}
