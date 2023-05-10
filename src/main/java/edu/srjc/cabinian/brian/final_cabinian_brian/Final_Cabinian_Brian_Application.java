/*
Name: Brian Cabinian
Email: brian.cabinian@gmail.com
Date: 22-12-19
Project Name: Final_Cabinian_Brian
Course: CS 17.11A
Description: Application start for 2048 game
*/

package edu.srjc.cabinian.brian.final_cabinian_brian;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Final_Cabinian_Brian_Application extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Final_Cabinian_Brian_Application.class.getResource("Final_Cabinian_Brian_UI_Layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 850);
        scene.getRoot().requestFocus();
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}