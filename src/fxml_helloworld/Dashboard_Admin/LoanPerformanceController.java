package fxml_helloworld.Dashboard_Admin;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
   
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.*;

import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;

// import untuk menangani format XML
import com.thoughtworks.xstream.XStream;

public class LoanPerformanceController implements Initializable {

    @FXML
    private TableColumn<SubmissionData, String> jobColumnPerform;

    @FXML
    private TableColumn<SubmissionData, Double> loanPlanColumnPerform;

    @FXML
    private TableView<SubmissionData> monitorLoanPerform;

    @FXML
    private TableColumn<SubmissionData, String> nameColumnPerform;

    @FXML
    private TableColumn<SubmissionData, String> npwpColumnPerform;

    @FXML
    private TableColumn<SubmissionData, String> skorCreditColumnPerform;

    @FXML
    private PieChart successfulSubmission;

    @FXML
    private TableColumn<SubmissionData, String> statusColumnPerform;

    private ObservableList<SubmissionData> submissionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumnPerform.setCellValueFactory(new PropertyValueFactory<>("name"));
        jobColumnPerform.setCellValueFactory(new PropertyValueFactory<>("job"));
        npwpColumnPerform.setCellValueFactory(new PropertyValueFactory<>("npwp"));
        loanPlanColumnPerform.setCellValueFactory(new PropertyValueFactory<>("loanPlan"));
        skorCreditColumnPerform.setCellValueFactory(new PropertyValueFactory<>("creditScore"));
        statusColumnPerform.setCellValueFactory(new PropertyValueFactory<>("verificationStatus"));
    
        loadDataFromXML();
    
        monitorLoanPerform.setItems(submissionList);
        updatePieChart();
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
            submissionList.addAll(allSubmissions);  // Add all submissions
            
            System.out.println("Loaded " + submissionList.size() + " verified submissions");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading XML: " + e.getMessage());
        }
    }

    private void updatePieChart() {
        int verifiedCount = 0;
        int pendingCount = 0;
    
        for (SubmissionData submission : submissionList) {
            if ("VERIFIED".equals(submission.getVerificationStatus())) {
                verifiedCount++;
            }
            else if ("PENDING".equals(submission.getVerificationStatus())) {
                pendingCount++;
            }
        }
    
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Verified", verifiedCount),
            new PieChart.Data("Pending", pendingCount)
        );
    
        successfulSubmission.setData(pieChartData);
        successfulSubmission.setTitle("Submission Verification Status");
    }
}
