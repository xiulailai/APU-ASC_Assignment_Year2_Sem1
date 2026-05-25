package apu.asc.manager;

import apu.asc.staff.Appointment;
import apu.asc.staff.BaseFileHelper;
import apu.asc.staff.Comment;
import apu.asc.staff.CounterStaff;
import apu.asc.staff.Customer;
import apu.asc.staff.Feedback;
import apu.asc.staff.Manager;
import apu.asc.staff.Payment;
import apu.asc.staff.Service;
import apu.asc.staff.Technician;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagerService extends BaseFileHelper {

    private static final String FILE_MANAGERS     = "Data/managers.txt";
    private static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    private static final String FILE_TECHNICIANS  = "Data/technicians.txt";
    private static final String FILE_CUSTOMERS    = "Data/customers.txt";
    private static final String FILE_USERS        = "Data/users.txt";
    private static final String FILE_SERVICES     = "Data/services.txt";
    private static final String FILE_APPOINTMENTS = "Data/appointments.txt";
    private static final String FILE_PAYMENTS     = "Data/payments.txt";
    private static final String FILE_FEEDBACKS    = "Data/feedback.txt";
    private static final String FILE_COMMENTS     = "Data/comments.txt";

    private static final String COMMENT_CHAR = "#";

    private final String managerId;

    public ManagerService(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerId() { return managerId; }

    // ============================================================
    // VALIDATION
    // ============================================================

    //Validates the common User fields. Returns null if OK, error string otherwise.
    public String validatePerson(String name, String ageStr, String phone, String email, String address, String nationality) {
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
        if (name.contains(",") || address.contains(",")) {
            return "ERR: Name and address cannot contain commas.";
        }
        return null;
    }

    //Validates username + password for a new account. Returns null if OK, error otherwise.
    public String validateAccount(String username, String password, String excludeUserId) {
        if (username == null || username.isBlank()) return "ERR: Username cannot be empty.";
        if (password == null || password.isBlank()) return "ERR: Password cannot be empty.";
        if (username.length() < 3) return "ERR: Username must be at least 3 characters.";
        if (password.length() < 4) return "ERR: Password must be at least 4 characters.";
        if (username.contains(",") || password.contains(",")) {
            return "ERR: Username and password cannot contain commas.";
        }
        //Duplicate username check
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[1].trim().equalsIgnoreCase(username.trim())) {
                if (excludeUserId == null || !p[0].trim().equals(excludeUserId)) {
                    return "ERR: Username already taken.";
                }
            }
        }
        return null;
    }

    //Validates a price string. Returns null if OK, error otherwise.
    public String validatePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return "ERR: Price cannot be empty.";
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) return "ERR: Price must be greater than 0.";
            if (price > 100000) return "ERR: Price seems too large.";
        } catch (NumberFormatException e) {
            return "ERR: Price must be a valid number.";
        }
        return null;
    }

    // ============================================================
    // PROFILE (own)
    // ============================================================

    //Returns the logged-in manager's profile object.
    public Manager getProfile() {
        for (Manager m : getAllManagers()) {
            if (m.getManagerId().equals(managerId)) return m;
        }
        return null;
    }

    //Returns the username for the logged-in manager.
    public String getMyUsername() {
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(managerId)) return p[1].trim();
        }
        return "";
    }

    //Updates the manager's own profile in managers.txt. Optionally changes username/password.
    public String updateProfile(String name, String gender, String ageStr,
                                 String phone, String email, String address, String nationality,
                                 String newUsername, String newPassword) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;

        int age = Integer.parseInt(ageStr.trim());

        //Update managers.txt
        List<String> lines = readLines(FILE_MANAGERS);
        boolean found = false;
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            Manager m = Manager.fromFileString(line);
            if (m.getManagerId().equals(managerId)) {
                m.setName(name.trim());
                m.setGender(gender);
                m.setAge(age);
                m.setPhone(phone.trim());
                m.setEmail(email.trim());
                m.setAddress(address.trim());
                m.setNationality(nationality.trim());
                updated.add(m.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) return "ERR: Manager record not found.";
        writeLines(FILE_MANAGERS, "# managerId,name,gender,age,phone,email,address,nationality", updated);

        //Optionally update users.txt for username/password
        if ((newUsername != null && !newUsername.isBlank())
         || (newPassword != null && !newPassword.isBlank())) {
            String accErr = updateOwnAccount(newUsername, newPassword);
            if (accErr != null) return accErr;
        }
        return "OK: Profile updated successfully!";
    }

    //Updates username/password for the logged-in manager in users.txt.
    private String updateOwnAccount(String newUsername, String newPassword) {
        //find own userId first
        String myUserId = null;
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(managerId)) {
                myUserId = p[0].trim();
                break;
            }
        }
        if (myUserId == null) return "ERR: User account not found.";

        //validate only if changing
        if (newUsername != null && !newUsername.isBlank()) {
            if (newUsername.trim().length() < 3) return "ERR: Username must be at least 3 characters.";
            if (newUsername.contains(",")) return "ERR: Username cannot contain commas.";
            //duplicate check excluding self
            for (String line : readLines(FILE_USERS)) {
                if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
                String[] p = line.split(",", 5);
                if (p.length == 5 && p[1].trim().equalsIgnoreCase(newUsername.trim())
                        && !p[0].trim().equals(myUserId)) {
                    return "ERR: Username already taken.";
                }
            }
        }
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 4) return "ERR: Password must be at least 4 characters.";
            if (newPassword.contains(",")) return "ERR: Password cannot contain commas.";
        }

        List<String> lines = readLines(FILE_USERS);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[0].trim().equals(myUserId)) {
                if (newUsername != null && !newUsername.isBlank()) p[1] = newUsername.trim();
                if (newPassword != null && !newPassword.isBlank()) p[2] = newPassword.trim();
                updated.add(String.join(",", p));
            } else {
                updated.add(line);
            }
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
        return null;
    }

    // ============================================================
    // CRUD: MANAGERS
    // ============================================================

    public List<Manager> getAllManagers() {
        List<Manager> list = new ArrayList<>();
        for (String line : readLines(FILE_MANAGERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Manager.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public Manager getManagerById(String id) {
        for (Manager m : getAllManagers()) {
            if (m.getManagerId().equals(id)) return m;
        }
        return null;
    }

    public List<Manager> searchManagers(String query) {
        List<Manager> all = getAllManagers();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<Manager> result = new ArrayList<>();
        for (Manager m : all) {
            if (m.getManagerId().toLowerCase().contains(q)
                || m.getName().toLowerCase().contains(q)
                || m.getPhone().contains(q)
                || m.getEmail().toLowerCase().contains(q)) {
                result.add(m);
            }
        }
        return result;
    }

    public String addManager(String name, String username, String password, String gender,
                              String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        String accErr = validateAccount(username, password, null);
        if (accErr != null) return accErr;

        int age = Integer.parseInt(ageStr.trim());
        String newId = generateManagerId();

        Manager m = new Manager(newId, name.trim(), gender, age, phone.trim(),
                                 email.trim(), address.trim(), nationality.trim());
        appendLine(FILE_MANAGERS, m.toFileString());
        createUserAccount(newId, username.trim(), password.trim(), "manager");
        return "OK: Manager " + newId + " added successfully!";
    }

    public String updateManager(String id, String name, String gender, String ageStr,
                                 String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        int age = Integer.parseInt(ageStr.trim());

        List<String> lines = readLines(FILE_MANAGERS);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            Manager m = Manager.fromFileString(line);
            if (m.getManagerId().equals(id)) {
                m.setName(name.trim()); m.setGender(gender); m.setAge(age);
                m.setPhone(phone.trim()); m.setEmail(email.trim());
                m.setAddress(address.trim()); m.setNationality(nationality.trim());
                updated.add(m.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) return "ERR: Manager record not found.";
        writeLines(FILE_MANAGERS, "# managerId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Manager " + id + " updated successfully!";
    }

    public String deleteManager(String id) {
        //Safety: cannot delete self
        if (id.equals(managerId)) return "ERR: You cannot delete your own account.";
        //Safety: cannot delete the only manager
        if (getAllManagers().size() <= 1) return "ERR: At least one manager must remain.";

        List<Manager> all = getAllManagers();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        updated.add("# managerId,name,gender,age,phone,email,address,nationality");
        for (Manager m : all) {
            if (m.getManagerId().equals(id)) { found = true; continue; }
            updated.add(m.toFileString());
        }
        if (!found) return "ERR: Manager not found.";
        writeLines(FILE_MANAGERS, "# managerId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Manager " + id + " deleted successfully!";
    }

    // ============================================================
    // CRUD: COUNTER STAFF
    // ============================================================

    public List<CounterStaff> getAllCounterStaff() {
        List<CounterStaff> list = new ArrayList<>();
        for (String line : readLines(FILE_COUNTERSTAFF)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(CounterStaff.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public CounterStaff getCounterStaffById(String id) {
        for (CounterStaff cs : getAllCounterStaff()) {
            if (cs.getCounterStaffId().equals(id)) return cs;
        }
        return null;
    }

    public List<CounterStaff> searchCounterStaff(String query) {
        List<CounterStaff> all = getAllCounterStaff();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<CounterStaff> result = new ArrayList<>();
        for (CounterStaff cs : all) {
            if (cs.getCounterStaffId().toLowerCase().contains(q)
                || cs.getName().toLowerCase().contains(q)
                || cs.getPhone().contains(q)
                || cs.getEmail().toLowerCase().contains(q)) {
                result.add(cs);
            }
        }
        return result;
    }

    public String addCounterStaff(String name, String username, String password, String gender,
                                   String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        String accErr = validateAccount(username, password, null);
        if (accErr != null) return accErr;

        int age = Integer.parseInt(ageStr.trim());
        String newId = generateCounterStaffId();

        CounterStaff cs = new CounterStaff(newId, name.trim(), gender, age, phone.trim(),
                                            email.trim(), address.trim(), nationality.trim());
        appendLine(FILE_COUNTERSTAFF, cs.toFileString());
        createUserAccount(newId, username.trim(), password.trim(), "counterstaff");
        return "OK: Counter Staff " + newId + " added successfully!";
    }

    public String updateCounterStaff(String id, String name, String gender, String ageStr,
                                      String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        int age = Integer.parseInt(ageStr.trim());

        List<String> lines = readLines(FILE_COUNTERSTAFF);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            CounterStaff cs = CounterStaff.fromFileString(line);
            if (cs.getCounterStaffId().equals(id)) {
                cs.setName(name.trim()); cs.setGender(gender); cs.setAge(age);
                cs.setPhone(phone.trim()); cs.setEmail(email.trim());
                cs.setAddress(address.trim()); cs.setNationality(nationality.trim());
                updated.add(cs.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) return "ERR: Counter Staff record not found.";
        writeLines(FILE_COUNTERSTAFF, "# counterStaffId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Counter Staff " + id + " updated successfully!";
    }

    public String deleteCounterStaff(String id) {
        List<CounterStaff> all = getAllCounterStaff();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        updated.add("# counterStaffId,name,gender,age,phone,email,address,nationality");
        for (CounterStaff cs : all) {
            if (cs.getCounterStaffId().equals(id)) { found = true; continue; }
            updated.add(cs.toFileString());
        }
        if (!found) return "ERR: Counter Staff not found.";
        writeLines(FILE_COUNTERSTAFF, "# counterStaffId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Counter Staff " + id + " deleted successfully!";
    }

    // ============================================================
    // CRUD: TECHNICIANS
    // ============================================================

    public List<Technician> getAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readLines(FILE_TECHNICIANS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Technician.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public Technician getTechnicianById(String id) {
        for (Technician t : getAllTechnicians()) {
            if (t.getTechnicianId().equals(id)) return t;
        }
        return null;
    }

    public List<Technician> searchTechnicians(String query) {
        List<Technician> all = getAllTechnicians();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<Technician> result = new ArrayList<>();
        for (Technician t : all) {
            if (t.getTechnicianId().toLowerCase().contains(q)
                || t.getName().toLowerCase().contains(q)
                || t.getPhone().contains(q)
                || t.getEmail().toLowerCase().contains(q)) {
                result.add(t);
            }
        }
        return result;
    }

    public String addTechnician(String name, String username, String password, String gender,
                                 String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        String accErr = validateAccount(username, password, null);
        if (accErr != null) return accErr;

        int age = Integer.parseInt(ageStr.trim());
        String newId = generateTechnicianId();

        Technician t = new Technician(newId, name.trim(), gender, age, phone.trim(),
                                       email.trim(), address.trim(), nationality.trim());
        appendLine(FILE_TECHNICIANS, t.toFileString());
        createUserAccount(newId, username.trim(), password.trim(), "technician");
        return "OK: Technician " + newId + " added successfully!";
    }

    public String updateTechnician(String id, String name, String gender, String ageStr,
                                    String phone, String email, String address, String nationality) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        int age = Integer.parseInt(ageStr.trim());

        List<String> lines = readLines(FILE_TECHNICIANS);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            Technician t = Technician.fromFileString(line);
            if (t.getTechnicianId().equals(id)) {
                t.setName(name.trim()); t.setGender(gender); t.setAge(age);
                t.setPhone(phone.trim()); t.setEmail(email.trim());
                t.setAddress(address.trim()); t.setNationality(nationality.trim());
                updated.add(t.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) return "ERR: Technician record not found.";
        writeLines(FILE_TECHNICIANS, "# technicianId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Technician " + id + " updated successfully!";
    }

    public String deleteTechnician(String id) {
        //Safety: cannot delete a technician with active (non-Completed) appointments
        for (Appointment a : getAllAppointments()) {
            if (a.getTechnicianId().equals(id) && !a.getStatus().equalsIgnoreCase("Completed")) {
                return "ERR: Cannot delete – technician has active appointments. "
                        + "Reassign or wait until they are Completed.";
            }
        }

        List<Technician> all = getAllTechnicians();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        updated.add("# technicianId,name,gender,age,phone,email,address,nationality");
        for (Technician t : all) {
            if (t.getTechnicianId().equals(id)) { found = true; continue; }
            updated.add(t.toFileString());
        }
        if (!found) return "ERR: Technician not found.";
        writeLines(FILE_TECHNICIANS, "# technicianId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Technician " + id + " deleted successfully!";
    }

    // ============================================================
    // SERVICE PRICES
    // ============================================================

    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readLines(FILE_SERVICES)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Service.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public Service getServiceById(String id) {
        for (Service s : getAllServices()) {
            if (s.getServiceId().equals(id)) return s;
        }
        return null;
    }

    public String updateServicePrice(String serviceId, String priceStr) {
        String err = validatePrice(priceStr);
        if (err != null) return err;
        double newPrice = Double.parseDouble(priceStr.trim());

        List<String> lines = readLines(FILE_SERVICES);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            Service s = Service.fromFileString(line);
            if (s.getServiceId().equals(serviceId)) {
                Service replaced = new Service(s.getServiceId(), s.getServiceName(),
                                                s.getDurationHours(), newPrice);
                updated.add(replaced.toFileString());
                found = true;
            } else {
                updated.add(line);
            }
        }
        if (!found) return "ERR: Service not found.";
        writeLines(FILE_SERVICES, "# serviceId,serviceName,durationHours,price", updated);
        return "OK: Price for " + serviceId + " updated to RM " + String.format("%.2f", newPrice) + ".";
    }

    // ============================================================
    // FEEDBACKS & COMMENTS (read-only views)
    // ============================================================

    public List<Feedback> getAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        for (String line : readLines(FILE_FEEDBACKS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Feedback.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Comment> getAllComments() {
        List<Comment> list = new ArrayList<>();
        for (String line : readLines(FILE_COMMENTS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Comment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    //Returns feedback rows ready for a JTable: [feedbackId, appointmentId, technicianName, feedbackText]
    public List<String[]> getFeedbackRows(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<String[]> rows = new ArrayList<>();
        for (Feedback f : getAllFeedbacks()) {
            String techName = lookupTechnicianName(f.getTechnicianId());
            String combined = (f.getFeedbackId() + " " + f.getAppointmentId() + " "
                                + techName + " " + f.getFeedbackText()).toLowerCase();
            if (!kw.isEmpty() && !combined.contains(kw)) continue;
            rows.add(new String[]{
                f.getFeedbackId(),
                f.getAppointmentId(),
                techName,
                f.getFeedbackText()
            });
        }
        return rows;
    }

    //Returns comment rows ready for a JTable: [commentId, appointmentId, fromCustomer, targetName, commentText]
    public List<String[]> getCommentRows(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<String[]> rows = new ArrayList<>();
        for (Comment c : getAllComments()) {
            String customerName = lookupCustomerName(c.getCustomerId());
            String targetName = lookupTargetName(c.getTargetUserId());
            String combined = (c.getCommentId() + " " + c.getAppointmentId() + " "
                                + customerName + " " + targetName + " " + c.getCommentText()).toLowerCase();
            if (!kw.isEmpty() && !combined.contains(kw)) continue;
            rows.add(new String[]{
                c.getCommentId(),
                c.getAppointmentId(),
                customerName,
                targetName,
                c.getCommentText()
            });
        }
        return rows;
    }

    // ============================================================
    // REPORTS  (5+ unique reports)
    // ============================================================

    //Report 1: REVENUE — total + per service breakdown
    //Returns: [serviceName, paymentCount, totalRevenueRM]
    public List<String[]> getRevenueReport() {
        Map<String, String> svcNameMap = buildServiceNameMap();
        Map<String, Double> totalByService = new LinkedHashMap<>();
        Map<String, Integer> countByService = new LinkedHashMap<>();

        //init zero rows so all services appear even with no payments
        for (Service s : getAllServices()) {
            totalByService.put(s.getServiceId(), 0.0);
            countByService.put(s.getServiceId(), 0);
        }

        for (Payment p : getAllPayments()) {
            Appointment a = getAppointmentById(p.getAppointmentId());
            if (a == null) continue;
            String svcId = a.getServiceId();
            totalByService.merge(svcId, p.getAmount(), Double::sum);
            countByService.merge(svcId, 1, Integer::sum);
        }

        List<String[]> rows = new ArrayList<>();
        double grandTotal = 0.0;
        int grandCount = 0;
        for (String svcId : totalByService.keySet()) {
            double tot = totalByService.get(svcId);
            int cnt = countByService.get(svcId);
            grandTotal += tot;
            grandCount += cnt;
            rows.add(new String[]{
                svcNameMap.getOrDefault(svcId, svcId),
                String.valueOf(cnt),
                String.format("RM %.2f", tot)
            });
        }
        rows.add(new String[]{
            "-- TOTAL --",
            String.valueOf(grandCount),
            String.format("RM %.2f", grandTotal)
        });
        return rows;
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (Payment p : getAllPayments()) total += p.getAmount();
        return total;
    }

    //Report 2: TECHNICIAN PERFORMANCE
    //Returns: [technicianId, name, assignedCount, completedCount, feedbacksGiven]
    public List<String[]> getTechnicianPerformanceReport() {
        List<Appointment> all = getAllAppointments();
        List<Feedback> feedbacks = getAllFeedbacks();

        List<String[]> rows = new ArrayList<>();
        for (Technician t : getAllTechnicians()) {
            int assigned = 0, completed = 0;
            for (Appointment a : all) {
                if (!a.getTechnicianId().equals(t.getTechnicianId())) continue;
                if (a.getStatus().equalsIgnoreCase("Assigned"))  assigned++;
                if (a.getStatus().equalsIgnoreCase("Completed")) completed++;
            }
            int fbCount = 0;
            for (Feedback f : feedbacks) {
                if (f.getTechnicianId().equals(t.getTechnicianId())) fbCount++;
            }
            rows.add(new String[]{
                t.getTechnicianId(),
                t.getName(),
                String.valueOf(assigned),
                String.valueOf(completed),
                String.valueOf(fbCount)
            });
        }
        return rows;
    }

    //Report 3: COUNTER STAFF PERFORMANCE
    //Returns: [counterStaffId, name, appointmentsCreated, paymentsCollected, revenueCollectedRM]
    public List<String[]> getCounterStaffPerformanceReport() {
        List<Appointment> all = getAllAppointments();
        List<Payment> payments = getAllPayments();

        List<String[]> rows = new ArrayList<>();
        for (CounterStaff cs : getAllCounterStaff()) {
            int appts = 0;
            int payCount = 0;
            double revenue = 0.0;
            //appointments created by this CS
            Set<String> myApptIds = new HashSet<>();
            for (Appointment a : all) {
                if (a.getCounterStaffId().equals(cs.getCounterStaffId())) {
                    appts++;
                    myApptIds.add(a.getAppointmentId());
                }
            }
            //payments tied to those appointments
            for (Payment p : payments) {
                if (myApptIds.contains(p.getAppointmentId())) {
                    payCount++;
                    revenue += p.getAmount();
                }
            }
            rows.add(new String[]{
                cs.getCounterStaffId(),
                cs.getName(),
                String.valueOf(appts),
                String.valueOf(payCount),
                String.format("RM %.2f", revenue)
            });
        }
        return rows;
    }

    //Report 4: SERVICE POPULARITY
    //Returns: [serviceId, serviceName, durationHours, totalAppointments, completedAppointments]
    public List<String[]> getServicePopularityReport() {
        List<Appointment> all = getAllAppointments();
        List<String[]> rows = new ArrayList<>();
        for (Service s : getAllServices()) {
            int total = 0;
            int completed = 0;
            for (Appointment a : all) {
                if (!a.getServiceId().equals(s.getServiceId())) continue;
                total++;
                if (a.getStatus().equalsIgnoreCase("Completed")) completed++;
            }
            rows.add(new String[]{
                s.getServiceId(),
                s.getServiceName(),
                s.getDurationHours() + " hr(s)",
                String.valueOf(total),
                String.valueOf(completed)
            });
        }
        return rows;
    }

    //Report 5: CUSTOMER ACTIVITY — top customers by spend + appointment count
    //Returns: [customerId, name, totalAppointments, totalSpentRM]
    public List<String[]> getCustomerActivityReport() {
        Map<String, Integer> apptCount = new HashMap<>();
        Map<String, Double>  spent     = new HashMap<>();

        for (Customer c : getAllCustomers()) {
            apptCount.put(c.getCustomerId(), 0);
            spent.put(c.getCustomerId(), 0.0);
        }
        for (Appointment a : getAllAppointments()) {
            apptCount.merge(a.getCustomerId(), 1, Integer::sum);
        }
        for (Payment p : getAllPayments()) {
            spent.merge(p.getCustomerId(), p.getAmount(), Double::sum);
        }

        //sort by total spent desc
        List<Customer> sorted = new ArrayList<>(getAllCustomers());
        sorted.sort((a, b) -> Double.compare(
                spent.getOrDefault(b.getCustomerId(), 0.0),
                spent.getOrDefault(a.getCustomerId(), 0.0)));

        List<String[]> rows = new ArrayList<>();
        for (Customer c : sorted) {
            rows.add(new String[]{
                c.getCustomerId(),
                c.getName(),
                String.valueOf(apptCount.getOrDefault(c.getCustomerId(), 0)),
                String.format("RM %.2f", spent.getOrDefault(c.getCustomerId(), 0.0))
            });
        }
        return rows;
    }

    //Report 6 (bonus): APPOINTMENT STATUS SUMMARY
    //Returns int[]{pending, assigned, completed, total}
    public int[] getAppointmentStatusCounts() {
        int pending = 0, assigned = 0, completed = 0;
        for (Appointment a : getAllAppointments()) {
            switch (a.getStatus().toLowerCase()) {
                case "pending":   pending++;   break;
                case "assigned":  assigned++;  break;
                case "completed": completed++; break;
            }
        }
        return new int[]{ pending, assigned, completed, pending + assigned + completed };
    }

    //Report 7 (bonus): MONTHLY REVENUE — current calendar year
    //Returns: [month (yyyy-MM), paymentCount, totalRM]
    public List<String[]> getMonthlyRevenueReport() {
        Map<String, Double> totals = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Payment p : getAllPayments()) {
            String month = p.getPaymentDate().length() >= 7
                    ? p.getPaymentDate().substring(0, 7) : "unknown";
            totals.merge(month, p.getAmount(), Double::sum);
            counts.merge(month, 1, Integer::sum);
        }
        //sort months ascending
        List<String> sortedKeys = new ArrayList<>(totals.keySet());
        Collections.sort(sortedKeys);
        List<String[]> rows = new ArrayList<>();
        for (String m : sortedKeys) {
            rows.add(new String[]{
                m,
                String.valueOf(counts.get(m)),
                String.format("RM %.2f", totals.get(m))
            });
        }
        return rows;
    }

    // ============================================================
    // DASHBOARD QUICK STATS
    // ============================================================

    public int countManagers()     { return getAllManagers().size(); }
    public int countCounterStaff() { return getAllCounterStaff().size(); }
    public int countTechnicians()  { return getAllTechnicians().size(); }
    public int countCustomers()    { return getAllCustomers().size(); }

    //Revenue for the current calendar month.
    public double getCurrentMonthRevenue() {
        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        double total = 0.0;
        for (Payment p : getAllPayments()) {
            if (p.getPaymentDate() != null && p.getPaymentDate().startsWith(thisMonth)) {
                total += p.getAmount();
            }
        }
        return total;
    }

    // ============================================================
    // LOOKUPS (helpers for display)
    // ============================================================

    public String lookupTechnicianName(String id) {
        if (id == null) return "-";
        for (Technician t : getAllTechnicians()) {
            if (t.getTechnicianId().equals(id)) return t.getName();
        }
        return id;
    }

    public String lookupCounterStaffName(String id) {
        if (id == null) return "-";
        for (CounterStaff cs : getAllCounterStaff()) {
            if (cs.getCounterStaffId().equals(id)) return cs.getName();
        }
        return id;
    }

    public String lookupCustomerName(String id) {
        if (id == null) return "-";
        for (Customer c : getAllCustomers()) {
            if (c.getCustomerId().equals(id)) return c.getName();
        }
        return id;
    }

    public String lookupServiceName(String id) {
        if (id == null) return "-";
        for (Service s : getAllServices()) {
            if (s.getServiceId().equals(id)) return s.getServiceName();
        }
        return id;
    }

    //Decides whether a target userId is a technician or counter staff and returns the name.
    public String lookupTargetName(String targetUserId) {
        if (targetUserId == null) return "-";
        if (targetUserId.startsWith("T")) {
            return "[Tech] " + lookupTechnicianName(targetUserId);
        }
        if (targetUserId.startsWith("CS")) {
            return "[CS] " + lookupCounterStaffName(targetUserId);
        }
        return targetUserId;
    }

    public Map<String, String> buildServiceNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Service s : getAllServices()) {
            map.put(s.getServiceId(), s.getServiceName());
        }
        return map;
    }

    // ============================================================
    // SHARED DATA LOADERS
    // ============================================================

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readLines(FILE_CUSTOMERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Customer.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readLines(FILE_APPOINTMENTS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Appointment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public Appointment getAppointmentById(String id) {
        for (Appointment a : getAllAppointments()) {
            if (a.getAppointmentId().equals(id)) return a;
        }
        return null;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        for (String line : readLines(FILE_PAYMENTS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            try { list.add(Payment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    // ============================================================
    // USER ACCOUNT MANAGEMENT
    // ============================================================

    //Appends a new user account row to users.txt.
    private void createUserAccount(String roleRefId, String username, String password, String role) {
        String userId = generateUserId();
        String line = String.join(",", userId, username, password, role, roleRefId);
        appendLine(FILE_USERS, line);
    }

    //Removes user account by roleRefId (e.g. T002, CS001, M002).
    private void removeUserByRoleRef(String roleRefId) {
        List<String> lines = readLines(FILE_USERS);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) continue;
            updated.add(line);
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
    }

    //Looks up the username for a given roleRefId. Returns "" if no account.
    public String lookupUsername(String roleRefId) {
        if (roleRefId == null) return "";
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) return p[1].trim();
        }
        return "";
    }

    //Resets a staff member's login credentials in users.txt. Either field may be blank to keep current.
    //Returns null if no changes made, OK/ERR otherwise.
    public String resetStaffAccount(String roleRefId, String newUsername, String newPassword) {
        boolean changeUser = newUsername != null && !newUsername.isBlank();
        boolean changePass = newPassword != null && !newPassword.isBlank();
        if (!changeUser && !changePass) return null; //no change

        //locate target user row
        String targetUserId = null;
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) {
                targetUserId = p[0].trim();
                break;
            }
        }
        if (targetUserId == null) return "ERR: Login account not found for " + roleRefId + ".";

        //validate
        if (changeUser) {
            if (newUsername.trim().length() < 3) return "ERR: Username must be at least 3 characters.";
            if (newUsername.contains(","))       return "ERR: Username cannot contain commas.";
            for (String line : readLines(FILE_USERS)) {
                if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
                String[] p = line.split(",", 5);
                if (p.length == 5 && p[1].trim().equalsIgnoreCase(newUsername.trim())
                        && !p[0].trim().equals(targetUserId)) {
                    return "ERR: Username already taken.";
                }
            }
        }
        if (changePass) {
            if (newPassword.length() < 4) return "ERR: Password must be at least 4 characters.";
            if (newPassword.contains(",")) return "ERR: Password cannot contain commas.";
        }

        //apply
        List<String> updated = new ArrayList<>();
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) {
                updated.add(line);
                continue;
            }
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[0].trim().equals(targetUserId)) {
                if (changeUser) p[1] = newUsername.trim();
                if (changePass) p[2] = newPassword.trim();
                updated.add(String.join(",", p));
            } else {
                updated.add(line);
            }
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
        return "OK: Login credentials updated for " + roleRefId + ".";
    }

    // ============================================================
    // ID GENERATION
    // ============================================================

    private String generateUserId() {
        int max = 0;
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith(COMMENT_CHAR) || line.isBlank()) continue;
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

    private String generateManagerId() {
        int max = 0;
        for (Manager m : getAllManagers()) {
            try {
                int num = Integer.parseInt(m.getManagerId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("M%03d", max + 1);
    }

    private String generateCounterStaffId() {
        int max = 0;
        for (CounterStaff cs : getAllCounterStaff()) {
            try {
                int num = Integer.parseInt(cs.getCounterStaffId().substring(2));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("CS%03d", max + 1);
    }

    private String generateTechnicianId() {
        int max = 0;
        for (Technician t : getAllTechnicians()) {
            try {
                int num = Integer.parseInt(t.getTechnicianId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("T%03d", max + 1);
    }
}
