package com.emts.vitrualcompiler.syntax;

import java.util.ArrayList;
import java.util.List;

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
public class Program extends ASTNode{

    public List<ASTNode> statements = new ArrayList<>();

    public Program() {
        super(0);
    }
}
