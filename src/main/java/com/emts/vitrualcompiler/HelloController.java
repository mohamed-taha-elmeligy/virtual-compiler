package com.emts.vitrualcompiler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML private TextArea codeEditor;
    @FXML private TextArea compilationOutput;
    @FXML private TextArea tokensOutput;
    @FXML private TextArea astOutput;
    @FXML private TextArea programOutput;
    @FXML private TextArea errorsOutput;
    @FXML private Label lineCountLabel;
    @FXML private Label statusLabel;
    @FXML private Label timeLabel;
    @FXML private Label errorCountLabel;
    @FXML private ProgressBar compilationProgress;

    private Thread compilationThread;

    @FXML
    public void initialize() {
        // مراقبة التغييرات في code editor
        codeEditor.textProperty().addListener((obs, oldVal, newVal) -> {
            updateLineCount();
            updateSyntaxHighlighting();
        });

        // تعيين نص افتراضي للاختبار
        codeEditor.setText("// اكتب الكود هنا\n" +
                "void main() {\n" +
                "    print(\"Hello World\");\n" +
                "}");
    }

    private void updateLineCount() {
        int lines = codeEditor.getText().split("\n").length;
        int chars = codeEditor.getText().length();
        lineCountLabel.setText("Lines: " + lines + " | Chars: " + chars);
    }

    private void updateSyntaxHighlighting() {
        // هنا تقدر تضيف syntax highlighting logic
        // أو تستخدم مكتبات زي RichTextFX
    }

    @FXML
    public void handleRun() {
        if (compilationThread != null && compilationThread.isAlive()) {
            showAlert("Compilation already running!");
            return;
        }

        compilationThread = new Thread(() -> {
            try {
                statusLabel.setText("Running...");
                compilationOutput.clear();
                tokensOutput.clear();
                astOutput.clear();
                programOutput.clear();
                errorsOutput.clear();
                compilationProgress.setProgress(-1); // Indeterminate

                String sourceCode = codeEditor.getText();

                // مرحلة 1: Lexical Analysis (Tokenization)
                performLexicalAnalysis(sourceCode);

                // مرحلة 2: Syntax Analysis (Parsing)
                performSyntaxAnalysis(sourceCode);

                // مرحلة 3: Semantic Analysis
                performSemanticAnalysis(sourceCode);

                // مرحلة 4: Code Generation & Execution
                performCodeGeneration(sourceCode);

                Platform.runLater(() -> {
                    statusLabel.setText("Completed ✓");
                    compilationProgress.setProgress(1.0);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorsOutput.appendText("ERROR: " + e.getMessage() + "\n");
                    statusLabel.setText("Failed ✗");
                    compilationProgress.setProgress(0);
                });
            }
        });

        compilationThread.setDaemon(true);
        compilationThread.start();
    }

    private void performLexicalAnalysis(String sourceCode) {
        Platform.runLater(() -> {
            compilationOutput.appendText("[LEXICAL ANALYSIS] Tokenizing source code...\n");
            compilationOutput.appendText("━".repeat(50) + "\n");
        });

        // محاكاة تحليل المفردات
        String[] tokens = sourceCode.split("\\s+|[(){};,]");
        StringBuilder tokenList = new StringBuilder();

        for (String token : tokens) {
            if (!token.isEmpty()) {
                String type = getTokenType(token);
                tokenList.append(String.format("%-20s -> %s\n", token, type));
            }
        }

        Platform.runLater(() -> {
            compilationOutput.appendText("[✓] Found " + tokens.length + " tokens\n\n");
            tokensOutput.setText(tokenList.toString());
            compilationProgress.setProgress(0.25);
        });
    }

    private void performSyntaxAnalysis(String sourceCode) {
        Platform.runLater(() -> {
            compilationOutput.appendText("[SYNTAX ANALYSIS] Building parse tree...\n");
            compilationOutput.appendText("━".repeat(50) + "\n");
        });

        // محاكاة بناء الشجرة
        String ast = generateAST(sourceCode);

        Platform.runLater(() -> {
            compilationOutput.appendText("[✓] Syntax tree generated\n\n");
            astOutput.setText(ast);
            compilationProgress.setProgress(0.50);
        });
    }

    private void performSemanticAnalysis(String sourceCode) {
        Platform.runLater(() -> {
            compilationOutput.appendText("[SEMANTIC ANALYSIS] Type checking & validation...\n");
            compilationOutput.appendText("━".repeat(50) + "\n");
        });

        // محاكاة الفحص الدلالي
        boolean hasErrors = sourceCode.contains("ERROR") || sourceCode.contains("error");

        if (hasErrors) {
            Platform.runLater(() -> {
                compilationOutput.appendText("[⚠] Semantic errors found\n");
                errorsOutput.setText("Semantic Error: Invalid operation\n");
                errorCountLabel.setText("Errors: 1");
            });
        } else {
            Platform.runLater(() -> {
                compilationOutput.appendText("[✓] All semantic checks passed\n\n");
                errorCountLabel.setText("Errors: 0");
                compilationProgress.setProgress(0.75);
            });
        }
    }

    private void performCodeGeneration(String sourceCode) {
        Platform.runLater(() -> {
            compilationOutput.appendText("[CODE GENERATION] Generating executable code...\n");
            compilationOutput.appendText("━".repeat(50) + "\n");

            long startTime = System.currentTimeMillis();

            // محاكاة التنفيذ
            programOutput.appendText(">>> Program Output:\n");
            programOutput.appendText("Hello World\n");
            programOutput.appendText("Process completed successfully\n");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            compilationOutput.appendText("[✓] Code generation completed\n");
            compilationOutput.appendText("Execution time: " + duration + "ms\n");
            timeLabel.setText("Time: " + duration + "ms");
            compilationProgress.setProgress(1.0);
        });
    }

    private String generateAST(String sourceCode) {
        StringBuilder ast = new StringBuilder();
        ast.append("Program\n");
        ast.append("├── Function: main\n");
        ast.append("│   ├── Return Type: void\n");
        ast.append("│   └── Body\n");
        ast.append("│       └── CallExpression: print\n");
        ast.append("│           └── Argument: \"Hello World\"\n");
        return ast.toString();
    }

    private String getTokenType(String token) {
        if (token.matches("\\d+")) return "NUMBER";
        if (token.matches("\".*\"")) return "STRING";
        if (token.matches("void|int|string|bool")) return "KEYWORD";
        if (token.matches("[(){};,]")) return "OPERATOR";
        return "IDENTIFIER";
    }

    @FXML
    public void handleNew() {
        codeEditor.clear();
        statusLabel.setText("New file created");
    }

    @FXML
    public void handleOpen() {
        // يمكنك إضافة FileChooser هنا
        statusLabel.setText("Open file dialog");
    }

    @FXML
    public void handleSave() {
        try {
            Files.write(Paths.get("code.txt"), codeEditor.getText().getBytes());
            statusLabel.setText("File saved successfully");
        } catch (IOException e) {
            showAlert("Error saving file: " + e.getMessage());
        }
    }

    @FXML
    public void handleStop() {
        if (compilationThread != null && compilationThread.isAlive()) {
            compilationThread.interrupt();
            statusLabel.setText("Stopped");
        }
    }

    @FXML
    public void handleClear() {
        compilationOutput.clear();
        tokensOutput.clear();
        astOutput.clear();
        programOutput.clear();
        errorsOutput.clear();
        statusLabel.setText("Cleared");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}