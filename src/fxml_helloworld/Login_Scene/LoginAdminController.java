package fxml_helloworld.Login_Scene;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;

import fxml_helloworld.OpenScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
   
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// import untuk menangani format XML
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class LoginAdminController implements Initializable{

    @FXML
    private TextField usernameAdmin;

    @FXML
    private PasswordField passwordAdmin;

    @FXML
    private Button loginAdminBtn;

    private OpenScene openScene = new OpenScene();

    private final String adminUsername = "adminSulthan";
    private final String adminPassword = "admin123";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void adminLogin(ActionEvent event) {
        try {
            Alert alert;

            if (usernameAdmin.getText().isEmpty() || passwordAdmin.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please enter username and password");
                alert.showAndWait();
                return;
            }

            if (usernameAdmin.getText().equals(adminUsername) && passwordAdmin.getText().equals(adminPassword)) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Login!");
                alert.showAndWait();

                openScene.openScene("/fxml_helloworld/Dashboard_Admin/AdminDashboard.fxml", loginAdminBtn);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Wrong username/Password");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
