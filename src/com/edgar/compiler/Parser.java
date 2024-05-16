package com.edgar.compiler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Vector;

public class Parser {

    private static Vector<Token> tokens;
    private static int currentToken;
    private static DefaultMutableTreeNode node;
    private static int expressionCount = 1;

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

    private static final List<String> DECLARATION_KEYWORDS = List.of(
            "int", "float", "boolean","char", "string", "void"
    );

    private static final List<String> KEYWORDS = List.of(
            "print", "while", "if", "return"
    );

    public static DefaultMutableTreeNode run(Vector<Token> tokenVector){
        tokens = tokenVector;
        currentToken = 0;
        expressionCount = 1;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parser Expression Tree");
        ruleProgram(root);
        return root;
    }

    public static void ruleProgram(DefaultMutableTreeNode parent){
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("{")){
            currentToken++;
            ruleBody(parent);
            if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("}"))
                currentToken++;
            else
                System.out.println("Error: '}' expected.");
        }else{
            System.out.println("Error: '{' expected.");
        }
    }

    public  static void ruleBody(DefaultMutableTreeNode parent){
        while(currentToken < tokens.size() && !tokens.get(currentToken).getWord().equals("}")){
            node = new DefaultMutableTreeNode("Expression " + expressionCount);
            if(tokens.get(currentToken).getToken().equals("ID")){
                parent.add(node);
                ruleAssignment(node);
                if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";"))
                    currentToken++;
                else
                    System.out.println("Error: ';' expected.");
            }
            else if(DECLARATION_KEYWORDS.contains(tokens.get(currentToken).getWord())){
                ruleVariable();
                if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";"))
                    currentToken++;
                else
                    System.out.println("Error: ';' expected.");
            }
            else if(KEYWORDS.contains(tokens.get(currentToken).getWord())){
                switch (tokens.get(currentToken).getWord()){
                    case "print":
                        parent.add(node);
                        rulePrint(node);
                        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";"))
                            currentToken++;
                        else
                            System.out.println("Error: ';' expected.");
                        break;
                    case "while":
                        parent.add(node);
                        ruleWhile(node);
                        break;
                    case "if":
                        parent.add(node);
                        ruleIf(node);
                        break;
                    case "return":
                        ruleReturn();
                        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";"))
                            currentToken++;
                        else
                            System.out.println("Error: ';' expected.");
                        break;
                }
            }
        }
    }

    public static void ruleAssignment(DefaultMutableTreeNode parent){
        currentToken++;

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("="))
            currentToken++;
        else printErrorMessage("=");

        ruleExpression(parent);
        expressionCount++;
    }

    public static void ruleVariable(){
        currentToken++;
        if(currentToken < tokens.size() && tokens.get(currentToken).getToken().equals("ID"))
            currentToken++;
        else printErrorMessage("Identifier");
    }

    public static void rulePrint(DefaultMutableTreeNode parent){
        currentToken++;
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else printErrorMessage("(");

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")")){
            currentToken++;
        }else printErrorMessage(")");

    }

    public static void ruleWhile(DefaultMutableTreeNode parent){
        currentToken++;

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else printErrorMessage("(");

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")"))
            currentToken++;
        else printErrorMessage(")");

        ruleProgram((DefaultMutableTreeNode) parent.getRoot());
    }

    public static void ruleIf(DefaultMutableTreeNode parent){
        currentToken++;
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("("))
            currentToken++;
        else printErrorMessage("(");

        ruleExpression(parent);
        expressionCount++;

        if(currentToken< tokens.size() && tokens.get(currentToken).getWord().equals(")"))
            currentToken++;
        else printErrorMessage(")");

        ruleProgram(parent);

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("else")){
            currentToken++;
            ruleProgram(parent);
        }
    }

    public static void ruleReturn(){
        currentToken++;
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
                    printErrorMessage(")");
                }
            } else {
                printErrorMessage("Value");
            }
        } else {
            printErrorMessage("Value");
        }
    }

    private static void printErrorMessage(String errorMessage){
        System.out.println("ERROR: '" + errorMessage + "' expected.");
    }
}
