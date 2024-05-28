package com.edgar.compiler;

import java.util.Hashtable;
import java.util.Vector;

public class SemanticAnalyzer {

    private static GUI gui;
    private static final Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<>();

    private static final int INT = 0;
    private static final int FLOAT = 1;
    private static final int DOUBLE = 2;
    private static final int BOOLEAN = 3;
    private static final int CHAR = 4;
    private static final int STRING = 5;
    private static final int VOID = 6;
    private static final int ERR = 7;
    private static final int OP_AND_OR = 0;
    private static final int OP_NOT = 1;
    private static final int OP_EQ_NOTEQ = 2;
    private static final int OP_MORE_LESS_EQ = 3;
    private static final int OP_PLUS = 4;
    private static final int OP_MIN_DIV_MULT = 5;
    private static final int OP_NEG = 6;

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
                    {"int", "float", "double", "error", "error", "string", "error","error"},
                    {"float", "float", "double", "error", "error", "string", "error","error"},
                    {"double", "double", "double", "error", "error", "string", "error","error"},
                    {"error", "error", "error", "error", "error", "string", "error","error"},
                    {"error", "error", "error", "error", "error", "string", "error","error"},
                    {"string", "string", "string", "string", "string", "string", "error","error"},
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
            }
    };

    public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable(){ return symbolTable; };

    public static void clearTable(){ symbolTable.clear(); }

    public static void checkVariable(String id, SymbolTableItem content, int line, GUI gui){
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
                errorHandler(gui, id, line, 1);
        }
    }

    public static void errorHandler(GUI gui, String id, int line, int err){

        switch (err) {
            case 1 -> gui.writeConsoleLine("Line " + line + ": variable '" + id + "' is already defined");
            case 2 -> gui.writeConsoleLine("Line " + line + ": variable '" + id + "' is not found");

        }
    }
}
