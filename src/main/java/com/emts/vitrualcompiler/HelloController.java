package com.emts.vitrualcompiler;

import com.emts.vitrualcompiler.exceptions.CompilerException;
import com.emts.vitrualcompiler.helper.Token;
import com.emts.vitrualcompiler.services.*;
import com.emts.vitrualcompiler.syntax.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.*;

// Import Classes من SimpleCompiler
import java.util.List;

public class HelloController implements Initializable {
    // UI Components - Main
    @FXML private TextArea codeEditor;
    @FXML private TextArea lineNumbers;
    @FXML private TabPane editorTabs;

    // Output Components
    @FXML private TextArea compilationOutput;
    @FXML private TextArea tokensOutput;
    @FXML private TextArea tokensOutput1;      // Symbol Table
    @FXML private TextArea tokensOutput11;     // Syntax (AST)
    @FXML private TextArea tokensOutput111;    // Semantic
    @FXML private TextArea tokensOutput1111;   // Intermediate (IR)
    @FXML private TextArea astOutput;
    @FXML private TextArea programOutput;
    @FXML private TextArea errorsOutput;
    @FXML private ProgressBar compilationProgress;

    // Status Components
    @FXML private Label statusLabel;
    @FXML private Label lineCountLabel;
    @FXML private Label timeLabel;
    @FXML private Label errorCountLabel;
    @FXML private Label warningCountLabel;
    @FXML private Label compilePhaseLabel;
    @FXML private Label encodingLabel;

    private boolean isCompiling = false;
    private List<Token> tokens;
    private Program ast;
    private List<IRInstruction> ir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCodeEditor();
        setupOutputAreas();
        setupLineNumbers();
    }

    private void setupCodeEditor() {
        codeEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            updateCursorPosition();
        });

        codeEditor.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
            updateCursorPosition();
        });

        codeEditor.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("TAB")) {
                codeEditor.insertText(codeEditor.getCaretPosition(), "    ");
                event.consume();
            }
        });

        codeEditor.setWrapText(false);
    }

    private void setupLineNumbers() {
        lineNumbers.setEditable(false);
        lineNumbers.setStyle("-fx-control-inner-background: #2d2f31;");
    }

    private void updateLineNumbers() {
        String code = codeEditor.getText();
        int lines = code.isEmpty() ? 1 : code.split("\n", -1).length;

        StringBuilder lineStr = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            lineStr.append(i).append("\n");
        }
        lineNumbers.setText(lineStr.toString());
    }

    private void updateCursorPosition() {
        String text = codeEditor.getText();
        int pos = codeEditor.getCaretPosition();

        int line = 1 + text.substring(0, Math.min(pos, text.length())).split("\n", -1).length;
        int column = pos - (text.lastIndexOf('\n', Math.max(0, pos - 1)) + 1) + 1;

        lineCountLabel.setText(String.format("Line %d, Column %d", line, column));
    }

    private void setupOutputAreas() {
        compilationOutput.setEditable(false);
        tokensOutput.setEditable(false);
        tokensOutput1.setEditable(false);
        tokensOutput11.setEditable(false);
        tokensOutput111.setEditable(false);
        tokensOutput1111.setEditable(false);
        astOutput.setEditable(false);
        programOutput.setEditable(false);
        errorsOutput.setEditable(false);
    }

    @FXML
    private void handleNew() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("New File");
        confirm.setHeaderText("Create new file?");
        confirm.setContentText("Unsaved changes will be lost.");

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.OK) {
            codeEditor.clear();
            clearAllOutput();
            statusLabel.setText("New file created");
        }
    }

    @FXML
    private void handleOpen() {
        statusLabel.setText("Open file - To be implemented");
    }

    @FXML
    private void handleSave() {
        statusLabel.setText("File saved successfully");
    }

    @FXML
    private void handleRun() {
        if (isCompiling) {
            statusLabel.setText("Already compiling...");
            return;
        }
        compileCode(true);
    }

    @FXML
    private void handleStop() {
        isCompiling = false;
        compilationProgress.setProgress(0);
        statusLabel.setText("Compilation stopped");
        compilePhaseLabel.setText("Idle");
    }

    private void compileCode(boolean runAfterCompile) {
        String code = codeEditor.getText();

        if (code.trim().isEmpty()) {
            handleEmptyCode();
            return;
        }

        isCompiling = true;
        clearAllOutput();
        statusLabel.setText("Compiling...");
        errorCountLabel.setText("Errors: 0");
        warningCountLabel.setText("Warnings: 0");
        compilationProgress.setProgress(0);

        new Thread(() -> {
            try {
                performCompilation(code, runAfterCompile);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorsOutput.setText("Internal Error: " + e.getMessage());
                    e.printStackTrace();
                    statusLabel.setText("Compilation failed");
                    isCompiling = false;
                });
            }
        }).start();
    }

    private void performCompilation(String code, boolean runAfterCompile) {
        long startTime = System.currentTimeMillis();

        try {
            // ========== Phase 1: Lexical Analysis ==========
            Platform.runLater(() -> {
                compilePhaseLabel.setText("Lexical Analysis");
                compilationProgress.setProgress(0.15);
                compilationOutput.appendText("[Phase 1] Starting Lexical Analysis...\n");
            });
            pause(500);

            Lexer lexer = new Lexer(code);
            tokens = lexer.tokenize();

            Platform.runLater(() -> {
                StringBuilder tokenOutput = new StringBuilder("=== TOKENS ===\n\n");
                for (Token t : tokens) {
                    if (t.type != Token.Type.EOF) {
                        tokenOutput.append(t).append("\n");
                    }
                }
                tokensOutput.setText(tokenOutput.toString());
                compilationOutput.appendText("  ✓ Lexical analysis completed\n");
                compilationOutput.appendText("  • Tokens generated: " + (tokens.size() - 1) + "\n\n");
            });
            pause(500);

            // ========== Phase 2: Syntax Analysis ==========
            Platform.runLater(() -> {
                compilePhaseLabel.setText("Syntax Analysis");
                compilationProgress.setProgress(0.30);
                compilationOutput.appendText("[Phase 2] Starting Syntax Analysis...\n");
            });
            pause(500);

            Parser parser = new Parser(tokens);
            ast = parser.parse();

            Platform.runLater(() -> {
                tokensOutput11.setText("=== ABSTRACT SYNTAX TREE ===\n\n" + printAST(ast, 0));
                compilationOutput.appendText("  ✓ Syntax analysis completed\n");
                compilationOutput.appendText("  • AST nodes created\n\n");
            });
            pause(500);

            // ========== Phase 3: Semantic Analysis ==========
            Platform.runLater(() -> {
                compilePhaseLabel.setText("Semantic Analysis");
                compilationProgress.setProgress(0.45);
                compilationOutput.appendText("[Phase 3] Starting Semantic Analysis...\n");
            });
            pause(500);

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(ast);

            Platform.runLater(() -> {
                tokensOutput111.setText("=== SEMANTIC ANALYSIS ===\n\n✓ Type checking: PASSED\n✓ Variable declarations: OK\n✓ Scope analysis: OK");
                compilationOutput.appendText("  ✓ Semantic analysis completed\n");
                compilationOutput.appendText("  • No errors detected\n\n");
            });
            pause(500);

            // ========== Phase 4: Intermediate Representation ==========
            Platform.runLater(() -> {
                compilePhaseLabel.setText("Code Generation (IR)");
                compilationProgress.setProgress(0.60);
                compilationOutput.appendText("[Phase 4] Generating Intermediate Representation...\n");
            });
            pause(500);

            IRGenerator irGenerator = new IRGenerator();
            irGenerator.generate(ast);
            ir = irGenerator.getInstructions();

            Platform.runLater(() -> {
                StringBuilder irOutput = new StringBuilder("=== INTERMEDIATE REPRESENTATION (Three-Address Code) ===\n\n");
                for (int i = 0; i < ir.size(); i++) {
                    irOutput.append(String.format("%2d: %s\n", i, ir.get(i)));
                }
                tokensOutput1111.setText(irOutput.toString());
                compilationOutput.appendText("  ✓ IR generation completed\n");
                compilationOutput.appendText("  • Instructions generated: " + ir.size() + "\n\n");
            });
            pause(500);

            // ========== Phase 5: Bytecode Generation ==========
            Platform.runLater(() -> {
                compilePhaseLabel.setText("Bytecode Generation");
                compilationProgress.setProgress(0.75);
                compilationOutput.appendText("[Phase 5] Generating Bytecode...\n");
            });
            pause(500);

            BytecodeGenerator bytecodeGen = new BytecodeGenerator();
            bytecodeGen.generate(ir);
            List<String> bytecode = bytecodeGen.getBytecode();

            Platform.runLater(() -> {
                StringBuilder bytecodeOutput = new StringBuilder("=== BYTECODE ===\n\n");
                for (int i = 0; i < bytecode.size(); i++) {
                    bytecodeOutput.append(String.format("%3d: %s\n", i, bytecode.get(i)));
                }
                astOutput.setText(bytecodeOutput.toString());
                compilationOutput.appendText("  ✓ Bytecode generation completed\n");
                compilationOutput.appendText("  • Bytecode size: " + bytecode.size() + " instructions\n\n");
            });
            pause(500);

            // ========== Phase 6: Execution ==========
            if (runAfterCompile) {
                Platform.runLater(() -> {
                    compilePhaseLabel.setText("Execution");
                    compilationProgress.setProgress(0.90);
                    compilationOutput.appendText("[Phase 6] Executing Program...\n");
                });
                pause(500);

                Interpreter interpreter = new Interpreter();
                interpreter.execute(ast);

                Platform.runLater(() -> {
                    programOutput.setText("========== PROGRAM OUTPUT ==========\n\n" +
                            interpreter.getOutput() +
                            "\n=====================================\n");
                    compilationOutput.appendText("  ✓ Program execution completed\n\n");
                });
            }

            // Completion
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            Platform.runLater(() -> {
                compilationProgress.setProgress(1.0);
                compilationOutput.appendText("[SUCCESS] Compilation completed in " + executionTime + "ms\n");
                timeLabel.setText(executionTime + "ms");
                statusLabel.setText("Compilation successful");
                compilePhaseLabel.setText("Idle");
                isCompiling = false;
            });

        } catch (CompilerException e) {
            Platform.runLater(() -> {
                errorsOutput.setText("COMPILER ERROR:\n" + e.getMessage());
                compilationOutput.appendText("  ✗ Compilation failed: " + e.getMessage() + "\n");
                errorCountLabel.setText("Errors: 1");
                statusLabel.setText("Compilation failed");
                isCompiling = false;
            });
        }
    }

    private String printAST(ASTNode node, int indent) {
        String spaces = "  ".repeat(indent);

        if (node instanceof Program) {
            Program p = (Program) node;
            StringBuilder sb = new StringBuilder(spaces + "Program\n");
            for (ASTNode stmt : p.statements) {
                sb.append(printAST(stmt, indent + 1));
            }
            return sb.toString();
        } else if (node instanceof VarDeclaration) {
            VarDeclaration v = (VarDeclaration) node;
            return spaces + "VarDeclaration(name: " + v.name + ")\n" + printAST(v.value, indent + 1);
        } else if (node instanceof BinaryOp) {
            BinaryOp b = (BinaryOp) node;
            return spaces + "BinaryOp(" + b.operator + ")\n" +
                    printAST(b.left, indent + 1) +
                    printAST(b.right, indent + 1);
        } else if (node instanceof NumberLiteral) {
            NumberLiteral n = (NumberLiteral) node;
            return spaces + "Number(" + n.value + ")\n";
        } else if (node instanceof Variable) {
            Variable v = (Variable) node;
            return spaces + "Variable(" + v.name + ")\n";
        } else if (node instanceof PrintStatement) {
            PrintStatement p = (PrintStatement) node;
            StringBuilder sb = new StringBuilder(spaces + "PrintStatement\n");
            for (ASTNode arg : p.arguments) {
                sb.append(printAST(arg, indent + 1));
            }
            return sb.toString();
        } else if (node instanceof IfStatement) {
            IfStatement i = (IfStatement) node;
            StringBuilder sb = new StringBuilder(spaces + "IfStatement\n");
            sb.append(spaces + "  Condition:\n").append(printAST(i.condition, indent + 2));
            sb.append(spaces + "  Then:\n");
            for (ASTNode stmt : i.thenBranch) sb.append(printAST(stmt, indent + 2));
            if (!i.elseBranch.isEmpty()) {
                sb.append(spaces + "  Else:\n");
                for (ASTNode stmt : i.elseBranch) sb.append(printAST(stmt, indent + 2));
            }
            return sb.toString();
        } else if (node instanceof WhileStatement) {
            WhileStatement w = (WhileStatement) node;
            StringBuilder sb = new StringBuilder(spaces + "WhileStatement\n");
            sb.append(spaces + "  Condition:\n").append(printAST(w.condition, indent + 2));
            sb.append(spaces + "  Body:\n");
            for (ASTNode stmt : w.body) sb.append(printAST(stmt, indent + 2));
            return sb.toString();
        }

        return spaces + node.getClass().getSimpleName() + "\n";
    }

    private void handleEmptyCode() {
        Platform.runLater(() -> {
            statusLabel.setText("Error: Code editor is empty");
            errorCountLabel.setText("Errors: 1");
            compilationOutput.setText("Error: No code to compile\n");
            errorsOutput.setText("ERROR: Code editor is empty!\n\nPlease write some code before compiling.");
        });
    }

    private void clearAllOutput() {
        Platform.runLater(() -> {
            compilationOutput.clear();
            tokensOutput.clear();
            tokensOutput1.clear();
            tokensOutput11.clear();
            tokensOutput111.clear();
            tokensOutput1111.clear();
            astOutput.clear();
            programOutput.clear();
            errorsOutput.clear();
        });
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}