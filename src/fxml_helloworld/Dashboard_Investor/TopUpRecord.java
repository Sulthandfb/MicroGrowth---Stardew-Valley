package fxml_helloworld.Dashboard_Investor;

import java.time.LocalDateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Top-Up-Record")
public class TopUpRecord {
    private double amount;
    private LocalDateTime timestamp;

    public TopUpRecord(double amount) {
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
