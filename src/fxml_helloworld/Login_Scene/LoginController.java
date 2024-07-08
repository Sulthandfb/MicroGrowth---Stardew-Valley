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

public class LoginController implements Initializable {

    @FXML
    private AnchorPane login_Form;

    @FXML
    private Label log_in;

    @FXML
    private Label welcomeBack;

    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField login_ShowPassword;

    @FXML
    private CheckBox showPassword;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Hyperlink adminLink;

    //Untuk Register Peminjam
    @FXML
    private Button loginPeminjam;

    private OpenScene openScene = new OpenScene();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void adminLogin(ActionEvent event) {
        try {
            Alert alert;
    
            if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please enter email and password");
                alert.showAndWait();
                return;
            } 
            XStream xStream = new XStream(new StaxDriver());
            xStream.allowTypes(new Class[] { RegisterData.class });

            RegisterData loadedUser = null;
            String userType = "";

            // Coba load dari dataBorrower.xml
            try {
                loadedUser = (RegisterData) xStream.fromXML(new java.io.File("dataBorrower.xml"));
                if (loadedUser != null && loginUsername.getText().equals(loadedUser.getName())
                        && loginPassword.getText().equals(loadedUser.getPassword())) {
                    userType = "Borrower";
                } else {
                    loadedUser = null; // Reset jika tidak cocok
                }
            } catch (Exception e) {
                // File tidak ditemukan atau error lainnya, lanjut ke dataInvestor.xml
            }

            // Jika tidak ditemukan di dataMember.xml, coba load dari dataInvestor.xml
            if (loadedUser == null) {
                try {
                    loadedUser = (RegisterData) xStream.fromXML(new java.io.File("dataInvestor.xml"));
                    if (loadedUser != null && loginUsername.getText().equals(loadedUser.getName())
                            && loginPassword.getText().equals(loadedUser.getPassword())) {
                        userType = "Investor";
                    } else {
                        loadedUser = null; // Reset jika tidak cocok
                    }
                } catch (Exception e) {
                    // File tidak ditemukan atau error lainnya
                }
            }

            if (loadedUser != null) {
                    // Jika user ditemukan data di dataUser
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login!");
                    alert.showAndWait();

                if (userType.equals("Borrower")) {
                    openScene.openScene("/fxml_helloworld/Dashboard_Borrower/dashboard.fxml", loginButton);
                } else if (userType.equals("Investor")){
                    openScene.openScene("/fxml_helloworld/Dashboard_Investor/dashboard.fxml", loginButton);
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Wrong email/Password");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegisterScene(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/Register.fxml", registerLink);
    }

    @FXML
    private void openAdminLogin(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/LoginAdmin.fxml", adminLink);
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
    }
}
