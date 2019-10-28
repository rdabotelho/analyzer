/*
 * JavaExpressionSupporter.java
 *
 * Created on 21 de Outubro de 2007, 12:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.comp;

import com.rim.analyzer.lex.LexicalConsts;
import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Raimundo Botelho
 */
public class ExpressionSupporter extends ComputingSupporter {
        
    protected String lastOperator;
    
    /** Creates a new instance of JavaExpressionSupporter */
    public ExpressionSupporter() {
    }
    
    protected void semanticError(String msg) throws ComputingException{        
        invocationError = msg;
        throw new ComputingException(msg);
    }

    protected void semanticError(String msg,TokenHandler tokenHandle) throws ComputingException{        
        invocationError = msg+" (Line: "+tokenHandle.getLine()+", Position: "+tokenHandle.getPosition()+")";
        throw new SemanticException(invocationError);
    }    
    
    protected boolean isAndOperator(String lexema){
        return lexema.equals("&&");
    }

    protected boolean isOrOperator(String lexema){
        return lexema.equals("||");
    }

    protected boolean isNotOperator(String lexema){
        return lexema.equals("!");
    }

    protected boolean isSumOperator(String lexema){
        return lexema.equals("+");
    }

    protected boolean isSubtractionOperator(String lexema){
        return lexema.equals("-");
    }

    protected boolean isMultiplicationOperator(String lexema){
        return lexema.equals("*");
    }

    protected boolean isDivisionOperator(String lexema){
        return lexema.equals("/");
    }

    protected boolean isModuleOperator(String lexema){
        return lexema.equals("%");
    }

    protected boolean isEqualOperator(String lexema){
        return lexema.equals("==");
    }
    
    protected boolean isDiferentOperator(String lexema){
        return lexema.equals("!=");
    }

    protected boolean isMoreOrEqualOperator(String lexema){
        return lexema.equals(">=");
    }

    protected boolean isLessOrEqualOperator(String lexema){
        return lexema.equals("<=");
    }

    protected boolean isMoreOperator(String lexema){
        return lexema.equals(">");
    }

    protected boolean isLessOperator(String lexema){
        return lexema.equals("<");
    }

    public TypeSupporter logicalOperation(List<Parameter> parameters) throws ComputingException{
        switch(parameters.size()){
            // "OR" or "AND" operation
            case 3: {
                TypeSupporter left = parameters.get(0).compute();
                TypeSupporter right = parameters.get(2).compute();                
                lastOperator = parameters.get(1).getTokenHandler().getLexema();
                if(isOrOperator(lastOperator))
                    return or(left,right);
                else if(isAndOperator(lastOperator))
                    return and(left,right);
                else
                    return null;
            }
            // "NOT" operation
            case 2: return not(null,parameters.get(1).compute());
            // other operation
            case 1: return parameters.get(0).compute();
            default: return null;
        }
    }              
    
    public TypeSupporter relationalOperation(List<Parameter> parameters) throws ComputingException{
        switch(parameters.size()){
            // relational operation
            case 3: {
                TypeSupporter left = parameters.get(0).compute();
                TypeSupporter right = parameters.get(2).compute();                
                lastOperator = parameters.get(1).getTokenHandler().getLexema();
                if(isEqualOperator(lastOperator))
                    return equal(left,right);
                else if(isDiferentOperator(lastOperator))
                    return different(left,right);
                else if(isMoreOrEqualOperator(lastOperator))
                    return moreOrEqual(left,right);
                else if(isLessOrEqualOperator(lastOperator))
                    return lessOrEqual(left,right);
                else if(isMoreOperator(lastOperator))
                    return more(left,right);
                else if(isLessOperator(lastOperator))
                    return less(left,right);
                else
                    return null;
            }
            // other operation
            case 1: return parameters.get(0).compute();
            default: return null;
        }
    }              

    public TypeSupporter arithmeticOperation(List<Parameter> parameters) throws ComputingException{
        switch(parameters.size()){
            // relational operation
            case 3: {
                TypeSupporter left = parameters.get(0).compute();
                TypeSupporter right = parameters.get(2).compute();                
                lastOperator = parameters.get(1).getTokenHandler().getLexema();
                if(isSumOperator(lastOperator))
                    return sum(left,right);
                else if(isSubtractionOperator(lastOperator))
                    return subtraction(left,right);
                else if(isMultiplicationOperator(lastOperator))
                    return multiplication(left,right);
                else if(isDivisionOperator(lastOperator))
                    return division(left,right);
                else if(isModuleOperator(lastOperator))
                    return module(left,right);
                else
                    return null;
            }
            // other operation
            case 2: {
                TypeSupporter right = parameters.get(1).compute();                
                lastOperator = parameters.get(0).getTokenHandler().getLexema();
                if(isSumOperator(lastOperator))
                    return sum(new IntegerSupporter(0),right);
                else if(isSubtractionOperator(lastOperator))
                    return subtraction(new IntegerSupporter(0),right);
                else
                    return null;
            }
            case 1: return parameters.get(0).compute();
            default: return null;
        }        
    }                      
    
    // Types Defineds    
    public TypeSupporter keywords(String lexema){
        return new StringSupporter(lexema);
    }    

    public TypeSupporter identifier(String lexema){
        return new StringSupporter(lexema);
    }    
    
    public TypeSupporter integer(String lexema){
        return new IntegerSupporter(Integer.parseInt(lexema));
    }

    public TypeSupporter real(String lexema){
        return new RealSupporter(Float.parseFloat(lexema));
    }

    public TypeSupporter bool(String lexema){
        if(lexema.equals(LexicalConsts.TRUE_VALUE))
            return new BooleanSupporter(true);
        else
            return new BooleanSupporter(false);
    }

    public TypeSupporter string(String lexema){
        return new StringSupporter(lexema);
    }
       
    // Conversions
    protected Integer toIntegerValue(TypeSupporter value){
        if(value.getType() == Integer.class )
            return (Integer)value.getValue();
        else
            return Integer.parseInt(value.toString());
    }
    
    protected Float toRealValue(TypeSupporter value){
        if(value.getType() == Float.class )
            return (Float)value.getValue();
        else
            return Float.parseFloat(value.toString());
    }
    
    protected Boolean toBoolValue(TypeSupporter value){
        if(value.getType() == Boolean.class)
            return (Boolean)value.getValue();
        else {
            if(value.toString().equals(LexicalConsts.TRUE_VALUE))
                return true;
            else if(value.toString().equals(LexicalConsts.FALSE_VALUE))
                return false;
            else
                return Boolean.parseBoolean(value.toString());
        }
   }
    
    protected String toStringValue(TypeSupporter value){
        return value.toString();
    }
    
    // Arithmetic Operations
    public TypeSupporter sum(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left == null)
            left = new IntegerSupporter(0);
        if(left.getType() == RealSupporter.class || right.getType() == RealSupporter.class)
            return new RealSupporter(toRealValue(left) + toRealValue(right));
        else if(left.getType() == IntegerSupporter.class && right.getType() == IntegerSupporter.class)
            return new IntegerSupporter(toIntegerValue(left) + toIntegerValue(right));
        else if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new StringSupporter(toStringValue(left) + toStringValue(right));
        else           
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }
    
    public TypeSupporter subtraction(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left == null)
            left = new IntegerSupporter(0);
        if(left.getType() == RealSupporter.class || right.getType() == RealSupporter.class)
            return new RealSupporter(toRealValue(left) - toRealValue(right));
        else if(left.getType() == IntegerSupporter.class && right.getType() == IntegerSupporter.class)
            return new IntegerSupporter(toIntegerValue(left) - toIntegerValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }

    public TypeSupporter multiplication(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == RealSupporter.class || right.getType() == RealSupporter.class)
            return new RealSupporter(toRealValue(left) * toRealValue(right));
        else if(left.getType() == IntegerSupporter.class && right.getType() == IntegerSupporter.class)
            return new IntegerSupporter(toIntegerValue(left) * toIntegerValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }

    public TypeSupporter division(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == RealSupporter.class || right.getType() == RealSupporter.class)
            return new RealSupporter(toRealValue(left) / toRealValue(right));
        else if(left.getType() == IntegerSupporter.class && right.getType() == IntegerSupporter.class)
            return new IntegerSupporter(toIntegerValue(left) / toIntegerValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }
    
    public TypeSupporter module(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == IntegerSupporter.class && right.getType() == IntegerSupporter.class)
            return new IntegerSupporter(toIntegerValue(left) % toIntegerValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }
    
    // Logical Operations
    public TypeSupporter and(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == BooleanSupporter.class && right.getType() == BooleanSupporter.class)
            return new BooleanSupporter(toBoolValue(left) && toBoolValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }

    public TypeSupporter or(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == BooleanSupporter.class && right.getType() == BooleanSupporter.class)
            return new BooleanSupporter(toBoolValue(left) || toBoolValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        return null;    
    }
    
    public TypeSupporter not(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(right.getType() == BooleanSupporter.class)
            return new BooleanSupporter(!toBoolValue(right));
        else
            semanticError("Operator "+lastOperator+" cannot be applied to "+right.getTypeName());        
        return null;    
    }
    
    // Relacional operations
    public TypeSupporter equal(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(left.toString().equals(right.toString()));
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            return new BooleanSupporter(left.toString().equals(right.toString()));
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 == f2);
        }
    }
    
    public TypeSupporter different(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(!left.toString().equals(right.toString()));
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            return new BooleanSupporter(!left.toString().equals(right.toString()));
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 != f2);
        }
    }    
    
    public TypeSupporter moreOrEqual(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(left.toString().compareTo(right.toString()) >= 0);
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 >= f2);
        }
        return null;
    }    
    
    public TypeSupporter lessOrEqual(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(left.toString().compareTo(right.toString()) <= 0);
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 <= f2);
        }
        return null;
    }    
    
    public TypeSupporter more(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(left.toString().compareTo(right.toString()) > 0);
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 > f2);
        }
        return null;
    }    
    
    public TypeSupporter less(TypeSupporter left,TypeSupporter right) throws ComputingException{
        if(left.getType() == StringSupporter.class || right.getType() == StringSupporter.class)
            return new BooleanSupporter(left.toString().compareTo(right.toString()) < 0);
        if(left.getType() == BooleanSupporter.class || right.getType() == BooleanSupporter.class)
            semanticError("Operator "+lastOperator+" cannot be applied to "+left.getTypeName()+","+right.getTypeName());        
        else{
            float f1 = toRealValue(left);
            float f2 = toRealValue(right);
            return new BooleanSupporter(f1 < f2);
        }
        return null;
    }    
}
