/*
 * SyntacticTree.java
 *
 * Created on 28 de Setembro de 2007, 16:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.comp.ComputingException;
import com.rim.analyzer.comp.TypeSupporter;

/**
 *
 * @author Administrador
 */
public class SyntacticTree {
    
    private SyntacticAnalyzer syntacticAnalyzer;
    private SyntacticNode root;
    
    /** Creates a new instance of SyntacticTree */
    public SyntacticTree(SyntacticAnalyzer syntacticAnalyzer) {
        setSyntacticAnalyzer(syntacticAnalyzer);
        root = new SyntacticNode(this);
    }

    public SyntacticTree(SyntacticAnalyzer syntacticAnalyzer,SyntacticNode root) {
        setSyntacticAnalyzer(syntacticAnalyzer);
        setRoot(root);
    }
    
    public SyntacticNode getRoot() {
        return root;
    }

    public void setRoot(SyntacticNode root) {
        this.root = root;
    }
    
    public void clear(){
        root.clearChildNodes();
    }                  

    public TypeSupporter compute() throws ComputingException{
        if(root.countChildNodes() == 0)
            throw new ComputingException("To compute is required the scanner and the parse phases");
        if(getSyntacticAnalyzer().getAnalyzer().getComputingSupporter() == null)
            throw new ComputingException("ComputingSupporter interface not defined");            
        return root.compute();
    }

    public SyntacticAnalyzer getSyntacticAnalyzer() {
        return syntacticAnalyzer;
    }

    public void setSyntacticAnalyzer(SyntacticAnalyzer syntacticAnalyzer) {
        this.syntacticAnalyzer = syntacticAnalyzer;
    }
}
