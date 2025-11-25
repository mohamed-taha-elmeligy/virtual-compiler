package com.emts.vitrualcompiler.helper;

/**
 * *******************************************************************
 * File: null.java
 * Package: com.emts.vitrualcompiler.helper
 * Project: eMTS Smart Attendance System
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 24/11/2025
 * Port Number: 8083
 * *******************************************************************
 */
public class Token {
    public enum Type {
        VAR, IF, ELSE, WHILE, PRINT,
        ID, NUMBER,
        ASSIGN, PLUS, MINUS, MUL, DIV, MOD,
        EQ, NE, LT, GT, LE, GE,
        LPAREN, RPAREN, LBRACE, RBRACE,
        SEMICOLON, EOF
    }

    public Type type;
    public String value;
    public int line;

    public Token(Type type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("[%s: '%s' at line %d]", type, value, line);
    }
}
