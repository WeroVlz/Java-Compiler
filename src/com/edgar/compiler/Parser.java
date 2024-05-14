package com.edgar.compiler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Parser {

    private static Vector<Token> tokens;
    private static int currentToken;
    private static DefaultMutableTreeNode node;
    private static boolean error;

    private static final List<String> RULE_R_OPERATORS = List.of(
            "!=", "==","<", ">", "<=", ">="
    );

    private static final List<String> RULE_E_OPERATORS = List.of(
            "-", "+"
    );

    private static final List<String> RULE_A_OPERATORS = List.of(
            "*", "/"
    );

    public static DefaultMutableTreeNode run(Vector<Token> tokenVector){
        tokens = tokenVector;
        currentToken = 0;
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
            else{
                System.out.println("Error: '}' expected.");
            }
        }else{
            System.out.println("Error: '{' expected.");
        }
    }

    public static void ruleBody(DefaultMutableTreeNode parent){
        int expressionsCount = 1;
        while(currentToken < tokens.size() && !tokens.get(currentToken).getWord().equals("}")){
            node = new DefaultMutableTreeNode("Expression " + expressionsCount);
            parent.add(node);
            ruleExpression(node);
            expressionsCount++;

            if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(";")){
                currentToken++;
            }
            else{
                System.out.println("Error: ';' expected.");
            }
        }
    }

    public static void ruleExpression(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("X");
        parent.add(node);
        ruleX(node);

        while(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("|")){
            node = new DefaultMutableTreeNode("|");
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("X"); parent.add(node);

            ruleX(node);
        }
    }

    public static void ruleX(DefaultMutableTreeNode parent){
        node = new DefaultMutableTreeNode("Y");
        parent.add(node);
        ruleY(node);

        while(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("&")){
            node = new DefaultMutableTreeNode("&");
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
            } else if(token.getWord().equals("(")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node); currentToken++;
                ruleExpression(parent);
                if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")){
                    node = new DefaultMutableTreeNode(")");
                    parent.add(node); currentToken++;
                }else{
                    System.out.println("ERROR: ')' expected.");
                }
            } else {
                System.out.println("ERROR: 'Value' expected.");
            }
        } else {
            System.out.println("ERROR: 'Value' expected.");
        }
    }
}
