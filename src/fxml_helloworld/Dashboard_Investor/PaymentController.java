package fxml_helloworld.Dashboard_Investor;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {
    @FXML
    private PasswordField accountNumberField;

    @FXML
    private Label accountRecipientLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailPayField;

    @FXML
    private Button fundItButton;

    @FXML
    private TextField nameCardField;

    @FXML
    private Label nameCardLabel;

    @FXML
    private Label nameRecipientLabel;

    @FXML
    private PasswordField passwordCardField;

    private SubmissionData submissionData;
    private InvestorData investorData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fundItButton.setOnAction(event -> handlePayment());
        cancelButton.setOnAction(event -> closeWindow());
    }

    public void setSubmissionData(SubmissionData submissionData) {
        this.submissionData = submissionData;
        nameRecipientLabel.setText(submissionData.getName());
        accountRecipientLabel.setText(submissionData.getAccountNumber());
        System.out.println("Submission data set: " + submissionData.getName() + ", " + submissionData.getAccountNumber());
    }

    public void setInvestorData(InvestorData investorData) {
        this.investorData = investorData;
        nameCardField.setText(investorData.getName());
        emailPayField.setText(investorData.getEmail());
        System.out.println("Investor data set: " + investorData.getName() + ", " + investorData.getEmail());
    }

    @FXML
    private void handlePayment() {
        System.out.println("Handle payment called");
        if (validateInputs()) {
            if (processPayment()) {
                showAlert("Success", "Payment successful!");
                closeWindow();
            } else {
                showAlert("Error", "Payment failed. Please try again.");
            }
        } else {
            showAlert("Error", "Please fill in all fields correctly.");
        }
    }
    

    private boolean validateInputs() {
        System.out.println("Validating inputs:");
        System.out.println("Name: " + nameCardField.getText() + " vs " + investorData.getName());
        System.out.println("Account: " + accountNumberField.getText() + " vs " + investorData.getAccountNumber());
        System.out.println("Email: " + emailPayField.getText() + " vs " + investorData.getEmail());
        System.out.println("PIN: " + passwordCardField.getText() + " vs " + investorData.getPin());
        return !nameCardField.getText().isEmpty() &&
               !accountNumberField.getText().isEmpty() &&
               !emailPayField.getText().isEmpty() &&
               !passwordCardField.getText().isEmpty() &&
               nameCardField.getText().equals(investorData.getName()) &&
               accountNumberField.getText().equals(investorData.getAccountNumber()) &&
               emailPayField.getText().equals(investorData.getEmail()) &&
               passwordCardField.getText().equals(investorData.getPin());
    }

    private boolean processPayment() {
        double paymentAmount = submissionData.getInstallmentAmount();
        System.out.println("Processing payment:");
        System.out.println("Payment amount: " + paymentAmount);
        System.out.println("Current balance: " + investorData.getBalance());
        System.out.println("Current outcome: " + investorData.getOutcome());

        if (investorData.getBalance() >= paymentAmount) {
            // Mengurangi saldo
            investorData.setBalance(investorData.getBalance() - paymentAmount);
            
            // Menambah outcome
            investorData.setOutcome(investorData.getOutcome() + paymentAmount);

            // Menambah income peminjam
            submissionData.setIncome(submissionData.getIncome() + paymentAmount);

            PaymentRecord paymentRecord = new PaymentRecord(
                paymentAmount, 
                submissionData.getName(), 
                submissionData.getAccountNumber(),
                investorData.getName(),
                investorData.getEmail(),
                investorData.getAccountNumber()
            );
            investorData.addPaymentRecord(paymentRecord);
            
            // Update submission data
            double remainingLoan = submissionData.getRemainingLoanAmount() - paymentAmount;
            submissionData.setRemainingLoanAmount(remainingLoan);
            if (remainingLoan <= 0) {
                submissionData.setPaymentStatus("Fully Funded");
            } else {
                submissionData.setPaymentStatus("Partially Funded");
            }
            
            saveInvestorData(investorData);
            saveSubmissionData(submissionData);

            System.out.println("Payment processed successfully");
            System.out.println("New balance: " + investorData.getBalance());
            System.out.println("New outcome: " + investorData.getOutcome());
            return true;
        } else {
            System.out.println("Insufficient balance");
            return false;
        }
    }

    private void saveSubmissionData(SubmissionData submissionData) {
        try {
            File xmlFile = new File("submission_data.xml");
            XStream xstream = new XStream(new StaxDriver());
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(new Class[]{SubmissionData.class});
            xstream.addPermission(AnyTypePermission.ANY);
            
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            xstream.processAnnotations(SubmissionData.class);

            List<SubmissionData> allSubmissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
            
            for (int i = 0; i < allSubmissions.size(); i++) {
                if (allSubmissions.get(i).getAccountNumber().equals(submissionData.getAccountNumber())) {
                    allSubmissions.set(i, submissionData);
                    break;
                }
            }

            String xml = xstream.toXML(allSubmissions);
            
            try (FileWriter writer = new FileWriter(xmlFile)) {
                writer.write(xml);
            }
            
            System.out.println("Saved submission data for: " + submissionData.getName());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving submission XML: " + e.getMessage());
        }
    }

    private void saveInvestorData(InvestorData investorData) {
        try {
            File xmlFile = new File("investorData.xml");
            XStream xstream = new XStream(new StaxDriver());
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(new Class[]{InvestorData.class, TopUpRecord.class, PaymentRecord.class});
            xstream.addPermission(AnyTypePermission.ANY);
            
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("Top-Up-Record", TopUpRecord.class);
            xstream.alias("Payment-Record", PaymentRecord.class);
            xstream.processAnnotations(InvestorData.class);
            xstream.processAnnotations(TopUpRecord.class);
            xstream.processAnnotations(PaymentRecord.class);

            String xml = xstream.toXML(investorData);
            
            try (FileWriter writer = new FileWriter(xmlFile)) {
                writer.write(xml);
            }
            
            System.out.println("Saved investor data for: " + investorData.getName());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving investor XML: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}