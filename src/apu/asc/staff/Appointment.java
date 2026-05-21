package apu.asc.staff;

public class Appointment implements FileStorable {
    private String appointmentId;
    private String customerId;
    private String technicianId;
    private String counterStaffId;
    private String serviceId;
    private String appointmentDate; // yyyy-MM-dd
    private String appointmentTime; // Hour:Minute
    private String status; // Pending | Assigned | Completed

    //constructor
    public Appointment(String appointmentId, String customerId, String technicianId, String counterStaffId, String serviceId, String appointmentDate, String appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.technicianId = technicianId;
        this.counterStaffId = counterStaffId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }
    
    //getter
    public String getAppointmentId(){ return appointmentId; }
    public String getCustomerId(){ return customerId; }
    public String getTechnicianId(){ return technicianId; }
    public String getCounterStaffId(){ return counterStaffId; }
    public String getServiceId(){ return serviceId; }
    public String getAppointmentDate(){ return appointmentDate; }
    public String getAppointmentTime(){ return appointmentTime; }
    public String getStatus(){ return status; }
    
    //setter (only status n technicianId can be changed or edit in this txt file)
    public void setStatus(String status) { this.status = status; }
    public void setTechnicianId(String id){ this.technicianId = id; }

    //convert object to txt file
    public String toFileString() {
        return String.join(",",appointmentId, customerId, technicianId,counterStaffId, serviceId,appointmentDate, appointmentTime, status);
    }
    
    //conver txt file to object
    public static Appointment fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new Appointment(p[0].trim(), p[1].trim(), p[2].trim(),p[3].trim(), p[4].trim(), p[5].trim(),p[6].trim(), p[7].trim());
    }
    
    @Override
    public String toString() {
        return String.format("[%s] Date: %s %s | Customer: %s | Service: %s | Status: %s",appointmentId, appointmentDate, appointmentTime,customerId, serviceId, status);
    }
}
