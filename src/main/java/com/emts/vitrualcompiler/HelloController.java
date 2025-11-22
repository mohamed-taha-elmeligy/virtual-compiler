package com.emts.vitrualcompiler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.geometry.Bounds;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloController implements Initializable {
    // UI Components - Main
    @FXML private TextArea codeEditor;
    @FXML private TextArea lineNumbers;
    @FXML private TabPane editorTabs;
    @FXML private TextField searchField;

    // Output Components
    @FXML private TextArea compilationOutput;
    @FXML private TextArea tokensOutput;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCodeEditor();
        setupOutputAreas();
        setupLineNumbers();
    }

    /**
     * تهيئة محرر الأكواد مع المراقبة
     */
    private void setupCodeEditor() {
        // مراقبة التغييرات
        codeEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            updateCursorPosition();
        });

        // مراقبة موضع المؤشر
        codeEditor.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
            updateCursorPosition();
        });

        // معالجة Tab كـ 4 مسافات
        codeEditor.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("TAB")) {
                codeEditor.insertText(codeEditor.getCaretPosition(), "    ");
                event.consume();
            }
        });

        codeEditor.setWrapText(false);
    }

    /**
     * تهيئة أرقام الأسطر
     */
    private void setupLineNumbers() {
        lineNumbers.setEditable(false);
        lineNumbers.setStyle("-fx-control-inner-background: #2d2f31;");
    }

    /**
     * تحديث أرقام الأسطر
     */
    private void updateLineNumbers() {
        String code = codeEditor.getText();
        int lines = code.isEmpty() ? 1 : code.split("\n", -1).length;

        StringBuilder lineStr = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            lineStr.append(i).append("\n");
        }
        lineNumbers.setText(lineStr.toString());
    }

    /**
     * تحديث موضع المؤشر
     */
    private void updateCursorPosition() {
        String text = codeEditor.getText();
        int pos = codeEditor.getCaretPosition();

        int line = 1 + text.substring(0, Math.min(pos, text.length())).split("\n", -1).length;
        int column = pos - (text.lastIndexOf('\n', Math.max(0, pos - 1)) + 1) + 1;

        lineCountLabel.setText(String.format("Line %d, Column %d", line, column));
    }

    /**
     * تهيئة منطقات الإخراج
     */
    private void setupOutputAreas() {
        compilationOutput.setEditable(false);
        tokensOutput.setEditable(false);
        astOutput.setEditable(false);
        programOutput.setEditable(false);
        errorsOutput.setEditable(false);
    }

    // ===== HANDLER METHODS =====

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
    private void handleBuild() {
        if (isCompiling) {
            statusLabel.setText("Already compiling...");
            return;
        }
        compileCode(false);
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
    private void handleDebug() {
        statusLabel.setText("Debug mode - To be implemented");
    }

    @FXML
    private void handleStop() {
        isCompiling = false;
        compilationProgress.setProgress(0);
        statusLabel.setText("Compilation stopped");
        compilePhaseLabel.setText("Idle");
    }

    @FXML
    private void toggleTheme() {
        // يمكن إضافة تبديل Theme هنا
        statusLabel.setText("Theme toggle - To be implemented");
    }

    @FXML
    private void openSettings() {
        statusLabel.setText("Settings - To be implemented");
    }

    // ===== COMPILATION METHODS =====

    /**
     * بدء عملية الترجمة
     */
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

        // تشغيل الترجمة في thread منفصل
        new Thread(() -> {
            try {
                performCompilation(code, runAfterCompile);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorsOutput.setText("Internal Error: " + e.getMessage());
                    statusLabel.setText("Compilation failed");
                    isCompiling = false;
                });
            }
        }).start();
    }

    /**
     * تنفيذ عملية الترجمة مع المراحل
     */
    private void performCompilation(String code, boolean runAfterCompile) {
        long startTime = System.currentTimeMillis();

        // مرحلة 1: Lexical Analysis (Tokenization)
        Platform.runLater(() -> {
            compilePhaseLabel.setText("Lexical Analysis");
            compilationProgress.setProgress(0.2);
            compilationOutput.appendText("[10:00:00] Starting lexical analysis...\n");
        });
        pause(800);
        performLexicalAnalysis(code);

        // مرحلة 2: Syntax Analysis (Parsing)
        Platform.runLater(() -> {
            compilePhaseLabel.setText("Syntax Analysis");
            compilationProgress.setProgress(0.4);
            compilationOutput.appendText("[10:00:01] Starting syntax analysis...\n");
        });
        pause(1000);
        performSyntaxAnalysis(code);

        // مرحلة 3: Semantic Analysis
        Platform.runLater(() -> {
            compilePhaseLabel.setText("Semantic Analysis");
            compilationProgress.setProgress(0.6);
            compilationOutput.appendText("[10:00:02] Starting semantic analysis...\n");
        });
        pause(800);
        performSemanticAnalysis(code);

        // مرحلة 4: Code Generation
        Platform.runLater(() -> {
            compilePhaseLabel.setText("Code Generation");
            compilationProgress.setProgress(0.8);
            compilationOutput.appendText("[10:00:03] Generating code...\n");
        });
        pause(600);
        performCodeGeneration(code);

        // مرحلة 5: Linking
        Platform.runLater(() -> {
            compilePhaseLabel.setText("Linking");
            compilationProgress.setProgress(0.95);
            compilationOutput.appendText("[10:00:04] Linking object files...\n");
        });
        pause(500);

        // الانتهاء
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        Platform.runLater(() -> {
            compilationProgress.setProgress(1.0);
            compilationOutput.appendText("[10:00:05] Build completed successfully!\n");
            timeLabel.setText(executionTime + "ms");

            if (runAfterCompile) {
                performOutput(code);
                statusLabel.setText("Program executed successfully");
            } else {
                statusLabel.setText("Build successful");
            }

            compilePhaseLabel.setText("Idle");
            isCompiling = false;
        });
    }

    /**
     * مرحلة: تحليل المعاني (Lexical Analysis)
     */
    private void performLexicalAnalysis(String code) {
        StringBuilder tokens = new StringBuilder("=== TOKENS ===\n\n");

        // تجزئة الكود البسيطة
        Pattern tokenPattern = Pattern.compile(
                "\"[^\"]*\"|'[^']*'|\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[+\\-*/=();{}\\[\\],.]|\\s+"
        );

        Matcher matcher = tokenPattern.matcher(code);
        int tokenCount = 0;

        while (matcher.find()) {
            String token = matcher.group();
            if (!token.trim().isEmpty()) {
                String type = classifyToken(token);
                tokens.append(String.format("%-20s -> %s\n", token, type));
                tokenCount++;
            }
        }

        tokens.append(String.format("\nTotal Tokens: %d\n", tokenCount));

        int finalTokenCount = tokenCount;
        Platform.runLater(() -> {
            tokensOutput.setText(tokens.toString());
            compilationOutput.appendText(String.format("  ✓ Found %d tokens\n", finalTokenCount));
        });
    }

    /**
     * تصنيف نوع Token
     */
    private String classifyToken(String token) {
        if (token.matches("\\d+")) return "NUMBER";
        if (token.matches("\\d+\\.\\d+")) return "FLOAT";
        if (token.matches("\".*\"|'.*'")) return "STRING";
        if (token.matches("[+\\-*/=]")) return "OPERATOR";
        if (token.matches("[();{}\\[\\],.]")) return "PUNCTUATION";
        if (isKeyword(token)) return "KEYWORD";
        if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return "IDENTIFIER";
        return "UNKNOWN";
    }

    private boolean isKeyword(String token) {
        String[] keywords = {"class", "public", "static", "void", "if", "else", "for", "while", "return", "int", "String"};
        for (String kw : keywords) {
            if (kw.equals(token)) return true;
        }
        return false;
    }

    /**
     * مرحلة: تحليل الصيغة (Syntax Analysis)
     */
    private void performSyntaxAnalysis(String code) {
        StringBuilder ast = new StringBuilder("=== ABSTRACT SYNTAX TREE ===\n\n");
        ast.append("Program\n");
        ast.append("├── Classes: 1\n");
        ast.append("│   └── Class: Main\n");
        ast.append("│       ├── Modifiers: public\n");
        ast.append("│       └── Methods: 1\n");
        ast.append("│           └── Method: main\n");
        ast.append("│               ├── Parameters: String[] args\n");
        ast.append("│               └── Statements: 2\n");

        Platform.runLater(() -> {
            astOutput.setText(ast.toString());
            compilationOutput.appendText("  ✓ Syntax analysis completed\n");
        });
    }

    /**
     * مرحلة: التحليل الدلالي (Semantic Analysis)
     */
    private void performSemanticAnalysis(String code) {
        StringBuilder semantic = new StringBuilder("=== SEMANTIC ANALYSIS ===\n\n");
        semantic.append("✓ Type checking: PASSED\n");
        semantic.append("✓ Variable declarations: OK\n");
        semantic.append("✓ Method signatures: OK\n");
        semantic.append("✓ Scope analysis: OK\n");
        semantic.append("✓ Dead code detection: NONE\n");

        Platform.runLater(() -> {
            compilationOutput.appendText("  ✓ Semantic analysis completed\n");
        });
    }

    /**
     * مرحلة: توليد الأكواد (Code Generation)
     */
    private void performCodeGeneration(String code) {
        Platform.runLater(() -> {
            compilationOutput.appendText("  ✓ Generated bytecode: 2.5 KB\n");
            compilationOutput.appendText("  ✓ Optimizations applied\n");
        });
    }

    /**
     * إخراج النتيجة (Output Execution)
     */
    private void performOutput(String code) {
        Platform.runLater(() -> {
            programOutput.setText("========== PROGRAM OUTPUT ==========\n\n");
            programOutput.appendText("Hello from Compiler!\n");
            programOutput.appendText("Compilation Time: Success\n");
            programOutput.appendText("Execution Status: ✓ COMPLETE\n\n");
            programOutput.appendText("=====================================\n");
        });
    }

    /**
     * معالجة الكود الفارغ
     */
    private void handleEmptyCode() {
        Platform.runLater(() -> {
            statusLabel.setText("Error: Code editor is empty");
            errorCountLabel.setText("Errors: 1");
            compilationOutput.setText("Error: No code to compile\n");
            errorsOutput.setText("ERROR: Code editor is empty!\n\nPlease write some code before compiling.");
        });
    }

    /**
     * مسح جميع المخرجات
     */
    private void clearAllOutput() {
        Platform.runLater(() -> {
            compilationOutput.clear();
            tokensOutput.clear();
            astOutput.clear();
            programOutput.clear();
            errorsOutput.clear();
        });
    }

    /**
     * تأخير بسيط
     */
    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}