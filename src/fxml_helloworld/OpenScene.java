package fxml_helloworld;

import java.io.IOException;

import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

public class OpenScene {
    private Pane halaman;

    public void openScene(String fullPath, Node node) {
        try {
            // Menutup stage/window sebelumnya
            node.getScene().getWindow().hide();

            URL resource = getClass().getResource(fullPath);
            if (resource == null) {
                throw new IOException("Cannot find resource: " + fullPath);
            }
            // Membuka scene/halaman baru
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error opening scene: " + fullPath);
        }
    }

    //Pindah Scene Smooth
    public void loadFXML(String fxmlFileName, Stage stage, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(OpenScene.class.getResource(fxmlFileName));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Jika ada SideBar nya
    public Pane getPane(String fullPath){
        try{
            URL fileHalaman=getClass().getResource(fullPath);
            
            if(fileHalaman==null){
                throw new java.io.FileNotFoundException("Halaman tidak ditemukan" + fullPath);
            }
            
            FXMLLoader loader = new FXMLLoader(fileHalaman);
            halaman = loader.load();
            
        }catch (Exception e){
       
            System.out.println("Tidak ditemukan halaman tersebut");
        }
        
        return halaman;
    }
}

