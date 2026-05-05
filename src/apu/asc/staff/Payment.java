package apu.asc.staff;

public class Payment {
    private String paymentId;
    private String appointmentId;
    private String customerId;
    private double amount;
    private String paymentDate;   // yyyy-MM-dd
    private String receiptNo;

    //constructor
    public Payment(String paymentId, String appointmentId, String customerId, double amount, String paymentDate, String receiptNo) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.receiptNo = receiptNo;
    }

    //getter
    public String getPaymentId() { return paymentId; }
    public String getAppointmentId() { return appointmentId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getReceiptNo() { return receiptNo; }

    //convert object to txt file
    public String toFileString() {
        return String.join(",",paymentId, appointmentId, customerId,String.format("%.2f", amount), paymentDate, receiptNo);
    }

    //conver txt file to object
    public static Payment fromFileString(String line) {
        String[] p = line.split(",", 6);
        return new Payment(p[0].trim(), p[1].trim(), p[2].trim(),Double.parseDouble(p[3].trim()),p[4].trim(), p[5].trim());
    }

    @Override
    public String toString() {
        return String.format("[%s] Appt: %s | Customer: %s | Amount: RM %.2f | Date: %s | Receipt: %s",paymentId, appointmentId, customerId, amount, paymentDate, receiptNo);
    }
}