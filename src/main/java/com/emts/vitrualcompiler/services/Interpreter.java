package com.emts.vitrualcompiler.services;

import com.emts.vitrualcompiler.syntax.*;

import java.util.HashMap;
import java.util.Map;

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
public class Interpreter {
    private Map<String, Integer> variables = new HashMap<>();
    private StringBuilder output = new StringBuilder();

    public void execute(Program program) {
        for (ASTNode stmt : program.statements) {
            executeStatement(stmt);
        }
    }

    private void executeStatement(ASTNode node) {
        if (node instanceof VarDeclaration) {
            VarDeclaration decl = (VarDeclaration) node;
            int value = evaluateExpression(decl.value);
            variables.put(decl.name, value);
        } else if (node instanceof Assignment) {
            Assignment assign = (Assignment) node;
            int value = evaluateExpression(assign.value);
            variables.put(assign.name, value);
        } else if (node instanceof PrintStatement) {
            PrintStatement print = (PrintStatement) node;
            for (ASTNode arg : print.arguments) {
                int value = evaluateExpression(arg);
                output.append(value).append("\n");
            }
        } else if (node instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) node;
            if (evaluateExpression(ifStmt.condition) != 0) {
                for (ASTNode stmt : ifStmt.thenBranch) executeStatement(stmt);
            } else {
                for (ASTNode stmt : ifStmt.elseBranch) executeStatement(stmt);
            }
        } else if (node instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) node;
            while (evaluateExpression(whileStmt.condition) != 0) {
                for (ASTNode stmt : whileStmt.body) executeStatement(stmt);
            }
        }
    }

    private int evaluateExpression(ASTNode node) {
        if (node instanceof NumberLiteral) {
            return ((NumberLiteral) node).value;
        } else if (node instanceof Variable) {
            String name = ((Variable) node).name;
            return variables.getOrDefault(name, 0);
        } else if (node instanceof BinaryOp) {
            BinaryOp op = (BinaryOp) node;
            int left = evaluateExpression(op.left);
            int right = evaluateExpression(op.right);
            return switch (op.operator) {
                case "+" -> left + right;
                case "-" -> left - right;
                case "*" -> left * right;
                case "/" -> left / right;
                case "%" -> left % right;
                case "<" -> left < right ? 1 : 0;
                case ">" -> left > right ? 1 : 0;
                case "<=" -> left <= right ? 1 : 0;
                case ">=" -> left >= right ? 1 : 0;
                case "==" -> left == right ? 1 : 0;
                case "!=" -> left != right ? 1 : 0;
                default -> 0;
            };
        } else if (node instanceof UnaryOp) {
            UnaryOp op = (UnaryOp) node;
            int operand = evaluateExpression(op.operand);
            return switch (op.operator) {
                case "-" -> -operand;
                default -> operand;
            };
        }
        return 0;
    }

    public String getOutput() {
        return output.toString().isEmpty() ? "لا يوجد مخرجات" : output.toString();
    }
}