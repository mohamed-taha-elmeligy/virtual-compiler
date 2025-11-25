package com.emts.vitrualcompiler.services;

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
public class IRInstruction {
    String op;
    String arg1;
    String arg2;
    String result;

    IRInstruction(String op, String arg1, String arg2, String result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        if (arg2 == null) {
            return String.format("%s = %s %s", result, op, arg1 == null ? "" : arg1);
        }
        return String.format("%s = %s %s %s", result, arg1, op, arg2);
    }
}
