package com.emts.vitrualcompiler.services;

import com.emts.vitrualcompiler.syntax.*;
import com.emts.vitrualcompiler.exceptions.CompilerException;
import com.emts.vitrualcompiler.helper.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * *******************************************************************
 * File: null.java
 * Package: com.emts.vitrualcompiler.services
 * Project: eMTS Smart Attendance System
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 25/11/2025
 * Port Number: 8083
 * *******************************************************************
 */
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() throws CompilerException {
        Program program = new Program();
        while (current().type != Token.Type.EOF) {
            program.statements.add(parseStatement());
        }
        return program;
    }

    private ASTNode parseStatement() throws CompilerException {
        Token token = current();

        if (token.type == Token.Type.VAR) return parseVarDeclaration();
        if (token.type == Token.Type.IF) return parseIfStatement();
        if (token.type == Token.Type.WHILE) return parseWhileStatement();
        if (token.type == Token.Type.PRINT) return parsePrintStatement();
        if (token.type == Token.Type.ID && peek().type == Token.Type.ASSIGN) return parseAssignment();

        ASTNode expr = parseExpression();
        if (current().type == Token.Type.SEMICOLON) consume(Token.Type.SEMICOLON);
        return expr;
    }

    private VarDeclaration parseVarDeclaration() throws CompilerException {
        int line = current().line;
        consume(Token.Type.VAR);
        String name = consume(Token.Type.ID).value;
        consume(Token.Type.ASSIGN);
        ASTNode value = parseExpression();
        consume(Token.Type.SEMICOLON);
        return new VarDeclaration(name, value, line);
    }

    private Assignment parseAssignment() throws CompilerException {
        int line = current().line;
        String name = consume(Token.Type.ID).value;
        consume(Token.Type.ASSIGN);
        ASTNode value = parseExpression();
        consume(Token.Type.SEMICOLON);
        return new Assignment(name, value, line);
    }

    private PrintStatement parsePrintStatement() throws CompilerException {
        int line = current().line;
        consume(Token.Type.PRINT);
        consume(Token.Type.LPAREN);
        PrintStatement stmt = new PrintStatement(line);
        if (current().type != Token.Type.RPAREN) {
            stmt.arguments.add(parseExpression());
        }
        consume(Token.Type.RPAREN);
        consume(Token.Type.SEMICOLON);
        return stmt;
    }

    private IfStatement parseIfStatement() throws CompilerException {
        int line = current().line;
        consume(Token.Type.IF);
        consume(Token.Type.LPAREN);
        ASTNode condition = parseExpression();
        consume(Token.Type.RPAREN);
        List<ASTNode> thenBranch = parseBlock();
        List<ASTNode> elseBranch = new ArrayList<>();
        if (current().type == Token.Type.ELSE) {
            consume(Token.Type.ELSE);
            elseBranch = parseBlock();
        }
        return new IfStatement(condition, thenBranch, elseBranch, line);
    }

    private WhileStatement parseWhileStatement() throws CompilerException {
        int line = current().line;
        consume(Token.Type.WHILE);
        consume(Token.Type.LPAREN);
        ASTNode condition = parseExpression();
        consume(Token.Type.RPAREN);
        List<ASTNode> body = parseBlock();
        return new WhileStatement(condition, body, line);
    }

    private List<ASTNode> parseBlock() throws CompilerException {
        List<ASTNode> statements = new ArrayList<>();
        consume(Token.Type.LBRACE);
        while (current().type != Token.Type.RBRACE && current().type != Token.Type.EOF) {
            statements.add(parseStatement());
        }
        consume(Token.Type.RBRACE);
        return statements;
    }

    private ASTNode parseExpression() throws CompilerException {
        return parseComparison();
    }

    private ASTNode parseComparison() throws CompilerException {
        ASTNode left = parseAddition();
        while (current().type == Token.Type.LT || current().type == Token.Type.GT ||
                current().type == Token.Type.LE || current().type == Token.Type.GE ||
                current().type == Token.Type.EQ || current().type == Token.Type.NE) {
            int line = current().line;
            String op = consume(current().type).value;
            ASTNode right = parseAddition();
            left = new BinaryOp(left, op, right, line);
        }
        return left;
    }

    private ASTNode parseAddition() throws CompilerException {
        ASTNode left = parseMultiplication();
        while (current().type == Token.Type.PLUS || current().type == Token.Type.MINUS) {
            int line = current().line;
            String op = consume(current().type).value;
            ASTNode right = parseMultiplication();
            left = new BinaryOp(left, op, right, line);
        }
        return left;
    }

    private ASTNode parseMultiplication() throws CompilerException {
        ASTNode left = parseUnary();
        while (current().type == Token.Type.MUL || current().type == Token.Type.DIV ||
                current().type == Token.Type.MOD) {
            int line = current().line;
            String op = consume(current().type).value;
            ASTNode right = parseUnary();
            left = new BinaryOp(left, op, right, line);
        }
        return left;
    }

    private ASTNode parseUnary() throws CompilerException {
        if (current().type == Token.Type.MINUS) {
            int line = current().line;
            String op = consume(Token.Type.MINUS).value;
            ASTNode operand = parseUnary();
            return new UnaryOp(op, operand, line);
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() throws CompilerException {
        int line = current().line;
        Token token = current();

        if (token.type == Token.Type.NUMBER) {
            int value = Integer.parseInt(consume(Token.Type.NUMBER).value);
            return new NumberLiteral(value, line);
        }
        if (token.type == Token.Type.ID) {
            String name = consume(Token.Type.ID).value;
            return new Variable(name, line);
        }
        if (token.type == Token.Type.LPAREN) {
            consume(Token.Type.LPAREN);
            ASTNode expr = parseExpression();
            consume(Token.Type.RPAREN);
            return expr;
        }
        throw new CompilerException("Unexpected token: " + token + " at line " + line);
    }

    private Token consume(Token.Type type) throws CompilerException {
        if (current().type != type) {
            throw new CompilerException("Expected " + type + " but got " + current().type +
                    " at line " + current().line);
        }
        return tokens.get(pos++);
    }

    private Token current() {
        return pos < tokens.size() ? tokens.get(pos) : tokens.getLast();
    }

    private Token peek() {
        return pos + 1 < tokens.size() ? tokens.get(pos + 1) : tokens.getLast();
    }
}
