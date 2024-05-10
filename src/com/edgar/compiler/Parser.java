package com.edgar.compiler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Parser {

    private static DefaultMutableTreeNode root;
    private static Vector<Token> tokens;
    private static int currentToken;
    static ArrayList<String> expression = new ArrayList<>();

    private static final List<String> RULE_R_OPERATORS = List.of(
            "!=", "==","<", ">"
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
        root = new DefaultMutableTreeNode("Expression");

        RULE_EXPRESSION();
        return root;
    }
//    public static void run(Vector<Token> tokenVector){
//        tokens = tokenVector;
//        currentToken = 0;
//        expression.clear();
//        RULE_EXPRESSION();
//        System.out.println(expression);
//    }

    public static void RULE_EXPRESSION(){
        RULE_X();

        while(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("|")){
            currentToken++;
            expression.add("|");
            RULE_X();
        }
    }

    public static void RULE_X(){
        RULE_Y();

        while(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("&")){
            currentToken++;
            expression.add("&");
            RULE_Y();
        }
    }

    public static void RULE_Y(){

        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("!")){
            currentToken++;
            expression.add("!");
        }

        RULE_R();
    }

    public static void RULE_R(){
        RULE_E();

        while(currentToken < tokens.size() && RULE_R_OPERATORS.contains(tokens.get(currentToken).getWord())){
            expression.add(tokens.get(currentToken).getWord());
            currentToken++;
            RULE_E();
        }
    }

    public static void RULE_E(){
        RULE_A();

        while(currentToken < tokens.size() && RULE_E_OPERATORS.contains(tokens.get(currentToken).getWord())){
            expression.add(tokens.get(currentToken).getWord());
            currentToken++;
            RULE_A();
        }
    }

    public static void RULE_A(){
        RULE_B();

        while(currentToken < tokens.size() && RULE_A_OPERATORS.contains(tokens.get(currentToken).getWord())){
            expression.add(tokens.get(currentToken).getWord());
            currentToken++;
            RULE_B();
        }
    }

    public static void RULE_B(){
        if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals("-")){
            currentToken++;
            expression.add("-");
        }

        RULE_C();
    }

    public static void RULE_C(){
        if(currentToken < tokens.size()){
            Token token = tokens.get(currentToken);
            if(token.getToken().equals("INT")){
                expression.add(token.getWord());
                currentToken++;
            } else if(token.getToken().equals("ID")){
                expression.add(token.getWord());
                currentToken++;
            } else if(token.getWord().equals("(")){
                expression.add("(");
                currentToken++;
                RULE_EXPRESSION();
                if(currentToken < tokens.size() && tokens.get(currentToken).getWord().equals(")")){
                    expression.add(")");
                    currentToken++;
                }else{
                    expression.add("ERR");
                    System.out.println("ERROR: ')' expected.");
                }
            } else {
                expression.add("ERR");
                System.out.println("ERROR: 'Value' expected.");
            }
        } else {
            expression.add("ERR");
            System.out.println("ERROR: 'Value' expected.");
        }
    }
}
