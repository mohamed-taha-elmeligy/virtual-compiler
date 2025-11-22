package com.emts.vitrualcompiler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    // UI Components
    @FXML private TextArea codeEditor;
    @FXML private TextArea compilationOutput;
    @FXML private TextArea tokensOutput;
    @FXML private TextArea astOutput;
    @FXML private TextArea programOutput;
    @FXML private TextArea errorsOutput;
    @FXML private ProgressBar compilationProgress;
    @FXML private Label statusLabel;
    @FXML private Label timeLabel;
    @FXML private Label errorCountLabel;
    @FXML private Label lineCountLabel;

    /**
     * تهيئة المكونات عند تحميل FXML
     */
    @FXML
    public void initialize() {
        setupCodeEditor();
        setupOutputAreas();
        updateLineCount();
    }

    /**
     * تهيئة محرر الأكواد مع مراقبة التغييرات
     */
    private void setupCodeEditor() {
        // مراقبة التغييرات في محرر الأكواد
        codeEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineCount();
        });

        // تفعيل الـ Wrapping
        codeEditor.setWrapText(true);
    }

    /**
     * تهيئة منطقات الإخراج
     */
    private void setupOutputAreas() {
        // تعطيل التعديل على جميع منطقات الإخراج
        compilationOutput.setEditable(false);
        tokensOutput.setEditable(false);
        astOutput.setEditable(false);
        programOutput.setEditable(false);
        errorsOutput.setEditable(false);

        // تفعيل الـ Wrapping
        compilationOutput.setWrapText(true);
        tokensOutput.setWrapText(true);
        astOutput.setWrapText(true);
        programOutput.setWrapText(true);
        errorsOutput.setWrapText(true);
    }

    /**
     * تحديث عدد الأسطر والأحرف
     */
    private void updateLineCount() {
        String text = codeEditor.getText();
        int lineCount = text.isEmpty() ? 1 : text.split("\n", -1).length;
        int charCount = text.length();
        lineCountLabel.setText(String.format("Lines: %d | Chars: %d", lineCount, charCount));
    }

    /**
     * معالج زر "New" - ملف جديد
     */
    @FXML
    private void handleNew() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("New File");
        confirmation.setHeaderText("Create a new file?");
        confirmation.setContentText("Do you want to create a new file?");

        if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.OK) {
            codeEditor.clear();
            clearAllOutput();
            statusLabel.setText("New file created");
        }
    }

    /**
     * معالج زر "Open" - فتح ملف
     */
    @FXML
    private void handleOpen() {
        statusLabel.setText("Open file functionality - To be implemented");
        // TODO: تطبيق فتح الملف
    }

    /**
     * معالج زر "Save" - حفظ الملف
     */
    @FXML
    private void handleSave() {
        statusLabel.setText("Save functionality - To be implemented");
        // TODO: تطبيق حفظ الملف
    }

    /**
     * معالج زر "Run" - تشغيل البرنامج
     */
    @FXML
    private void handleRun() {
        String code = codeEditor.getText();
        if (code.trim().isEmpty()) {
            statusLabel.setText("No code to compile");
            errorCountLabel.setText("Errors: 1");
            errorsOutput.setText("Error: Code editor is empty!");
            return;
        }

        statusLabel.setText("Compiling...");
        compilationProgress.setProgress(0.3);

        // محاكاة عملية الترجمة
        simulateCompilation(code);
    }

    /**
     * معالج زر "Stop" - إيقاف البرنامج
     */
    @FXML
    private void handleStop() {
        statusLabel.setText("Compilation stopped");
        compilationProgress.setProgress(0);
    }

    /**
     * معالج زر "Clear" - مسح الإخراج
     */
    @FXML
    private void handleClear() {
        clearAllOutput();
        statusLabel.setText("Output cleared");
    }

    /**
     * مسح جميع منطقات الإخراج
     */
    private void clearAllOutput() {
        compilationOutput.clear();
        tokensOutput.clear();
        astOutput.clear();
        programOutput.clear();
        errorsOutput.clear();
        compilationProgress.setProgress(0);
        errorCountLabel.setText("Errors: 0");
        timeLabel.setText("Time: 0ms");
    }

    /**
     * محاكاة عملية الترجمة
     */
    private void simulateCompilation(String code) {
        long startTime = System.currentTimeMillis();

        // Compilation Output
        compilationOutput.setText("Starting compilation...\n" +
                "Lexical Analysis: OK\n" +
                "Syntax Analysis: OK\n" +
                "Semantic Analysis: OK\n" +
                "Code Generation: OK");
        compilationProgress.setProgress(0.5);

        // Tokens Output
        tokensOutput.setText("Tokens found:\n" +
                "Token 1: IDENTIFIER (\"variable\")\n" +
                "Token 2: ASSIGN (\"=\")\n" +
                "Token 3: NUMBER (\"42\")\n" +
                "Token 4: SEMICOLON (\";\")");
        compilationProgress.setProgress(0.7);

        // AST Output
        astOutput.setText("Abstract Syntax Tree:\n" +
                "Program\n" +
                "├── Statement\n" +
                "│   ├── Identifier: variable\n" +
                "│   └── Value: 42\n");
        compilationProgress.setProgress(0.9);

        // Program Output
        programOutput.setText("Program output:\n" +
                "Hello from compiler!\n" +
                "Variable value: 42\n" +
                "Compilation successful!\n");

        // Time calculation
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        timeLabel.setText("Time: " + executionTime + "ms");

        // Final status
        compilationProgress.setProgress(1.0);
        statusLabel.setText("Compilation successful!");
        errorCountLabel.setText("Errors: 0");
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
        // hello mohamed
    }
}