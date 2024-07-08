package fxml_helloworld.Dashboard_Investor;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;

import fxml_helloworld.OpenScene;
import fxml_helloworld.Dashboard_Admin.ApplicantDetailController;
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

public class DetailLoan implements Initializable{

    @FXML
    private Label accountDetail;

    @FXML
    private Label addressDetail;

    @FXML
    private Label annualDetail;

    @FXML
    private Label businessDetail;

    @FXML
    private Label emailDetail;

    @FXML
    private Label jobDetail;

    @FXML
    private ImageView ktpDetail;

    @FXML
    private Label loanDetail;

    @FXML
    private Label nameDetail;

    @FXML
    private Label nikDetail;

    @FXML
    private Label npwpDetail;

    @FXML
    private Button okButton;

    @FXML
    private Label phoneDetail;

    @FXML
    private Label profitDetail;

    @FXML
    private Label sectorDetail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // Method Memunculkan Detail Pengajuan
    public void setSubmissionData(SubmissionData submission) {
        nameDetail.setText(submission.getName());
        emailDetail.setText(submission.getEmail());
        nikDetail.setText(submission.getNik());
        phoneDetail.setText(submission.getPhoneNumber());
        npwpDetail.setText(submission.getNpwp());
        addressDetail.setText(submission.getAddress());
        businessDetail.setText(submission.getBusinessType());
        jobDetail.setText(submission.getJob());
        sectorDetail.setText(submission.getSector());
        annualDetail.setText(String.format("Rp %.2f", submission.getAnnualIncome()));
        loanDetail.setText(String.format("Rp %.2f", submission.getLoanPlan()));
        accountDetail.setText(submission.getAccountNumber());
        profitDetail.setText(String.format("Rp %.2f", submission.getProfitSharingAmount()));
        
        // Munculin atau Ngeload foto KTP
        if (submission.getKtpImagePath() != null && !submission.getKtpImagePath().isEmpty()) {
            try {
                Image image = new Image(submission.getKtpImagePath());
                ktpDetail.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading KTP image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleOkButton(ActionEvent event) {
        ((Stage) okButton.getScene().getWindow()).close();
    }
    
}
