package fxml_helloworld.Dashboard_Investor;

import java.time.LocalDateTime;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Payment-Record")
public class PaymentRecord {
    private double amount;
    private String recipientName;
    private String recipientAccountNumber;
    private LocalDateTime timestamp;
    private String investorName;
    private String investorEmail;
    private String investorAccountNumber;
    private String paymentStatus;

    public PaymentRecord(double amount, String recipientName, String recipientAccountNumber, 
                         String investorName, String investorEmail, String investorAccountNumber) {
        this.amount = amount;
        this.recipientName = recipientName;
        this.recipientAccountNumber = recipientAccountNumber;
        this.timestamp = LocalDateTime.now();
        this.investorName = investorName;
        this.investorEmail = investorEmail;
        this.investorAccountNumber = investorAccountNumber;
        this.paymentStatus = "Completed"; // Atau status awal yang sesuai
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getInvestorEmail() {
        return investorEmail;
    }

    public void setInvestorEmail(String investorEmail) {
        this.investorEmail = investorEmail;
    }

    public String getInvestorAccountNumber() {
        return investorAccountNumber;
    }

    public void setInvestorAccountNumber(String investorAccountNumber) {
        this.investorAccountNumber = investorAccountNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isCompleted() {
        return "Completed".equals(this.paymentStatus);
    }

    public void markAsCompleted() {
        this.paymentStatus = "Completed";
    }
}