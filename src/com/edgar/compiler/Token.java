package com.edgar.compiler;

public class Token {

    private int line;
    private String word;
    private String token;

    public Token(int row, String word, String token){
        setLine(row);
        setWord(word);
        setToken(token);
    }

    public int getLine() {
        return line;
    }

    public String getWord() {
        return word;
    }

    public String getToken() {
        return token;
    }


    public void setLine(int line) {
        this.line = line;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
