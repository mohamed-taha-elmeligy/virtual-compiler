package com.emts.vitrualcompiler.syntax;

/**
 * *******************************************************************
 * File: null.java
 * Package: com.emts.vitrualcompiler.ast
 * Project: eMTS Smart Attendance System
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 25/11/2025
 * Port Number: 8083
 * *******************************************************************
 */
public class UnaryOp extends ASTNode {
    public String operator;
    public ASTNode operand;
    public UnaryOp(String op, ASTNode operand, int line) {
        super(line);
        this.operator = op;
        this.operand = operand;
    }
}
