package fxml_helloworld.Login_Scene;

import fxml_helloworld.OpenScene;
import javafx.collections.*;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// import untuk menangani format XML
import com.thoughtworks.xstream.XStream;
import javafx.scene.control.Hyperlink;

public class RegisterInvestor implements Initializable{

    @FXML
    private TextField emailRegister;

    @FXML
    private TextField nameRegister;

    @FXML
    private PasswordField passwordRegister;

    @FXML
    private TextField phoneRegister;

    @FXML
    private ChoiceBox<String> typeUser;

    @FXML
    private Button signupButton;

    @FXML
    private Hyperlink alreadyHave;

    private OpenScene openScene = new OpenScene();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        typeUser.setItems(FXCollections.observableArrayList("Borrower", "Investor"));
    }

    @FXML
    private void signUp(ActionEvent event) {
        String name = nameRegister.getText();
        String email = emailRegister.getText();
        String password = passwordRegister.getText();
        String phoneNumber = phoneRegister.getText();
        String typeRegister = typeUser.getValue();

        try {
            // Validasi semua field harus terisi
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || typeRegister == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("All Fields Must Be Filled In!");
                alert.showAndWait();
            } else {
                RegisterData newUser = new RegisterData(name, email, password, phoneNumber, typeRegister);
                
                XStream xstream = new XStream(new StaxDriver());
                xstream.allowTypes(new Class[] { RegisterData.class });

                String xml = xstream.toXML(newUser);

                try {
                    String filePath = "data" + typeRegister + ".xml"; // dataBorrower.xml atau dataInvestor.xml
                    FileOutputStream output = new FileOutputStream(filePath);
                    output.write(xml.getBytes());
                    output.close();

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Registration Success Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Registration is successful, please log in again.");
                    alert.showAndWait();

                    openScene.openScene("/fxml_helloworld/Login_Scene/Login.fxml", signupButton);
                }catch(IOException e){
                    System.out.println("Failed to Save File");
                    System.exit(0);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void alreadyHaveAccount(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/Login.fxml", alreadyHave);
    }
}
