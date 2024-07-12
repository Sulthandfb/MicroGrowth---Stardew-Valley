package fxml_helloworld.Dashboard_Borrower;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import fxml_helloworld.Dashboard_Borrower.SubmissionData;
import fxml_helloworld.Dashboard_Investor.InvestorData;
import fxml_helloworld.Dashboard_Investor.PaymentRecord;
import fxml_helloworld.Dashboard_Investor.ReceivedPaymentRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DetailPaymentController implements Initializable{
    @FXML
    private PasswordField accountNumberField;

    @FXML
    private Label accountRecipientLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailPayField;

    @FXML
    private TextField nameCardField;

    @FXML
    private Label nameCardLabel;

    @FXML
    private Label nameRecipientLabel;

    @FXML
    private PasswordField passwordCardField;

    @FXML
    private Button payBack;

    private SubmissionData submissionData;
    private InvestorData investorData;
    private PaymentRecord paymentData;

    private boolean dataChanged = false;

    public boolean isDataChanged() {
        return dataChanged;
    }   

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        payBack.setOnAction(event -> handlePayBack());
        cancelButton.setOnAction(event -> closeWindow());
    }

    public void setPaymentData(PaymentRecord paymentData) {
        this.paymentData = paymentData;
        updatePaymentInfo();
    }

    public void setSubmissionData(SubmissionData submissionData) {
        this.submissionData = submissionData;
        updateBorrowerInfo();
    }

    public void setInvestorData(InvestorData investorData) {
        this.investorData = investorData;
    }

    private void updatePaymentInfo() {
        if (paymentData != null) {
            nameRecipientLabel.setText(paymentData.getInvestorName());
            accountRecipientLabel.setText(paymentData.getInvestorAccountNumber());
        }
    }

    private void updateBorrowerInfo() {
        if (submissionData != null) {
            nameCardLabel.setText(submissionData.getName());
        }
    }

    @FXML
    private void handlePayBack() {
        String name = nameCardField.getText();
        String accountNumber = accountNumberField.getText();
        String email = emailPayField.getText();
        String password = passwordCardField.getText();

        // Validasi input
        if (name.isEmpty() || accountNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        // Validasi data peminjam
        if (submissionData == null) {
            showAlert("Error", "Borrower data is not available.");
            return;
        }

        // Validasi nama, email, password, dan nomor rekening
        if (!name.equals(submissionData.getName()) || 
            !accountNumber.equals(submissionData.getAccountNumber()) ||
            !email.equals(submissionData.getEmail()) ||
            !password.equals(submissionData.getPassword())) {
            showAlert("Error", "Name, account number, email, or password does not match borrower data.");
            return;
        }

        // Validasi data pembayaran
        if (paymentData == null) {
            showAlert("Error", "Payment data is not available.");
            return;
        }

        try {
            double paymentAmount = paymentData.getAmount();
            double profitSharingAmount = submissionData.getProfitSharingAmount();
            
            if (investorData != null) {
                // Update balance investor dengan jumlah income
                investorData.setBalance(investorData.getBalance() + profitSharingAmount);
                
                // Update income investor hanya dengan jumlah bagi hasil untuk pembayaran ini
                investorData.setIncome(investorData.getIncome() + profitSharingAmount);
    
                // Create and add a new ReceivedPaymentRecord
                ReceivedPaymentRecord receivedPayment = new ReceivedPaymentRecord(
                    profitSharingAmount,  // Gunakan profitSharingForThisPayment
                    submissionData.getName(),
                    submissionData.getAccountNumber(),
                    LocalDateTime.now(),
                    investorData.getName(),
                    investorData.getAccountNumber(),
                    "Completed"
                );
                investorData.getReceivedPayments().add(receivedPayment);
            } else {
                showAlert("Warning", "Investor data is not available. Payment processed for borrower only.");
            }
    
            paymentData.setPaymentStatus("Completed");

            // Simpan perubahan
            saveSubmissionData();
            if (investorData != null) {
                saveInvestorData();
            }

            dataChanged = true;  // Tandai bahwa data telah berubah
            showAlert("Success", "Payment successful!");
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while processing the payment: " + e.getMessage());
        }
    }

    private void saveSubmissionData() {
        try {
            List<SubmissionData> submissions = loadSubmissions();
            if (submissions == null) {
                showAlert("Error", "Cannot save data due to loading error.");
                return;
            }
            
            boolean found = false;
            for (int i = 0; i < submissions.size(); i++) {
                if (submissions.get(i).getAccountNumber().equals(submissionData.getAccountNumber())) {
                    submissions.set(i, submissionData);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                showAlert("Error", "Submission data not found in the list.");
                return;
            }
    
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
    
            FileWriter writer = new FileWriter("submission_data.xml");
            xstream.toXML(submissions, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save submission data.");
        }
    }

    private void saveInvestorData() {
        try {
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.processAnnotations(InvestorData.class);
            xstream.processAnnotations(ReceivedPaymentRecord.class);
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
                return new ArrayList<>();
            }
            XStream xstream = new XStream(new StaxDriver());
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.processAnnotations(SubmissionData.class);  // Proses anotasi
            xstream.alias("submissions", List.class);
            xstream.alias("submission", SubmissionData.class);
            
            // Konfigurasi konverter untuk LocalDate
            xstream.registerConverter(new LocalDateConverter());
            
            // Tambahkan ini untuk menangani field yang mungkin tidak ada di kelas
            xstream.ignoreUnknownElements();
            
            List<SubmissionData> submissions = (List<SubmissionData>) xstream.fromXML(file);
            return submissions != null ? submissions : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load submission data. Data will not be modified.");
            return null; // Return null instead of empty list
        }
    }

    // Tambahkan kelas konverter untuk LocalDate
    private static class LocalDateConverter implements Converter {
        public boolean canConvert(Class clazz) {
            return clazz.equals(LocalDate.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            LocalDate date = (LocalDate) value;
            writer.setValue(date.toString());
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return LocalDate.parse(reader.getValue());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
