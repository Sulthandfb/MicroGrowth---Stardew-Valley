package fxml_helloworld.Dashboard_Investor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PopUpController implements Initializable {

    @FXML
    private Label bankLabel;

    @FXML
    private Label accountLabel;

    @FXML
    private Label topupLabel;

    @FXML
    private TextField nameTopupField;

    @FXML
    private TextField emailTopupField;

    @FXML
    private PasswordField passwordTopupField;

    @FXML
    private Button purchaseButton;

    private boolean isConfirmed = false;
    private String bank;
    private String accountNumber;
    private String topupAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        purchaseButton.setOnAction(event -> handlePurchase());
    }

    public void setTopUpDetails(String bank, String accountNumber, String topupAmount) {
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.topupAmount = topupAmount;

        bankLabel.setText(bank);
        accountLabel.setText(accountNumber);
        topupLabel.setText("Rp " + topupAmount);
    }

    public void setPrefilledData(String name, String email) {
        nameTopupField.setText(name);
        emailTopupField.setText(email);
        nameTopupField.setEditable(false);
        emailTopupField.setEditable(false);
    }

    @FXML
    private void handlePurchase() {
        if (validateInputs()) {
            isConfirmed = true;
            closeWindow();
        } else {
            // Show error message
            // You can implement a method to show an alert here
        }
    }

    private boolean validateInputs() {
        return !nameTopupField.getText().isEmpty() &&
               !emailTopupField.getText().isEmpty() &&
               !passwordTopupField.getText().isEmpty();
    }

    private void closeWindow() {
        Stage stage = (Stage) purchaseButton.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public String getName() {
        return nameTopupField.getText();
    }

    public String getEmail() {
        return emailTopupField.getText();
    }

    public String getPassword() {
        return passwordTopupField.getText();
    }

    public String getBank() {
        return bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTopupAmount() {
        return topupAmount;
    }
}