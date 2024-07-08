package fxml_helloworld.Dashboard_Investor;

import java.time.*;

public class ReceivedPaymentRecord {
    private double amount;
    private String payerName;
    private String payerAccountNumber;
    private LocalDateTime timestamp;
    private String investorName;
    private String investorAccountNumber;
    private String paymentStatus;

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getPayerName() {
        return payerName;
    }
    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
    public String getPayerAccountNumber() {
        return payerAccountNumber;
    }
    public void setPayerAccountNumber(String payerAccountNumber) {
        this.payerAccountNumber = payerAccountNumber;
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

    public ReceivedPaymentRecord(double amount, String payerName, String payerAccountNumber, 
                                 LocalDateTime timestamp, String investorName, 
                                 String investorAccountNumber, String paymentStatus) {
        this.amount = amount;
        this.payerName = payerName;
        this.payerAccountNumber = payerAccountNumber;
        this.timestamp = timestamp;
        this.investorName = investorName;
        this.investorAccountNumber = investorAccountNumber;
        this.paymentStatus = paymentStatus;
    }
}
