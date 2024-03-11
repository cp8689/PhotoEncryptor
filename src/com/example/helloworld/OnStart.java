package com.example.helloworld;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class OnStart extends Application{
    Parent root;

    //Scene scene;
    private Stage stage;
    private AnchorPane mainLayout;
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;
    FXMLLoader loader = new FXMLLoader();
    String hash, name;
    byte[] salt;

    public OnStart() throws IOException {
    }

    //this is the main method
    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;
//       root = FXMLLoader.load(getClass().getResource("mainmenu.fxml"));
//       main = new Scene(root);

        //initialize the first scene
        setScene("/com/example/helloworld/mainmenu.fxml");
        this.stage.show();
        MainController main = ControllerHelper.getLoadedFxmlController();
        main.setOnStartReference(this);
    }


    public Parent getRoot() {
        return root;
    }


    public void setScene(String resourcePath) throws Exception {
        ControllerHelper.setFxmlLoader(resourcePath);
        stage.setTitle("Photo Encryptor 9000");
        stage.setScene(new Scene(ControllerHelper.loadFxml()));
    }


    public Scene getScene() { return main; }


    public void showpasswordprompt() throws IOException{ //this doesn't work either, unsure why it doesn't work
        FXMLLoader loader = new FXMLLoader();
        root = FXMLLoader.load(getClass().getResource("password.fxml"));
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();
    }


    //works for only password.fxml, but not mainmenu.fxml
    public void changeScene(String fxml) throws Exception {
        ControllerHelper.setFxmlLoader(fxml);
        //stage.getScene().setRoot(root);
        stage.setScene(new Scene(ControllerHelper.loadFxml()));
    }


}
