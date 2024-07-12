package fxml_helloworld.Dashboard_Investor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@XStreamAlias("investor-data")
public class InvestorData {
    private String name;
    private String email;
    private String pin;
    private String bank;
    private String accountNumber;
    private double balance;
    @XStreamAlias("topUpHistory")
    private List<TopUpRecord> topUpHistory;
    @XStreamAlias("paymentHistory")
    private List<PaymentRecord> paymentHistory;
    @XStreamAlias("receivedPayment")
    private List<ReceivedPaymentRecord> receivedPayments;
    private double income;
    private double outcome;

    public InvestorData() {
        this.topUpHistory = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
        this.receivedPayments = new ArrayList<>();
        this.balance = 0;
        this.income = 0;
    }

    public void addPaymentRecord(PaymentRecord paymentRecord) {
        if (paymentHistory == null) {
            paymentHistory = new ArrayList<>();
        }
        paymentHistory.add(paymentRecord);
    }
    
    public List<PaymentRecord> getPaymentHistory() {
        if (paymentHistory == null) {
            paymentHistory = new ArrayList<>();
        }
        return paymentHistory;
    }

    public List<TopUpRecord> getTopUpHistory() {
        if (topUpHistory == null) {
            topUpHistory = new ArrayList<>();
        }
        return topUpHistory;
    }

    public void receivePayment(double amount, String payerName, String payerAccountNumber) {
        ReceivedPaymentRecord record = new ReceivedPaymentRecord(
            amount, payerName, payerAccountNumber, LocalDateTime.now(), 
            this.getName(), this.getAccountNumber(), "Received"
        );
        this.receivedPayments.add(record);
        this.setIncome(this.getIncome() + amount);
        this.setBalance(this.getBalance() + amount);
    }

    // Getters and setters remain the same
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addTopUp(double amount) {
        this.balance += amount;
        this.topUpHistory.add(new TopUpRecord(amount));
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getOutcome() {
        return outcome;
    }

    public void setOutcome(double outcome) {
        this.outcome = outcome;
    }

    public void addIncome(double amount) {
        this.income += amount;
    }

    public void addOutcome(double amount) {
        this.outcome += amount;
    }

    public List<ReceivedPaymentRecord> getReceivedPayments() {
        if (receivedPayments == null) {
            receivedPayments = new ArrayList<>();
        }
        return receivedPayments;
    }

    public void setReceivedPayments(List<ReceivedPaymentRecord> receivedPayments) {
        this.receivedPayments = receivedPayments;
    }

    public boolean matchesCredentials(String bank, String accountNumber, String name, String email, String pin) {
        return this.bank.equals(bank) &&
               this.accountNumber.equals(accountNumber) &&
               this.name.equals(name) &&
               this.email.equals(email) &&
               this.pin.equals(pin);
    }

    private Object readResolve() {
        if (topUpHistory == null) {
            topUpHistory = new ArrayList<>();
        }
        if (paymentHistory == null) {
            paymentHistory = new ArrayList<>();
        }
        return this;
    }
}
