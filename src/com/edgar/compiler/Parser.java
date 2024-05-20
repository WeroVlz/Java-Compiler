package com.edgar.compiler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Vector;

public class Parser {

    private static Vector<Token> tokens;
    private static int currentToken;
    private static DefaultMutableTreeNode node;
    private static int expressionCount = 1;
    private static GUI gui;

    private static final List<String> ACCESS_MODIFIER = List.of(
            "public", "private", "protected"
    );

    private static final List<String> DECLARATION_KEYWORDS = List.of(
            "int", "float", "boolean","char", "string", "void"
    );

    private static final List<String> KEYWORDS = List.of(
            "print", "while", "if", "return", "do", "for", "switch"
    );

    private static final List<String> FOR_DECLARATION_KEYWORDS = List.of(
            "int", "float", "double", "char"
    );

    private static final List<String> RULE_X_OPERATORS = List.of(
            "&", "&&"
    );

    private static final List<String> RULE_Y_OPERATORS = List.of(
            "|","||"
    );

    private static final List<String> RULE_R_OPERATORS = List.of(
            "!=", "==","<", ">", "<=", ">=", "="
    );

    private static final List<String> RULE_E_OPERATORS = List.of(
            "-", "+"
    );

    private static final List<String> RULE_A_OPERATORS = List.of(
            "*", "/"
    );

    public static DefaultMutableTreeNode run(Vector<Token> tokenVector, GUI userInterface){
        tokens = tokenVector;
        gui = userInterface;
        currentToken = 0;
        expressionCount = 1;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parser Expression Tree");
        ruleProgram(root);
        return root;
    }

    public static void ruleProgram(DefaultMutableTreeNode parent){
        while(currentToken < tokens.size()){
            if(searchTokenInList(currentToken, ACCESS_MODIFIER))
                ruleMethod(parent);
            else if (searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
                ruleVariable(parent);
                if(checkTokenWord(currentToken, ";"))
                    currentToken++;
                else
                    errorHandler(3);
            }
        }
    }

    public static void ruleMethod(DefaultMutableTreeNode parent){
        currentToken++;

        if(checkTokenWord(currentToken,"static"))
            currentToken++;
        if(checkTokenWord(currentToken,"final"))
            currentToken++;

        if(searchTokenInList(currentToken, DECLARATION_KEYWORDS))
            currentToken++;
        else
            errorHandler(1);

        if(checkTokenType(currentToken, "ID"))
            currentToken++;
        else
            errorHandler(6);

        if(checkTokenWord(currentToken, "(")){
            currentToken++;
            if(searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
                currentToken--;
                ruleParameter();
            }
        }else
            errorHandler(8);

        if (checkTokenWord(currentToken, ")"))
            currentToken++;
        else
            errorHandler(7);

        ruleBody(parent);

    }

    public static void ruleParameter(){
        do{
            currentToken++;
            if(searchTokenInList(currentToken, DECLARATION_KEYWORDS))
                currentToken++;
            else
                errorHandler(1);

            if(checkTokenType(currentToken,"ID"))
                currentToken++;
            else
                errorHandler(6);
        }while(checkTokenWord(currentToken,","));
    }

    public static void body(DefaultMutableTreeNode parent,DefaultMutableTreeNode node){
        if(checkTokenType(currentToken, "ID")){
            parent.add(node);
            ruleAssignment(node);
            if(checkTokenWord(currentToken, ";"))
                currentToken++;
            else
                errorHandler(3);
        }
        else if(searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
            ruleVariable(parent);
            if(checkTokenWord(currentToken, ";"))
                currentToken++;
            else
                errorHandler(3);
        }
        else if(searchTokenInList(currentToken, KEYWORDS)){
            switch (tokens.get(currentToken).getWord()){
                case "print":
                    parent.add(node);
                    rulePrint(node);
                    if(checkTokenWord(currentToken, ";"))
                        currentToken++;
                    else
                        errorHandler(3);
                    break;
                case "while":
                    parent.add(node); ruleWhile(node); break;
                case "if":
                    parent.add(node); ruleIf(node); break;
                case "return":
                    ruleReturn(node);
                    if(checkTokenWord(currentToken, ";"))
                        currentToken++;
                    else
                        errorHandler(3);
                    break;
                case "do":
                    ruleDoWhile(parent);
                    if(checkTokenWord(currentToken, ";"))
                        currentToken++;
                    else
                        errorHandler(3);
                    break;
                case "for":
                    ruleFor(parent); break;
                case "switch":
                    ruleSwitch(parent); break;
            }
        }
    }

    public  static void ruleBody(DefaultMutableTreeNode parent){

        if(checkTokenWord(currentToken, "{"))
            currentToken++;
        else
            errorHandler(10);

        while(currentToken < tokens.size() && !checkTokenWord(currentToken,"}")){
            node = new DefaultMutableTreeNode("Expression " + expressionCount);
            body(parent, node);
        }

        if(checkTokenWord(currentToken, "}"))
            currentToken++;
        else
            errorHandler(2);
    }

    public static void ruleAssignment(DefaultMutableTreeNode parent){
        if(checkTokenType(currentToken,"ID"))
            currentToken++;
        else errorHandler(6);

        if(checkTokenWord(currentToken,"="))
            currentToken++;
        else errorHandler(5);

        ruleExpression(parent);
        expressionCount++;
    }

    public static void ruleVariable(DefaultMutableTreeNode parent){
        currentToken++;
        if(checkTokenType(currentToken,"ID"))
            currentToken++;
        else
            errorHandler(6);

        if(checkTokenWord(currentToken,"=")){
            currentToken++;
            ruleExpression(parent);
        }
    }

    public static void rulePrint(DefaultMutableTreeNode parent){
        currentToken++;
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else errorHandler(8);

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")")){
            currentToken++;
        }else errorHandler(7);

    }

    public static void ruleWhile(DefaultMutableTreeNode parent){
        currentToken++;

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else errorHandler(8);

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")"))
            currentToken++;
        else errorHandler(7);

        ruleBody((DefaultMutableTreeNode) parent.getRoot());
    }

    public static void ruleIf(DefaultMutableTreeNode parent){
        currentToken++;
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else errorHandler(8);

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")"))
            currentToken++;
        else errorHandler(7);

        ruleBody(parent);

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("else")){
            currentToken++;
            ruleBody(parent);
        }
    }

    public static void ruleReturn(DefaultMutableTreeNode parent){
        int returnLine = tokens.get(currentToken).getRow();
        currentToken++;
        if ((currentToken >= tokens.size()) || tokens.get(currentToken).getWord().equals(";")
                || (returnLine != tokens.get(currentToken).getRow())) {
            return;
        }
        ruleExpression(parent);
        expressionCount++;
    }

    public static void ruleDoWhile(DefaultMutableTreeNode parent){
        currentToken++;
        ruleBody(parent);

        if(checkTokenWord(currentToken,"while"))
            currentToken++;
        else
            errorHandler(11);

        if(checkTokenWord(currentToken, "("))
            currentToken++;
        else
            errorHandler(8);

        ruleExpression(parent);

        if(checkTokenWord(currentToken, ")"))
            currentToken++;
        else
            errorHandler(7);

    }

    public static void ruleFor(DefaultMutableTreeNode parent){
        currentToken++;

        if(checkTokenWord(currentToken,"("))
            currentToken++;
        else
            errorHandler(8);

        if(searchTokenInList(currentToken,FOR_DECLARATION_KEYWORDS))
            currentToken++;

        ruleAssignment(parent);

        if(checkTokenWord(currentToken,";"))
            currentToken++;
        else
            errorHandler(3);

        ruleExpression(parent);

        if(checkTokenWord(currentToken,";"))
            currentToken++;
        else
            errorHandler(3);

        if(checkTokenType(currentToken,"ID")){
            currentToken++;
            if(checkTokenWord(currentToken,"=")){
                currentToken--;
                ruleAssignment(parent);
            }else{
                if(checkTokenWord(currentToken,"++") || checkTokenWord(currentToken,"--"))
                    currentToken++;
                else
                    errorHandler(12);
            }
        }

        if(checkTokenWord(currentToken,")"))
            currentToken++;
        else errorHandler(7);

        ruleBody(parent);

    }

    public static void ruleSwitch(DefaultMutableTreeNode parent){
        currentToken++;

        if(checkTokenWord(currentToken,"("))
            currentToken++;
        else errorHandler(8);

        ruleExpression(parent);

        if(checkTokenWord(currentToken,")"))
            currentToken++;
        else errorHandler(7);

        if(checkTokenWord(currentToken,"{"))
            currentToken++;
        else errorHandler(10);

        while(currentToken < tokens.size() && !checkTokenWord(currentToken, "}") && !checkTokenWord(currentToken, "default")){
            if(checkTokenWord(currentToken, "case"))
                currentToken++;
            else errorHandler(13);

            ruleExpression(parent);

            if(checkTokenWord(currentToken, ":"))
                currentToken++;
            else errorHandler(14);

            while(currentToken < tokens.size() && !checkTokenWord(currentToken,"break" )
                    && !checkTokenWord(currentToken,"case")
                    && !checkTokenWord(currentToken,"default")
                    && !checkTokenWord(currentToken, "}")){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("test");
                body(parent, node);
            }

            if(checkTokenWord(currentToken, "break"))
                currentToken++;
            else
                errorHandler(15);

            if(checkTokenWord(currentToken, ";"))
                currentToken++;
            else
                errorHandler(3);

        }

        if(checkTokenWord(currentToken, "default")){
            currentToken++;

            if(checkTokenWord(currentToken, ":"))
                currentToken++;
            else errorHandler(14);

            while(currentToken < tokens.size() && !checkTokenWord(currentToken, "break") && !checkTokenWord(currentToken, "}")){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("test");
                body(parent, node);
            }

            if(checkTokenWord(currentToken, "break"))
                currentToken++;
            else
                errorHandler(15);

            if(checkTokenWord(currentToken, ";"))
                currentToken++;
            else
                errorHandler(3);
        }

        if(checkTokenWord(currentToken, "}"))
            currentToken++;
        else errorHandler(2);

    }

    public static void ruleExpression(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("X");
        parent.add(node);
        ruleX(node);

        while(currentToken < tokens.size() && RULE_X_OPERATORS.contains(tokens.get(currentToken).getWord())){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("X"); parent.add(node);

            ruleX(node);
        }
    }

    public static void ruleX(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("Y");
        parent.add(node);
        ruleY(node);

        while(currentToken < tokens.size() && RULE_Y_OPERATORS.contains(tokens.get(currentToken).getWord())){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("Y"); parent.add(node);

            ruleY(node);
        }
    }

    public static void ruleY(DefaultMutableTreeNode parent){

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("!")){
            node = new DefaultMutableTreeNode("!");
            parent.add(node); currentToken++;
        }
        node = new DefaultMutableTreeNode("R");
        parent.add(node);
        ruleR(node);
    }

    public static void ruleR(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("E");
        parent.add(node);
        ruleE(node);

        while(currentToken < tokens.size() && RULE_R_OPERATORS.contains(tokens.get(currentToken).getWord())){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("E"); parent.add(node);
            ruleE(node);
        }
    }

    public static void ruleE(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("A");
        parent.add(node);
        ruleA(node);

        while(currentToken < tokens.size() && RULE_E_OPERATORS.contains(tokens.get(currentToken).getWord())){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("A"); parent.add(node);
            ruleA(node);
        }
    }

    public static void ruleA(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("B");
        parent.add(node);
        ruleB(node);

        while(currentToken < tokens.size() && RULE_A_OPERATORS.contains(tokens.get(currentToken).getWord())){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("B"); parent.add(node);
            ruleB(node);
        }
    }

    public static void ruleB(DefaultMutableTreeNode parent){
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("-")){
            node = new DefaultMutableTreeNode("-");
            parent.add(node); currentToken++;
        }

        node = new DefaultMutableTreeNode("C");
        parent.add(node);
        ruleC(node);
    }

    public static void ruleC(DefaultMutableTreeNode parent){
        if(currentToken < tokens.size()){
            Token token = tokens.get(currentToken);
            DefaultMutableTreeNode node;
            if(token.getToken().equals("INT")){
                node = new DefaultMutableTreeNode("Integer(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("ID")){
                node = new DefaultMutableTreeNode("Identifier(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("OCTAL")){
                node = new DefaultMutableTreeNode("Octal(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("HEX")){
                node = new DefaultMutableTreeNode("Hexadecimal(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("BINARY")){
                node = new DefaultMutableTreeNode("Binary(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("STRING")){
                node = new DefaultMutableTreeNode("String(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("CHAR")){
                node = new DefaultMutableTreeNode("Char(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("FLOAT")){
                node = new DefaultMutableTreeNode("Float(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("DOUBLE")){
                node = new DefaultMutableTreeNode("Double(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getWord().equals("true")){
                node = new DefaultMutableTreeNode("Boolean(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getWord().equals("false")){
                node = new DefaultMutableTreeNode("Boolean(" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getWord().equals("(")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node); currentToken++;
                ruleExpression(parent);
                if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")){
                    node = new DefaultMutableTreeNode(")");
                    parent.add(node); currentToken++;
                }else{
                    errorHandler(7);
                }
            } else {
                errorHandler(9);
            }
        } else {
            errorHandler(9);
        }
    }

    private static boolean checkTokenWord(int token, String word){
        if(currentToken >= tokens.size()){
            return false;
        }
        return tokens.get(token).getWord().equals(word);
    }

    public static boolean checkTokenType(int token, String tokenType){
        if(currentToken >= tokens.size()){
            return false;
        }
        return tokens.get(token).getToken().equals(tokenType);
    }

    private static boolean searchTokenInList(int token, List<String> tokenList){
        if(currentToken >= tokens.size()){
            return false;
        }
        return tokenList.contains(tokens.get(token).getWord());
    }

    public static void errorHandler(int err){
        int line = tokens.get(currentToken-1).getRow();

        switch (err){
            case 1: gui.writeConsoleLine("Line " + line + ": expected type"); break;
            case 2: gui.writeConsoleLine("Line " + line + ": expected '}'"); break;
            case 3: gui.writeConsoleLine("Line " + line + ": expected ';'"); break;
            case 4: gui.writeConsoleLine("Line " + line + ": expected identifier or keyword"); break;
            case 5: gui.writeConsoleLine("Line " + line + ": expected '='"); break;
            case 6: gui.writeConsoleLine("Line " + line + ": expected identifier"); break;
            case 7: gui.writeConsoleLine("Line " + line + ": expected ')'"); break;
            case 8: gui.writeConsoleLine("Line " + line + ": expected '('"); break;
            case 9: gui.writeConsoleLine("Line " + line + ": expected value, identifier or '('"); break;
            case 10: gui.writeConsoleLine("Line " + line + ": expected '{'"); break;
            case 11: gui.writeConsoleLine("Line " + line + ": expected 'while'"); break;
            case 12: gui.writeConsoleLine("Line " + line + ": expected assignment or operator"); break;
            case 13: gui.writeConsoleLine("Line " + line + ": expected case label"); break;
            case 14: gui.writeConsoleLine("Line " + line + ": expected ':'"); break;
            case 15: gui.writeConsoleLine("Line " + line + ": expected break label"); break;
        }
    }


}
