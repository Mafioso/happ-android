package com.happ.models;

import io.realm.RealmObject;

/**
 * Created by dante on 7/26/16.
 */
public class Currency extends RealmObject {

    private String code;
    private String symbol;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        if (symbol == null) return code;
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
