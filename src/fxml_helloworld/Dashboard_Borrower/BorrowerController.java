package fxml_helloworld.Dashboard_Borrower;

import fxml_helloworld.*;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import fxml_helloworld.OpenScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert.AlertType;
   
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

// import untuk menangani format XML
import com.thoughtworks.xstream.XStream;

public class BorrowerController implements Initializable {

    // Home page
    @FXML
    private BorderPane borrowerSideBar;

    @FXML
    private Button homePage;

    @FXML
    private Button submissionPage;

    @FXML
    private Button paymentPage;

    @FXML
    private Button borrowerLogoutButton;

    // Loan Application Page
    @FXML
    private ScrollPane loanApplicationScrollPane;

    @FXML
    private TextField addressLoan;

    @FXML
    private TextField annualIncomeLoan;

    @FXML
    private TextField emailLoan;

    @FXML
    private PasswordField passwordLoan;

    @FXML
    private TextField jobLoan;

    @FXML
    private ImageView ktpImageLoan;

    @FXML
    private TextField loanPlan;

    @FXML
    private TextField nameLoan;

    @FXML
    private TextField nikLoan;

    @FXML
    private TextField npwpLoan;

    @FXML
    private TextField accountLoan;

    @FXML
    private TextField phoneLoan;

    @FXML
    private Button processLoanApplication;

    @FXML
    private TextField sectorLoan;

    @FXML
    private TextField typeLoan;

    @FXML
    private Button uploadKTP;

    private OpenScene openScene = new OpenScene();

    private SubmissionData submissionData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submissionData = new SubmissionData();
    }

    @FXML
    private void uploadKTP(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select KTP Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            ktpImageLoan.setImage(new Image(imagePath));
            submissionData.setKtpImagePath(imagePath);
        }
    }

    @FXML
    private void processLoanApplication(ActionEvent event) {
        // Gather data from form fields
        submissionData.setName(nameLoan.getText());
        submissionData.setEmail(emailLoan.getText());
        submissionData.setPassword(passwordLoan.getText());
        submissionData.setNik(nikLoan.getText());
        submissionData.setPhoneNumber(phoneLoan.getText());
        submissionData.setNpwp(npwpLoan.getText());
        submissionData.setAccountNumber(accountLoan.getText());
        submissionData.setAddress(addressLoan.getText());
        submissionData.setBusinessType(typeLoan.getText());
        submissionData.setJob(jobLoan.getText());
        submissionData.setSector(sectorLoan.getText());
        submissionData.setAnnualIncome(Double.parseDouble(annualIncomeLoan.getText()));
        submissionData.setLoanPlan(Double.parseDouble(loanPlan.getText()));

        // Open Loan Detail popup
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Dashboard_Borrower/LoanDetail.fxml"));
            Parent root = loader.load();
            
            LoanDetailController detailController = loader.getController();
            detailController.setSubmissionData(submissionData);
            
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
            
            if (detailController.isSubmissionSuccessful()) {
                saveSubmissionToXML();
                showSuccessMessage();
                clearForm();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSubmissionToXML() {
        try {
            // Inisialisasi XStream untuk serialisasi XML
            XStream xstream = new XStream(new StaxDriver());
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            // Memproses anotasi XStream pada kelas SubmissionData
            xstream.processAnnotations(SubmissionData.class);
            
            // Mengatur keamanan XStream (Sebenarnya nggak terlalu perlu)
            XStream.setupDefaultSecurity(xstream);

            //array list menyimpan objek objek SubmissionData
            xstream.allowTypes(new Class[]{SubmissionData.class, ArrayList.class});
    
            File file = new File("submission_data.xml");
            List<SubmissionData> submissions = new ArrayList<>();
            
            // Cek apakah file sudah ada, jika iya, maka membaca data yang ada di file
            if (file.exists() && file.length() > 0) {
                try (FileReader reader = new FileReader(file)) {
                    submissions = (List<SubmissionData>) xstream.fromXML(reader);
                }
            }
            
            // Menambahkan data pengajuan baru ke dalam daftar
            submissionData.setSubmissionDate(java.time.LocalDate.now());
            submissions.add(submissionData);
            // Menulis data ke file XML
            try (FileWriter writer = new FileWriter(file)) {
                xstream.toXML(submissions, writer);
            }
    
            showSuccessMessage();
    
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Error saving submission: " + e.getMessage());
        }
    }
    
    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    //Tambahan Method
    private void showSuccessMessage() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pengajuan Berhasil");
        alert.setHeaderText(null);
        alert.setContentText("Pengajuan pinjaman Anda telah berhasil diproses dan disimpan.");
        alert.showAndWait();
    }
    
    private void clearForm() {
        nameLoan.clear();
        emailLoan.clear();
        passwordLoan.clear();
        nikLoan.clear();
        phoneLoan.clear();
        npwpLoan.clear();
        accountLoan.clear();
        addressLoan.clear();
        typeLoan.clear();
        jobLoan.clear();
        sectorLoan.clear();
        annualIncomeLoan.clear();
        loanPlan.clear();
        ktpImageLoan.setImage(null);
    }

    //Transisi Pindah-pindah Scene
    @FXML
    private void keHomePage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openScene.loadFXML("/fxml_helloworld/Dashboard_Borrower/Dashboard.fxml", stage, event);
    }

    @FXML
    private void keSubmissionPage(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Borrower/LoanSubmission.fxml");
        borrowerSideBar.setCenter(halaman);
    }

    @FXML
    private void kePaymentPage(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Borrower/MakePayment.fxml");
        borrowerSideBar.setCenter(halaman);
    }
    
    @FXML
    private void logoutButton(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/Login.fxml", borrowerLogoutButton);
    }

}
