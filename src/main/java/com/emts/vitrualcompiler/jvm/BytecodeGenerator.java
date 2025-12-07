package com.emts.vitrualcompiler.jvm;

import com.emts.vitrualcompiler.services.IRInstruction;

import java.util.ArrayList;
import java.util.List;

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
public class BytecodeGenerator {
    private final List<String> bytecode = new ArrayList<>();

    public void generate(List<IRInstruction> ir) {
        for (IRInstruction instr : ir) {
            String bc = switch (instr.op) {
                case "ASSIGN" -> "ASSIGN " + instr.arg1 + " " + instr.result;
                case "+" -> "ADD " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "-" -> "SUB " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "*" -> "MUL " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "/" -> "DIV " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "%" -> "MOD " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "<" -> "LT " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case ">" -> "GT " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "<=" -> "LE " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case ">=" -> "GE " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "==" -> "EQ " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "!=" -> "NE " + instr.arg1 + " " + instr.arg2 + " " + instr.result;
                case "PRINT" -> "PRINT " + instr.arg1;
                case "JMP_FALSE" -> "JMP_FALSE " + instr.arg1 + " " + instr.result;
                case "JMP" -> "JMP " + instr.result;
                default -> instr.op;
            };
            bytecode.add(bc);
        }
    }

    public List<String> getBytecode() {
        return bytecode;
    }
}
