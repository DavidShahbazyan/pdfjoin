package am.davsoft.pdfjoin.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private BooleanProperty processRunning = new SimpleBooleanProperty();
    private ListProperty<File> filesList = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private VBox processIndicatorLayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        processIndicatorLayer.visibleProperty().bind(processRunning);
        processIndicatorLayer.managedProperty().bind(processRunning);
    }
}
