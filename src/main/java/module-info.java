module edu.srjc.cabinian.brian.final_cabinian_brian {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.srjc.cabinian.brian.final_cabinian_brian to javafx.fxml;
    exports edu.srjc.cabinian.brian.final_cabinian_brian;
}