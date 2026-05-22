package apu.asc.staff;

public class Feedback implements FileStorable {
    private String feedbackId;
    private String appointmentId;
    private String technicianId;
    private String feedbackText;
    
    //constructor
    public Feedback(String feedbackId, String appointmentId, String technicianId, String feedbackText) {
        this.feedbackId = feedbackId;
        this.appointmentId = appointmentId;
        this.technicianId = technicianId;
        this.feedbackText = feedbackText;
    }
    
    //getter
    public String getFeedbackId(){ return feedbackId; }
    public String getAppointmentId(){ return appointmentId; }
    public String getTechnicianId(){ return technicianId; }
    public String getFeedbackText(){ return feedbackText; }
    
    //convert object to txt file
    @Override
    public String toFileString() {
        return String.join(",",feedbackId, appointmentId, technicianId, feedbackText);
    }
    
    //split txt file attributes
    public static Feedback fromFileString(String line) {
        String[] p = line.split(",", 4);
        if (p.length < 4) {
            throw new IllegalArgumentException("Invalid feedback record: " + line);
        }
        return new Feedback(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim());
    }

    @Override
    public String toString() {
        return String.format("[%s] Appointment: %s | Technician: %s\n  \"%s\"",feedbackId, appointmentId, technicianId, feedbackText);
    }    
}
