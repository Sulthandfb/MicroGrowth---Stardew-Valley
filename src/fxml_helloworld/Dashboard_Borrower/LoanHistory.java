package fxml_helloworld.Dashboard_Borrower;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;

import fxml_helloworld.OpenScene;
import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
   
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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

// import untuk menangani format XML
import com.thoughtworks.xstream.XStream;

public class LoanHistory implements Initializable{

    @FXML
    private TableColumn<SubmissionData, String> dateColumnHistory;

    @FXML
    private TextField historyJobField;

    @FXML
    private TextField historyNameField;

    @FXML
    private TextField historyNpwpField;

    @FXML
    private TextField historyPhoneField;

    @FXML
    private TableView<SubmissionData> historySubmissionTable;

    @FXML
    private TableColumn<SubmissionData, String> jobColumnHistory;

    @FXML
    private TableColumn<SubmissionData, String> nameColumnHistory;

    @FXML
    private TableColumn<SubmissionData, String> npwpColumnHistory;

    @FXML
    private TableColumn<SubmissionData, String> phoneColumnHistory;

    @FXML
    private Button updateSubmission;

    private SubmissionData submission;
    private ObservableList<SubmissionData> submissionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (nameColumnHistory != null) {
            nameColumnHistory.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (dateColumnHistory != null) {
            dateColumnHistory.setCellValueFactory(new PropertyValueFactory<>("submissionDate"));
        }
        if (npwpColumnHistory!= null) {
            npwpColumnHistory.setCellValueFactory(new PropertyValueFactory<>("npwp"));
        }
        if (phoneColumnHistory != null) {
            phoneColumnHistory.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        }
        if (jobColumnHistory!= null) {
            jobColumnHistory.setCellValueFactory(new PropertyValueFactory<>("job"));
        }
    
        // Baca data dari XML
        loadDataFromXML();
    
        // Set items ke TableView
        if (historySubmissionTable != null) {
            historySubmissionTable.setItems(submissionList);
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

    //Method Meng-Update Data
    @FXML
    private void updateSubmission(ActionEvent event) {
        SubmissionData selectedSubmission = historySubmissionTable.getSelectionModel().getSelectedItem();
        if (selectedSubmission == null) {
            showAlert("No Selection", "Please select a submission to update.");
            return;
        }
    
        String originalNpwp = selectedSubmission.getNpwp(); // Save original NPWP
    
        String newName = historyNameField.getText();
        String newNpwp = historyNpwpField.getText();
        String newPhone = historyPhoneField.getText();
        String newJob = historyJobField.getText();
    
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
            historySubmissionTable.refresh();
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

    private void clearFields() {
        historyNameField.clear();
        historyNpwpField.clear();
        historyPhoneField.clear();
        historyJobField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
