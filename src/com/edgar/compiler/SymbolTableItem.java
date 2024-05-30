package com.edgar.compiler;

public class SymbolTableItem {

    private final String type;
    private final String scope;
    private final String value;

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public String getValue() {
        return value;
    }


    public SymbolTableItem(String type, String scope, String value) {
        this.type = type;
        this.scope = scope;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SymbolTableItem{" +
                "type='" + type + '\'' +
                ", scope='" + scope + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
