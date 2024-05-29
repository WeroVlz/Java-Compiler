package com.edgar.compiler;

public class SymbolTableItem {

    private String type;
    private String scope;
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
