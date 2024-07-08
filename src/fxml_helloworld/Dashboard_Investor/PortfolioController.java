package fxml_helloworld.Dashboard_Investor;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;

import fxml_helloworld.OpenScene;
import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import fxml_helloworld.Dashboard_Investor.InvestorData;
import fxml_helloworld.Dashboard_Investor.PaymentRecord;
import fxml_helloworld.Dashboard_Investor.ReceivedPaymentRecord;
import fxml_helloworld.Dashboard_Investor.TopUpRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyObjectWrapper;


import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import javafx.fxml.Initializable;
import javafx.event.EventHandler;

public class PortfolioController implements Initializable{

    @FXML
    private TableColumn<SubmissionData, String> borrorwerNameColumn;

    @FXML
    private TableView<SubmissionData> myFundingTableView;

    @FXML
    private TableColumn<SubmissionData, Double> profitShareColumn;

    @FXML
    private PieChart pieChart;

    @FXML
    private TableColumn<SubmissionData, Double> remainingColumn;

    @FXML
    private TableColumn<PaymentRecord, String> recipientColumn;

    @FXML
    private TableColumn<PaymentRecord, String> accountColumn;

    @FXML
    private TableColumn<PaymentRecord, Double> amountColumn;

    @FXML
    private TableColumn<PaymentRecord, LocalDateTime> dateColumn;

    @FXML
    private TableView<PaymentRecord> historyTable;

    @FXML
    private TextField searchBar;

    private ObservableList<SubmissionData> submissionList = FXCollections.observableArrayList();
    private ObservableList<SubmissionData> allSubmissionList = FXCollections.observableArrayList();
    private ObservableList<PaymentRecord> paymentHistoryList = FXCollections.observableArrayList();

    private XStream xstream;
    private InvestorData investorData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadDataFromXML();
            loadInvestorDataFromXML();
            updatePieChart();

            // Set data to TableView
            setupHistoryTableColumns();
            updateHistoryTable();
            // Debugging output
            System.out.println("Submission list size: " + submissionList.size());
            for (SubmissionData submission : submissionList) {
                System.out.println(submission.getName() + " - " + submission.getVerificationStatus());
            }
            myFundingTableView.setItems(submissionList);

            // Menambahkan listener untuk searchBar
            searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTable(newValue);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in initialize: " + e.getMessage());
        }
    }

    private void setupHistoryTableColumns() {
        recipientColumn.setCellValueFactory(new PropertyValueFactory<>("recipientName"));
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("recipientAccountNumber"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    public PortfolioController() {
        xstream = new XStream(new StaxDriver());
        xstream.ignoreUnknownElements();
        xstream.allowTypesByWildcard(new String[]{"fxml_helloworld.**"});
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.processAnnotations(SubmissionData.class);
    }

    private void setupTableColumns() {
        borrorwerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        profitShareColumn.setCellValueFactory(new PropertyValueFactory<>("profitSharingAmount"));
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remainingLoanAmount"));
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
            myFundingTableView.setItems(submissionList);
            System.out.println("Loaded " + submissionList.size() + " verified submissions");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading XML: " + e.getMessage());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            myFundingTableView.setItems(allSubmissionList);
        } else {
            ObservableList<SubmissionData> filteredList = FXCollections.observableArrayList();
            for (SubmissionData submission : allSubmissionList) {
                if (matchesKeyword(submission, keyword.toLowerCase())) {
                    filteredList.add(submission);
                }
            }
            myFundingTableView.setItems(filteredList);
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

    private void loadInvestorDataFromXML() {
        try {
            File xmlFile = new File("investorData.xml");
            XStream xstream = new XStream(new StaxDriver());
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(new Class[]{InvestorData.class, TopUpRecord.class, PaymentRecord.class, ReceivedPaymentRecord.class});
            xstream.addPermission(AnyTypePermission.ANY);
            
            xstream.processAnnotations(InvestorData.class);
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("Top-Up-Record", TopUpRecord.class);
            xstream.alias("Payment-Record", PaymentRecord.class);
            xstream.alias("Received-Payment-Record", ReceivedPaymentRecord.class);
            
            investorData = (InvestorData) xstream.fromXML(xmlFile);
            System.out.println("Loaded investor data for: " + investorData.getName());

            // Populate paymentHistory
            paymentHistoryList.addAll(investorData.getPaymentHistory());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading investor XML: " + e.getMessage());
        }
    }

    private void updateHistoryTable() {
        historyTable.setItems(paymentHistoryList);
    }

    private void updatePieChart() {
        if (investorData != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    
            // Calculate total top-up amount
            double totalTopUp = 0.0;
            for (TopUpRecord record : investorData.getTopUpHistory()) {
                totalTopUp += record.getAmount();
            }
    
            // Calculate total payment amount
            double totalPayment = 0.0;
            for (PaymentRecord record : investorData.getPaymentHistory()) {
                totalPayment += record.getAmount();
            }
    
            // Add data to pie chart
            pieChartData.add(new PieChart.Data("Top-Up Amount", totalTopUp));
            pieChartData.add(new PieChart.Data("Payment Amount", totalPayment));
    
            pieChart.setData(pieChartData);
        }
    }
    
}
