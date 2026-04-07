package apu.asc.technician;
import apu.asc.staff.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class TechnicianService {
private static final String FILE_TECHNICIANS = "Data/technicians.txt";
private static final String FILE_APPOINTMENTS = "Data/appointments.txt";
private static final String FILE_COMMENTS = "Data/comments.txt";
private static final String FILE_FEEDBACKS = "Data/feedback.txt";
private static final String FILE_SERVICES = "Data/services.txt";
private static final String FILE_USERS = "Data/users.txt";
    private static final String COMMENT_CHAR = "#";
    private final String technicianId;

    public TechnicianService(String technicianId) {
        this.technicianId = technicianId;
    }
 
    public String getTechnicianId() { return technicianId; }
    
    // 1. VIEW OWN PROFILE
    public Technician getProfile() {
        for (Technician t : loadAllTechnicians()) {
            if (t.getTechnicianId().equals(technicianId)) return t;
        }
        throw new IllegalStateException("Technician not found: " + technicianId);
    }
    
    // 2. EDIT OWN PROFILE
    public void editProfile(String name, String gender, int age,
                            String phone, String email,
                            String address, String nationality,
                            String newPassword) throws IOException {
 
        // ── Update technicians.txt ──
        List<String> rawLines = readRawLines(FILE_TECHNICIANS);
        List<String> updated  = new ArrayList<>();
 
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) {
                updated.add(raw);
                continue;
            }
            Technician t = Technician.fromFileString(raw);
            if (t.getTechnicianId().equals(technicianId)) {
                if (name        != null && !name.isBlank())   t.setName(name);
                if (gender      != null && !gender.isBlank())  t.setGender(gender);
                if (age > 0)                                   t.setAge(age);
                if (phone       != null && !phone.isBlank())   t.setPhone(phone);
                if (email       != null && !email.isBlank())   t.setEmail(email);
                if (address     != null && !address.isBlank()) t.setAddress(address);
                if (nationality != null && !nationality.isBlank()) t.setNationality(nationality);
                updated.add(t.toFileString());
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_TECHNICIANS, updated);
 
        // Update password in user.txt
        if (newPassword != null && !newPassword.isBlank()) {
            updatePasswordInFile(newPassword);
        }
    }
    private void updatePasswordInFile(String newPassword) throws IOException {
        List<String> rawLines = readRawLines(FILE_USERS);
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) {
                updated.add(raw);
                continue;
            }
            // userId,username,password,role,roleRefId
            String[] p = raw.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(technicianId)) {
                p[2] = newPassword;
                updated.add(String.join(",", p));
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_USERS, updated);
    }
    
    // 3. CHECK ASSIGNED TASKS  (with search + filter)
    public List<Appointment> getMyAppointments() { //return all appointment to the technician
        return loadAllAppointments().stream()
                .filter(a -> a.getTechnicianId().equals(technicianId))
                .collect(Collectors.toList());
    }
    public List<Appointment> filterByStatus(String status) {//filter by "pending","assigned", or "completed"
        List<Appointment> mine = getMyAppointments();
        if (status == null || status.isBlank()) return mine;
        return mine.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    public List<Appointment> searchTasks(String keyword) { //search appointment by appointmentID, serviceID, or service name
        if (keyword == null || keyword.isBlank()) return getMyAppointments();
        Map<String, String> serviceNames = buildServiceNameMap();
        String kw = keyword.trim().toLowerCase();
 
        return getMyAppointments().stream()
                .filter(a ->
                        a.getAppointmentId().toLowerCase().contains(kw)
                        || a.getServiceId().toLowerCase().contains(kw)
                        || serviceNames.getOrDefault(a.getServiceId(), "").toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
    
    // 4. VIEW TASK DETAILS
    public Appointment getTaskDetail(String appointmentId) {//provide appointment detail appointment id
        return getMyAppointments().stream()
                .filter(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElse(null);
    }
    public Service getServiceInfo(String serviceId) {//get service info by service id
        return loadAllServices().stream()
                .filter(s -> s.getServiceId().equalsIgnoreCase(serviceId))
                .findFirst()
                .orElse(null);
    }
    // 5. VIEW COMMENTS FOR ASSIGNED TASKS
    public List<Comment> getMyComments() {//get all comments to the technician by technician id
        Set<String> myAppIds = getMyAppointments().stream()
                .map(Appointment::getAppointmentId)
                .collect(Collectors.toSet());
 
        return loadAllComments().stream()
                .filter(c -> myAppIds.contains(c.getAppointmentId())
                          && c.getTargetUserId().equals(technicianId))
                .collect(Collectors.toList());
    }
    public List<Comment> getCommentsForAppointment(String appointmentId) {//get appointment comment by appointment id
        // Security check – appointment must belong to this technician
        if (getTaskDetail(appointmentId) == null) return Collections.emptyList();
 
        return loadAllComments().stream()
                .filter(c -> c.getAppointmentId().equalsIgnoreCase(appointmentId)
                          && c.getTargetUserId().equals(technicianId))
                .collect(Collectors.toList());
    }
    
    // 6. MARK ASSIGNED TASK AS COMPLETED
    public String markTaskCompleted(String appointmentId) throws IOException {
        Appointment task = getTaskDetail(appointmentId);
 
        if (task == null) {
            return "ERROR: Appointment not found or not assigned to you.";
        }
        if (task.getStatus().equalsIgnoreCase("Completed")) {
            return "ERROR: Task is already marked as Completed.";
        }
        if (!task.getStatus().equalsIgnoreCase("Assigned")) {
            return "ERROR: Only tasks with status 'Assigned' can be marked as Completed. "
                 + "Current status: " + task.getStatus();
        }
        List<String> rawLines = readRawLines(FILE_APPOINTMENTS); //update to file
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) {
                updated.add(raw);
                continue;
            }
            Appointment a = Appointment.fromFileString(raw);
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus("Completed");
                updated.add(a.toFileString());
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_APPOINTMENTS, updated);
        return "SUCCESS: Appointment " + appointmentId + " marked as Completed.";
    }
    
    // 7. PROVIDE FEEDBACK FOR COMPLETED TASKS
    public String provideFeedback(String appointmentId,
                                  String feedbackText) throws IOException {
 
        Appointment task = getTaskDetail(appointmentId);
 
        if (task == null) {
            return "ERROR: Appointment not found or not assigned to you.";
        }
        if (!task.getStatus().equalsIgnoreCase("Completed")) {
            return "ERROR: Feedback can only be given for Completed tasks. "
                 + "Current status: " + task.getStatus();
        }
        if (feedbackText == null || feedbackText.isBlank()) {
            return "ERROR: Feedback text cannot be empty.";
        }
 
        // Check one-feedback-per-task rule
        boolean alreadyGiven = loadAllFeedbacks().stream()
                .anyMatch(f -> f.getAppointmentId().equals(appointmentId)
                            && f.getTechnicianId().equals(technicianId));
        if (alreadyGiven) {
            return "ERROR: You have already submitted feedback for appointment " + appointmentId + ".";
        }
 
        // create new feedbackId
        String newFeedbackId = generateNextFeedbackId();
 
        // convert to feedback.txt
        Feedback fb = new Feedback(newFeedbackId, appointmentId, technicianId, feedbackText);
        appendLine(FILE_FEEDBACKS, fb.toFileString());
 
        return "SUCCESS: Feedback submitted successfully (ID: " + newFeedbackId + ").";
    }

    // 8. ID GENERATION
    private String generateNextFeedbackId() {
        List<Feedback> all = loadAllFeedbacks();
        int max = 0;
        for (Feedback f : all) {
            String idNum = f.getFeedbackId().replaceAll("[^0-9]", "");
            try { max = Math.max(max, Integer.parseInt(idNum)); } catch (NumberFormatException ignored) {}
        }
        return String.format("F%03d", max + 1);
    }

    //9. File input and output
    //Read all non-comment, non-blank lines as Technician objects.
    public List<Technician> loadAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readDataLines(FILE_TECHNICIANS)) {
            try { list.add(Technician.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Appointment objects.
    public List<Appointment> loadAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readDataLines(FILE_APPOINTMENTS)) {
            try { list.add(Appointment.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Comment objects.
    public List<Comment> loadAllComments() {
        List<Comment> list = new ArrayList<>();
        for (String line : readDataLines(FILE_COMMENTS)) {
            try { list.add(Comment.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Feedback objects. 
    public List<Feedback> loadAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        for (String line : readDataLines(FILE_FEEDBACKS)) {
            try { list.add(Feedback.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Service objects.
    public List<Service> loadAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readDataLines(FILE_SERVICES)) {
            try { list.add(Service.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Build a map of serviceId → serviceName for display purposes.
    public Map<String, String> buildServiceNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Service s : loadAllServices()) {
            map.put(s.getServiceId(), s.getServiceName());
        }
        return map;
    }
 
    //Returns data lines only (strips comments and blanks). 
    private List<String> readDataLines(String filename) {
        List<String> result = new ArrayList<>();
        for (String raw : readRawLines(filename)) {
            if (!raw.startsWith(COMMENT_CHAR) && !raw.isBlank()) result.add(raw);
        }
        return result;
    }
 
    //Returns ALL lines including comment header. 
    private List<String> readRawLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
        }
        return lines;
    }
 
    //Overwrites the file with the given lines.
    private void writeLines(String filename, List<String> lines) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, false))) {
            for (String l : lines) pw.println(l);
        }
    }
 
    //Appends a single line to the file. 
    private void appendLine(String filename, String line) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println(line);
        }
    }

}
