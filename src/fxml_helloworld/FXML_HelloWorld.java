package fxml_helloworld;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXML_HelloWorld extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Login_Scene/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("MicroGrowth");

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
