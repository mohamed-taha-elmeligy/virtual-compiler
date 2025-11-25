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
public class BinaryOp extends ASTNode {
    public ASTNode left;
    public ASTNode right;
    public String operator;
    public BinaryOp(ASTNode left, String op, ASTNode right, int line) {
        super(line);
        this.left = left;
        this.operator = op;
        this.right = right;
    }
}
