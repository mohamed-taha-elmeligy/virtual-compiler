package com.emts.vitrualcompiler.services;

import com.emts.vitrualcompiler.exceptions.CompilerException;
import com.emts.vitrualcompiler.helper.SymbolTable;
import com.emts.vitrualcompiler.syntax.*;

/**
 * *******************************************************************
 * File: null.java
 * Package: com.emts.vitrualcompiler.services
 * Project: eMTS Smart Attendance System
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 24/11/2025
 * Port Number: 8083
 * *******************************************************************
 */
public class SemanticAnalyzer {
    private SymbolTable globalSymbolTable;
    private SymbolTable currentSymbolTable;

    public SemanticAnalyzer() {
        this.globalSymbolTable = new SymbolTable();
        this.currentSymbolTable = globalSymbolTable;
    }

    public void analyze(Program program) throws CompilerException {
        for (ASTNode stmt : program.statements) {
            analyzeStatement(stmt);
        }
    }

    private void analyzeStatement(ASTNode node) throws CompilerException {
        if (node instanceof VarDeclaration) {
            VarDeclaration decl = (VarDeclaration) node;
            currentSymbolTable.define(decl.name, node.line);
            analyzeExpression(decl.value);
        } else if (node instanceof Assignment) {
            Assignment assign = (Assignment) node;
            SymbolTable.Symbol sym = currentSymbolTable.lookup(assign.name);
            if (sym == null) throw new CompilerException("Undefined variable: " + assign.name);
            analyzeExpression(assign.value);
        } else if (node instanceof PrintStatement) {
            PrintStatement print = (PrintStatement) node;
            for (ASTNode arg : print.arguments) analyzeExpression(arg);
        } else if (node instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) node;
            analyzeExpression(ifStmt.condition);
            SymbolTable saved = currentSymbolTable;
            currentSymbolTable = new SymbolTable(currentSymbolTable);
            for (ASTNode stmt : ifStmt.thenBranch) analyzeStatement(stmt);
            currentSymbolTable = saved;
            if (!ifStmt.elseBranch.isEmpty()) {
                currentSymbolTable = new SymbolTable(currentSymbolTable);
                for (ASTNode stmt : ifStmt.elseBranch) analyzeStatement(stmt);
                currentSymbolTable = saved;
            }
        } else if (node instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) node;
            analyzeExpression(whileStmt.condition);
            SymbolTable saved = currentSymbolTable;
            currentSymbolTable = new SymbolTable(currentSymbolTable);
            for (ASTNode stmt : whileStmt.body) analyzeStatement(stmt);
            currentSymbolTable = saved;
        }
    }

    private void analyzeExpression(ASTNode node) throws CompilerException {
        if (node instanceof Variable) {
            Variable var = (Variable) node;
            if (currentSymbolTable.lookup(var.name) == null) {
                throw new CompilerException("Undefined variable: " + var.name);
            }
        } else if (node instanceof BinaryOp) {
            BinaryOp op = (BinaryOp) node;
            analyzeExpression(op.left);
            analyzeExpression(op.right);
        } else if (node instanceof UnaryOp) {
            UnaryOp op = (UnaryOp) node;
            analyzeExpression(op.operand);
        }
    }
}

