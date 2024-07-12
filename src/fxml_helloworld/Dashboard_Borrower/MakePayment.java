package fxml_helloworld.Dashboard_Borrower;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.util.*;

import fxml_helloworld.Dashboard_Investor.InvestorData;
import fxml_helloworld.Dashboard_Investor.PaymentRecord;
import fxml_helloworld.Dashboard_Investor.ReceivedPaymentRecord;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.io.IOException;

public class MakePayment implements Initializable {

    @FXML
    private TextField AccountField;

    @FXML
    private TableColumn<PaymentRecord, String> accountInvestorColumn;

    @FXML
    private TableColumn<PaymentRecord, Double> amountColumn;

    @FXML
    private TableColumn<PaymentRecord, String> emailInvestorColumn;

    @FXML
    private TableColumn<PaymentRecord, LocalDateTime> fundDateColumn;

    @FXML
    private Label incomeFundsLabel;

    @FXML
    private TableView<PaymentRecord> paymentTableView;

    @FXML
    private TableColumn<PaymentRecord, String> investorNameColumn;

    @FXML
    private TextField investorNameField;

    @FXML
    private Label loanPlanFund;

    @FXML
    private Label remainingLoanLabel;

    @FXML
    private Button payLoan;

    @FXML
    private Label accountBorrowerLabel;

    @FXML
    private Label emailBorrowerLabel;

    @FXML
    private Label nameBorrowerLabel;

    @FXML
    private Label npwpBorrowerLabel;

    private SubmissionData borrowerData;
    private InvestorData investorData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        
        // Load data from XML
        loadInvestorData();

        // Load borrower data
        loadBorrowerData();
    }


    public void initializeTableColumns() {
        // Initialize TableView columns
        investorNameColumn.setCellValueFactory(new PropertyValueFactory<>("investorName"));
        emailInvestorColumn.setCellValueFactory(new PropertyValueFactory<>("investorEmail"));
        accountInvestorColumn.setCellValueFactory(new PropertyValueFactory<>("investorAccountNumber"));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        fundDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTimestamp()));
    }

    private void updateBorrowerInfo() {
        if (borrowerData != null) {
            loanPlanFund.setText("Rp. " + String.format("%,.2f", borrowerData.getLoanPlan()));
            remainingLoanLabel.setText("Rp. " + String.format("%,.2f", borrowerData.getRemainingLoanAmount()));
            incomeFundsLabel.setText("Rp. " + String.format("%,.2f", borrowerData.getIncome()));
            nameBorrowerLabel.setText(borrowerData.getName());
            emailBorrowerLabel.setText(borrowerData.getEmail());
            npwpBorrowerLabel.setText(borrowerData.getNpwp());
            accountBorrowerLabel.setText(borrowerData.getAccountNumber());
        }
    }

    private void loadBorrowerData() {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            xstream.processAnnotations(SubmissionData.class);

            FileInputStream fis = new FileInputStream("submission_data.xml");
            List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(fis);
            fis.close();

            if (submissions != null && !submissions.isEmpty()) {
                borrowerData = submissions.get(submissions.size() - 1); // Get the latest submission
                updateBorrowerInfo();
            } else {
                System.out.println("No submissions found in the XML file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading borrower data: " + e.getMessage());
        }
    }

    private void loadInvestorData() {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.processAnnotations(InvestorData.class);
            xstream.processAnnotations(ReceivedPaymentRecord.class);
            xstream.processAnnotations(PaymentRecord.class);
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("received-payment-record", ReceivedPaymentRecord.class);
            xstream.alias("Payment-Record", PaymentRecord.class);

            FileInputStream fis = new FileInputStream("investorData.xml");
            investorData = (InvestorData) xstream.fromXML(fis);
            fis.close();

            paymentTableView.setItems(FXCollections.observableArrayList(investorData.getPaymentHistory()));

            if (borrowerData != null) {
                for (PaymentRecord payment : investorData.getPaymentHistory()) {
                    if (borrowerData.getAccountNumber().equals(payment.getRecipientAccountNumber())) {

                    }
                }
                updateBorrowerInfo();
                saveBorrowerData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBorrowerData() {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.processAnnotations(SubmissionData.class);
    
            List<SubmissionData> submissions = loadSubmissions();
            if (submissions != null) {
                for (int i = 0; i < submissions.size(); i++) {
                    if (submissions.get(i).getAccountNumber().equals(borrowerData.getAccountNumber())) {
                        submissions.set(i, borrowerData);
                        break;
                    }
                }
    
                FileWriter writer = new FileWriter("submission_data.xml");
                xstream.toXML(submissions, writer);
                writer.close();
            } else {
                showAlert("Error", "Failed to load submission data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save borrower data.");
        }
    }

    private void saveInvestorData() {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("received-payment", ReceivedPaymentRecord.class);
    
            FileWriter writer = new FileWriter("investorData.xml");
            xstream.toXML(investorData, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save investor data.");
        }
    }

    private List<SubmissionData> loadSubmissions() {
        try {
            File file = new File("submission_data.xml");
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>(); // Return empty list instead of null
            }
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(file);
            return submissions != null ? submissions : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load submission data.");
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    @FXML
    private void handlePayLoan() {
        String investorName = investorNameField.getText();
        String accountNumber = AccountField.getText();
    
        if (investorName.isEmpty() || accountNumber.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
    
        PaymentRecord selectedPayment = paymentTableView.getItems().stream()
            .filter(payment -> payment.getInvestorName().equals(investorName) 
                            && payment.getInvestorAccountNumber().equals(accountNumber))
            .findFirst()
            .orElse(null);
    
        if (selectedPayment == null) {
            showAlert("Error", "No matching investor found. Please check the name and account number.");
            return;
        }
    
        DetailPaymentController detailController = openDetailPaymentPopup(selectedPayment);
        if (detailController != null && detailController.isDataChanged()) {
            loadInvestorData();
            updateBorrowerInfo();
            saveInvestorData();  // Save the updated investor data
        }
    }

    private DetailPaymentController openDetailPaymentPopup(PaymentRecord payment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Dashboard_Borrower/DetailPayment.fxml"));
            Parent root = loader.load();
            
            DetailPaymentController detailController = loader.getController();
            detailController.setPaymentData(payment);
            detailController.setSubmissionData(borrowerData);
            detailController.setInvestorData(investorData);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Payment Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            return detailController;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open payment details window.");
            return null;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
    
