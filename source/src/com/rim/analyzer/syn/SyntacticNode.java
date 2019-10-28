/*
 * SyntacticNode.java
 *
 * Created on 28 de Setembro de 2007, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.comp.ComputingException;
import com.rim.analyzer.comp.Parameter;
import com.rim.analyzer.comp.TypeSupporter;
import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.lex.def.LexicalTokenType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class SyntacticNode implements Parameter {

    private int index;
    private SyntacticTree tree;
    private SyntacticMatcher token;
    private SyntacticNode parent;
    private List<SyntacticNode> childNodes;
    
    /** Creates a new instance of SyntacticNode */
    public SyntacticNode(SyntacticTree tree) {
        this.tree = tree;
        setChildNodes(new ArrayList());                
        computingReset();
    }
    
    public void computingReset(){
        index = -1;
    }

    public SyntacticNode(SyntacticTree tree,SyntacticMatcher token) {
        this(tree);
        setToken(token);
    }

    public SyntacticNode(SyntacticTree tree,SyntacticMatcher token,SyntacticNode parent) {
        this(tree,token);
        parent.addChildNode(this);
    }

    public List<SyntacticNode> getChildNodes() {
        return childNodes;
    }

    void setChildNodes(List<SyntacticNode> childNodes) {
        this.childNodes = childNodes;
    }
    
    public boolean addChildNode(SyntacticNode childNode){
        index++;
        childNode.parent = this;
        return childNodes.add(childNode);
    }    
    
    public SyntacticNode getChildNode(int index){
        return childNodes.get(index);
    }
    
    public int countChildNodes(){
        return childNodes.size();
    }
    
    public boolean removeChildNode(SyntacticNode childNode){
        return childNodes.remove(childNode);
    }
    
    public SyntacticNode removeChildNode(int index){
        return childNodes.remove(index);
    }    
    
    public void clearChildNodes(){
        childNodes.clear();
    }

    public SyntacticMatcher getToken() {
        return token;
    }

    public void setToken(SyntacticMatcher token) {
        this.token = token;
    }

    public SyntacticNode getParent() {
        return parent;
    }    
    
    public void setParent(SyntacticNode parent) {
        this.parent = parent;
    }
    
    public String toString(){
        return token != null ? token.toString() : null;
    }
    
    public SyntacticTree getTree() {
        return tree;
    }
/*    
    public boolean hasComputed(){
        for(int i=0; i<countChildNodes(); i++){
            if(getChildNode(i).getToken().isComputed() || getChildNode(i).getToken().isType())
                return true;
            else{
                if(getChildNode(i).hasComputed())
                    return true;
            }                
        }    
        return false;
    }
*/    
    public void getComputingNodes(List<Parameter> parameters){
        for(int i=(countChildNodes()-1); i>=0; i--){
            SyntacticNode node = getChildNode(i);
            if(node.getToken().isComputed())
                parameters.add(node);
            else
                node.getComputingNodes(parameters);
        }
    }
    
    public TypeSupporter compute() throws ComputingException{
        return tree.getSyntacticAnalyzer().getAnalyzer().getComputingSupporter().compute(this);
    }    
 
    /*
    public SyntacticNode getNodeToCompute(int index){
        for(int i=(countChildNodes()-1); i>=0; i--){
            if(getChildNode(i).getToken().isComputed() || getChildNode(i).getToken().isType() || getChildNode(i).hasComputed()){
                index--;
                if(index == -1)
                    return getChildNode(i);
            }
        }        
        return null;
    }
     */
    
    public TokenHandler getTokenHandler(){
        if(token.isTerminal())
            return token.get(TokenHandler.class);
        else
            return null;            
    }    
}
