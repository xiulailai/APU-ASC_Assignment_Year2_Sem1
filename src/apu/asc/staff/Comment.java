package apu.asc.staff;

public class Comment implements FileStorable {
    private String commentId;
    private String appointmentId;
    private String customerId;
    private String targetUserId;
    private String commentText;
    
    //constructor
    public Comment(String commentId, String appointmentId, String customerId, String targetUserId, String commentText) {
        this.commentId = commentId;
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.targetUserId = targetUserId;
        this.commentText = commentText;
    }
    
    //getter
    public String getCommentId(){ return commentId; }
    public String getAppointmentId(){ return appointmentId; }
    public String getCustomerId(){ return customerId; }
    public String getTargetUserId(){ return targetUserId; }
    public String getCommentText(){ return commentText; }
    
    //convert object to txt file
    public String toFileString() {
        return String.join(",",commentId, appointmentId, customerId, targetUserId, commentText);
    }

    //let system have 5 attributes in the txt file, some comment have "," so just split it with first four ","
    public static Comment fromFileString(String line) {
        String[] p = line.split(",", 5);
        return new Comment(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim());
    }
    
    @Override
    public String toString() {
        return String.format("[%s] Appointment: %s | From: %s → %s\n  \"%s\"",commentId, appointmentId, customerId, targetUserId, commentText);
    }
}
