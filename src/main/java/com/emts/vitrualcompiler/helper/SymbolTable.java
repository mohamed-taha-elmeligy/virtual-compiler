package com.emts.vitrualcompiler.helper;

import com.emts.vitrualcompiler.exceptions.CompilerException;

import java.util.HashMap;
import java.util.Map;

/**
 * *******************************************************************
 * File: null.java
 * Package: com.emts.vitrualcompiler.helper
 * Project: eMTS Smart Attendance System
 * © ٢٠٢٥ Mohamed Taha Elmeligy - eMTS (e Modern Tech Solutions)
 * This file is part of the eMTS Smart Attendance System.
 * Created on: 24/11/2025
 * Port Number: 8083
 * *******************************************************************
 */

public class SymbolTable {
    public static class Symbol {
        String name;
        boolean initialized;
        int line;

        Symbol(String name, int line) {
            this.name = name;
            this.line = line;
            this.initialized = false;
        }
    }

    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable parent;

    public SymbolTable() { this.parent = null; }
    public SymbolTable(SymbolTable parent) { this.parent = parent; }

    public void define(String name, int line) throws CompilerException {
        if (symbols.containsKey(name)) {
            throw new CompilerException("Symbol '" + name + "' already defined at line " + line);
        }
        symbols.put(name, new Symbol(name, line));
    }

    public Symbol lookup(String name) {
        if (symbols.containsKey(name)) return symbols.get(name);
        if (parent != null) return parent.lookup(name);
        return null;
    }
}

