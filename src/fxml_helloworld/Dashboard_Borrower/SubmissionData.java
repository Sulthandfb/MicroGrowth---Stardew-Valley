package fxml_helloworld.Dashboard_Borrower;

import java.time.LocalDate;

// import untuk menangani format XML
import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("submission")
public class SubmissionData {
    private String name;
    private String email;
    private String password;
    private String nik;
    private String phoneNumber;

    @XStreamAlias("ktpImage")
    private String ktpImagePath;
    private String npwp;
    private String accountNumber;
    private String address;
    private String businessType;
    private String job;
    private String sector;
    private double annualIncome;
    private double loanPlan;
    private String installmentType;
    private double installmentAmount;
    private double profitSharingPercentage;
    private double profitSharingAmount;
    private LocalDate submissionDate;
    private String verificationStatus;
    private String creditScore;
    private String paymentStatus;
    private double remainingLoanAmount;
    private double income;

    public SubmissionData() {
        this.submissionDate = LocalDate.now();
    }

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

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getKtpImagePath() {
        return ktpImagePath;
    }

    public void setKtpImagePath(String ktpImagePath) {
        this.ktpImagePath = ktpImagePath;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public double getLoanPlan() {
        return loanPlan;
    }

    public void setLoanPlan(double loanPlan) {
        this.loanPlan = loanPlan;
    }

    public String getInstallmentType() {
        return installmentType;
    }

    public void setInstallmentType(String installmentType) {
        this.installmentType = installmentType;
    }

    public double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public double getProfitSharingPercentage() {
        return profitSharingPercentage;
    }

    public void setProfitSharingPercentage(double profitSharingPercentage) {
        this.profitSharingPercentage = profitSharingPercentage;
    }

    public double getProfitSharingAmount() {
        return profitSharingAmount;
    }
    
    public void setProfitSharingAmount(double profitSharingAmount) {
        this.profitSharingAmount = profitSharingAmount;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(String creditScore) {
        this.creditScore = creditScore;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getRemainingLoanAmount() {
        return remainingLoanAmount;
    }

    public void setRemainingLoanAmount(double remainingLoanAmount) {
        this.remainingLoanAmount = remainingLoanAmount;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void addIncome(double amount) {
        this.income += amount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Buat Ngitung total pembayaran
    public double getTotalPaymentAmount() {
        return this.loanPlan + this.profitSharingAmount;
    }
    
    public double getRemainingTotalPaymentAmount() {
        return this.remainingLoanAmount + (this.profitSharingAmount * (this.remainingLoanAmount / this.loanPlan));
    }
    
}
