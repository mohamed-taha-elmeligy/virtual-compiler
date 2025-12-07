package com.emts.vitrualcompiler.services;

import com.emts.vitrualcompiler.exceptions.CompilerException;
import com.emts.vitrualcompiler.helper.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * *******************************************************************
 * File: Lexer.java
 * Package: com.emts.vitrualcompiler.services
 * Project: eMTS Virtual Compiler
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 24/11/2025
 * Port Number: 8083
 * *******************************************************************
 */

public class Lexer {

    private final String input;
    private int pos = 0;
    private int line = 1;
    private final List<Token> tokens = new ArrayList<>();
    private final List<Token> printTokens = new ArrayList<>();


    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() throws CompilerException {
        while (pos < input.length()) {
            char c = input.charAt(pos);

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                if (c == '\n') line++;
                pos++;
                continue;
            }

            // Skip comments
            if (c == '/' && peek() == '/') {
                while (pos < input.length() && input.charAt(pos) != '\n') pos++;
                continue;
            }

            // Numbers
            if (Character.isDigit(c)) {
                readNumber();
                continue;
            }

            // Identifiers & Keywords
            if (Character.isLetter(c) || c == '_') {
                readIdentifier();
                continue;
            }

            // Operators & Delimiters
            if (!readOperator()) {
                throw new CompilerException("Unknown character: '" + c + "' at line " + line);
            }
        }

        tokens.add(new Token(Token.Type.EOF, "", line));
        return tokens;
    }

    private void readNumber() {
        int startLine = line;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos++));
        }
        tokens.add(new Token(Token.Type.NUMBER, sb.toString(), startLine));
        printTokens.add(new Token(Token.Type.IDENTIFIER, sb.toString(), startLine));

    }

    private void readIdentifier() {
        int startLine = line;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() &&
                (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
            sb.append(input.charAt(pos++));
        }
        String text = sb.toString();
        Token.Type type = switch (text) {
            case "var" -> Token.Type.VAR;
            case "if" -> Token.Type.IF;
            case "else" -> Token.Type.ELSE;
            case "while" -> Token.Type.WHILE;
            case "print" -> Token.Type.PRINT;
            default -> Token.Type.ID;
        };
        tokens.add(new Token(type, text, startLine));
        if(type == (Token.Type.ID))
            printTokens.add(new Token(Token.Type.LITERALS, text, startLine));
        else {printTokens.add(new Token(Token.Type.KEYWORD, text, startLine));}
    }

    private boolean readOperator() {
        int startLine = line;
        char c = input.charAt(pos);

        // Two-character operators
        if (pos + 1 < input.length()) {
            String two = "" + c + input.charAt(pos + 1);
            Token.Type type = switch (two) {
                case "==" -> Token.Type.EQ;
                case "!=" -> Token.Type.NE;
                case "<=" -> Token.Type.LE;
                case ">=" -> Token.Type.GE;
                default -> null;
            };
            if (type != null) {
                tokens.add(new Token(type, two, startLine));
                printTokens.add(new Token(Token.Type.OPERATOR, two, startLine));
                pos += 2;
                return true;
            }
        }

        // Single-character operators
        Token.Type typeOperator = switch (c) {
            case '=' -> Token.Type.ASSIGN;
            case '+' -> Token.Type.PLUS;
            case '-' -> Token.Type.MINUS;
            case '*' -> Token.Type.MUL;
            case '/' -> Token.Type.DIV;
            case '%' -> Token.Type.MOD;
            case '<' -> Token.Type.LT;
            case '>' -> Token.Type.GT;
            default -> null;
        };

        Token.Type typeDelimiter = switch (c) {
            case '(' -> Token.Type.LPAREN;
            case ')' -> Token.Type.RPAREN;
            case '{' -> Token.Type.LBRACE;
            case '}' -> Token.Type.RBRACE;
            case ';' -> Token.Type.SEMICOLON;
            default -> null;
        };

        if (typeOperator != null) {
            tokens.add(new Token(typeOperator, "" + c, startLine));
            printTokens.add(new Token(Token.Type.OPERATOR, ""+ c, startLine));
            pos++;
            return true;
        }

        if (typeDelimiter != null) {
            tokens.add(new Token(typeDelimiter, "" + c, startLine));
            printTokens.add(new Token(Token.Type.DELIMITER, ""+ c, startLine));
            pos++;
            return true;
        }
        return false;
    }

    private char peek() {
        return pos + 1 < input.length() ? input.charAt(pos + 1) : '\0';
    }

    public List<Token> getPrintTokens() {
        printTokens.add(new Token(Token.Type.EOF, "", line));
        return printTokens;
    }

}
