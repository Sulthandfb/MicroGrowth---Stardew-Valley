package fxml_helloworld.Dashboard_Admin;

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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

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

public class AdminController implements Initializable{

    @FXML
    private Button dashboardPage;

    @FXML
    private Button logOutBtn;

    @FXML
    private Button evaluatePage;

    @FXML
    private Button performancePage;

    @FXML
    private BorderPane adminMainPane;

    @FXML 
    private TableView<SubmissionData> submissionTable;

    @FXML 
    private TableColumn<SubmissionData, String> nameColumn;

    @FXML 
    private TableColumn<SubmissionData, String> npwpColumn;

    @FXML 
    private TableColumn<SubmissionData, String> phoneColumn;

    @FXML 
    private TableColumn<SubmissionData, String> jobColumn;

    @FXML 
    private TableColumn<SubmissionData, LocalDate> dateColumn;

    @FXML 
    private TextField userIdField;

    @FXML 
    private TextField fullNameField;

    @FXML 
    private TextField npwpField;

    @FXML 
    private TextField phoneField;

    @FXML 
    private TextField jobField;

    @FXML 
    private Button deleteButton;

    @FXML 
    private Button updateButton;

    @FXML 
    private Button detailsButton;

    @FXML
    private Label totalTransactionLabel;

    @FXML
    private Label totalInvestor;

    @FXML
    private Label totalBorrower;

    @FXML
    private LineChart<String, Number> totalChart;
    

    private OpenScene openScene = new OpenScene();
    private ObservableList<SubmissionData> submissionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        }
        if (npwpColumn!= null) {
            npwpColumn.setCellValueFactory(new PropertyValueFactory<>("npwp"));
        }
        if (phoneColumn != null) {
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        }
        if (jobColumn!= null) {
            jobColumn.setCellValueFactory(new PropertyValueFactory<>("job"));
        }
    
        // Baca data dari XML
        loadDataFromXML();
    
        // Set items ke TableView
        if (submissionTable != null) {
            submissionTable.setItems(submissionList);
        }

        // Cek null sebelum memanggil updateLineChart
        if (totalChart != null && totalTransactionLabel != null) {
            updateLineChart();
            updateTotalLabels();
            System.out.println("Chart ada");
        } else {
        }
    }

    //Method untuk memuat data XML
    private void loadDataFromXML() {
        try {
            File xmlFile = new File("submission_data.xml");
            XStream xstream = new XStream(new StaxDriver());
            // Mengatur atribut untuk profitSharingAmount
            xstream.useAttributeFor(SubmissionData.class, "profitSharingAmount");
            
            XStream.setupDefaultSecurity(xstream);
            xstream.allowTypes(new Class[]{SubmissionData.class});
            xstream.addPermission(AnyTypePermission.ANY);
            
            // Mengatur alias untuk elemen XML
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            xstream.processAnnotations(SubmissionData.class);
            // Membaca data dari file XML jika ada
            if (xmlFile.exists() && xmlFile.length() > 0) {
                List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
                // Menetapkan tanggal pengajuan jika belum ada
                if (submissions != null) {
                    for (SubmissionData submission : submissions) {
                        if (submission.getSubmissionDate() == null) {
                            submission.setSubmissionDate(LocalDate.now());
                        }
                    }
                    // Memperbarui daftar pengajuan
                    submissionList.clear();
                    submissionList.addAll(submissions);
                }
            }
            
            System.out.println("Loaded " + submissionList.size() + " submissions");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading XML: " + e.getMessage());
        }
    }

    // Method Memunculkan Detail Pengajuan
    @FXML
    private void showDetail(ActionEvent event) {
        SubmissionData selectedSubmission = submissionTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            showAlert("No Selection", "Please select a submission from the table.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ApplicantDetail.fxml"));
            Parent root = loader.load();
            
            ApplicantDetailController detailController = loader.getController();
            detailController.setSubmissionData(selectedSubmission);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Applicant Detail");
            stage.initModality(Modality.APPLICATION_MODAL);
            detailController.setStage(stage);
            stage.showAndWait();
            
            // Refresh table after closing the detail window
            loadDataFromXML();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load applicant detail view.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //Untuk Delete Button
    @FXML
    private void deleteSubmission(ActionEvent event) {
        SubmissionData selectedSubmission = submissionTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            showAlert("No Selection", "Please select a submission to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this submission?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteFromXmlFile(selectedSubmission)) {
                submissionList.remove(selectedSubmission);
                showAlert("Deletion Successful", "The submission has been deleted.");
            } else {
                showAlert("Deletion Failed", "Failed to delete the submission. Please try again.");
            }
        }
    }

    private boolean deleteFromXmlFile(SubmissionData submissionToDelete) {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[]{SubmissionData.class});
        xstream.alias("submissions", List.class);
        xstream.alias("submission", SubmissionData.class);
        xstream.processAnnotations(SubmissionData.class);
    
        try {
            File xmlFile = new File("submission_data.xml");
            if (xmlFile.exists()) {
                List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
    
                // Hapus submission dari daftar
                boolean removed = submissions.removeIf(submission -> 
                    submission.getName().equals(submissionToDelete.getName()) &&
                    submission.getNpwp().equals(submissionToDelete.getNpwp()) &&
                    submission.getPhoneNumber().equals(submissionToDelete.getPhoneNumber()));
    
                if (removed) {
                    try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
                        xstream.toXML(submissions, fos);
                    }
    
                    System.out.println("Submission deleted from XML file successfully.");
                    return true;
                } else {
                    System.out.println("Submission not found in XML file.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting from XML file: " + e.getMessage());
            e.printStackTrace();
        }
    
        return false;
    }
    

    //Method Meng-Update Data
    @FXML
    private void updateSubmission(ActionEvent event) {
        SubmissionData selectedSubmission = submissionTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            showAlert("No Selection", "Please select a submission to update.");
            return;
        }
    
        String originalNpwp = selectedSubmission.getNpwp(); // Save original NPWP
    
        String newName = fullNameField.getText();
        String newNpwp = npwpField.getText();
        String newPhone = phoneField.getText();
        String newJob = jobField.getText();
    
        if (newName.isEmpty() || newNpwp.isEmpty() || newPhone.isEmpty() || newJob.isEmpty()) {
            showAlert("Invalid Input", "Please fill all fields.");
            return;
        }
    
        selectedSubmission.setName(newName);
        selectedSubmission.setNpwp(newNpwp);
        selectedSubmission.setPhoneNumber(newPhone);
        selectedSubmission.setJob(newJob);
    
        System.out.println("Updated Submission: " + selectedSubmission);
    
        if (updateXmlFile(selectedSubmission, originalNpwp)) {
            submissionTable.refresh();
            showAlert("Update Successful", "The submission has been updated.");
            clearFields();
        } else {
            showAlert("Update Failed", "Failed to update the submission. Please try again.");
        }
    }
    
    private boolean updateXmlFile(SubmissionData updatedSubmission, String originalNpwp) {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[]{SubmissionData.class});
        xstream.alias("submissions", List.class);
        xstream.alias("submission", SubmissionData.class);
        xstream.processAnnotations(SubmissionData.class);
        xstream.useAttributeFor(SubmissionData.class, "profitSharingAmount");
    
        try {
            File xmlFile = new File("submission_data.xml");
            List<SubmissionData> submissions;
            if (xmlFile.exists()) {
                submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
            } else {
                submissions = new ArrayList<>();
            }
    
            System.out.println("Submissions before update: " + submissions);
    
            boolean found = false;
            for (int i = 0; i < submissions.size(); i++) {
                if (submissions.get(i).getNpwp().equals(originalNpwp)) {
                    submissions.set(i, updatedSubmission);
                    found = true;
                    break;
                }
            }
    
            if (found) {
                System.out.println("Submission found and updated: " + updatedSubmission);
            } else {
                System.out.println("Submission not found in XML file.");
                return false;
            }
    
            try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
                xstream.toXML(submissions, fos);
            }
    
            System.out.println("XML file updated successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error updating XML file: " + e.getMessage());
            e.printStackTrace();
        }
    
        return false;
    }
    
    
    
    

    private boolean updateXmlFile(SubmissionData updatedSubmission) {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[]{SubmissionData.class});
        xstream.alias("submissions", List.class);
        xstream.alias("submission", SubmissionData.class);
        xstream.processAnnotations(SubmissionData.class);
    
        try {
            File xmlFile = new File("submission_data.xml");
            List<SubmissionData> submissions;
            if (xmlFile.exists()) {
                submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
            } else {
                submissions = new ArrayList<>();
            }
    
            // Logging the submissions list before updating
            System.out.println("Submissions before update: " + submissions);
    
            // Cari dan update submission yang sesuai berdasarkan NPWP
            boolean found = false;
            for (int i = 0; i < submissions.size(); i++) {
                if (submissions.get(i).getNpwp().equals(updatedSubmission.getNpwp())) {
                    submissions.set(i, updatedSubmission);
                    found = true;
                    break;
                }
            }
    
            // Logging the result of the search
            if (found) {
                System.out.println("Submission found and updated: " + updatedSubmission);
            } else {
                System.out.println("Submission not found in XML file.");
                return false;
            }
    
            // Tulis kembali seluruh list ke file XML
            try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
                xstream.toXML(submissions, fos);
            }
    
            System.out.println("XML file updated successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error updating XML file: " + e.getMessage());
            e.printStackTrace();
        }
    
        return false;
    }
    
    @FXML
    private void updateLineChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Investor Funding");

        // Baca data XML investor
        InvestorData investorData = loadInvestorDataFromXML();

        int transactionCount = 0;

        if (investorData != null && investorData.getPaymentHistory() != null) {
            for (PaymentRecord payment : investorData.getPaymentHistory()) {
                // Menggunakan tanggal sebagai sumbu X dan jumlah sebagai sumbu Y
                series.getData().add(new XYChart.Data<>(
                    payment.getTimestamp().toLocalDate().toString(),
                    payment.getAmount()
                ));
                transactionCount++;
            }
        }

        totalChart.getData().clear();
        totalChart.getData().add(series);

        // Update total transaction label dengan jumlah transaksi
        totalTransactionLabel.setText(transactionCount + " Transactions ");
    }
    
    private InvestorData loadInvestorDataFromXML() {
        try {
            File xmlFile = new File("investorData.xml");
            if (!xmlFile.exists()) {
                System.err.println("XML file does not exist");
                return null;
            }

            XStream xstream = new XStream(new StaxDriver());
            xstream.allowTypes(new Class[]{InvestorData.class, PaymentRecord.class, TopUpRecord.class, ReceivedPaymentRecord.class});
            xstream.alias("investor-data", InvestorData.class);
            xstream.alias("PaymentRecord", PaymentRecord.class);
            xstream.processAnnotations(InvestorData.class);

            return (InvestorData) xstream.fromXML(xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading investor data from XML: " + e.getMessage());
            return null;
        }
    }

    // Label Investor dan Peminjam
    private int countInvestors() {
        try {
            File xmlFile = new File("investorData.xml");
            if (!xmlFile.exists()) {
                System.err.println("Investor XML file does not exist");
                return 0;
            }
    
            XStream xstream = new XStream(new StaxDriver());
            xstream.allowTypes(new Class[]{InvestorData.class, PaymentRecord.class, TopUpRecord.class, ReceivedPaymentRecord.class});
            xstream.alias("investor-data", InvestorData.class);
            xstream.processAnnotations(InvestorData.class);
    
            // Baca XML sebagai InvestorData, bukan List
            InvestorData investor = (InvestorData) xstream.fromXML(xmlFile);
            
            // Jika ada data investor, kembalikan 1, jika tidak, kembalikan 0
            return (investor != null) ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading investor data from XML: " + e.getMessage());
            return 0;
        }
    }

    private int countBorrowers() {
        try {
            File xmlFile = new File("submission_data.xml");
            if (!xmlFile.exists()) {
                System.err.println("Submission XML file does not exist");
                return 0;
            }
    
            XStream xstream = new XStream(new StaxDriver());
            xstream.allowTypes(new Class[]{SubmissionData.class});
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            xstream.processAnnotations(SubmissionData.class);
    
            List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
            return submissions.size();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading submission data from XML: " + e.getMessage());
            return 0;
        }
    }

    private void updateTotalLabels() {
        int investorCount = countInvestors();
        int borrowerCount = countBorrowers();
    
        totalInvestor.setText(investorCount + " Investor ");
        totalBorrower.setText(borrowerCount + " Borrower ");
    }

    private void clearFields() {
        fullNameField.clear();
        npwpField.clear();
        phoneField.clear();
        jobField.clear();
    }

    @FXML
    private void handleTableSelection(MouseEvent event) {
        SubmissionData selectedSubmission = submissionTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission != null) {
            fullNameField.setText(selectedSubmission.getName());
            npwpField.setText(selectedSubmission.getNpwp());
            phoneField.setText(selectedSubmission.getPhoneNumber());
            jobField.setText(selectedSubmission.getJob());
        }
    }


    @FXML
    private void keDashboard(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openScene.loadFXML("/fxml_helloworld/Dashboard_Admin/AdminDashboard.fxml", stage, event);
    }

    @FXML
    private void keEvaluate(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Admin/Evaluate.fxml");
        adminMainPane.setCenter(halaman);
    }

    @FXML
    private void keLoanPerformance(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Admin/LoanPerformance.fxml");
        adminMainPane.setCenter(halaman);
    }

    @FXML
    private void logoutButton(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/Login.fxml", logOutBtn);
    }

}
