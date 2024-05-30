package com.edgar.compiler;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {

    private static GUI gui;
    private static final Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<>();
    private static final Stack<String> variableStack = new Stack<>();

    private static final int OP_AND_OR = 0;
    private static final int OP_NOT = 1;
    private static final int OP_EQ_NOTEQ = 2;
    private static final int OP_MORE_LESS_EQ = 3;
    private static final int OP_PLUS = 4;
    private static final int OP_MIN_DIV_MUL = 5;
    private static final int OP_NEG = 6;
    private static final int OP_ASSIGN = 7;

    private static final Hashtable<String, Integer> VARIABLE_TYPES = new Hashtable<>(){{
        put("int",0);
        put("float",1);
        put("double",2);
        put("boolean", 3);
        put("char", 4);
        put("String",5);
        put("void",6);
        put("error",7);
    }};

    private static final String[][][] OPERATORS_CUBE = {
            {
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "boolean", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }, {
                    {"error", "error", "error", "boolean", "error", "error", "error","error"},
            }, {
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "boolean", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "boolean", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "boolean", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }, {
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"boolean", "boolean", "boolean", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }, {
                    {"int", "float", "double", "error", "error", "String", "error","error"},
                    {"float", "float", "double", "error", "error", "String", "error","error"},
                    {"double", "double", "double", "error", "error", "String", "error","error"},
                    {"error", "error", "error", "error", "error", "String", "error","error"},
                    {"error", "error", "error", "error", "error", "String", "error","error"},
                    {"String", "String", "String", "String", "String", "String", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }, {
                    {"int", "float", "double", "error", "error", "error", "error","error"},
                    {"float", "float", "double", "error", "error", "error", "error","error"},
                    {"double", "double", "double", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }, {
                    {"int", "float", "double", "error", "error", "error", "error","error"},
            }, {
                    {"ok", "error", "error", "error", "error", "error", "error","error"},
                    {"ok", "ok", "error", "error", "error", "error", "error","error"},
                    {"ok", "ok", "ok", "error", "error", "error", "error","error"},
                    {"error", "error", "error", "ok", "error", "error", "error","error"},
                    {"error", "error", "error", "error", "ok", "error", "error","error"},
                    {"error", "error", "error", "error", "error", "ok", "error","error"},
                    {"error", "error", "error", "error", "error", "error", "ok","error"},
                    {"error", "error", "error", "error", "error", "error", "error","error"}
            }
    };

    public static void run(GUI g){
        gui = g;
        clearTable();
    }


    public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable(){ return symbolTable; }

    public static void clearTable(){
        symbolTable.clear();
        while(!variableStack.isEmpty())
            variableStack.pop();

    }

    public static void clearStack(){
        while(!variableStack.isEmpty())
            variableStack.pop();
    }

    public static void pushStack(String type){
        variableStack.add(type);
    }

    public static String popStack(){
        if(!variableStack.isEmpty())
            return variableStack.pop();
        else
            return "";
    }

    public static void addVariable(String id, SymbolTableItem content, int line){
        boolean isSameScope = false;
        Vector<SymbolTableItem> v = new Vector<>();
        if(!symbolTable.containsKey(id)){

            v.add(new SymbolTableItem(content.getType(),content.getScope(),content.getValue()));
            symbolTable.put(id,v);
        }else{
            Vector<SymbolTableItem> variableDeclarations = symbolTable.get(id);
            for (SymbolTableItem declaration : variableDeclarations){
                if (declaration.getScope().equals(content.getScope())) {
                    isSameScope = true;
                    break;
                }
            }

            if (!isSameScope)
                symbolTable.get(id).add(new SymbolTableItem(content.getType(),content.getScope(),content.getValue()));
            else
                errorHandler(id, line, 1);
        }
    }

    public static String getTypeById(String id, int line){
        Vector<SymbolTableItem> idDeclarations = symbolTable.get(id);
        if (idDeclarations == null){
            errorHandler(id, line, 3);
            return "error";
        }
        return idDeclarations.firstElement().getType();
    }

    public static String calculateOperatorCube(String operator, String variable1){
      if (operator.equals("-")){
          return OPERATORS_CUBE[OP_NEG][0][VARIABLE_TYPES.get(variable1)];
      }else if (operator.equals("!")){
          return OPERATORS_CUBE[OP_NOT][0][VARIABLE_TYPES.get(variable1)];
      }else{
          return OPERATORS_CUBE[2][0][7];
      }
    }

    public static String calculateOperatorCube(String operator, String variable1, String variable2){
        int operatorCube = switch (operator) {
            case "&", "&&", "|", "||" -> OP_AND_OR;
            case "==", "!=" -> OP_EQ_NOTEQ;
            case "<", ">", "<=", ">=" -> OP_MORE_LESS_EQ;
            case "+" -> OP_PLUS;
            case "-", "*", "/" -> OP_MIN_DIV_MUL;
            case "=" -> OP_ASSIGN;
            default -> 0;
        };
        if (!variable2.isEmpty())
            return OPERATORS_CUBE[operatorCube][VARIABLE_TYPES.get(variable1)][VARIABLE_TYPES.get(variable2)];
        else return "error";
    }

    public static void errorHandler(String id, int line, int err){

        switch (err) {
            case 1 -> gui.writeConsoleLine("Line " + line + ": variable '" + id + "' is already defined");
            case 2 -> gui.writeConsoleLine("Line " + line + ": variable '" + id + "' was never declared");
            case 3 -> gui.writeConsoleLine("Line " + line + ": variable '" + id + "' was not found");
            case 4 -> gui.writeConsoleLine("Line " + line + ": incompatible types: type mismatch");
            case 5 -> gui.writeConsoleLine("Line " + line + ": incompatible types: expected boolean");
            case 6 -> gui.writeConsoleLine("Line " + line + ": incompatible types: expected integer/float/double");
            case 7 -> gui.writeConsoleLine("Line " + line + ": incompatible types: expected integer");
            case 8 -> gui.writeConsoleLine("Line " + line + ": incompatible return value");
            case 9 -> gui.writeConsoleLine("Line " + line + ": function with given parameters not found");
        }
    }
}
