package com.edgar.compiler;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class Parser {

    private static GUI gui;
    private static Vector<Token> tokens;
    private static DefaultMutableTreeNode node;
    private static int currentToken;
    private static boolean isSwitchBody;
    private static StringBuilder parameterTyping;
    private static final Vector<ArrayList<String>> methodParameters = new Vector<>();
    private static boolean correctAssignment;


    private static final List<String> ACCESS_MODIFIER = List.of(
            "public", "private", "protected"
    );
    private static final List<String> DECLARATION_KEYWORDS = List.of(
            "int", "float", "boolean","char", "String", "void", "double"
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
            "!=", "==","<", ">", "<=", ">="
    );

    private static final List<String> RULE_E_OPERATORS = List.of(
            "-", "+"
    );

    private static final List<String> RULE_A_OPERATORS = List.of(
            "*", "/"
    );

    private static final Map<String, List<String>> FIRST_SET = new HashMap<>() {{
        put("PROGRAM", new ArrayList<>(List.of("public","private","protected","int","char","boolean","float","double","void","String")));
        put("BODY", new ArrayList<>(List.of("{")));
        put("METHOD", new ArrayList<>(List.of("public","private","protected")));
        put("CALLMETHOD", new ArrayList<>(List.of("ID")));
        put("PARAMETER1", new ArrayList<>(List.of("int","char","boolean","float","double","void","String")));
        put("PARAMETER2", new ArrayList<>(List.of("!","-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("VARIABLE", new ArrayList<>(List.of("int","char","boolean","float","double","void","String")));
        put("ASSIGNMENT", new ArrayList<>(List.of("ID")));
        put("ARRAY", new ArrayList<>(List.of("ID")));
        put("PRINT", new ArrayList<>(List.of("print")));
        put("WHILE", new ArrayList<>(List.of("while")));
        put("DO", new ArrayList<>(List.of("do")));
        put("IF", new ArrayList<>(List.of("if")));
        put("FOR", new ArrayList<>(List.of("for")));
        put("SWITCH", new ArrayList<>(List.of("switch")));
        put("CASE", new ArrayList<>(List.of("case")));
        put("DEFAULT", new ArrayList<>(List.of("default")));
        put("RETURN", new ArrayList<>(List.of("return")));
        put("EXPRESSION", new ArrayList<>(List.of("!","-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("X", new ArrayList<>(List.of("!","-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("Y", new ArrayList<>(List.of("!","-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("R", new ArrayList<>(List.of("-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("E", new ArrayList<>(List.of("-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("A", new ArrayList<>(List.of("-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("B", new ArrayList<>(List.of("-","(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
        put("C", new ArrayList<>(List.of("(","INT","OCTAL","HEX","BINARY","STRING","CHAR","FLOAT","DOUBLE","ID","BOOLEAN")));
    }};

    private static final Map<String, List<String>> FOLLOW_SET = new HashMap<>() {{
        put("PROGRAM", new ArrayList<>(List.of()));
        put("BODY", new ArrayList<>(List.of("public","private","protected","int","char","boolean","float","double","void","String","}",
                "ID","print","while","if","return","do","for","switch","break","else")));
        put("METHOD", new ArrayList<>(List.of("public","private","protected","int","char","boolean","float","double","void","String")));
        put("CALLMETHOD", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=","+","-","*","/")));
        put("PARAMETER1", new ArrayList<>(List.of(")")));
        put("PARAMETER2", new ArrayList<>(List.of(")","}")));
        put("VARIABLE", new ArrayList<>(List.of(";")));
        put("ASSIGNMENT", new ArrayList<>(List.of(";",")")));
        put("ARRAY", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=","+","-","*","/")));
        put("PRINT", new ArrayList<>(List.of(";")));
        put("WHILE", new ArrayList<>(List.of("}","ID","int","char","boolean","float","double","void","String","print","while","if","return","do","for","switch","break")));
        put("DO", new ArrayList<>(List.of("}","ID","int","char","boolean","float","double","void","String","print","while","if","return","do","for","switch","break")));
        put("IF", new ArrayList<>(List.of("}","ID","int","char","boolean","float","double","void","String","print","while","if","return","do","for","switch","break")));
        put("FOR", new ArrayList<>(List.of("}","ID","int","char","boolean","float","double","void","String","print","while","if","return","do","for","switch","break")));
        put("SWITCH", new ArrayList<>(List.of("}","ID","int","char","boolean","float","double","void","String","print","while","if","return","do","for","switch","break")));
        put("CASE", new ArrayList<>(List.of("default")));
        put("DEFAULT", new ArrayList<>(List.of("}")));
        put("RETURN", new ArrayList<>(List.of(";")));
        put("EXPRESSION", new ArrayList<>(List.of(")",";",":","]","}")));
        put("X", new ArrayList<>(List.of(")",";",":","|")));
        put("Y", new ArrayList<>(List.of(")",";",":","|","||","&","&&")));
        put("R", new ArrayList<>(List.of(")",";",":","|","||","&","&&")));
        put("E", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=")));
        put("A", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=","+","-")));
        put("B", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=","+","-","*","/")));
        put("C", new ArrayList<>(List.of(")",";",":","|","||","&","&&","!=", "==","<", ">", "<=", ">=", "=","+","-","*","/")));
    }};

    private static final Map<String, String> DEFAULT_VALUE = new HashMap<>() {{
        put("int", "0");
        put("float","0.0f");
        put("double","0.0");
        put("boolean","false");
        put("char","");
        put("String","");
    }};

    public static DefaultMutableTreeNode run(Vector<Token> tokenVector, GUI userInterface){
        tokens = tokenVector;
        gui = userInterface;
        SemanticAnalyzer.initializeGui(gui);
        currentToken = 0;
        isSwitchBody = false;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parser Expression Tree");
        ruleProgram(root);
        return root;
    }

    public static void ruleProgram(DefaultMutableTreeNode parent){
        while(tokensExist()){
            while (tokensExist() && !isFirst("METHOD") && !isFirst("VARIABLE")){
                if(isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }
            if(searchTokenInList(currentToken, ACCESS_MODIFIER)){
                node = new DefaultMutableTreeNode("METHOD");
                parent.add(node);
                ruleMethod(node);
            }
            else if (searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
                node = new DefaultMutableTreeNode("VARIABLE");
                parent.add(node);
                ruleVariable(node, "global");
                if(checkTokenWord(currentToken, ";"))
                    currentToken++;
                else
                    errorHandler(3);
            }
        }
    }

    public static void ruleMethod(DefaultMutableTreeNode parent){
        StringBuilder methodFirm = new StringBuilder();
        String methodName = "";
        String type = "";
        if (tokensExist() && searchTokenInList(currentToken,ACCESS_MODIFIER)){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node);
            currentToken++;

            if(checkTokenWord(currentToken,"static")){
                node = new DefaultMutableTreeNode("static");
                parent.add(node);
                currentToken++;
            }

            if(checkTokenWord(currentToken,"final")){
                node = new DefaultMutableTreeNode("final");
                parent.add(node);
                currentToken++;
            }

            if(searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                type = tokens.get(currentToken).getWord();
                currentToken++;
            }
            else
                errorHandler(1);

            if(checkTokenType(currentToken, "ID")){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                methodName = tokens.get(currentToken).getWord();
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(6);

            if(checkTokenWord(currentToken, "(")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }else{
                errorHandler(8);
                while(tokensExist() && !(isFirst("PARAMETER1") ||
                        isFirst("BODY") || checkTokenWord(currentToken, ")"))){
                    if(isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            if (isFirst("PARAMETER1")){
                node = new DefaultMutableTreeNode("PARAMETER1");
                parent.add(node);
                ruleParameter1(node);
            }

            if (checkTokenWord(currentToken, ")")){
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
                if (parameterTyping != null)
                    methodFirm.append(methodName).append(parameterTyping);
                else
                    methodFirm.append(methodName);
                SemanticAnalyzer.addVariable(methodFirm.toString(), new SymbolTableItem(type,"function",""),
                        tokens.get(currentToken).getLine());


                    for (ArrayList<String> methodParameter : methodParameters) {
                        SemanticAnalyzer.addVariable(methodParameter.get(1),
                                new SymbolTableItem(methodParameter.get(0), methodFirm.toString(), DEFAULT_VALUE.get(type)),
                                tokens.get(currentToken).getLine());
                    }


            }
            else{
                errorHandler(7);
                while(tokensExist() && !(isFirst("BODY"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("BODY");
            parent.add(node);
            ruleBody(node,methodFirm.toString());
        }
    }

    public static void ruleCallMethod(DefaultMutableTreeNode parent){

        if (checkTokenType(currentToken, "ID")){
            node = new DefaultMutableTreeNode("Identifier (" + tokens.get(currentToken).getWord() + ")");
            parent.add(node);
            currentToken++;

            if (checkTokenWord(currentToken, "(")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }else errorHandler(8);

            while(tokensExist() && isSameLine() && !(isFirst("PARAMETER2") || isFollow("PARAMETER2"))){
                if(isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }

            if (isFirst("PARAMETER2")){
                node = new DefaultMutableTreeNode("PARAMETER2");
                parent.add(node);
                ruleParameter2(node);
            }

            if (checkTokenWord(currentToken, ")")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }else errorHandler(7);
      }
    }

    public static void ruleParameter1(DefaultMutableTreeNode parent){
        String type = "";
        parameterTyping = new StringBuilder();
        methodParameters.clear();
        boolean isComma;
        do{
            isComma = false;
            if (searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                type = tokens.get(currentToken).getWord();
                parent.add(node);
                currentToken++;
            }
            else errorHandler(1);

            if (checkTokenType(currentToken, "ID")){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                parameterTyping.append("-").append(type);
                methodParameters.add(new ArrayList<>(List.of(type, tokens.get(currentToken).getWord())));
                currentToken++;
            }
            else errorHandler(6);
            if (checkTokenWord(currentToken,",")){
                node = new DefaultMutableTreeNode(",");
                parent.add(node);
                currentToken++;
                isComma = true;
            }

        }while(isComma);
    }

    public static void ruleParameter2(DefaultMutableTreeNode parent){
        boolean isComma;
        do{
            isComma = false;

            while (tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFollow("EXPRESSION"))){
                if(isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }

            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);

            if (checkTokenWord(currentToken,",")){
                node = new DefaultMutableTreeNode(",");
                parent.add(node);
                currentToken++;
                isComma = true;
            }

        }while(isComma);
    }

    public static void body(DefaultMutableTreeNode parent,String methodFirm){
        if(checkTokenType(currentToken, "ID")){
            node = new DefaultMutableTreeNode("ASSIGNMENT");
            parent.add(node);
            ruleAssignment(node);
            if(checkTokenWord(currentToken, ";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(3);
        }
        else if(searchTokenInList(currentToken, DECLARATION_KEYWORDS)){
            node = new DefaultMutableTreeNode("VARIABLE");
            parent.add(node);
            ruleVariable(node, methodFirm);
            if(checkTokenWord(currentToken, ";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(3);
        }
        else if(searchTokenInList(currentToken, KEYWORDS)){
            switch (tokens.get(currentToken).getWord()){
                case "print":
                    node = new DefaultMutableTreeNode("PRINT");
                    parent.add(node); rulePrint(node);
                    if(checkTokenWord(currentToken, ";") && isSameLine()){
                        node = new DefaultMutableTreeNode(";");
                        parent.add(node);
                        currentToken++;
                    }
                    else
                        errorHandler(3);
                    break;
                case "while":
                    node = new DefaultMutableTreeNode("WHILE");
                    parent.add(node); ruleWhile(node, methodFirm);
                    break;
                case "if":
                    node = new DefaultMutableTreeNode("IF");
                    parent.add(node); ruleIf(node, methodFirm);
                    break;
                case "return":
                    node = new DefaultMutableTreeNode("RETURN");
                    parent.add(node); ruleReturn(node);
                    if(checkTokenWord(currentToken, ";") && isSameLine()){
                        node = new DefaultMutableTreeNode(";");
                        parent.add(node);
                        currentToken++;
                    }
                    else
                        errorHandler(3);
                    break;
                case "do":
                    node = new DefaultMutableTreeNode("DO");
                    parent.add(node); ruleDoWhile(node, methodFirm);
                    if(checkTokenWord(currentToken, ";") && isSameLine()){
                        node = new DefaultMutableTreeNode(";");
                        parent.add(node);
                        currentToken++;
                    }
                    else
                        errorHandler(3);
                    break;
                case "for":
                    node = new DefaultMutableTreeNode("FOR");
                    parent.add(node); ruleFor(node, methodFirm);
                    break;
                case "switch":
                    node = new DefaultMutableTreeNode("SWITCH");
                    parent.add(node); ruleSwitch(node, methodFirm);
                    break;
            }
        }else{
            while(tokensExist() && !(isFirst("PRINT") || isFirst("ASSIGNMENT") ||
                    isFirst("VARIABLE") || isFirst("WHILE") || isFirst("IF") || isFirst("RETURN") ||
                    isFirst("DO") || isFirst( "FOR") || isFirst("SWITCH") || checkTokenWord(currentToken, "}") ||
                    (checkTokenWord(currentToken,"break") && isSwitchBody))){
                if (isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }
        }
    }

    public  static void ruleBody(DefaultMutableTreeNode parent, String methodFirm){

        if(checkTokenWord(currentToken, "{")){
            node = new DefaultMutableTreeNode("{");
            parent.add(node);
            currentToken++;
        }
        else  errorHandler(10);

        while(tokensExist() && !checkTokenWord(currentToken, "}")){
            body(parent, methodFirm);
        }

        if(checkTokenWord(currentToken, "}")){
            node = new DefaultMutableTreeNode("}");
            parent.add(node);
            currentToken++;
        }
        else errorHandler(2);
    }

    public static void ruleAssignment(DefaultMutableTreeNode parent){
        correctAssignment = true;
        String id = "";
        int line = 0;
        if(checkTokenType(currentToken,"ID")){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node);
            id = tokens.get(currentToken).getWord();
            line = tokens.get(currentToken).getLine();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.getTypeById(id,line));
            currentToken++;
        }
        else{
            errorHandler(6);
            correctAssignment = false;
        }

        if(tokensExist() && checkTokenWord(currentToken,"=") && isSameLine()){
            node = new DefaultMutableTreeNode("=");
            parent.add(node);
            currentToken++;

        }
        else{
            errorHandler(5);
            correctAssignment = false;
            while (tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFollow("EXPRESSION") || checkTokenWord(currentToken,"}"))){
                if (isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }
        }

        node = new DefaultMutableTreeNode("EXPRESSION");
        parent.add(node);
        ruleExpression(node);

        if (correctAssignment){
            //SemanticAnalyzer.assignVariable(id,line);
            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            String resultAssignation = SemanticAnalyzer.calculateOperatorCube("=",var1,var2);

            if(!resultAssignation.equals("ok") && !var2.isEmpty())
                SemanticAnalyzer.errorHandler(id,line,4);
        }
    }

    public static void ruleVariable(DefaultMutableTreeNode parent, String scope){
        boolean isArray = false;
        String type;
        if(searchTokenInList(currentToken,DECLARATION_KEYWORDS)){
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node);
            currentToken++;
            if(tokensExist() && checkTokenType(currentToken,"ID") && isSameLine()){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);

                type = tokens.get(currentToken-1).getWord();
                SemanticAnalyzer.addVariable(tokens.get(currentToken).getWord(),new SymbolTableItem(type,scope,DEFAULT_VALUE.get(type)),
                        tokens.get(currentToken).getLine());
                currentToken++;
            }
            else  errorHandler(6);

            if (tokensExist() && checkTokenWord(currentToken, "[")){
                isArray = true;
                node = new DefaultMutableTreeNode("[");
                parent.add(node);
                currentToken++;

                while(tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFollow("EXPRESSION"))){
                    if(isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }

                node = new DefaultMutableTreeNode("EXPRESSION");
                parent.add(node);
                ruleExpression(node);

                if (checkTokenWord(currentToken, "]")){
                    node = new DefaultMutableTreeNode("]");
                    parent.add(node);
                    currentToken++;
                }else errorHandler(17);
            }

            if(tokensExist() && checkTokenWord(currentToken,"=") && isSameLine()){
                node = new DefaultMutableTreeNode("=");
                parent.add(node);
                currentToken++;

                while(tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFollow("EXPRESSION") ||
                        checkTokenWord(currentToken,"{"))){
                    if(isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }

                if (isArray){
                    if (checkTokenWord(currentToken,"{")){
                        node = new DefaultMutableTreeNode("{");
                        parent.add(node);
                        currentToken++;
                    }else errorHandler(10);


                    while(tokensExist() && isSameLine() && !(isFirst("PARAMETER2") || isFollow("PARAMETER2"))){
                        if(isError(currentToken)){
                            node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                            parent.add(node);
                        }
                        currentToken++;
                    }

                    if (isFirst("PARAMETER2")){
                        node = new DefaultMutableTreeNode("PARAMETER2");
                        parent.add(node);
                        ruleParameter2(node);
                    }

                    if (checkTokenWord(currentToken, "}")){
                        node = new DefaultMutableTreeNode("}");
                        parent.add(node);
                        currentToken++;
                    }else errorHandler(2);

                }else{
                    node = new DefaultMutableTreeNode("EXPRESSION");
                    parent.add(node);
                    ruleExpression(node);
                }
            }
        }
    }

    public static void ruleArray(DefaultMutableTreeNode parent){
        if (checkTokenType(currentToken, "ID")){
            node = new DefaultMutableTreeNode("Identifier (" + tokens.get(currentToken).getWord() + ")");
            parent.add(node);
            currentToken++;

            if (checkTokenWord(currentToken, "[")){
                node = new DefaultMutableTreeNode("[");
                parent.add(node);
                currentToken++;
            }else{
                errorHandler(16);
                while(tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFollow("EXPRESSION"))){
                    if(isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }


            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);


            if (checkTokenWord(currentToken, "]")){
                node = new DefaultMutableTreeNode("]");
                parent.add(node);
                currentToken++;
            }else errorHandler(17);
        }
    }

    public static void rulePrint(DefaultMutableTreeNode parent){

        if(checkTokenWord(currentToken,"print")){
            node = new DefaultMutableTreeNode("print");
            parent.add(node);
            currentToken++;
            if(tokensExist() && checkTokenWord(currentToken,"(") && isSameLine()){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }
            else{
                errorHandler(8);
                while(tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || checkTokenWord(currentToken,")") || checkTokenWord(currentToken,"}"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);

            if(tokensExist() && checkTokenWord(currentToken,")") && isSameLine()){
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
            }else errorHandler(7);
        }
    }

    public static void ruleWhile(DefaultMutableTreeNode parent, String methodFirm){

        if(checkTokenWord(currentToken, "while")){
            node = new DefaultMutableTreeNode("WHILE");
            parent.add(node);
            currentToken++;

            if(tokensExist() && checkTokenWord(currentToken,"(") && isSameLine()){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }
            else{
                errorHandler(8);
                while (tokensExist() && isSameLine() && !(isFirst("EXPRESSION") || isFirst("BODY")
                        || checkTokenWord(currentToken, ")") || isFollow("WHILE"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);

            if(tokensExist() && isSameLine() && checkTokenWord(currentToken,")")) {
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
            }
            else{
                errorHandler(7);
                while(tokensExist() && !(isFirst("BODY") || isFollow("WHILE"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("BODY");
            parent.add(node);
            ruleBody(node, methodFirm);
        }


    }

    public static void ruleIf(DefaultMutableTreeNode parent, String methodFirm){


        if(checkTokenWord(currentToken, "if")){
            node = new DefaultMutableTreeNode("IF");
            parent.add(node);
            currentToken++;

            if(tokensExist() && checkTokenWord(currentToken,"(") && isSameLine()){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }
            else{
                errorHandler(8);
                while (tokensExist() &&  isSameLine() && !(isFirst("EXPRESSION") || isFirst("BODY")
                        || checkTokenWord(currentToken, ")") || isFollow("IF"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);

            if(tokensExist() && checkTokenWord(currentToken,")") && isSameLine()){
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
            }
            else {
                errorHandler(7);
                while (tokensExist() &&  !(checkTokenWord(currentToken,"else") || isFirst("BODY") || isFollow("IF"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            node = new DefaultMutableTreeNode("BODY");
            parent.add(node);
            ruleBody(node, methodFirm);

            if(checkTokenWord(currentToken,"else")){
                node = new DefaultMutableTreeNode("else");
                parent.add(node);
                currentToken++;

                node = new DefaultMutableTreeNode("BODY");
                parent.add(node);
                ruleBody(node, methodFirm);
            }
        }

    }

    public static void ruleReturn(DefaultMutableTreeNode parent){
        if (checkTokenWord(currentToken,"return")){
            node = new DefaultMutableTreeNode("return");
            parent.add(node);
            currentToken++;

            if (tokensExist() && !checkTokenWord(currentToken, ";") && isSameLine()){
                node = new DefaultMutableTreeNode("EXPRESSION");
                parent.add(node);
                ruleExpression(node);
            }
        }
    }

    public static void ruleDoWhile(DefaultMutableTreeNode parent, String methodFirm){

        if (checkTokenWord(currentToken, "do")){
            node = new DefaultMutableTreeNode("DO");
            parent.add(node);
            currentToken++;

            while(tokensExist() && !(isFirst("BODY") || isFollow("BODY") || checkTokenWord(currentToken,"}"))){
                if (isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                }
                currentToken++;
            }
        }

        node = new DefaultMutableTreeNode("BODY");
        parent.add(node);
        ruleBody(node, methodFirm);

        if(checkTokenWord(currentToken,"while")){
            node = new DefaultMutableTreeNode("while");
            parent.add(node);
            currentToken++;
        }
        else errorHandler(11);

        if(checkTokenWord(currentToken, "(") && isSameLine()){
            node = new DefaultMutableTreeNode("(");
            parent.add(node);
            currentToken++;
        }
        else errorHandler(8);

        node = new DefaultMutableTreeNode("EXPRESSION");
        parent.add(node);
        ruleExpression(node);

        if(checkTokenWord(currentToken, ")") && isSameLine()) {
            node = new DefaultMutableTreeNode(")");
            parent.add(node);
            currentToken++;
        }
        else
            errorHandler(7);

    }

    public static void ruleFor(DefaultMutableTreeNode parent, String methodFirm){

        if(checkTokenWord(currentToken,"for")){
            node = new DefaultMutableTreeNode("FOR");
            parent.add(node);
            currentToken++;

            if(checkTokenWord(currentToken,"(") && isSameLine()){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(8);

            if(searchTokenInList(currentToken,FOR_DECLARATION_KEYWORDS) && isSameLine()){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                currentToken++;
            }

            node = new DefaultMutableTreeNode("ASSIGNMENT");
            parent.add(node);
            ruleAssignment(node);

            if(checkTokenWord(currentToken,";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(3);

            node = new DefaultMutableTreeNode("EXPRESSION");
            parent.add(node);
            ruleExpression(node);

            if(checkTokenWord(currentToken,";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(3);

            if((checkTokenWord(currentToken+1,"++") || checkTokenWord(currentToken+1,"--")) && isSameLine()){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                currentToken++;
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                currentToken++;
            }else {
                if(isSameLine()){
                    node = new DefaultMutableTreeNode("ASSIGNMENT");
                    parent.add(node);
                    ruleAssignment(node);
                }
            }


            if(checkTokenWord(currentToken,")") && isSameLine()){
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
            }
            else
                errorHandler(7);

            node = new DefaultMutableTreeNode("BODY");
            parent.add(node);
            ruleBody(node, methodFirm);
        }

    }

    public static void ruleSwitch(DefaultMutableTreeNode parent, String methodFirm){

        if (checkTokenWord(currentToken,"switch")){
            isSwitchBody = true;

            node = new DefaultMutableTreeNode("switch");
            parent.add(node);
            currentToken++;

            if(checkTokenWord(currentToken,"(") && isSameLine()){
                node = new DefaultMutableTreeNode("(");
                parent.add(node);
                currentToken++;
            }
            else errorHandler(8);

            if (checkTokenType(currentToken,"ID") && isSameLine()){
                node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
                parent.add(node);
                currentToken++;
            }else errorHandler(6);

            if(checkTokenWord(currentToken,")") && isSameLine()){
                node = new DefaultMutableTreeNode(")");
                parent.add(node);
                currentToken++;
            }
            else errorHandler(7);

            if(checkTokenWord(currentToken,"{")){
                node = new DefaultMutableTreeNode("{");
                parent.add(node);
                currentToken++;
            }
            else {
                errorHandler(10);
                while (tokensExist() && !(isFirst("CASE") || isFirst("DEFAULT") || isFollow("DEFAULT"))){
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            if(isFirst("CASE")){
               ruleCase(parent, methodFirm);
            }

            if(isFirst("DEFAULT")){
                node = new DefaultMutableTreeNode("DEFAULT");
                parent.add(node);
                ruleDefault(node, methodFirm);
            }

            if(checkTokenWord(currentToken,"}")){
                node = new DefaultMutableTreeNode("}");
                parent.add(node);
                currentToken++;
            }else errorHandler(2);

            isSwitchBody = false;
        }
    }

    public static void ruleCase(DefaultMutableTreeNode parent, String methodFirm){
        while (tokensExist() && checkTokenWord(currentToken,"case")){
            DefaultMutableTreeNode caseNode = new DefaultMutableTreeNode("CASE");
            parent.add(caseNode);
            node = new DefaultMutableTreeNode("case");
            caseNode.add(node);
            currentToken++;

            if (checkTokenType(currentToken,"INT") && isSameLine()){
                node = new DefaultMutableTreeNode("Integer (" + tokens.get(currentToken).getWord() + ")");
                caseNode.add(node);
                currentToken++;
            } else if(checkTokenType(currentToken,"BINARY") && isSameLine()){
                node = new DefaultMutableTreeNode("Binary (" + tokens.get(currentToken).getWord() + ")");
                caseNode.add(node);
                currentToken++;
            }else if(checkTokenType(currentToken,"HEX") && isSameLine()){
                node = new DefaultMutableTreeNode("Hexadecimal (" + tokens.get(currentToken).getWord() + ")");
                caseNode.add(node);
                currentToken++;
            }else if(checkTokenType(currentToken,"OCTAL") && isSameLine()){
                node = new DefaultMutableTreeNode("Octal (" + tokens.get(currentToken).getWord() + ")");
                caseNode.add(node);
                currentToken++;
            }else{
                errorHandler(13);
                while(tokensExist() && isSameLine() && !(isFirst("PRINT") || isFirst("ASSIGNMENT") ||
                        isFirst("VARIABLE") || isFirst("WHILE") || isFirst("IF") || isFirst("RETURN") ||
                        isFirst("DO") || isFirst( "FOR") || isFirst("SWITCH") || checkTokenWord(currentToken,"}") ||
                        checkTokenWord(currentToken, ":") || checkTokenWord(currentToken, "break"))) {
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        caseNode.add(node);
                    }
                    currentToken++;
                }
            }

            if (checkTokenWord(currentToken, ":") && isSameLine()){
                node = new DefaultMutableTreeNode(":");
                caseNode.add(node);
                currentToken++;
            }else{
                errorHandler(14);
                while(tokensExist() && !(isFirst("PRINT") || isFirst("ASSIGNMENT") ||
                        isFirst("VARIABLE") || isFirst("WHILE") || isFirst("IF") || isFirst("RETURN") ||
                        isFirst("DO") || isFirst( "FOR") || isFirst("SWITCH") ||
                        checkTokenWord(currentToken, "break") || checkTokenWord(currentToken, "}"))) {
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        caseNode.add(node);
                    }
                    currentToken++;
                }
            }

            DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode("BODY");
            caseNode.add(bodyNode);
            while(tokensExist() && !(checkTokenWord(currentToken, "}") || checkTokenWord(currentToken,"break"))){
                body(bodyNode, methodFirm);
            }

            if (checkTokenWord(currentToken,"break")){
                node = new DefaultMutableTreeNode("break");
                caseNode.add(node);
                currentToken++;
            }else errorHandler(15);

            if (checkTokenWord(currentToken,";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                caseNode.add(node);
                currentToken++;
            }else errorHandler(3);
        }
    }

    public static void ruleDefault(DefaultMutableTreeNode parent, String methodFirm){
        if(checkTokenWord(currentToken,"default")){
            node = new DefaultMutableTreeNode("default");
            parent.add(node);
            currentToken++;

            if (checkTokenWord(currentToken, ":") && isSameLine()){
                node = new DefaultMutableTreeNode(":");
                parent.add(node);
                currentToken++;
            }else{
                errorHandler(14);
                while(tokensExist() && !(isFirst("PRINT") || isFirst("ASSIGNMENT") ||
                        isFirst("VARIABLE") || isFirst("WHILE") || isFirst("IF") || isFirst("RETURN") ||
                        isFirst("DO") || isFirst( "FOR") || isFirst("SWITCH") ||
                        checkTokenWord(currentToken, "break"))) {
                    if (isError(currentToken)){
                        node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                        parent.add(node);
                    }
                    currentToken++;
                }
            }

            DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode("BODY");
            parent.add(bodyNode);
            while(tokensExist() && !(checkTokenWord(currentToken, "}") || checkTokenWord(currentToken,"break"))){
                body(bodyNode, methodFirm);
            }

            if (checkTokenWord(currentToken,"break")){
                node = new DefaultMutableTreeNode("break");
                parent.add(node);
                currentToken++;
            }else errorHandler(15);

            if (checkTokenWord(currentToken,";") && isSameLine()){
                node = new DefaultMutableTreeNode(";");
                parent.add(node);
                currentToken++;
            }else errorHandler(3);
        }
    }

    public static void ruleExpression(DefaultMutableTreeNode parent){
        String operator;

        node = new DefaultMutableTreeNode("X");
        parent.add(node);
        ruleX(node);

        while(tokensExist() && searchTokenInList(currentToken,RULE_X_OPERATORS) && isSameLine()){
            operator = tokens.get(currentToken).getWord();
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("X");
            parent.add(node);
            ruleX(node);

            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube(operator,var1,var2));
        }
    }

    public static void ruleX(DefaultMutableTreeNode parent){
        String operator;

        node = new DefaultMutableTreeNode("Y");
        parent.add(node);
        ruleY(node);

        while(tokensExist() && searchTokenInList(currentToken, RULE_Y_OPERATORS) && isSameLine()){
            operator = tokens.get(currentToken).getWord();
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("Y");
            parent.add(node);
            ruleY(node);

            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube(operator,var1,var2));


        }
    }

    public static void ruleY(DefaultMutableTreeNode parent){
        boolean operatorUsed = false;

        if(tokensExist() && checkTokenWord(currentToken, "!") && isSameLine()){
            operatorUsed = true;
            node = new DefaultMutableTreeNode("!");
            parent.add(node); currentToken++;
        }
        node = new DefaultMutableTreeNode("R");
        parent.add(node);
        ruleR(node);

        if (operatorUsed){
            String var = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube("!",var));
        }
    }

    public static void ruleR(DefaultMutableTreeNode parent){
        String operator;

        node = new DefaultMutableTreeNode("E");
        parent.add(node);
        ruleE(node);

        while(tokensExist() && searchTokenInList(currentToken, RULE_R_OPERATORS) && isSameLine()){
            operator = tokens.get(currentToken).getWord();
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("E");
            parent.add(node);
            ruleE(node);

            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube(operator,var1,var2));
        }
    }

    public static void ruleE(DefaultMutableTreeNode parent){
        String operator;

        node = new DefaultMutableTreeNode("A");
        parent.add(node);
        ruleA(node);

        while(tokensExist() && searchTokenInList(currentToken, RULE_E_OPERATORS) && isSameLine()){
            operator = tokens.get(currentToken).getWord();
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("A");
            parent.add(node);
            ruleA(node);

            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube(operator,var1,var2));
        }
    }

    public static void ruleA(DefaultMutableTreeNode parent){
        String operator;

        node = new DefaultMutableTreeNode("B");
        parent.add(node);
        ruleB(node);

        while(tokensExist() && searchTokenInList(currentToken,RULE_A_OPERATORS) && isSameLine()){
            operator = tokens.get(currentToken).getWord();
            node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
            parent.add(node); currentToken++;
            node = new DefaultMutableTreeNode("B");
            parent.add(node);
            ruleB(node);

            String var1 = SemanticAnalyzer.popStack();
            String var2 = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube(operator,var1,var2));

        }
    }

    public static void ruleB(DefaultMutableTreeNode parent){
        boolean operatorUsed = false;
        if(tokensExist() && checkTokenWord(currentToken,"-") && isSameLine()){
            operatorUsed = true;
            node = new DefaultMutableTreeNode("-");
            parent.add(node); currentToken++;
        }

        node = new DefaultMutableTreeNode("C");
        parent.add(node);
        ruleC(node);

        if (operatorUsed){
            String var = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateOperatorCube("-",var));
        }
    }

    public static void ruleC(DefaultMutableTreeNode parent){
        if(tokensExist() && isSameLine()){
            Token token = tokens.get(currentToken);
            if(token.getToken().equals("INT")){
                SemanticAnalyzer.pushStack("int");
                node = new DefaultMutableTreeNode("Integer (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("ID")){
                if (checkTokenWord(currentToken+1, "(")){
                    SemanticAnalyzer.pushStack(SemanticAnalyzer.getTypeById(token.getWord(),token.getLine()));
                    node = new DefaultMutableTreeNode("CALLMETHOD");
                    parent.add(node);
                    ruleCallMethod(node);
                }else if (checkTokenWord(currentToken+1,"[")){
                    SemanticAnalyzer.pushStack(SemanticAnalyzer.getTypeById(token.getWord(),token.getLine()));
                    node = new DefaultMutableTreeNode("ARRAY");
                    parent.add(node);
                    ruleArray(node);
                }else{
                    SemanticAnalyzer.pushStack(SemanticAnalyzer.getTypeById(token.getWord(),token.getLine()));
                    node = new DefaultMutableTreeNode("Identifier (" + token.getWord() + ")");
                    parent.add(node); currentToken++;
                }
            } else if(token.getToken().equals("OCTAL")){
                SemanticAnalyzer.pushStack("int");
                node = new DefaultMutableTreeNode("Octal (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("HEX")){
                SemanticAnalyzer.pushStack("int");
                node = new DefaultMutableTreeNode("Hexadecimal (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("BINARY")){
                SemanticAnalyzer.pushStack("int");
                node = new DefaultMutableTreeNode("Binary (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("STRING")){
                SemanticAnalyzer.pushStack("string");
                node = new DefaultMutableTreeNode("String (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("CHAR")){
                SemanticAnalyzer.pushStack("char");
                node = new DefaultMutableTreeNode("Char (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("FLOAT")){
                SemanticAnalyzer.pushStack("float");
                node = new DefaultMutableTreeNode("Float (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("DOUBLE")){
                SemanticAnalyzer.pushStack("double");
                node = new DefaultMutableTreeNode("Double (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getToken().equals("BOOLEAN")){
                SemanticAnalyzer.pushStack("boolean");
                node = new DefaultMutableTreeNode("Boolean (" + token.getWord() + ")");
                parent.add(node); currentToken++;
            } else if(token.getWord().equals("(")){
                node = new DefaultMutableTreeNode("(");
                parent.add(node); currentToken++;
                node = new DefaultMutableTreeNode("EXPRESSION");
                parent.add(node);
                ruleExpression(node);
                if(tokensExist() && checkTokenWord(currentToken,")")){
                    node = new DefaultMutableTreeNode(")");
                    parent.add(node); currentToken++;
                }else{
                    errorHandler(7);
                }
            } else {
                errorHandler(9);
                correctAssignment = false;
                if (isError(currentToken)){
                    node = new DefaultMutableTreeNode("Error ("+tokens.get(currentToken).getWord() + ")");
                    parent.add(node);
                    currentToken++;
                }
            }
        } else {
            errorHandler(9);
            correctAssignment = false;
        }
    }

    public static boolean isSameLine() {
        return (tokens.get(currentToken).getLine() == tokens.get(currentToken - 1).getLine());
    }

    private static boolean isFirst(String rule){
        final List<String> CHECK_WORD_RULES = List.of(
                "PROGRAM","BODY","METHOD","PARAMETER1","VARIABLE","PRINT",
                "WHILE","DO","RETURN","IF","FOR","SWITCH","CASE","DEFAULT"
        );

        final List<String> FACTORIAL_RULES = List.of(
                "PARAMETER2","EXPRESSION","X","Y"
        );

        List<String> currentRuleFirstSet = FIRST_SET.get(rule);

        if(!tokensExist())
            return false;

        if(rule.equals("ASSIGNMENT")){
            return currentRuleFirstSet.contains(tokens.get(currentToken).getToken());
        }else if(CHECK_WORD_RULES.contains(rule)){
            return currentRuleFirstSet.contains(tokens.get(currentToken).getWord());
        }else{
            if(checkTokenWord(currentToken,"("))
                return true;
            else if(checkTokenWord(currentToken,"-") && !rule.equals("C"))
                return true;
            else if(checkTokenWord(currentToken,"!") && FACTORIAL_RULES.contains(rule))
                return true;

            return currentRuleFirstSet.contains(tokens.get(currentToken).getToken());
        }
    }

    private static boolean isFollow(String rule){
        final List<String> CHECK_ID_RULES = List.of(
                "BODY","WHILE","DO","IF","FOR","SWITCH"
        );

        List<String> currentRuleFollowSet = FOLLOW_SET.get(rule);

        if (!tokensExist())
            return false;

        if(CHECK_ID_RULES.contains(rule)){
            if(checkTokenType(currentToken,"ID")){
                return true;
            } else
                return currentRuleFollowSet.contains(tokens.get(currentToken).getWord());
        }else{
            return currentRuleFollowSet.contains(tokens.get(currentToken).getWord());
        }
    }

    private static boolean checkTokenWord(int token, String word){
        if(token >= tokens.size()){
            return false;
        }
        return tokens.get(token).getWord().equals(word);
    }

    private static boolean checkTokenType(int token, String tokenType){
        if(token >= tokens.size()){
            return false;
        }
        return tokens.get(token).getToken().equals(tokenType);
    }

    private static boolean tokensExist(){
        return currentToken < tokens.size();
    }

    private static boolean isError(int token){
        int line = tokens.get(currentToken-1).getLine();
        if (checkTokenType(token,"ERROR")){
            gui.writeConsoleLine("Line " + line + ": '" + tokens.get(token).getWord() + "' illegal statement");
            return true;
        }else{
            return false;
        }
    }

    private static boolean searchTokenInList(int token, List<String> tokenList){
        if(currentToken >= tokens.size()){
            return false;
        }
        return tokenList.contains(tokens.get(token).getWord());
    }

    public static void errorHandler(int err){
        int line = tokens.get(currentToken-1).getLine();

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
            case 13: gui.writeConsoleLine("Line " + line + ": expected value"); break;
            case 14: gui.writeConsoleLine("Line " + line + ": expected ':'"); break;
            case 15: gui.writeConsoleLine("Line " + line + ": expected break label"); break;
            case 16: gui.writeConsoleLine("Line " + line + ": expected '['"); break;
            case 17: gui.writeConsoleLine("Line " + line + ": expected ']'"); break;
        }
    }
}
