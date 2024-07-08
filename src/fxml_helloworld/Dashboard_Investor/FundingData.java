package fxml_helloworld.Dashboard_Investor;

public class FundingData {
    private String borrowerName;
    private double remainingLoanAmount;
    private double profitSharingAmount;

    // Constructor
    public FundingData(String borrowerName, double remainingLoanAmount, double profitSharingAmount) {
        this.borrowerName = borrowerName;
        this.remainingLoanAmount = remainingLoanAmount;
        this.profitSharingAmount = profitSharingAmount;
    }

    public String getBorrowerName() {
        return borrowerName;
    }
    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }
    public double getRemainingLoanAmount() {
        return remainingLoanAmount;
    }
    public void setRemainingLoanAmount(double remainingLoanAmount) {
        this.remainingLoanAmount = remainingLoanAmount;
    }
    public double getProfitSharingAmount() {
        return profitSharingAmount;
    }
    public void setProfitSharingAmount(double profitSharingAmount) {
        this.profitSharingAmount = profitSharingAmount;
    }
    
}
