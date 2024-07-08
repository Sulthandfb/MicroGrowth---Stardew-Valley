package fxml_helloworld.Dashboard_Borrower;

import fxml_helloworld.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
   
import javafx.scene.control.*;

public class LoanDetailController {
    @FXML
    private ChoiceBox<String> installmentTypeChoice;

    @FXML
    private TextField installmentAmountField;

    @FXML
    private ChoiceBox<String> profitSharingChoice;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private SubmissionData submissionData;
    private boolean submissionSuccessful = false;   // Untuk menandakan keberhasilan pengajuan

    // Inisialisasi ChoiceBox
    @FXML
    public void initialize() {
        installmentTypeChoice.getItems().addAll("Monthly", "weekly", "Annually");
        profitSharingChoice.getItems().addAll("10%", "15%", "20%", "25%", "30%","35%", "40%", "45%", "50%");
    }

    public void setSubmissionData(SubmissionData data) {
        this.submissionData = data;
    }

    // Menangani tombol Submit dan Cancel
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Mengisi data submissionData/ pengajuan
        submissionData.setInstallmentType(installmentTypeChoice.getValue());
        submissionData.setInstallmentAmount(Double.parseDouble(installmentAmountField.getText())); // Mengambil jumlah angsuran dan diubah ke double
        double profitSharingPercentage = Double.parseDouble(profitSharingChoice.getValue().replace("%", "")) / 100.0; //Menghitung persentase bagi hasil dalam bentuk desimal.
        submissionData.setProfitSharingPercentage(profitSharingPercentage); //Menetapkan nilai yang telah dihitung 
        
        // Menghitung Jumlah bagi hasil
        double loanAmount = submissionData.getLoanPlan(); // Ambil jumlah pinjaman yang diajukan
        double profitSharingAmount = loanAmount * profitSharingPercentage;  // Menghitung jumlah bagi hasil
        submissionData.setProfitSharingAmount(profitSharingAmount); // menetapkan hasil

        // Pengajuan berhasil
        submissionSuccessful = true;
        closeStage(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    // Mengecek apakah pengajuan berhasi
    public boolean isSubmissionSuccessful() {
        return submissionSuccessful;
    }
}
