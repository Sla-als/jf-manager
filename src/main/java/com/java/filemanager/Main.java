package com.java.filemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{ // Stage - окно
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml")); // / - тк /sample переместили в папку --
        primaryStage.setTitle("Java File Manager [SVNK]");
        primaryStage.setScene(new Scene(root, 1300, 600)); // размеры окна
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
