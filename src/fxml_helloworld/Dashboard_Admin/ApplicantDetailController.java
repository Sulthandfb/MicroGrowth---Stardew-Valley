package fxml_helloworld.Dashboard_Admin;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;
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
import com.thoughtworks.xstream.annotations.XStreamAlias;
import javafx.fxml.Initializable;

public class ApplicantDetailController implements Initializable{

    @FXML 
    private Label fullNameLabel;

    @FXML 
    private Label emailLabel;

    @FXML 
    private Label nikLabel;

    @FXML 
    private Label phoneNumberLabel;

    @FXML 
    private Label npwpLabel;

    @FXML 
    private Label accountLabel;

    @FXML 
    private Label addressLabel;

    @FXML 
    private Label businessTypeLabel;

    @FXML 
    private Label jobLabel;

    @FXML 
    private Label sectorLabel;

    @FXML 
    private Label annualIncomeLabel;

    @FXML 
    private Label loanPlanLabel;

    @FXML 
    private Label profitSharingLabel;

    @FXML 
    private ImageView ktpImageView;

    @FXML 
    private ChoiceBox<String> verificationChoiceBox;

    @FXML 
    private ChoiceBox<String> creditScoreChoiceBox;

    @FXML 
    private Button okButton;

    @FXML 
    private Button cancelButton;

    private SubmissionData submission;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi ChoiceBox
        verificationChoiceBox.setItems(FXCollections.observableArrayList("VERIFIED", "PENDING"));
        creditScoreChoiceBox.setItems(FXCollections.observableArrayList("A+", "A", "B", "C"));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSubmissionData(SubmissionData submission) {
        this.submission = submission;
        populateFields();
    }

    private void populateFields() {
        if (submission != null) {
            fullNameLabel.setText(submission.getName());
            emailLabel.setText(submission.getEmail());
            nikLabel.setText(submission.getNik());
            phoneNumberLabel.setText(submission.getPhoneNumber());
            npwpLabel.setText(submission.getNpwp());
            accountLabel.setText(submission.getAccountNumber());
            addressLabel.setText(submission.getAddress());
            businessTypeLabel.setText(submission.getBusinessType());
            jobLabel.setText(submission.getJob());
            sectorLabel.setText(submission.getSector());
            annualIncomeLabel.setText(String.format("Rp %.2f", submission.getAnnualIncome()));
            loanPlanLabel.setText(String.format("Rp %.2f", submission.getLoanPlan()));
            profitSharingLabel.setText(String.format("Rp %.2f", submission.getProfitSharingAmount()));

            // Munculin atau Ngeload foto KTP
            if (submission.getKtpImagePath() != null && !submission.getKtpImagePath().isEmpty()) {
                try {
                    Image image = new Image(submission.getKtpImagePath());
                    ktpImageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading KTP image: " + e.getMessage());
                }
            }

            verificationChoiceBox.setValue(submission.getVerificationStatus());
            creditScoreChoiceBox.setValue(submission.getCreditScore());
        }
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void okAction(ActionEvent event) {
        submission.setVerificationStatus(verificationChoiceBox.getValue());
        submission.setCreditScore(creditScoreChoiceBox.getValue());
    
        System.out.println("Updating submission...");
        System.out.println("Credit Score: " + submission.getCreditScore());
        System.out.println("Verification Status: " + submission.getVerificationStatus());
    
        if (updateXmlFile()) {
            System.out.println("XML update successful");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Update Successful");
            alert.setHeaderText(null);
            alert.setContentText("The data has been updated successfully.");
            alert.showAndWait();
            stage.close();
        } else {
            System.out.println("XML update failed");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update data. Please try again.");
            alert.showAndWait();
        }
    }

    //Method untuk memperbarui file XML
    private boolean updateXmlFile() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[]{SubmissionData.class});
        xstream.alias("submissions", List.class);
        xstream.alias("submission", SubmissionData.class);
        xstream.processAnnotations(SubmissionData.class);
    
        try {
            File xmlFile = new File("submission_data.xml");
            System.out.println("Reading XML file from: " + xmlFile.getAbsolutePath());
            // Membaca data dari file XML
            List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(xmlFile);
            System.out.println("Number of submissions read: " + submissions.size());
    
            boolean updated = false;
            // Mencari dan memperbarui data pengajuan yang sesuai dengan XML
            for (SubmissionData sub : submissions) {
                if (sub.getName().equals(submission.getName()) && sub.getNik().equals(submission.getNik())) {
                    System.out.println("Updating submission for: " + sub.getName());
                    System.out.println("Old credit score: " + sub.getCreditScore());
                    System.out.println("Old verification status: " + sub.getVerificationStatus());
                    
                    sub.setCreditScore(submission.getCreditScore());
                    sub.setVerificationStatus(submission.getVerificationStatus());
                    
                    System.out.println("New credit score: " + sub.getCreditScore());
                    System.out.println("New verification status: " + sub.getVerificationStatus());
                    updated = true;
                    break;
                }
            }
    
            if (!updated) {
                System.out.println("No matching submission found for: " + submission.getName());
                return false;
            }
            // Menyimpan data yang diperbarui ke file XML
            FileOutputStream fos = new FileOutputStream(xmlFile);
            xstream.toXML(submissions, fos);
            fos.close();
    
            System.out.println("XML file updated successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Error updating XML file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

