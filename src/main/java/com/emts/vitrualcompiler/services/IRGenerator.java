package com.emts.vitrualcompiler.services;

import com.emts.vitrualcompiler.syntax.*;

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
public class IRGenerator {
    private List<IRInstruction> instructions = new ArrayList<>();
    private int tempCounter = 0;

    public void generate(Program program) {
        for (ASTNode stmt : program.statements) {
            generateStatement(stmt);
        }
    }

    private void generateStatement(ASTNode node) {
        if (node instanceof VarDeclaration) {
            VarDeclaration decl = (VarDeclaration) node;
            String tempVar = generateExpression(decl.value);
            addInstruction("ASSIGN", tempVar, null, decl.name);
        } else if (node instanceof Assignment) {
            Assignment assign = (Assignment) node;
            String tempVar = generateExpression(assign.value);
            addInstruction("ASSIGN", tempVar, null, assign.name);
        } else if (node instanceof PrintStatement) {
            PrintStatement print = (PrintStatement) node;
            for (ASTNode arg : print.arguments) {
                String tempVar = generateExpression(arg);
                addInstruction("PRINT", tempVar, null, null);
            }
        } else if (node instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) node;
            String condVar = generateExpression(ifStmt.condition);
            int jumpFalse = instructions.size();
            addInstruction("JMP_FALSE", condVar, null, "?");
            for (ASTNode stmt : ifStmt.thenBranch) generateStatement(stmt);
            if (!ifStmt.elseBranch.isEmpty()) {
                int jumpEnd = instructions.size();
                instructions.set(jumpFalse, new IRInstruction("JMP_FALSE", condVar, null, String.valueOf(instructions.size())));
                for (ASTNode stmt : ifStmt.elseBranch) generateStatement(stmt);
                instructions.set(jumpEnd, new IRInstruction("JMP", null, null, String.valueOf(instructions.size())));
            } else {
                instructions.set(jumpFalse, new IRInstruction("JMP_FALSE", condVar, null, String.valueOf(instructions.size())));
            }
        } else if (node instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) node;
            int loopStart = instructions.size();
            String condVar = generateExpression(whileStmt.condition);
            int jumpFalse = instructions.size();
            addInstruction("JMP_FALSE", condVar, null, "?");
            for (ASTNode stmt : whileStmt.body) generateStatement(stmt);
            addInstruction("JMP", null, null, String.valueOf(loopStart));
            instructions.set(jumpFalse, new IRInstruction("JMP_FALSE", condVar, null, String.valueOf(instructions.size())));
        }
    }

    private String generateExpression(ASTNode node) {
        if (node instanceof NumberLiteral) {
            return String.valueOf(((NumberLiteral) node).value);
        } else if (node instanceof Variable) {
            return ((Variable) node).name;
        } else if (node instanceof BinaryOp) {
            BinaryOp op = (BinaryOp) node;
            String left = generateExpression(op.left);
            String right = generateExpression(op.right);
            String temp = getTemp();
            addInstruction(op.operator, left, right, temp);
            return temp;
        } else if (node instanceof UnaryOp) {
            UnaryOp op = (UnaryOp) node;
            String operand = generateExpression(op.operand);
            String temp = getTemp();
            addInstruction(op.operator, operand, null, temp);
            return temp;
        }
        return "";
    }

    private void addInstruction(String op, String arg1, String arg2, String result) {
        instructions.add(new IRInstruction(op, arg1, arg2, result));
    }

    private String getTemp() {
        return "t" + (tempCounter++);
    }

    public List<IRInstruction> getInstructions() {
        return instructions;
    }
}
