package apu.asc.staff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseFileHelper {
    
    protected static final String FILE_USERS = "Data/users.txt";
    protected static final String FILE_CUSTOMERS = "Data/customers.txt";
    protected static final String FILE_TECHNICIANS = "Data/technicians.txt";
    protected static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    protected static final String FILE_MANAGERS = "Data/managers.txt";
    protected static final String FILE_SERVICES = "Data/services.txt";
    protected static final String FILE_APPOINTMENTS = "Data/appointments.txt";
    protected static final String FILE_PAYMENTS = "Data/payments.txt";
    protected static final String FILE_FEEDBACKS = "Data/feedback.txt";
    protected static final String FILE_COMMENTS = "Data/comments.txt";
 
    protected static final String COMMENT_CHAR = "#";
    
    // File Input and Output
    protected List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank())
                    lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("readLines error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }
    
    protected List<String> readRawLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("readRawLines error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }
    
    protected void writeLines(String filePath, String header, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            if (header != null) { bw.write(header); bw.newLine(); }
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) {
            System.err.println("writeLines error [" + filePath + "]: " + e.getMessage());
        }
    }
    
    protected void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("appendLine error [" + filePath + "]: " + e.getMessage());
        }
    }
    
    //Shared entity loaders
    protected List<Customer> loadAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readLines(FILE_CUSTOMERS)) {
            try { list.add(Customer.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Technician> loadAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readLines(FILE_TECHNICIANS)) {
            try { list.add(Technician.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<CounterStaff> loadAllCounterStaff() {
        List<CounterStaff> list = new ArrayList<>();
        for (String line : readLines(FILE_COUNTERSTAFF)) {
            try { list.add(CounterStaff.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Manager> loadAllManagers() {
        List<Manager> list = new ArrayList<>();
        for (String line : readLines(FILE_MANAGERS)) {
            try { list.add(Manager.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Service> loadAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readLines(FILE_SERVICES)) {
            try { list.add(Service.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Appointment> loadAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readLines(FILE_APPOINTMENTS)) {
            try { list.add(Appointment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Payment> loadAllPayments() {
        List<Payment> list = new ArrayList<>();
        for (String line : readLines(FILE_PAYMENTS)) {
            try { list.add(Payment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Feedback> loadAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        for (String line : readLines(FILE_FEEDBACKS)) {
            try { list.add(Feedback.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    protected List<Comment> loadAllComments() {
        List<Comment> list = new ArrayList<>();
        for (String line : readLines(FILE_COMMENTS)) {
            try { list.add(Comment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }
    
    //Helper method
    protected Customer findCustomerById(String customerId) {
        for (Customer c : loadAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) return c;
        }
        return null;
    }
    
    protected Technician findTechnicianById(String technicianId) {
        for (Technician t : loadAllTechnicians()) {
            if (t.getTechnicianId().equalsIgnoreCase(technicianId)) return t;
        }
        return null;
    }
    
    protected CounterStaff findCounterStaffById(String counterStaffId) {
        for (CounterStaff cs : loadAllCounterStaff()) {
            if (cs.getCounterStaffId().equalsIgnoreCase(counterStaffId)) return cs;
        }
        return null;
    }
    
    protected Appointment findAppointmentById(String appointmentId) {
        for (Appointment a : loadAllAppointments()) {
            if (a.getAppointmentId().equals(appointmentId)) return a;
        }
        return null;
    }
    
    public String lookupCustomerName(String customerId) {
        if (customerId == null) return "-";
        Customer c = findCustomerById(customerId);
        return c != null ? c.getName() : customerId;
    }

    public String lookupTechnicianName(String technicianId) {
        if (technicianId == null || technicianId.equals("TBD")) return "TBD";
        Technician t = findTechnicianById(technicianId);
        return t != null ? t.getName() : technicianId;
    }
    
    public String lookupCounterStaffName(String counterStaffId) {
        if (counterStaffId == null) return "-";
        CounterStaff cs = findCounterStaffById(counterStaffId);
        return cs != null ? cs.getName() : counterStaffId;
    }
 
    public String lookupServiceName(String serviceId) {
        if (serviceId == null) return "-";
        for (Service s : loadAllServices()) {
            if (s.getServiceId().equals(serviceId)) return s.getServiceName();
        }
        return serviceId;
    }
 
    public double lookupServicePrice(String serviceId) {
        for (Service s : loadAllServices()) {
            if (s.getServiceId().equals(serviceId)) return s.getPrice();
        }
        return 0.0;
    }
    
    protected Map<String, String> buildServiceNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Service s : loadAllServices()) map.put(s.getServiceId(), s.getServiceName());
        return map;
    }
    
    //Validate
    protected String validatePersonFields(String name, String ageStr, String phone,
                                          String email, String address, String nationality) {
        if (name == null || name.isBlank() || ageStr == null || ageStr.isBlank()
                || phone == null || phone.isBlank() || email == null || email.isBlank()
                || address == null || address.isBlank() || nationality == null || nationality.isBlank()) {
            return "ERR: All fields are required.";
        }
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 100) return "ERR: Age must be between 0 and 100.";
        } catch (NumberFormatException e) {
            return "ERR: Age must be a valid number.";
        }
        if (!phone.trim().matches("\\d{10,12}")) {
            return "ERR: Phone must be 10 to 12 digits.";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "ERR: Enter a valid email address.";
        }
        return null;
    }
    
    // Creates a new row in users.txt with an explicit username and password (used by Manager)
    protected void createUserAccount(String roleRefId, String username, String password, String role) {
        String userId = generateUserId();
        appendLine(FILE_USERS, String.join(",", userId, username, password, role, roleRefId));
    }
 
    // Creates a default user account (auto-generated username/password) for customers added by counter staff
    protected String createDefaultUserAccount(String roleRefId, String name) {
        int maxId = 0;
        for (String line : readLines(FILE_USERS)) {
            String[] data = line.split(",", 5);
            if (data.length >= 1) {
                try {
                    int num = Integer.parseInt(data[0].trim().substring(1));
                    if (num > maxId) maxId = num;
                } catch (Exception ignored) {}
            }
        }
        String userId = String.format("U%03d", maxId + 1);
        String idNum = roleRefId.substring(1);
        String safeName = name.replaceAll("\\s+", "").toLowerCase();
        String username = safeName.substring(0, Math.min(4, safeName.length())) + idNum;
        String password = "pass" + idNum;
        appendLine(FILE_USERS, String.join(",", userId, username, password, "customer", roleRefId));
        return "OK";
    }
 
    // Removes the users.txt row whose roleRefId column matches the given ID
    protected void removeUserByRoleRef(String roleRefId) {
        List<String> allUsers = readLines(FILE_USERS);
        List<String> updated  = new ArrayList<>();
        for (String line : allUsers) {
            String[] data = line.split(",", 5);
            if (data.length == 5 && data[4].trim().equals(roleRefId)) continue;
            updated.add(line);
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
    }
 
    // Updates the password for the user whose roleRefId matches (used by Customer and Technician self-edit)
    protected void updatePasswordByRoleRef(String roleRefId, String newPassword) {
        List<String> rawLines = readRawLines(FILE_USERS);
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) { updated.add(raw); continue; }
            String[] p = raw.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) {
                p[2] = newPassword;
                updated.add(String.join(",", p));
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
    }
 
    // Returns the username for a given roleRefId, or "" if not found
    public String lookupUsername(String roleRefId) {
        if (roleRefId == null) return "";
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) return p[1].trim();
        }
        return "";
    }
    
    //ID generate
    protected String generateUserId() {
        int max = 0;
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length >= 1) {
                try {
                    int num = Integer.parseInt(p[0].trim().substring(1));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("U%03d", max + 1);
    }
}
