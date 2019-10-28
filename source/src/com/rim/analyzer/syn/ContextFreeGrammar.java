/*
 * ContextFreeGrammar.java
 *
 * Created on 1 de Setembro de 2007, 21:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rim.analyzer.syn;

import com.rim.analyzer.syn.def.SyntacticTokenDefinition;
import com.rim.analyzer.lex.LexicalToken;
import com.rim.analyzer.lex.TokenHandler;
import com.rim.analyzer.lex.def.LexicalTokenDefinition;
import com.rim.analyzer.syn.def.EmptyDefinition;
import com.rim.analyzer.syn.def.EndDefinition;
import com.rim.analyzer.syn.def.NotTerminalDefinition;
import com.rim.analyzer.syn.def.TerminalDefinition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Raimundo Botelho
 */
public class ContextFreeGrammar {
    
    private SyntacticAnalyzer syntacticAnalyzer;
    public EmptyDefinition empty;
    public EndDefinition end;
    private int lastId = 0;
    private List<TerminalDefinition> terminals = new ArrayList();
    private List<NotTerminalDefinition> grammar = new ArrayList();  
    //private Object[][] analysisTable;
    private List<Production>[][] analysisTable;
    
    private boolean viewContextFreeGrammar;
    private boolean viewSyntacticTable;
    
    /** Creates a new instance of ContextFreeGrammar */
    public ContextFreeGrammar() {
        empty = new EmptyDefinition();
        end = new EndDefinition();
    }        
    
    public void addNotTerminalToGrammar(NotTerminalDefinition notTerminal){
        grammar.add(notTerminal);
    }
    
    public NotTerminalDefinition createNotTerminal(String value){
        NotTerminalDefinition nt = new NotTerminalDefinition(value);
        return nt;
    }

    public TerminalDefinition createTerminal(String name,String lexema){
        for(TerminalDefinition t : terminals)
            if(t.getTokenDefinition().getName().equals(name) && t.getLexema().equals(lexema))
                return t;
        TerminalDefinition t = new TerminalDefinition(lexema);
        terminals.add(t);
        return t;
    }
    
    public NotTerminalDefinition getInitialToken(){
        return grammar.size() > 0 ? grammar.get(0) : null;
    }
    
    public NotTerminalDefinition findNotTerminal(String value){
        for(NotTerminalDefinition nt : grammar)
            if(nt.getName().equals(value))
                return nt;
        return null;
    }
    
    private void beginParse(List<TokenHandler> tokensSequence) throws SyntacticException{
        if(viewContextFreeGrammar)
            showContextFreeGrammar("ORIGINAL CONTEXT FREE GRAMMAR");
        removeLeftRecursionInGrammar();
        fatorarGrammar();
        if(viewContextFreeGrammar)
            showContextFreeGrammar("OPTIMIZE CONTEXT GRAMMAR");
        buildAnalizyTable();    
    }
    
    private void showContextFreeGrammar(String title){        
        syntacticAnalyzer.getAnalyzer().getAppender().println("\n---"+title+"---\n");
        syntacticAnalyzer.getAnalyzer().getAppender().println(toString());                        
    }
    
    private void error(String msg,SyntacticMatcher token) throws SyntacticException{
        TokenHandler handler = token.get(TokenHandler.class);
        error(msg+" (Line: "+handler.getLine()+", Position: "+(handler.getPosition()-1)+")");
    }

    private void error(String msg) throws SyntacticException{
        throw new SyntacticException(msg);
    }
    
    public void parse(List<TokenHandler> tokensSequence,List<LexicalToken> tokensTable) throws SyntacticException{        
        if(tokensSequence.size() == 0)
            return;
        SyntacticTree tree = syntacticAnalyzer.getSyntacticTree();
        tree.getRoot().clearChildNodes();
        tree.getRoot().setToken(getInitialToken());        
        beginParse(tokensSequence);
        int tokenSeqIndex = 0;
        SyntacticStack stack = new SyntacticStack();
        stack.push(new SyntacticNode(tree,end)); // adiciona o símbolo $(final) na base da pilha
        stack.push(tree.getRoot()); // adiciona o símbolo inicial sob o $(final)
        SyntacticMatcher X; // não terminal do topo da pilha
        SyntacticMatcher a; // a = simbolo atual da entrada
        int insertedProd = 0;
        Iterator<Production> productions = null;
        SyntacticNode parent = null;
        while(true){                        
            X = stack.top().getToken(); // stack top
            a = tokenSeqIndex == tokensSequence.size() ? end : tokensSequence.get(tokenSeqIndex);
            if(X.isEnd() && a.isEnd())
                break;
            else if(X.isTerminal()){
                if(X.match(a)){                      
                    SyntacticNode node = stack.pop();
                    node.setToken(a);
                    tokenSeqIndex++;                                        
                }
                else if(a.isEnd())
                    error("Unexpected end of sentence");
                else{
                    if(productions != null && productions.hasNext()){
                        Production prod = productions.next();
                        insertedProd = 0;
                        for(int i=(prod.tokensCount()-1); i>=0; i--){
                            if(!prod.getToken(i).isEmpty()){                        
                                stack.push(new SyntacticNode(tree,prod.getToken(i),parent));                                            
                                insertedProd++;
                            }    
                        }    
                    }
                    else                    
                        error("Token \""+a.getLexema()+"\" not expected",a);
                }    
            }
            else{          
                List<Production> listProd = getProductionInAnalysisTable(X,a);
                if(listProd != null){
                    productions = listProd.iterator();
                    parent = stack.pop();                                
                    Production prod = productions.next();
                    insertedProd = 0;
                    for(int i=(prod.tokensCount()-1); i>=0; i--){
                        if(!prod.getToken(i).isEmpty()){                        
                            stack.push(new SyntacticNode(tree,prod.getToken(i),parent));                                            
                            insertedProd++;
                        }    
                    }    
                }
                else {
                    if(productions != null && productions.hasNext()){
                        for(int i=0; i<insertedProd; i++)
                            stack.pop();                                
                        Production prod = productions.next();
                        insertedProd = 0;
                        for(int i=(prod.tokensCount()-1); i>=0; i--){
                            if(!prod.getToken(i).isEmpty()){                        
                                stack.push(new SyntacticNode(tree,prod.getToken(i),parent));                                            
                                insertedProd++;
                            }    
                        }                            
                    }
                    else if(a.isEnd())
                        error("Unexpected end of sentence");
                    else
                        error("Token \""+a.getLexema()+"\" not expected",a);
                }                
            }
            
            // show stack
            /*for(int i=0; i<stack.size(); i++)
                getSyntacticAnalyzer().getAnalyzer().getAppender().print(stack.get(i).toString()+" ");
            getSyntacticAnalyzer().getAnalyzer().getAppender().println();            
            */
        }
    }    
    
    private TerminalDefinition findTerminal(LexicalTokenDefinition token){
        for(TerminalDefinition t : terminals)
            if(t.getTokenDefinition() == token)
                return t;
        return null;
    }
    
    private byte verifyLeftRecursion(NotTerminalDefinition source, NotTerminalDefinition current){
        for(Production p : current.getProductions()){
            if(p.tokensCount() > 0 && !p.getToken(0).isTerminal()){
                NotTerminalDefinition ntc = (NotTerminalDefinition) p.getToken(0);
                if(ntc == source){
                    if(source == current)
                        return 1;
                    else
                        return 2;
                }    
                else if(current != ntc){
                    byte ret = verifyLeftRecursion(source,ntc);
                    if(ret > 0)
                        return ret;
                }
            }
        }
        return 0;
    }
    
    private void removeLeftRecursionInGrammar(){          
        List<NotTerminalDefinition> ntTempList = grammar;
        List<Production> pdTempList;
        grammar = new ArrayList();
        for(int i=0; i<ntTempList.size(); i++){
            NotTerminalDefinition nti = ntTempList.get(i);    
            byte rec = verifyLeftRecursion(nti,nti);
            if(rec == 2)
            for(int j=0; j<=(i-1); j++){
                NotTerminalDefinition ntj = ntTempList.get(j);
                pdTempList = nti.getProductions();
                nti.setProdutions(new ArrayList());
                for(Production pj : pdTempList){
                    if(pj.tokensCount() > 0 && ntj == pj.getToken(0)){
                        for(Production pi : ntj.getProductions()){
                            Production newPd = new Production();
                            for(SyntacticTokenDefinition t : pi.getTokens())
                                newPd.addToken(t);
                            for(int k=1; k<pj.tokensCount(); k++)
                                newPd.addToken(pj.getToken(k));
                            nti.addProduction(newPd);
                        }
                    }
                    else
                        nti.addProduction(pj);
                }
            }
            grammar.add(nti);
            // elimina a recursão imediata à esquerda
            if(rec > 0){                
                NotTerminalDefinition newNt = null;
                pdTempList = nti.getProductions();
                nti.setProdutions(new ArrayList());
                for(Production pi : pdTempList){
                    if(pi.tokensCount() > 0 && nti == pi.getToken(0)){
                        Production newPd = new Production();
                        for(int k=1; k<pi.tokensCount(); k++)
                            newPd.addToken(pi.getToken(k));
                        if(newNt == null){
                            newNt = createNotTerminal(pi.getToken(0)+"'");
                            addNotTerminalToGrammar(newNt);
                        }    
                        newPd.addToken(newNt);
                        newNt.addProduction(newPd);                    
                    }
                    else{
                        if(newNt != null)
                            pi.addToken(newNt);
                        nti.addProduction(pi);
                    }    
                }
                if(newNt != null){
                    Production emptyPd = new Production(true);
                    emptyPd.addToken(empty);
                    newNt.addProduction(emptyPd);
                }            
            }
        }
    } 
    
    private void fatorarGrammar(){
        int numNewProd = 1;
        List<NotTerminalDefinition> newGrammar = null;
        // para cada não-terminal, encontrar um prefixo
        // comum a duas ou mais de suas alternativas
        boolean cont = true;
        while(cont){
            cont = false;
            newGrammar = new ArrayList();
            for(int k=0; k<grammar.size(); k++){
                NotTerminalDefinition nt = grammar.get(k);
                List<SyntacticTokenDefinition> prefix = new ArrayList();
                // busca um prefixo comum entre as produções
                int i = 0;
                while(prefix.size()==0 && i<nt.productionsCount()){
                    Production p1 = nt.getProduction(i);
                    int e = (i+1);
                    while(prefix.size()==0 && e<nt.productionsCount()){
                        Production p2 = nt.getProduction(e);
                        int pos = p1.compareTo(p2.getTokens());
                        if(pos > -1)
                            p1.prefix(pos,prefix);
                        e++;
                    }
                    i++;
                }
                // se existir um prefixo comum entre as produções, cria uma nova produções
                // senão preserva a produções corrente
                newGrammar.add(nt);
                if(prefix.size() > 0){
                    cont = true;
                    NotTerminalDefinition newNt = createNotTerminal("X");
                    newGrammar.add(newNt);
                    //for(int m=0; i<numNewProd; i++)
                    //    newNt.setName(newNt.getName()+"'");
                    newNt.setName(newNt.getName()+numNewProd);
                    numNewProd++;
                    List<Production> toRemove = new ArrayList();
                    for(Production p : nt.getProductions()){
                        int pos = p.compareTo(prefix);
                        if(pos > -1){                                                
                            if(((p.tokensCount()-1) == pos) && (!newNt.hasEmpty())){
                                Production emptyProd = p.subproduction(pos+1);
                                emptyProd.setIsEmpty(true);
                                emptyProd.addToken(empty);
                                newNt.addProduction(emptyProd);
                            }
                            else{
                                Production newProd = p.subproduction(pos+1);
                                newNt.addProduction(newProd);
                            }
                            toRemove.add(p);
                        }
                    }
                    for(Production p : toRemove)
                        nt.removeProduction(p);
                    Production newProd = new Production();
                    for(SyntacticTokenDefinition t : prefix)
                        newProd.addToken(t);
                    newProd.addToken(newNt);
                    nt.addProduction(newProd);
                }                
            }
            grammar = newGrammar;
        }
    }
        
    private List<TerminalDefinition> first(SyntacticTokenDefinition token){
        List<TerminalDefinition> result = new ArrayList<TerminalDefinition>();
        List<SyntacticTokenDefinition> solicitants = new ArrayList<SyntacticTokenDefinition>();        
        first(token,result,solicitants);
        return result;
    }
    
    private List<TerminalDefinition> first(Production prod){
        List<TerminalDefinition> result = new ArrayList<TerminalDefinition>();
        List<SyntacticTokenDefinition> solicitants = new ArrayList<SyntacticTokenDefinition>();        
        first(prod,result,solicitants);
        return result;
    }

    private void first(SyntacticTokenDefinition token, List<TerminalDefinition> result, List<SyntacticTokenDefinition> solicitants){
        // Se X for um terminal então First(X) = {X}
        if(token instanceof TerminalDefinition){
            if(!result.contains(token))
                result.add((TerminalDefinition)token);
        }    
        else {
            NotTerminalDefinition nt = (NotTerminalDefinition)token;
            for(Production prod : nt.getProductions())
                first(prod,result,solicitants);
        }  
    }    
    
    private void first(Production prod, List<TerminalDefinition> result, List<SyntacticTokenDefinition> solicitants){
        // Se existir X -> "vazio", então inclua "vazio" à First(X)
        if(prod.isEmpty()){
            if(!result.contains(empty))
                result.add(empty);                    
        }
        else{
            // Se X for um não-terminal e X -> Y1 Y2...Yk uma produções, então
            // inclua "a" em First(X) se para algum i, "a" estiver em First(Yi) e "vazio"
            // estiver em todos First(Y1), First(Y2), ... First(Yi-1),
            for(int i=0; i<prod.tokensCount(); i++){
                if(solicitants.contains(prod.getToken(i)))
                    continue;
                solicitants.add(prod.getToken(i));    
                List<TerminalDefinition> firsts = new ArrayList<TerminalDefinition>();
                first(prod.getToken(i),firsts,solicitants);                        
                solicitants.remove(prod.getToken(i));
                for(SyntacticTokenDefinition t : firsts)
                    if(!result.contains(t))
                        result.add((TerminalDefinition)t);
                if(!firsts.contains(empty))
                    break;
                if(i == (prod.tokensCount()-1))
                    if(!result.contains(empty))
                        result.add(empty);                             
            }
        }                    
    }
    
    private List<TerminalDefinition> follow(SyntacticTokenDefinition token){
        List<TerminalDefinition> conj = new ArrayList();
        List<SyntacticTokenDefinition> requests = new ArrayList();
        follow(token,conj,requests);
        return conj;        
    }
    
    private void follow(SyntacticTokenDefinition token, List<TerminalDefinition> conj, List<SyntacticTokenDefinition> requests){
        // Coloque $ em Follow(S), onde S à o símbolo inicial da gramática
        // e $ o marcador de fim da cadeia de entrada.
        if(grammar.size() > 0 && token == grammar.get(0))
            if(!conj.contains(end))
                conj.add(end);
        // varre todos os não terminais da gramática        
        for(NotTerminalDefinition nt : grammar){
            for(Production prod : nt.getProductions()){
                List<SyntacticTokenDefinition> nexts = new ArrayList<SyntacticTokenDefinition>();
                for(int i=(prod.tokensCount()-1); i>=0; i--){
                    SyntacticTokenDefinition t = prod.getToken(i);
                    if(t == token){
                        List<TerminalDefinition> list = null;
                        // Se existe uma produçõeo X -> a A b, então inclua em Follow(A)
                        // tudo o que estiver em First(b), exceto "vazio".        
                        // se "vazio", também adicione First do próximo        
                        boolean insertNext = true;
                        for(int e=(nexts.size()-1); e>=0; e--){
                            SyntacticTokenDefinition next = nexts.get(e);
                            list = first(next);
                            insertNext = false;
                            for(SyntacticTokenDefinition tk : list){
                                if(tk == empty)
                                    insertNext = true;
                                else
                                    conj.add((TerminalDefinition)tk);
                            }
                            if(!insertNext)
                                break;                                    
                        }                        
                        // Se existirem as produções X -> a A ou X -> a A b, em que First
                        // (b) contém "vazio", então inclua em Follow(A) tudo que estiver em Follow(X).
                        if(insertNext){
                            if((nt != token) && (!requests.contains(nt))){
                                requests.add(token);
                                follow(nt,conj,requests);                            
                            }
                        }    
                    }
                    nexts.add(t);
                }
            }
        }        
    }
    
    private void addProductionInAnalysisTable(Production production,SyntacticMatcher notTerminal,SyntacticMatcher terminal) throws SyntacticException{
        int nt_index = notTerminal.getIndex();
        int t_index = terminal.getIndex();        
//        if(analysisTable[nt_index][t_index] != null && analysisTable[nt_index][t_index] !=production)
//            error("Ambiguos grammar (Not Terminal: "+notTerminal+", Terminal: "+terminal+", Production 1: "+analysisTable[nt_index][t_index]+", Production 2: "+production+")");
        if(analysisTable[nt_index][t_index] == null)
            analysisTable[nt_index][t_index] = new ArrayList<Production>();
        if(!analysisTable[nt_index][t_index].contains(production)){
            boolean full = false;
            for(Production p : analysisTable[nt_index][t_index]){ 
                if(p.tokensCount() > 1){
                    full = true;
                    break;
                }    
                else if(!p.getToken(0).isTerminal()){
                    full = true;                    
                    break;
                }
            }
            if(production.tokensCount() == 1 && production.getToken(0).isTerminal())
                analysisTable[nt_index][t_index].add(0,production);
            else if(!full)
                analysisTable[nt_index][t_index].add(production);
            else
                error("Ambiguos grammar (Not Terminal: "+notTerminal+", Terminal: "+terminal+", Production 1: "+analysisTable[nt_index][t_index]+", Production 2: "+production+")");            
            
            int emptyIndex = -1;
            int lastTerminal = -1;
            for(int i=0; i<analysisTable[nt_index][t_index].size(); i++){
                Production p = analysisTable[nt_index][t_index].get(i);
                if(p.isEmpty())
                    emptyIndex = i;
                else if(p.tokensCount() == 1 && p.getToken(0).isTerminal())
                    lastTerminal = i;                    
            }
            if(emptyIndex > -1 && lastTerminal > -1){
                Production p = analysisTable[nt_index][t_index].get(emptyIndex);
                analysisTable[nt_index][t_index].remove(p);
                analysisTable[nt_index][t_index].add(lastTerminal,p);
            }                
        }
        
        // teste
        /*if(analysisTable[nt_index][t_index].size()>1){
            for(int i=0; i<analysisTable[nt_index][t_index].size(); i++)
                getSyntacticAnalyzer().getAnalyzer().getAppender().print(analysisTable[nt_index][t_index].get(i)+" ");
            getSyntacticAnalyzer().getAnalyzer().getAppender().println();    
            getSyntacticAnalyzer().getAnalyzer().getAppender().println();    
        } */  
    }

    private List<Production> getProductionInAnalysisTable(SyntacticMatcher notTerminal,SyntacticMatcher terminal){
        int nt_index = notTerminal.getIndex();
        if(terminal.isEnd())
            return analysisTable[nt_index][terminals.size()];        
        for(int i=0; i<terminals.size(); i++)
            if(terminal.match(terminals.get(i)))
                return analysisTable[nt_index][i];
        return null;
    }
    
    private void defineIndexs(){
        for(int i=0; i<terminals.size(); i++)
            terminals.get(i).setIndex(i);
        for(int i=0; i<grammar.size(); i++)
            grammar.get(i).setIndex(i);        
        end.setIndex(terminals.size());
        empty.setIndex(terminals.size()+1);
    }

    private void buildAnalizyTable() throws SyntacticException{
        defineIndexs();
        //analysisTable = new Object[grammar.size()][terminals.size()+1];
        analysisTable = new List[grammar.size()][terminals.size()+1];
        for(int row=0; row<grammar.size(); row++){
            NotTerminalDefinition nt = grammar.get(row);
            for(Production prod : nt.getProductions()){
                List<TerminalDefinition> firsts = first(prod);
                if(firsts.contains(empty)){
                    List<TerminalDefinition> follows = follow(nt);
                    for(TerminalDefinition t : follows){
                        addProductionInAnalysisTable(prod,nt,t);
                        if(follows.contains(end))
                            addProductionInAnalysisTable(prod,nt,end);
                    }
                }
                for(TerminalDefinition t : firsts)
                    if(t != empty)
                        addProductionInAnalysisTable(prod,nt,t);
            }
        }
        if(viewSyntacticTable)
            showSyntacticTable();
    }   
    
    private void showSyntacticTable(){
        String colsTitles[] = new String[terminals.size()+2];
        int colsLength[] = new int[terminals.size()+2];
        colsTitles[0] = "";
        colsLength[0] = 10;
        for(int c=1; c<colsLength.length; c++){
            if(c == (colsLength.length-1))
                colsTitles[c] = "$";
            else
                colsTitles[c] = terminals.get(c-1).toString();
            colsLength[c] = 0;
            for(int r=0; r<analysisTable.length; r++){
                if(analysisTable[r][c-1] != null && colsLength[c] < analysisTable[r][c-1].toString().length())
                    colsLength[c] = analysisTable[r][c-1] == null ? 0 : analysisTable[r][c-1].toString().length();
            } 
        }    
        String rowsTitles[] = new String[grammar.size()+1];         
        int rowsBreak[] = new int[grammar.size()+1];         
        rowsTitles[0] = "";
        rowsBreak[0] = 1; 
        for(int r=1; r<analysisTable.length; r++){
            rowsBreak[r] = 1;
            rowsTitles[r] = grammar.get(r-1).toString();
            for(int c=0; c<analysisTable[r-1].length; c++)
                if(analysisTable[r-1][c] != null && analysisTable[r-1][c] instanceof List)
                    if(rowsBreak[r] < ((List)analysisTable[r-1][c]).size())
                        rowsBreak[r] = ((List)analysisTable[r-1][c]).size();
        }        
        StringBuffer line = new StringBuffer("");
        for(int r=0; r<rowsTitles.length; r++){
            line.append('+');
            for(int i=0; i<colsLength.length; i++){  
                StringBuffer col = new StringBuffer("");
                while(col.length() < colsLength[i])
                    col.append('-');
                col.append('+');
                line.append(col);
            }    
            line.append("\n");
            if(r == 0){
                for(int c =0; c<colsTitles.length; c++)                    
                    line.append(String.format("|%-"+(colsLength[c]+1)+"s",colsTitles[c]));            
                line.append('|');
            }    
            else {
                line.append(String.format("|%-"+colsLength[0]+"s",rowsTitles[r]));
                for(int c =1; c<colsTitles.length; c++){
                    Object obj = analysisTable[r-1][c-1];
                    if(obj == null)
                        line.append(String.format("|%-"+(colsLength[c]+1)+"s",""));            
                    else if(obj instanceof List)
                        line.append(String.format("|%-"+(colsLength[c]+1)+"s",((List)obj).get(0)));                        
                    else
                        line.append(String.format("|%-"+(colsLength[c]+1)+"s",obj));
                }    
                line.append('|');
            }    
            line.append("\n");
        }
        line.append('+');
        for(int i=0; i<colsLength.length; i++){  
            StringBuffer col = new StringBuffer("");
            while(col.length() < colsLength[i])
                col.append('-');
            col.append('+');
            line.append(col);
        }    
        line.append("\n");
        syntacticAnalyzer.getAnalyzer().getAppender().println("\n---SYNTACTIC TABLE---\n");
        syntacticAnalyzer.getAnalyzer().getAppender().println(line.toString());
    }
    
    public String toString(){
        String ret = "";
        for(NotTerminalDefinition nt : grammar){
            ret += nt + " -> ";
            for(int i=0; i< nt.productionsCount(); i++){
                for(SyntacticTokenDefinition t : nt.getProduction(i).getTokens())
                    ret += t + " ";
                ret += (i < (nt.productionsCount()-1) ? "| " : "");                
            }
            ret += "\n";
        }
        return ret;
    }

    public List<NotTerminalDefinition> getGrammar() {
        return grammar;
    }

    public void setGrammar(List<NotTerminalDefinition> grammar) {
        this.grammar = grammar;
    }

    public void viewContextFreeGrammar(boolean view) {
        this.viewContextFreeGrammar = view;
    }        

    public void viewSyntacticTable(boolean view) {
        this.viewSyntacticTable = view;
    }
    
    public SyntacticAnalyzer getSyntacticAnalyzer(){
        return this.syntacticAnalyzer;
    }
    
    public void setSyntacticAnalyzer(SyntacticAnalyzer syntacticAnalyzer){
        this.syntacticAnalyzer = syntacticAnalyzer;
    }        
}
