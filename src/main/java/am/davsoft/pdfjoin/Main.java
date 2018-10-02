package am.davsoft.pdfjoin;

import am.davsoft.pdfjoin.util.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        StackPane root = loader.load(getClass().getResourceAsStream("/views/main.fxml"));
        primaryStage.setTitle("PDF Join");
        primaryStage.getIcons().setAll(new Image("images/icon-mergepdf.png"));
        primaryStage.setMinHeight(root.getPrefHeight());
        primaryStage.setMinWidth(root.getPrefWidth());
        primaryStage.setOnCloseRequest(event -> Platform.exit());
        Scene rootScene = new Scene(root);
        rootScene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.F1)) {
                Dialogs.showAboutAppDialog(root);
            }
        });
        primaryStage.setScene(rootScene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.requestFocus();
    }
}
