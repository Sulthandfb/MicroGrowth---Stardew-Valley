package fxml_helloworld.Dashboard_Investor;

import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class FundingController implements Initializable {

    @FXML
    private TableView<SubmissionData> fundingTable;

    @FXML
    private TableColumn<SubmissionData, String> accountFundColumn;

    @FXML
    private TableColumn<SubmissionData, Double> amountFundColumn;

    @FXML
    private TableColumn<SubmissionData, String> installmentFundColumn;

    @FXML
    private TableColumn<SubmissionData, String> nameFundColumn;

    @FXML
    private TableColumn<SubmissionData, Double> profitFundColumn;

    @FXML
    private TableColumn<SubmissionData, String> scoreFundColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private Button seeDetail;

    @FXML
    private TableColumn<SubmissionData, String> typeFundColumn;

    @FXML
    private TableColumn<SubmissionData, String> paymentStatusColumn;

    @FXML
    private TextField nameBorrowerFund;

    @FXML
    private TextField accNumberFund;

    @FXML
    private Button paymentButton;

    private ObservableList<SubmissionData> submissionList = FXCollections.observableArrayList();
    private ObservableList<SubmissionData> allSubmissionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadDataFromXML();
        fundingTable.setItems(submissionList);
        
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });

        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
    }

    private void setupTableColumns() {
        nameFundColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeFundColumn.setCellValueFactory(new PropertyValueFactory<>("businessType"));
        amountFundColumn.setCellValueFactory(new PropertyValueFactory<>("installmentAmount"));
        profitFundColumn.setCellValueFactory(new PropertyValueFactory<>("profitSharingAmount"));
        installmentFundColumn.setCellValueFactory(new PropertyValueFactory<>("installmentType"));
        scoreFundColumn.setCellValueFactory(new PropertyValueFactory<>("creditScore"));
        accountFundColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
    }

    @FXML
    private void showDetail() {
        SubmissionData selectedSubmission = fundingTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission != null) {
            openDetailPopup(selectedSubmission);
        } else {
            showAlert("No Selection", "Please select a row in the table.");
        }
    }

    private void openDetailPopup(SubmissionData selectedSubmission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Dashboard_Investor/DetailLoan.fxml"));
            Parent root = loader.load();
            
            DetailLoan detailController = loader.getController();
            detailController.setSubmissionData(selectedSubmission);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Loan Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open detail window.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadDataFromXML() {
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

            for (SubmissionData submission : allSubmissions) {
                if ("VERIFIED".equals(submission.getVerificationStatus())) {
                    submission.setPaymentStatus("Not Funded");
                    submission.setRemainingLoanAmount(submission.getLoanPlan());
                    allSubmissionList.add(submission);
                }
            }
            
            submissionList.addAll(allSubmissionList);
            System.out.println("Loaded " + submissionList.size() + " verified submissions");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading XML: " + e.getMessage());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            fundingTable.setItems(allSubmissionList);
        } else {
            ObservableList<SubmissionData> filteredList = FXCollections.observableArrayList();
            for (SubmissionData submission : allSubmissionList) {
                if (matchesKeyword(submission, keyword.toLowerCase())) {
                    filteredList.add(submission);
                }
            }
            fundingTable.setItems(filteredList);
        }
    }

    private boolean matchesKeyword(SubmissionData submission, String keyword) {
        return submission.getName().toLowerCase().contains(keyword)
            || submission.getBusinessType().toLowerCase().contains(keyword)
            || submission.getInstallmentType().toLowerCase().contains(keyword)
            || submission.getCreditScore().toLowerCase().contains(keyword)
            || submission.getAccountNumber().toLowerCase().contains(keyword)
            || String.valueOf(submission.getInstallmentAmount()).contains(keyword)
            || String.valueOf(submission.getProfitSharingAmount()).contains(keyword);
    }

    @FXML
    private void handlePayment() {
        String name = nameBorrowerFund.getText();
        String accountNumber = accNumberFund.getText();

        SubmissionData selectedSubmission = fundingTable.getItems().stream()
            .filter(submission -> submission.getName().equals(name) && submission.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElse(null);

        if (selectedSubmission != null) {
            openPaymentPopup(selectedSubmission);
        } else {
            showAlert("Error", "No matching submission found. Please check the name and account number.");
        }
    }

    private void openPaymentPopup(SubmissionData submission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Dashboard_Investor/Payment.fxml"));
            Parent root = loader.load();
            
            PaymentController paymentController = loader.getController();
            paymentController.setSubmissionData(submission);
            
            InvestorData investorData = loadInvestorData();
            if (investorData == null) {
                showAlert("Error", "Failed to load investor data. Please try again.");
                return;
            }
            paymentController.setInvestorData(investorData);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Payment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open payment window.");
        }
    }

    private InvestorData loadInvestorData() {
        try {
            File xmlFile = new File("investorData.xml");
            XStream xstream = new XStream(new StaxDriver());
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(new Class[]{InvestorData.class, TopUpRecord.class, PaymentRecord.class, ArrayList.class});
            xstream.addPermission(AnyTypePermission.ANY);
            
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("Top-Up-Record", TopUpRecord.class);
            xstream.alias("Payment-Record", PaymentRecord.class);
            xstream.processAnnotations(InvestorData.class);
            xstream.processAnnotations(TopUpRecord.class);
            xstream.processAnnotations(PaymentRecord.class);
    
            InvestorData investorData = (InvestorData) xstream.fromXML(xmlFile);
            
            if (investorData == null) {
                throw new Exception("Failed to load investor data");
            }
            
            System.out.println("Loaded investor data for: " + investorData.getName());
            return investorData;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading investor XML: " + e.getMessage());
            return null;
        }
    }
}