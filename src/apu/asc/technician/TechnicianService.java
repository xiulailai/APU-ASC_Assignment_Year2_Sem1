package apu.asc.technician;
import apu.asc.staff.Technician;
import apu.asc.staff.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class TechnicianService {
    private static final String TECHNICIAN_FILE = "Data/technicians.txt";
    private static final String APPOINTMENT_FILE = "Data/appointments.txt";
    private static final String COMMENT_FILE = "Data/comments.txt";
    private static final String FEEDBACK_FILE = "Data/feedback.txt";
    private static final String SERVICE_FILE = "Data/services.txt";
    private static final String USER_FILE = "Data/users.txt";
    private static final String CUSTOMER_FILE = "Data/customers.txt";

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
    public void editProfile(String name, String gender, int age, String phone, String email, String address, String nationality, String newPassword) throws IOException {
        // ── Update technicians.txt ──
        List<String> rawLines = readRawLines(TECHNICIAN_FILE);
        List<String> updated  = new ArrayList<>();
        
        if (gender != null && !gender.isBlank()) {
            gender = gender.substring(0, 1).toUpperCase()
                     + gender.substring(1).toLowerCase();
        }

        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) {
                updated.add(raw);
                continue;
            }
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
        writeLines(TECHNICIAN_FILE, updated);
 
        // Update password in user.txt
        if (newPassword != null && !newPassword.isBlank()) {
            updatePasswordInFile(newPassword);
        }
    }
    private void updatePasswordInFile(String newPassword) throws IOException {
        List<String> rawLines = readRawLines(USER_FILE);
        List<String> updated = new ArrayList<>();
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
        writeLines(USER_FILE, updated);
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
                .filter(c -> c.getAppointmentId().equalsIgnoreCase(appointmentId) && c.getTargetUserId().equals(technicianId))
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
            return "ERROR: Only tasks with status 'Assigned' can be marked as Completed. " + "Current status: " + task.getStatus();
        }
        List<String> rawLines = readRawLines(APPOINTMENT_FILE); //update to file
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
        writeLines(APPOINTMENT_FILE, updated);
        return "SUCCESS: Appointment " + appointmentId + " marked as Completed.";
    }
    
    // 7. PROVIDE FEEDBACK FOR COMPLETED TASKS
    public String provideFeedback(String appointmentId, String feedbackText) throws IOException {
 
        Appointment task = getTaskDetail(appointmentId);
 
        if (task == null) {
            return "ERROR: Appointment not found or not assigned to you.";
        }
        if (!task.getStatus().equalsIgnoreCase("Completed")) {
            return "ERROR: Feedback can only be given for Completed tasks. " + "Current status: " + task.getStatus();
        }
        if (feedbackText == null || feedbackText.isBlank()) {
            return "ERROR: Feedback text cannot be empty.";
        }
 
        // Check one-feedback-per-task rule
        boolean alreadyGiven = loadAllFeedbacks().stream()
                .anyMatch(f -> f.getAppointmentId().equals(appointmentId) && f.getTechnicianId().equals(technicianId));
        if (alreadyGiven) {
            return "ERROR: You have already submitted feedback for appointment " + appointmentId + ".";
        }
 
        // create new feedbackId
        String newFeedbackId = generateNextFeedbackId();
 
        // convert to feedback.txt
        Feedback fb = new Feedback(newFeedbackId, appointmentId, technicianId, feedbackText);
        appendLine(FEEDBACK_FILE, fb.toFileString());
 
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

    //9. Look Customer Info
    //Returns Customer info by looking CustomerID, if not found return null.
    public Customer getCustomerById(String customerId) {
        for (Customer c : loadAllCustomers()) {
            if (c.getCustomerId().equalsIgnoreCase(customerId)) return c;
        }
        return null;
    }
    
    public List<Customer> loadAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readDataLines(CUSTOMER_FILE)) {
            try { list.add(Customer.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
    
    //10. Upcoming Schedule
    //Return Technician Appointment by Date Sorting (Early first)
    public List<Appointment> getUpcomingSchedule() {
        return getMyAppointments().stream()
                .filter(a -> !a.getStatus().equalsIgnoreCase("Completed"))
                .sorted((a, b) -> {
                    // Compare date first, then time
                    int dateCmp = a.getAppointmentDate().compareTo(b.getAppointmentDate());
                    if (dateCmp != 0) return dateCmp;
                    return a.getAppointmentTime().compareTo(b.getAppointmentTime());
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    //Calculates Total working hours of upcoming tasks
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
    
    //11. File input and output
    //Read all non-comment, non-blank lines as Technician objects.
    public List<Technician> loadAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readDataLines(TECHNICIAN_FILE)) {
            try { list.add(Technician.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
    
    //12. Validates profile input fields
    public String validateProfile(String name, String gender, String phone, String email, String ageStr) {
        if (name == null || name.isBlank())
            return "Name cannot be empty.";
        if (gender != null && !gender.isBlank()) {
            gender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
        }
        if (gender == null || (!gender.equals("Male") && !gender.equals("Female")))
            return "Gender must be Male or Female.";
        if (ageStr == null || ageStr.isBlank())
            return "Age cannot be empty.";
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0)
                return "Age must be a positive number.";
        } catch (NumberFormatException e) {
            return "Age must be a valid number.";
        }
        if (phone == null || phone.isBlank())
            return "Phone cannot be empty.";
        if (!phone.matches("\\d{10,12}"))
            return "Phone must be 10-12 digits.";
        if (email == null || email.isBlank())
            return "Email cannot be empty.";
        if (!email.contains("@"))
            return "Invalid email format.";
        return null;
    }
    
    //13. Get Day name
    public String getDayName(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.getDayOfWeek().toString().charAt(0)
                    + date.getDayOfWeek().toString().substring(1).toLowerCase();
        } catch (Exception e) {
            return "-";
        }
    }
    
    //14. Check is it fits condition to submit feedback
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
    
    //15. Search task keywords
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
    
    //16. Get Technician Feedback
    public List<Feedback> getMyFeedbacks() {
        return loadAllFeedbacks().stream()
                .filter(f -> f.getTechnicianId().equals(technicianId))
                .collect(Collectors.toList());
    }
    
    //17. status counts
    public int[] getUpcomingStatusCounts() {
    List<Appointment> upcoming = getUpcomingSchedule();
        int assigned = (int) upcoming.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Assigned")).count();
        int pending  = (int) upcoming.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Pending")).count();
        return new int[]{upcoming.size(), assigned, pending};
    }
    
    //18. Counts Status
    public int[] getAllStatusCounts() {
        List<Appointment> all = getMyAppointments();
        int assigned  = (int) all.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Assigned")).count();
        int pending   = (int) all.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Pending")).count();
        int completed = (int) all.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Completed")).count();
        return new int[]{assigned, pending, completed};
    }
    
    public String editProfileFromInput(String name, String gender, String ageStr, String phone, String email, String address, String nationality, String newPassword) {
        String error = validateProfile(name, gender, phone, email, ageStr);
        if (error != null) {
            return "ERROR: " + error;
        }
        
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 4) {
                return "ERROR: Password must be at least 4 characters.";
            }
            if (newPassword.contains(",")) {
                return "ERROR: Password cannot contain commas.";
            }
        }

        try {
            int age = Integer.parseInt(ageStr.trim());

            editProfile(name, gender, age, phone, email, address, nationality, newPassword);

            return "SUCCESS: Profile updated successfully.";
        } catch (IOException e) {
            return "ERROR: Save error: " + e.getMessage();
        }
    }
    
    // For tchnSchedule - returns ready-to-display rows
    public List<String[]> getScheduleRows() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : getUpcomingSchedule()) {
            Customer cust    = getCustomerById(a.getCustomerId());
            Service  svcInfo = getServiceInfo(a.getServiceId());
            rows.add(new String[]{
                a.getAppointmentDate(),
                getDayName(a.getAppointmentDate()),
                a.getAppointmentTime(),
                svcNames.getOrDefault(a.getServiceId(), a.getServiceId()),
                svcInfo  != null ? svcInfo.getDurationHours() + " hr(s)" : "-",
                cust     != null ? cust.getName()  : a.getCustomerId(),
                cust     != null ? cust.getPhone() : "-",
                a.getStatus()
            });
        }
        return rows;
    }
    
    // Returns display-ready rows for the tasks table
    public List<String[]> getTaskRows(String keyword, String status) {
        Map<String, String> svcNames = buildServiceNameMap();
        List<Appointment> list = searchTasks(keyword, status);
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : list) {
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
    
    // For tchnSchedule - returns today's date string
    public String getTodayString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // For tchnFeedback - returns display labels paired with appointment IDs
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

    // Returns a map of display-ready strings for a task detail dialog
    public Map<String, String> getTaskDetailMap(String appointmentId) {
        Appointment task = getTaskDetail(appointmentId);
        if (task == null) return null;

        Service svcInfo = getServiceInfo(task.getServiceId());
        Customer customer = getCustomerById(task.getCustomerId());
        Map<String, String> svcNames = buildServiceNameMap();

        Map<String, String> detail = new LinkedHashMap<>();
        detail.put("appointmentId", task.getAppointmentId());
        detail.put("date", task.getAppointmentDate());
        detail.put("day", getDayName(task.getAppointmentDate()));
        detail.put("time", task.getAppointmentTime());
        detail.put("status", task.getStatus());
        detail.put("service", svcNames.getOrDefault(task.getServiceId(), task.getServiceId()));
        detail.put("duration", svcInfo != null ? svcInfo.getDurationHours() + " hr(s)" : "-");
        detail.put("price", svcInfo != null ? String.format("RM %.2f", svcInfo.getPrice()) : "-");
        detail.put("customerId", task.getCustomerId());
        detail.put("customerName", customer != null ? customer.getName()  : "Not found");
        detail.put("customerPhone", customer != null ? customer.getPhone() : "-");
        detail.put("customerEmail", customer != null ? customer.getEmail() : "-");
        return detail;
    }

    // Unified method: returns comments filtered by appointment if provided, else all
    public List<Comment> getComments(String apptId) {
        if (apptId != null && !apptId.isBlank()) {
            return getCommentsForAppointment(apptId);
        }
        return getMyComments();
    }
 
    //Read all non-comment, non-blank lines as Appointment objects.
    public List<Appointment> loadAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readDataLines(APPOINTMENT_FILE)) {
            try { list.add(Appointment.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Comment objects.
    public List<Comment> loadAllComments() {
        List<Comment> list = new ArrayList<>();
        for (String line : readDataLines(COMMENT_FILE)) {
            try { list.add(Comment.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Feedback objects. 
    public List<Feedback> loadAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        for (String line : readDataLines(FEEDBACK_FILE)) {
            try { list.add(Feedback.fromFileString(line)); }
            catch (Exception ignored) {}
        }
        return list;
    }
 
    //Read all non-comment, non-blank lines as Service objects.
    public List<Service> loadAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readDataLines(SERVICE_FILE)) {
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
