module com.emts.vitrualcompiler {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.emts.vitrualcompiler to javafx.fxml;
    exports com.emts.vitrualcompiler;
}