/*
 * InterpreterBuilder.java
 *
 * Created on 1 de Setembro de 2007, 19:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer;

import com.rim.analyzer.comp.ComputingSupporter;
import com.rim.analyzer.lex.LexicalAnalyzer;
import com.rim.analyzer.lex.LexicalConsts;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.lex.def.LexicalTokenType;
import com.rim.analyzer.syn.ContextFreeGrammar;
import com.rim.analyzer.syn.Production;
import com.rim.analyzer.syn.SyntacticAnalyzer;
import com.rim.analyzer.syn.def.NotTerminalDefinition;
import com.rim.analyzer.syn.def.TerminalDefinition;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Raimundo Botelho
 */
public class AnalyzerFactory {
    
    private static String ANALYZER_TAG = "analyzer";
    private static String LANG_NAME_TAG = "language-name";
    private static String LANG_DESC_TAG = "language-description";
    private static String LANG_VERSION_TAG = "language-version";
    private static String CASE_SENSITIVE_TAG = "case-sensitive";
    private static String LEXICAL_TAG = "lexical";
    private static String TOKENS_TAG = "tokens";
    private static String COMMENT_TAG = "comment";
    private static String STRING_TAG = "string";
    private static String REAL_TAG = "real";
    private static String BOOLEAN_TAG = "bool";
    private static String INTEGER_TAG = "integer";
    private static String DELIMITER_TAG = "delimiter";        
    private static String ARITHMETIC_OPERATOR_TAG = "arithmetic-operator";
    private static String LOGICAL_OPERATOR_TAG = "logical-operator";
    private static String RELATIONAL_OPERATOR_TAG = "relational-operator";
    private static String ATTRIBUTION_OPERATOR_TAG = "attribution-operator";    
    private static String IDENTIFIER_TAG = "identifier";
    private static String KEY_WORDS_TAG = "key-words";
    private static String CUSTOM_TOKEN_TAG = "custom-token";           
    private static String PATTERN_TAG = "pattern";
    private static String SYNTACTIC_TAG = "syntactic";
    private static String CONTEXT_FREE_GRAMMAR_TAG = "context-free-grammar";
    private static String NOT_TERMINAL_TAG = "not-terminal";
    private static String TERMINAL_TAG = "terminal";
    private static String EMPTY_TAG = "empty";
    private static String PRODUCTIONS_TAG = "productions";
    private static String PRODUCTION_TAG = "production";
    private static String COMPUTING_CLASS_TAG = "computing-class";
    
    public static String COMMENT_PATTERN = "/\\*.*?\\*/";
    public static String STRING_PATTERN = "\\\"(\\\\\\\"|[.&[^\\n\\\\\\\"]])*?\\\"";
    public static String INTEGER_PATTERN = "[0-9]+";
    public static String REAL_PATTERN = "[0-9]+(\\.[0-9]+)";
    public static String BOOLEAN_PATTERN = "true|false";
    public static String DELIMITER_PATTERN = "[\\:\\.\\;\\,\\(\\)\\[\\]\\{\\}]";
    public static String ARITHMETIC_OPERATOR_PATTERN = "([\\*/\\+\\-%])";
    public static String LOGICAL_OPERATOR_PATTERN = "(&&|\\|\\||!)";
    public static String RELATIONAL_OPERATOR_PATTERN = "(==|!=|>=|<=|[><])";
    public static String ATTRIBUTION_OPERATOR_PATTERN = "(=)";
    public static String IDENTIFIER_PATTERN = "[a-zA-Z_][a-zA-Z_0-9]*";
    
    /** Creates a new instance of InterpreterBuilder */
    public AnalyzerFactory() {
    }
    
    private static String substituteToScapeChar(String c){        
        return c;
    }
    
    private static Node getTag(NodeList nodes,String name,boolean required) throws AnalyzerFactoryException{
        for(int i=0; i<nodes.getLength(); i++){
            if(nodes.item(i).getNodeName().equalsIgnoreCase(name))
                return nodes.item(i);
        }        
        if(required)
        throw new AnalyzerFactoryException("Tag "+name+" not found");
        return null;
    }
    
    private static String getAttribute(Node node,String name,boolean required) throws AnalyzerFactoryException{
        Node attribute = node.getAttributes().getNamedItem(name);
        if(attribute == null)
            if(required)
                throw new AnalyzerFactoryException("Attribute "+name+" required");
            else
                return null;
        return attribute.getNodeValue();
    }
    
    public static Pattern resolvePattern(String tag,String def,int flag){
        if(tag == null)
            return Pattern.compile(def,flag);
        return Pattern.compile(tag,flag);
    }
    
    public static Analyzer createAnalyzer(String uri)
        throws ParserConfigurationException, SAXException, AnalyzerFactoryException, IOException, InstantiationException, IllegalAccessException{
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();            
        Document doc = docBuilder.parse(uri);
        return createAnalyzer(doc);
    }
    
    public static Analyzer createAnalyzer(File file)
        throws ParserConfigurationException, SAXException, AnalyzerFactoryException, IOException, InstantiationException, IllegalAccessException{
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();            
        Document doc = docBuilder.parse(file);
        return createAnalyzer(doc);
    }

    public static Analyzer createAnalyzer(InputStream is)
        throws ParserConfigurationException, SAXException, AnalyzerFactoryException, IOException, InstantiationException, IllegalAccessException{
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();            
        Document doc = docBuilder.parse(is);
        return createAnalyzer(doc);
    }
    
    public static Analyzer createAnalyzer(Document doc) 
        throws ParserConfigurationException, SAXException, AnalyzerFactoryException, IOException, InstantiationException, IllegalAccessException{
        Node analyzerTag = getTag(doc.getChildNodes(),ANALYZER_TAG,true);
        // LANGUAGE
        Node languageNameTag = getTag(analyzerTag.getChildNodes(),LANG_NAME_TAG,true);
        Node languageDescTag = getTag(analyzerTag.getChildNodes(),LANG_DESC_TAG,false);
        Node languageVersionTag = getTag(analyzerTag.getChildNodes(),LANG_VERSION_TAG,true);
        Node caseSensitiveTag = getTag(analyzerTag.getChildNodes(),CASE_SENSITIVE_TAG,false);
        String languageName = languageNameTag.getTextContent();
        String languageDesc = languageDescTag == null ? "" : languageDescTag.getTextContent();
        String languageVersion = languageVersionTag.getTextContent();
        boolean caseSensitive = caseSensitiveTag == null || (caseSensitiveTag != null && caseSensitiveTag.getTextContent() != null && caseSensitiveTag.getTextContent().equalsIgnoreCase("TRUE"));
        int flag = caseSensitive ? Pattern.DOTALL : Pattern.DOTALL+Pattern.CASE_INSENSITIVE;
        
        // COMPUTING CLASS
        Node computingClassTag = getTag(analyzerTag.getChildNodes(),COMPUTING_CLASS_TAG,false);        
        String computingClass = null;
        if(computingClassTag != null)
            computingClass = computingClassTag.getTextContent();
        
        // LEXICAL ANALIZER 
        Node lexicalAnalyzerTag = getTag(analyzerTag.getChildNodes(),LEXICAL_TAG,true);
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        Node tokensTag = getTag(lexicalAnalyzerTag.getChildNodes(),TOKENS_TAG,true);
        if(tokensTag != null)
        for(int i=0; i<tokensTag.getChildNodes().getLength(); i++){
            Node tokenTag = tokensTag.getChildNodes().item(i);
            if(tokenTag.getNodeType() != 1)
                continue;                                    
            String TokenName = getAttribute(tokenTag,"name",false);
            if(TokenName == null)
                TokenName = tokenTag.getNodeName();
            String begin = getAttribute(tokenTag,"begin",false);
            String end = getAttribute(tokenTag,"end",false);
            String isComputedAtt = getAttribute(tokenTag,"isComputed",false);
            boolean isComputed = isComputedAtt == null ? false : isComputedAtt.equalsIgnoreCase("true");
            String isTypeAtt = getAttribute(tokenTag,"isType",false);
            boolean isType = isTypeAtt == null ? false : isTypeAtt.equalsIgnoreCase("true");
            Node patternTag = getTag(tokenTag.getChildNodes(),PATTERN_TAG,false);
            String pattern = null;
            if(patternTag != null)
                pattern = patternTag.getTextContent();            
            LexicalTokenDefinition tokenDef;
            if(tokenTag.getNodeName().equalsIgnoreCase(CUSTOM_TOKEN_TAG)){
                if(pattern == null || pattern.equalsIgnoreCase(""))
                    throw new AnalyzerFactoryException("Tag pattern not defined to custom-token");
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.CUSTOM,TokenName,Pattern.compile(pattern,flag),begin,end,isComputed,isType,null,null);
            }    
            else if(tokenTag.getNodeName().equalsIgnoreCase(KEY_WORDS_TAG)){
                if(pattern == null || pattern.equalsIgnoreCase(""))
                    throw new AnalyzerFactoryException("Tag pattern not defined to key-words");
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.KEY_WORDS,TokenName,Pattern.compile(pattern,flag),null,null,true,isType,null,null);
            }
            else if(tokenTag.getNodeName().equalsIgnoreCase(IDENTIFIER_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.IDENTIFIER,TokenName,resolvePattern(pattern,IDENTIFIER_PATTERN,flag),null,null,true,true,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(ARITHMETIC_OPERATOR_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.ARITHMETIC_OPERATOR,TokenName,resolvePattern(pattern,ARITHMETIC_OPERATOR_PATTERN,flag),null,null,true,false,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(LOGICAL_OPERATOR_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.LOGICAL_OPERATOR,TokenName,resolvePattern(pattern,LOGICAL_OPERATOR_PATTERN,flag),null,null,true,false,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(RELATIONAL_OPERATOR_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.RELATIONAL_OPERATOR,TokenName,resolvePattern(pattern,RELATIONAL_OPERATOR_PATTERN,flag),null,null,true,false,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(ATTRIBUTION_OPERATOR_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.ATTRIBUTION_OPERATOR,TokenName,resolvePattern(pattern,ATTRIBUTION_OPERATOR_PATTERN,flag),null,null,true,false,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(DELIMITER_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.DELIMITER,TokenName,resolvePattern(pattern,DELIMITER_PATTERN,flag),null,null,false,false,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(INTEGER_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.INTEGER,TokenName,resolvePattern(pattern,INTEGER_PATTERN,flag),null,null,true,true,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(REAL_TAG))
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.REAL,TokenName,resolvePattern(pattern,REAL_PATTERN,flag),null,null,true,true,null,null);
            else if(tokenTag.getNodeName().equalsIgnoreCase(BOOLEAN_TAG)){
                String trueValue = getAttribute(tokenTag,"trueValue",false);
                String falseValue = getAttribute(tokenTag,"falseValue",false);
                if(pattern == null && trueValue != null && trueValue != null)
                    pattern = substituteToScapeChar(trueValue) + "|" + substituteToScapeChar(falseValue);
                else{
                    trueValue = "true";
                    falseValue = "false";
                }                
                LexicalConsts.TRUE_VALUE = trueValue;
                LexicalConsts.FALSE_VALUE = falseValue;
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.BOOLEAN,TokenName,resolvePattern(pattern,BOOLEAN_PATTERN,flag),null,null,true,true,trueValue,falseValue);                
            }
            else if(tokenTag.getNodeName().equalsIgnoreCase(STRING_TAG)){
                if(pattern == null && begin != null && end != null)
                    pattern = substituteToScapeChar(begin) + STRING_PATTERN.substring(2,STRING_PATTERN.length()-2) + substituteToScapeChar(end);
                else{
                    begin = "\"";
                    end = "\"";
                }                    
                LexicalConsts.STRING_BEGIN_DELIMITER = begin;
                LexicalConsts.STRING_END_DELIMITER = end;
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.STRING,TokenName,resolvePattern(pattern,STRING_PATTERN,flag),begin,end,true,true,null,null);
            }    
            else if(tokenTag.getNodeName().equalsIgnoreCase(COMMENT_TAG)){
                if(pattern == null && begin != null && end != null)
                    pattern = substituteToScapeChar(begin) + COMMENT_PATTERN.substring(3,COMMENT_PATTERN.length()-3) + substituteToScapeChar(end);                
                else {
                    begin = "/\\*";
                    end = "\\*/";
                }
                LexicalConsts.COMMENT_BEGIN_DELIMITER = begin;
                LexicalConsts.COMMENT_END_DELIMITER = end;
                tokenDef = new LexicalTokenDefinition(LexicalTokenType.COMMENT,TokenName,resolvePattern(pattern,COMMENT_PATTERN,flag),begin,end,false,false,null,null);
            }
            else
                throw new AnalyzerFactoryException("Token type "+tokenTag.getNodeName()+" no definid");                            
            lexicalAnalyzer.getTokensDefinitions().add(tokenDef);
            tokenDef.setLexicalAnalyzer(lexicalAnalyzer);
        }
        
        // SYNTACTIC ANALYZER
        Node syntacticAnalyzerTag = getTag(analyzerTag.getChildNodes(),SYNTACTIC_TAG,true);        
        Node contextFreeGrammarTag = getTag(syntacticAnalyzerTag.getChildNodes(),CONTEXT_FREE_GRAMMAR_TAG,true);                
        ContextFreeGrammar cfg = new ContextFreeGrammar();
        for(int i=0; i<contextFreeGrammarTag.getChildNodes().getLength(); i++){
            Node notTerminalTag = contextFreeGrammarTag.getChildNodes().item(i);            
            if(notTerminalTag.getNodeType() == 1)
            if(notTerminalTag.getNodeName().equalsIgnoreCase(NOT_TERMINAL_TAG)){
                String name = getAttribute(notTerminalTag,"name",true);
                String method = getAttribute(notTerminalTag,"method",false);
                NotTerminalDefinition nt = cfg.createNotTerminal(name);
                nt.setMethod(method);
                cfg.addNotTerminalToGrammar(nt);
            }
            else
                throw new AnalyzerFactoryException("Tag "+notTerminalTag.getNodeName()+" invalid");                        
        }
        for(int i=0; i<contextFreeGrammarTag.getChildNodes().getLength(); i++){
            Node notTerminalTag = contextFreeGrammarTag.getChildNodes().item(i);            
            if(notTerminalTag.getNodeType() == 1)
            if(notTerminalTag.getNodeName().equalsIgnoreCase(NOT_TERMINAL_TAG)){
                String name = getAttribute(notTerminalTag,"name",true);
                NotTerminalDefinition nt = cfg.findNotTerminal(name);
                if(nt == null)
                    throw new AnalyzerFactoryException("Not Terminal "+name+" not defined");
                for(int e=0; e<notTerminalTag.getChildNodes().getLength(); e++){
                    Node productionsTag = notTerminalTag.getChildNodes().item(e);
                    for(int a=0; a<productionsTag.getChildNodes().getLength(); a++){
                        Node productionTag = productionsTag.getChildNodes().item(a);                        
                        if(productionTag.getNodeType() != 1)
                            continue;
                        Production prod = new Production();
                        nt.addProduction(prod);                        
                        for(int k=0; k<productionTag.getChildNodes().getLength(); k++){
                            Node token = productionTag.getChildNodes().item(k);
                            if(token.getNodeType() == 1)
                            if(token.getNodeName().equalsIgnoreCase(NOT_TERMINAL_TAG)){
                                String termName = token.getTextContent();
                                if(termName == null || termName.equals(""))
                                    termName = getAttribute(token,"name",true);
                                NotTerminalDefinition ntrm = cfg.findNotTerminal(termName);
                                if(ntrm == null)
                                    throw new AnalyzerFactoryException("Not terminal "+termName+" not found");
                                prod.addToken(ntrm);
                            }   
                            else if(token.getNodeName().equalsIgnoreCase(TERMINAL_TAG)){                                
                                String termName = getAttribute(token,"name",false);
                                TerminalDefinition trm = cfg.createTerminal(termName,token.getTextContent());
                                if(termName != null){
                                    LexicalTokenDefinition tokenDefinition = lexicalAnalyzer.findTokenDefinition(termName);
                                    if(tokenDefinition == null)
                                        throw new AnalyzerFactoryException("Terminal "+termName+" no defined in lexical analyzer");
                                    trm.setTokenDefinition(tokenDefinition);
                                }
                                prod.addToken(trm);
                            }
                            else if(token.getNodeName().equalsIgnoreCase(EMPTY_TAG)){
                                prod.addToken(cfg.empty);
                                prod.setIsEmpty(true);
                            }    
                            else
                                throw new AnalyzerFactoryException("Tag "+token.getNodeName()+" invalid");                        
                        }
                    }
                }
            }
            else
                throw new AnalyzerFactoryException("Tag "+notTerminalTag.getNodeName()+" invalid");                        
        }
        Analyzer analyzer = new Analyzer(new ConsoleAppender());
        analyzer.setLanguageName(languageName);
        analyzer.setLanguageDescription(languageDesc);
        analyzer.setLanguageVersion(languageVersion);
        analyzer.setCaseSensitive(caseSensitive);
        if(computingClass != null){
            try {
                analyzer.setComputingSupporter((ComputingSupporter)Class.forName(computingClass).newInstance());
            }
            catch(ClassNotFoundException e){
                throw new AnalyzerFactoryException("Class "+computingClass+" not found");
            }
        }    
        analyzer.setLexicalAnalyzer(lexicalAnalyzer);
        SyntacticAnalyzer syntecticAnalizador = new SyntacticAnalyzer();
        syntecticAnalizador.setContextFreeGrammar(cfg);
        analyzer.setSyntacticAnalizador(syntecticAnalizador);
        return analyzer;
    }        
}
