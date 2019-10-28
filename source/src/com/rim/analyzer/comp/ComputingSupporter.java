/*
 * ComputingSupport.java
 *
 * Created on 19 de Outubro de 2007, 15:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

import com.rim.analyzer.Analyzer;
import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.syn.SyntacticNode;
import com.rim.analyzer.syn.def.NotTerminalDefinition;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrador
 */
public abstract class ComputingSupporter {
    
    private Analyzer analyzer;
    protected String invocationError;
    private TokenHandler lastTokenHandler;
    
    public ComputingSupporter(){        
    }
    
    protected void error(String msg) throws ComputingException{ 
        invocationError = msg;
        throw new ComputingException(msg);
    }

    protected void error(String msg,TokenHandler tokenHandle) throws ComputingException{        
        invocationError = msg;
        throw new ComputingException(msg+" (Line: "+tokenHandle.getLine()+", Position: "+tokenHandle.getPosition()+")");
    }

    public TypeSupporter compute(SyntacticNode node) throws ComputingException{
        if(node.getToken().isType()){
            lastTokenHandler = node.getToken().get(TokenHandler.class);
            return invokeComputingMethodToType(lastTokenHandler.getHandle().getDefinition().getName(),node.getToken().getLexema());            
        }    
        else if(node.getToken().isTerminal())
            return new StringSupporter(node.getToken().getLexema());
        else{
            NotTerminalDefinition ntd = node.getToken().get(NotTerminalDefinition.class);
            String methodName = ntd.getMethod();
            if(methodName != null){
                List<Parameter> parameters = new ArrayList<Parameter>();
                node.getComputingNodes(parameters);
                return invokeComputingMethod(methodName,parameters);
            }
            else
                return null;
        }
    }
    
    public TypeSupporter invokeComputingMethodToType(String methodName,String lexema) throws ComputingException{
        try {   
            methodName = methodName.replaceAll("\\-",methodName);
            Method method = this.getClass().getMethod(methodName,String.class);
            if(method.getReturnType() != TypeSupporter.class)
                throw new NoSuchMethodException("");
            return (TypeSupporter)method.invoke(this,lexema);            
        }    
        catch(NoSuchMethodException e){
            error("Method \"TypeSupporter "+methodName+"(String)\" not found for type \""+methodName+"\"");
        }
        catch(IllegalAccessException e){
            error(e.getMessage());                
        }
        catch(InvocationTargetException e){
            error(invocationError);                
        }
        return null;
    }    

    public TypeSupporter invokeComputingMethod(String methodName,List<Parameter> parameters) throws ComputingException{
        try {                
            Method method = this.getClass().getMethod(methodName,List.class);
            if(method.getReturnType() != TypeSupporter.class)
                throw new NoSuchMethodException("");
            return (TypeSupporter)method.invoke(this,parameters);            
        }    
        catch(NoSuchMethodException e){
            error("Method \"TypeSupporter "+methodName+"(List<Parameter>)\" not found");
        }
        catch(IllegalAccessException e){
            error(e.getMessage());                
        }
        catch(InvocationTargetException e){
            error(invocationError);                
        }
        return null;
    }            

    protected TokenHandler getLastTokenHandler() {
        return this.lastTokenHandler;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

}
