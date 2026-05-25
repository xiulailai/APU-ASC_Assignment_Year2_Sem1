package apu.asc.technician;
import apu.asc.staff.Technician;
import apu.asc.staff.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class TechnicianService extends BaseFileHelper {
    
    private static final String COMMENT_CHAR_LOCAL = "#"; // kept for clarity; same as COMMENT_CHAR in base
    private final String technicianId;
 
    public TechnicianService(String technicianId) {
        this.technicianId = technicianId;
    }
 
    public String getTechnicianId() { return technicianId; }
    
    // 1. View Profile
    public Technician getProfile() {
        Technician t = findTechnicianById(technicianId);
        if (t == null) throw new IllegalStateException("Technician not found: " + technicianId);
        return t;
    }
    
    // 2. Edit Profile
    public void editProfile(String name, String gender, int age, String phone,
                            String email, String address, String nationality,
                            String newPassword) throws IOException {
        List<String> rawLines = readRawLines(FILE_TECHNICIANS);
        List<String> updated  = new ArrayList<>();
 
        if (gender != null && !gender.isBlank()) {
            gender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
        }
 
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) { updated.add(raw); continue; }
            Technician t = Technician.fromFileString(raw);
            if (t.getTechnicianId().equals(technicianId)) {
                if (name != null && !name.isBlank()) t.setName(name);
                if (gender != null && !gender.isBlank()) t.setGender(gender);
                if (age > 0) t.setAge(age);
                if (phone != null && !phone.isBlank()) t.setPhone(phone);
                if (email != null && !email.isBlank()) t.setEmail(email);
                if (address != null && !address.isBlank()) t.setAddress(address);
                if (nationality != null && !nationality.isBlank()) t.setNationality(nationality);
                updated.add(t.toFileString());
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_TECHNICIANS, "# technicianId,name,gender,age,phone,email,address,nationality", updated);
 
        // Delegate password update to base helper
        if (newPassword != null && !newPassword.isBlank()) {
            updatePasswordByRoleRef(technicianId, newPassword);
        }
    }
 
    public String editProfileFromInput(String name, String gender, String ageStr, String phone,
                                       String email, String address, String nationality,
                                       String newPassword) {
        String error = validateProfile(name, gender, phone, email, ageStr);
        if (error != null) return "ERROR: " + error;
 
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 4) return "ERROR: Password must be at least 4 characters.";
            if (newPassword.contains(",")) return "ERROR: Password cannot contain commas.";
        }
 
        try {
            int age = Integer.parseInt(ageStr.trim());
            editProfile(name, gender, age, phone, email, address, nationality, newPassword);
            return "SUCCESS: Profile updated successfully.";
        } catch (IOException e) {
            return "ERROR: Save error: " + e.getMessage();
        }
    }
    
    // 3. Search and Filter
    public List<Appointment> getMyAppointments() {
        return loadAllAppointments().stream()
                .filter(a -> a.getTechnicianId().equals(technicianId))
                .collect(Collectors.toList());
    }
 
    public List<Appointment> filterByStatus(String status) {
        List<Appointment> mine = getMyAppointments();
        if (status == null || status.isBlank()) return mine;
        return mine.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
 
    public List<Appointment> searchTasks(String keyword) {
        if (keyword == null || keyword.isBlank()) return getMyAppointments();
        Map<String, String> serviceNames = buildServiceNameMap();
        String kw = keyword.trim().toLowerCase();
        return getMyAppointments().stream()
                .filter(a -> a.getAppointmentId().toLowerCase().contains(kw)
                          || a.getServiceId().toLowerCase().contains(kw)
                          || serviceNames.getOrDefault(a.getServiceId(), "").toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
 
    public List<Appointment> searchTasks(String keyword, String status) {
        List<Appointment> list = (keyword != null && !keyword.isBlank())
                ? searchTasks(keyword)
                : getMyAppointments();
        if (status != null && !status.isBlank()) {
            list = list.stream()
                    .filter(a -> a.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        return list;
    }
    
    // 4. View task
    public Appointment getTaskDetail(String appointmentId) {
        return getMyAppointments().stream()
                .filter(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId))
                .findFirst().orElse(null);
    }
 
    public Service getServiceInfo(String serviceId) {
        return loadAllServices().stream()
                .filter(s -> s.getServiceId().equalsIgnoreCase(serviceId))
                .findFirst().orElse(null);
    }
 
    public Map<String, String> getTaskDetailMap(String appointmentId) {
        Appointment task = getTaskDetail(appointmentId);
        if (task == null) return null;
 
        Service  svcInfo  = getServiceInfo(task.getServiceId());
        Customer customer = findCustomerById(task.getCustomerId());
        Map<String, String> svcNames = buildServiceNameMap();
 
        Map<String, String> detail = new LinkedHashMap<>();
        detail.put("appointmentId", task.getAppointmentId());
        detail.put("date", task.getAppointmentDate());
        detail.put("day", getDayName(task.getAppointmentDate()));
        detail.put("time", task.getAppointmentTime());
        detail.put("status", task.getStatus());
        detail.put("service", svcNames.getOrDefault(task.getServiceId(), task.getServiceId()));
        detail.put("duration", svcInfo  != null ? svcInfo.getDurationHours() + " hr(s)" : "-");
        detail.put("price", svcInfo  != null ? String.format("RM %.2f", svcInfo.getPrice()) : "-");
        detail.put("customerId", task.getCustomerId());
        detail.put("customerName", customer != null ? customer.getName()  : "Not found");
        detail.put("customerPhone", customer != null ? customer.getPhone() : "-");
        detail.put("customerEmail", customer != null ? customer.getEmail() : "-");
        return detail;
    }
    
    // 5. View comments and assigned task
    public List<Comment> getMyComments() {
        Set<String> myAppIds = getMyAppointments().stream()
                .map(Appointment::getAppointmentId)
                .collect(Collectors.toSet());
        return loadAllComments().stream()
                .filter(c -> myAppIds.contains(c.getAppointmentId()) && c.getTargetUserId().equals(technicianId))
                .collect(Collectors.toList());
    }
 
    public List<Comment> getCommentsForAppointment(String appointmentId) {
        if (getTaskDetail(appointmentId) == null) return Collections.emptyList();
        return loadAllComments().stream()
                .filter(c -> c.getAppointmentId().equalsIgnoreCase(appointmentId) && c.getTargetUserId().equals(technicianId))
                .collect(Collectors.toList());
    }
 
    public List<Comment> getComments(String apptId) {
        if (apptId != null && !apptId.isBlank()) return getCommentsForAppointment(apptId);
        return getMyComments();
    }
    
    // 6. Mark Task complete
    public String markTaskCompleted(String appointmentId) throws IOException {
        Appointment task = getTaskDetail(appointmentId);
        if (task == null) return "ERROR: Appointment not found or not assigned to you.";
        if (task.getStatus().equalsIgnoreCase("Completed")) return "ERROR: Task is already marked as Completed.";
        if (!task.getStatus().equalsIgnoreCase("Assigned"))
            return "ERROR: Only tasks with status 'Assigned' can be marked as Completed. Current status: " + task.getStatus();
 
        List<String> rawLines = readRawLines(FILE_APPOINTMENTS);
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) { updated.add(raw); continue; }
            Appointment a = Appointment.fromFileString(raw);
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus("Completed");
                updated.add(a.toFileString());
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_APPOINTMENTS, "# appointmentId,customerId,technicianId,counterStaffId,serviceId,appointmentDate,appointmentTime,status", updated);
        return "SUCCESS: Appointment " + appointmentId + " marked as Completed.";
    }
    
    // 7. Provide feedback
    public String provideFeedback(String appointmentId, String feedbackText) throws IOException {
        Appointment task = getTaskDetail(appointmentId);
        if (task == null) return "ERROR: Appointment not found or not assigned to you.";
        if (!task.getStatus().equalsIgnoreCase("Completed"))
            return "ERROR: Feedback can only be given for Completed tasks. Current status: " + task.getStatus();
        if (feedbackText == null || feedbackText.isBlank()) return "ERROR: Feedback text cannot be empty.";
 
        boolean alreadyGiven = loadAllFeedbacks().stream()
                .anyMatch(f -> f.getAppointmentId().equals(appointmentId)
                            && f.getTechnicianId().equals(technicianId));
        if (alreadyGiven)
            return "ERROR: You have already submitted feedback for appointment " + appointmentId + ".";
 
        String newFeedbackId = generateNextFeedbackId();
        appendLine(FILE_FEEDBACKS, new Feedback(newFeedbackId, appointmentId, technicianId, feedbackText).toFileString());
        return "SUCCESS: Feedback submitted successfully (ID: " + newFeedbackId + ").";
    }
 
    public List<Appointment> getEligibleAppointmentsForFeedback() {
        Set<String> alreadySubmitted = loadAllFeedbacks().stream()
                .filter(f -> f.getTechnicianId().equals(technicianId))
                .map(Feedback::getAppointmentId)
                .collect(Collectors.toSet());
        return getMyAppointments().stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Completed")
                          && !alreadySubmitted.contains(a.getAppointmentId()))
                .collect(Collectors.toList());
    }
 
    public List<String[]> getEligibleAppointmentLabels() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> result = new ArrayList<>();
        for (Appointment a : getEligibleAppointmentsForFeedback()) {
            String label = a.getAppointmentId()
                    + "  \u2013  " + svcNames.getOrDefault(a.getServiceId(), a.getServiceId())
                    + "  (" + a.getAppointmentDate() + ")";
            result.add(new String[]{ a.getAppointmentId(), label });
        }
        return result;
    }
 
    public List<Feedback> getMyFeedbacks() {
        return loadAllFeedbacks().stream()
                .filter(f -> f.getTechnicianId().equals(technicianId))
                .collect(Collectors.toList());
    }
    
    // 8. Schedule
    public List<Appointment> getUpcomingSchedule() {
        return getMyAppointments().stream()
                .filter(a -> !a.getStatus().equalsIgnoreCase("Completed"))
                .sorted((a, b) -> {
                    int dateCmp = a.getAppointmentDate().compareTo(b.getAppointmentDate());
                    if (dateCmp != 0) return dateCmp;
                    return a.getAppointmentTime().compareTo(b.getAppointmentTime());
                })
                .collect(Collectors.toList());
    }
 
    public int getTotalHoursForDate(String date) {
        int total = 0;
        for (Appointment a : getUpcomingSchedule()) {
            if (a.getAppointmentDate().equals(date)) {
                Service s = getServiceInfo(a.getServiceId());
                if (s != null) total += s.getDurationHours();
            }
        }
        return total;
    }
 
    public List<String[]> getScheduleRows() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : getUpcomingSchedule()) {
            Customer cust    = findCustomerById(a.getCustomerId());
            Service  svcInfo = getServiceInfo(a.getServiceId());
            rows.add(new String[]{
                a.getAppointmentDate(),
                getDayName(a.getAppointmentDate()),
                a.getAppointmentTime(),
                svcNames.getOrDefault(a.getServiceId(), a.getServiceId()),
                svcInfo != null ? svcInfo.getDurationHours() + " hr(s)" : "-",
                cust != null ? cust.getName()  : a.getCustomerId(),
                cust != null ? cust.getPhone() : "-",
                a.getStatus()
            });
        }
        return rows;
    }
 
    public List<String[]> getTaskRows(String keyword, String status) {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : searchTasks(keyword, status)) {
            rows.add(new String[]{
                a.getAppointmentId(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getCustomerId(),
                svcNames.getOrDefault(a.getServiceId(), a.getServiceId()),
                a.getStatus()
            });
        }
        return rows;
    }
    
    // 9. Stats Counts
    public int[] getUpcomingStatusCounts() {
        List<Appointment> upcoming = getUpcomingSchedule();
        int assigned = (int) upcoming.stream().filter(a -> a.getStatus().equalsIgnoreCase("Assigned")).count();
        int pending = (int) upcoming.stream().filter(a -> a.getStatus().equalsIgnoreCase("Pending")).count();
        return new int[]{ upcoming.size(), assigned, pending };
    }
 
    public int[] getAllStatusCounts() {
        List<Appointment> all = getMyAppointments();
        int assigned = (int) all.stream().filter(a -> a.getStatus().equalsIgnoreCase("Assigned")).count();
        int pending = (int) all.stream().filter(a -> a.getStatus().equalsIgnoreCase("Pending")).count();
        int completed = (int) all.stream().filter(a -> a.getStatus().equalsIgnoreCase("Completed")).count();
        return new int[]{ assigned, pending, completed };
    }
    
    // 10. Validate
    public String validateProfile(String name, String gender, String phone, String email, String ageStr) {
        if (name == null || name.isBlank()) return "Name cannot be empty.";
        if (gender != null && !gender.isBlank()) {
            gender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
        }
        if (gender == null || (!gender.equals("Male") && !gender.equals("Female")))
            return "Gender must be Male or Female.";
        if (ageStr == null || ageStr.isBlank()) return "Age cannot be empty.";
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0) return "Age must be a positive number.";
        } catch (NumberFormatException e) { return "Age must be a valid number."; }
        if (phone == null || phone.isBlank()) return "Phone cannot be empty.";
        if (!phone.matches("\\d{10,12}")) return "Phone must be 10-12 digits.";
        if (email == null || email.isBlank()) return "Email cannot be empty.";
        if (!email.contains("@")) return "Invalid email format.";
        return null;
    }
    
    // 11. utility
    public String getDayName(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String day = date.getDayOfWeek().toString();
            return day.charAt(0) + day.substring(1).toLowerCase();
        } catch (Exception e) { return "-"; }
    }
 
    public String getTodayString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    // 12. id generate
    private String generateNextFeedbackId() {
        int max = 0;
        for (Feedback f : loadAllFeedbacks()) {
            try { max = Math.max(max, Integer.parseInt(f.getFeedbackId().replaceAll("[^0-9]", ""))); }
            catch (NumberFormatException ignored) {}
        }
        return String.format("F%03d", max + 1);
    }
}
