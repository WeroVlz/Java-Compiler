package com.edgar.compiler;

public class Token {

    private int row;
    private String word;
    private String token;

    public Token(int row, String word, String token){
        setRow(row);
        setWord(word);
        setToken(token);
    }

    public int getRow() {
        return row;
    }

    public String getWord() {
        return word;
    }

    public String getToken() {
        return token;
    }


    public void setRow(int row) {
        this.row = row;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
