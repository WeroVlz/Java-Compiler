package com.edgar.compiler;

import java.util.*;

public class Lexer {

    private static final List<String> KEYWORDS = List.of(
            "if", "else", "when", "while", "do", "switch", "case", "print",
            "void", "private", "public", "protected", "boolean",
            "String", "int", "char", "float", "double","final","return","static",
            "continue", "end","for","break","default"
    );

    private static final List<String> BOOLEAN_KEYWORDS = List.of(
            "true","false"
    );

    private static final List<Character> OPERATORS = List.of(
            '+','-','*','/','=','!','<','>','%','&','|','^'
    );

    private static final List<String> COMPARISON_OPERATORS = List.of(
        "!=", "==", "<=", ">=", "++", "--", "+=", "-=", "*=", "/=", "&&", "||"
    );

    private static final List<Character> DELIMITERS = List.of(
            '(',')','[',']','{','}',';',':',','
    );


    private static final int DOLLAR_SIGN = 0;
    private static final int UNDERSCORE = 1;
    private static final int ZERO = 2;
    private static final int ONE = 3;
    private static final int NUM_2_7 = 4;
    private static final int NUM_8_9 = 5;
    private static final int A = 6;
    private static final int B = 7;
    private static final int C = 8;
    private static final int D = 9;
    private static final int E = 10;
    private static final int F = 11;
    private static final int G_W = 12;
    private static final int N = 13;
    private static final int T = 14;
    private static final int X = 15;
    private static final int Y_Z = 16;
    private static final int SINGLE_QUOTE = 17;
    private static final int DOUBLE_QUOTE = 18;
    private static final int DOT = 19;
    private static final int PLUS_MINUS = 20;
    private static final int BACKSLASH = 21;
    private static final int OTHER = 22;
    private static final int DELIMITER = 23;
    private static final int ERROR = 25;
    private static final int STOP = -1;

    private static final int[][] STATE_TABLE = {
            {   16,   16,    1,   18,   18,   18,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   13,   11,   17,   10,ERROR,ERROR, STOP},
            {ERROR,ERROR,    6,ERROR,    6,ERROR,ERROR,    2,ERROR,   21,   19,    8,ERROR,ERROR,ERROR,    4,ERROR,ERROR,ERROR,    7,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    3,    3,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    3,    3,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    6,    6,    6,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    7,    7,    7,    7,ERROR,ERROR,ERROR,   21,   19,    8,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,ERROR,   22,   22,   22,   22,   22},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,ERROR,   14,   14,   14,   24,   14,   14},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,   15,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,   16,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,    7,    7,    7,    7,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,   18,   18,   18,   18,ERROR,ERROR,ERROR,   21,   19,    8,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,    7,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,   23,   23,   23,   23,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,   20,ERROR,ERROR, STOP},
            {ERROR,ERROR,   23,   23,   23,   23,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,   12,   22,   22,   22,   22,   22},
            {ERROR,ERROR,   23,   23,   23,   23,ERROR,ERROR,ERROR,   21,ERROR,    8,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,   14,   14,ERROR,ERROR,   14,ERROR,ERROR,ERROR,   14,ERROR, STOP},
            {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR, STOP},
    };

    private static final Set<Integer> stateErrorSet = new HashSet<>(Arrays.asList(
            2,4,11,13,14,17,19,20,22,24,25
    ));
    private final Vector<Token> Tokens = new Vector<>();
    private int rowIndex = 1;
    private int errorCount;


    public Lexer(String editorText){
        String[] editorLines = editorText.split("\n");

        for(String line: editorLines){
            if (!line.isEmpty())
                splitLine(line, rowIndex);
            rowIndex++;
        }
    }

    public Vector<Token> getTokens() {
        return Tokens;
    }

    public int getRows() {return rowIndex; }

    public int getErrorCount() {return errorCount; }

    private boolean isSpace(char c){
        return c == ' ';
    }

    private boolean isDelimiter(char c){
        return DELIMITERS.contains(c);
    }

    private boolean isOperator(char c){
        return OPERATORS.contains(c);
    }

    private boolean isSingleQuote(char c){
        return c == '\'';
    }

    private boolean isDoubleQuote(char c){
        return c == '"';
    }

    private int calculateState(int state, char charRead){
        if(isSpace(charRead) || isDelimiter(charRead) || (isOperator(charRead) && state != 19 && state != 20))
            return STATE_TABLE[state][DELIMITER];

        charRead = (charRead != 'n' && charRead != 't') ? Character.toUpperCase(charRead) : charRead;

        if(charRead == '$') return STATE_TABLE[state][DOLLAR_SIGN];
        if(charRead == '_') return STATE_TABLE[state][UNDERSCORE];
        if(charRead == '0') return STATE_TABLE[state][ZERO];
        if(charRead == '1') return STATE_TABLE[state][ONE];
        if(charRead >= '2' && charRead <= '7') return STATE_TABLE[state][NUM_2_7];
        if(charRead == '8' || charRead == '9') return STATE_TABLE[state][NUM_8_9];
        if(charRead == 'A') return STATE_TABLE[state][A];
        if(charRead == 'B') return STATE_TABLE[state][B];
        if(charRead == 'C') return STATE_TABLE[state][C];
        if(charRead == 'D') return STATE_TABLE[state][D];
        if(charRead == 'E') return STATE_TABLE[state][E];
        if(charRead == 'F') return STATE_TABLE[state][F];
        if(charRead >= 'G' && charRead <= 'W') return STATE_TABLE[state][G_W];
        if(charRead == 'n') return STATE_TABLE[state][N];
        if(charRead == 't') return STATE_TABLE[state][T];
        if(charRead == 'X') return STATE_TABLE[state][X];
        if(charRead == 'Y' || charRead == 'Z') return STATE_TABLE[state][Y_Z];
        if(charRead == '\'') return STATE_TABLE[state][SINGLE_QUOTE];
        if(charRead == '"') return STATE_TABLE[state][DOUBLE_QUOTE];
        if(charRead == '.') return STATE_TABLE[state][DOT];
        if(charRead == '+' || charRead == '-') return STATE_TABLE[state][PLUS_MINUS];
        if(charRead == '\\') return STATE_TABLE[state][BACKSLASH];
        return STATE_TABLE[state][OTHER];
    }



    private void splitLine(String line, int row){
        line = line.replace("\t","        ");
        char charRead;
        int index = 0;
        int state = 0;
        int nextState;
        boolean isChar = false;
        boolean isString = false;
        boolean isScientificNotation = false;
        boolean isComparisonOperator = false;
        String combinedOperator = "";
        int lineLength = line.length();

        StringBuilder formedString = new StringBuilder();
        do{
            charRead = line.charAt(index);
            nextState = calculateState(state,charRead);

            if(isSingleQuote(charRead) && !isString){
                isChar = !isChar;
            }

            if(isDoubleQuote(charRead) && !isChar){
                isString = !isString;
            }

            if(nextState == 19){
                isScientificNotation = true;
            }

            if(isOperator(charRead)){
                int nextCharIndex = index + 1;
                if (nextCharIndex < line.length()){
                    char nextCharRead = line.charAt(nextCharIndex);
                    combinedOperator = new String(new  char[]{charRead, nextCharRead});
                    if(COMPARISON_OPERATORS.contains(combinedOperator)){
                        index++;
                        isComparisonOperator = true;
                    }
                }
            }

            if(isChar || isString){
                state = calculateState(state,charRead);
                if(state == STOP){
                    nextState = ERROR;
                    state = ERROR;
                }
                formedString.append(charRead);
            }else if(isScientificNotation){
                if(isDelimiter(charRead) || isSpace(charRead)){
                    isScientificNotation = false;
                }else{
                    state = calculateState(state,charRead);
                    if(state == STOP){
                        nextState = ERROR;
                        state = ERROR;
                    }
                    formedString.append(charRead);
                }

            }
            else if(!isDelimiter(charRead) && !isSpace(charRead) && !isOperator(charRead)){
                state = calculateState(state,charRead);
                formedString.append(charRead);
            }

            index++;

        } while (index < lineLength && nextState != STOP);

        if(state == 1 || state == 18){
            Tokens.add(new Token(row, formedString.toString(),"INT"));
        }else if(state == 3){
            Tokens.add(new Token(row,formedString.toString(),"BINARY"));
        }else if(state == 5){
            Tokens.add(new Token(row,formedString.toString(),"HEX"));
        }else if(state == 6){
            Tokens.add(new Token(row,formedString.toString(),"OCTAL"));
        }else if(state == 7 || state == 21 || state == 23){
            Tokens.add(new Token(row,formedString.toString(),"DOUBLE"));
        }else if(state == 8){
            Tokens.add(new Token(row,formedString.toString(),"FLOAT"));
        }else if(state == 12){
            Tokens.add(new Token(row,formedString.toString(),"STRING"));
        }else if(state == 15){
            Tokens.add(new Token(row,formedString.toString(),"CHAR"));
        }else if(state == 16){
            if(KEYWORDS.contains(formedString.toString())){
                Tokens.add(new Token(row,formedString.toString(),"KEYWORD"));
            }else if(BOOLEAN_KEYWORDS.contains(formedString.toString())){
                Tokens.add(new Token(row,formedString.toString(),"BOOLEAN"));
            }else{
                Tokens.add(new Token(row, formedString.toString(), "ID"));
            }
        } else if(stateErrorSet.contains(state)){
            Tokens.add(new Token(row,formedString.toString(),"ERROR"));
            errorCount++;
        }

        if(isDelimiter(charRead)){
            Tokens.add(new Token(row,charRead+"", "DELIMITER"));
        }
        else if(isOperator(charRead) && !isScientificNotation){
            if (isComparisonOperator){
                Tokens.add(new Token(row, combinedOperator, "OPERATOR"));
            }else{
                Tokens.add(new Token(row,charRead+"", "OPERATOR"));
            }

        }


        if(index< lineLength){
            splitLine(line.substring(index),row);
        }
    }
}
