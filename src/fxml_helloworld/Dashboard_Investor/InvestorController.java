package fxml_helloworld.Dashboard_Investor;

import fxml_helloworld.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;

import fxml_helloworld.OpenScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
   
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDateTime;

public class InvestorController implements Initializable {

    @FXML 
    private Button invDashboardBtn;

    @FXML
    private Button logOutInvestorBtn;

    @FXML 
    private Button invFundingBtn;

    @FXML 
    private BorderPane investorMainPane;

    @FXML 
    private ChoiceBox<String> bankTopup;

    @FXML 
    private PasswordField accountTopup;

    @FXML 
    private TextField mountTopup;

    @FXML 
    private Button topupButton;

    @FXML 
    private Label balanceLabel;

    @FXML 
    private Label balanceLabel2;

    @FXML 
    private Label nameCredit;
    
    @FXML 
    private Label nameProfil;

    @FXML
    private Label outcomeLabel;

    @FXML
    private Label incomeLabel;

    @FXML
    private Button grafikButton;

    @FXML
    private AreaChart<String, Number> incomeAreaChart;

    private OpenScene openScene = new OpenScene();
    private XStream xstream;
    private InvestorData investorData;
    private List<InvestorData> investors;


    public InvestorController() {
        xstream = new XStream(new StaxDriver());
        xstream.addPermission(NoTypePermission.NONE);
        xstream.allowTypes(new Class[]{
            InvestorData.class, 
            TopUpRecord.class, 
            PaymentRecord.class,
            LocalDateTime.class
        });
        xstream.allowTypesByWildcard(new String[]{
            "fxml_helloworld.Dashboard_Investor.**", 
            "java.time.**",
            "java.util.**"
        });
        xstream.processAnnotations(InvestorData.class);
        xstream.processAnnotations(TopUpRecord.class);
        xstream.processAnnotations(PaymentRecord.class);
        
        xstream.alias("investor-data", InvestorData.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bankTopup.setItems(FXCollections.observableArrayList("Master Card", "BNI", "Gopay", "BRI", "ESPAY Dana", "Mandiri", "OVO"));
        investors = new ArrayList<>();
        loadUserData();
        setupTopUpButton();
        setupCurrencyFormatters();
    }

    private void loadUserData() {
        try {
            File file = new File("investorData.xml");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                investorData = (InvestorData) xstream.fromXML(fis);
                fis.close();
            } else {
                investorData = new InvestorData();
            }
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading user data");
            investorData = new InvestorData();
        }
    }

    private void initializeNewInvestorData() {
        investorData = new InvestorData();
    }

    private void updateUI() {
        nameProfil.setText(investorData.getName());
        nameCredit.setText(investorData.getName());
        balanceLabel.setText("Rp " + formatRupiahOutput(investorData.getBalance()));
        balanceLabel2.setText("Rp " + formatRupiahOutput(investorData.getBalance()));
        outcomeLabel.setText("- Rp " + formatRupiahOutput(investorData.getOutcome()));
        incomeLabel.setText("+ Rp " + formatRupiahOutput(investorData.getIncome()));
    }

    private void setupCurrencyFormatters() {
        if (mountTopup != null) {
            mountTopup.setTextFormatter(createCurrencyFormatter());
        }
    }

    private TextFormatter<String> createCurrencyFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText().replaceAll("[^\\d]", "");
            if (newText.isEmpty()) {
                return change; // If the new text is empty, return the change as is.
            }
            try {
                long value = Long.parseLong(newText);
                change.setText(formatRupiahInput(value));
                change.setRange(0, change.getControlText().length());
                change.setAnchor(change.getCaretPosition());
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        });
    }

    private String formatRupiahInput(long amount) {
        return String.format("%,d", amount).replace(",", ".");
    }

    private String formatRupiahOutput(double amount) {
        return String.format("%,.0f", amount);
    }

    private double parseRupiah(String amount) {
        // Hapus semua karakter non-digit
        String numericString = amount.replaceAll("[^\\d]", "");
        // Parse sebagai double
        return Double.parseDouble(numericString);
    }

    private void setupTopUpButton() {
        topupButton.setOnAction(event -> {
            String selectedBank = bankTopup.getValue();
            String accountNumber = accountTopup.getText();
            String amount = mountTopup.getText();

            if (selectedBank == null || accountNumber.isEmpty() || amount.isEmpty()) {
                showAlert("Please fill all fields");
                return;
            }

            showTopUpConfirmation(selectedBank, accountNumber, amount);
        });
    }

    private void showTopUpConfirmation(String bank, String accountNumber, String amount) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_helloworld/Dashboard_Investor/popupTopup.fxml"));
            Parent root = loader.load();
            
            PopUpController popupController = loader.getController();
            popupController.setTopUpDetails(bank, accountNumber, amount);

            // if (investorData.getName() != null) {
            //     popupController.setPrefilledData(investorData.getName(), investorData.getEmail());
            // }

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Top Up Confirmation");
            popupStage.setScene(new Scene(root));
            
            popupStage.showAndWait();

            if (popupController.isConfirmed()) {
                processTopUp(bank, accountNumber, amount, 
                             popupController.getName(), 
                             popupController.getEmail(), 
                             popupController.getPassword());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error opening top-up confirmation window");
        }
    }

    private void processTopUp(String bank, String accountNumber, String amount, 
                        String name, String email, String pin) {
        double topUpAmount = parseRupiah(amount);

        if (investorData == null) {
        investorData = new InvestorData();
        }

        // Update data investor jika belum diset
        if (investorData.getName() == null || investorData.getName().isEmpty()) {
            investorData.setBank(bank);
            investorData.setAccountNumber(accountNumber);
            investorData.setName(name);
            investorData.setEmail(email);
            investorData.setPin(pin);
        } else {
            // Verifikasi kredensial jika data sudah ada
            if (!investorData.matchesCredentials(bank, accountNumber, name, email, pin)) {
                showAlert("Invalid credentials. Please check your information.");
                return;
            }
        }

        // Lakukan top up
        investorData.addTopUp(topUpAmount);

        saveUserData();
        nameCredit.setText(investorData.getName());
        updateUI();
        showAlert("Top Up successful!");
    }

    @FXML
    private void updateChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Income");
        
    
        // Pastikan investorData sudah dimuat
        if (investorData != null) {
            // Tambahkan data income ke series
            series.getData().add(new XYChart.Data<>("Income", investorData.getIncome()));
            
            // Jika Anda ingin menambahkan balance juga
            series.getData().add(new XYChart.Data<>("Balance", investorData.getBalance()));
            
            // Jika Anda ingin menambahkan outcome juga
            series.getData().add(new XYChart.Data<>("Outcome", investorData.getOutcome()));
        }
    
        incomeAreaChart.getData().clear();
        incomeAreaChart.getData().add(series);
    }
    

    private void saveUserData() {
        try {
            FileOutputStream fos = new FileOutputStream("investorData.xml");
            xstream.toXML(investorData, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error saving data");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void keFunding(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Investor/Funding.fxml");
        investorMainPane.setCenter(halaman);
    }

    @FXML
    private void kePortfolio(ActionEvent event) {
        try {
            OpenScene object = new OpenScene();
            Pane halaman = object.getPane("/fxml_helloworld/Dashboard_Investor/Portfolio.fxml");
            if (halaman != null) {
                investorMainPane.setCenter(halaman);
            } else {
                System.out.println("Halaman Portfolio tidak dapat dimuat.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saat memuat Portfolio: " + e.getMessage());
        }
    }

    @FXML
    private void keDashboard(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openScene.loadFXML("/fxml_helloworld/Dashboard_Investor/dashboard.fxml", stage, event);
    }

    @FXML
    private void logoutButton(ActionEvent event) {
        openScene.openScene("/fxml_helloworld/Login_Scene/Login.fxml", logOutInvestorBtn);
    }
}