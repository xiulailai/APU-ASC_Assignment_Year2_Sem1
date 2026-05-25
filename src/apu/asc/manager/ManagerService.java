package apu.asc.manager;

import apu.asc.staff.*;
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

     private final String managerId;
 
    public ManagerService(String managerId) {
        this.managerId = managerId;
    }
 
    public String getManagerId() { return managerId; }
    
    // Validate
    public String validatePerson(String name, String ageStr, String phone, String email,
                                  String address, String nationality) {
        String err = validatePersonFields(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
        if (name.contains(",") || address.contains(","))
            return "ERR: Name and address cannot contain commas.";
        return null;
    }
 
    public String validateAccount(String username, String password, String excludeUserId) {
        if (username == null || username.isBlank()) return "ERR: Username cannot be empty.";
        if (password == null || password.isBlank()) return "ERR: Password cannot be empty.";
        if (username.length() < 3) return "ERR: Username must be at least 3 characters.";
        if (password.length() < 4) return "ERR: Password must be at least 4 characters.";
        if (username.contains(",") || password.contains(","))
            return "ERR: Username and password cannot contain commas.";
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[1].trim().equalsIgnoreCase(username.trim())) {
                if (excludeUserId == null || !p[0].trim().equals(excludeUserId))
                    return "ERR: Username already taken.";
            }
        }
        return null;
    }
 
    public String validatePrice(String priceStr) {
        if (priceStr == null || priceStr.isBlank()) return "ERR: Price cannot be empty.";
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0)      return "ERR: Price must be greater than 0.";
            if (price > 100000)  return "ERR: Price seems too large.";
        } catch (NumberFormatException e) { return "ERR: Price must be a valid number."; }
        return null;
    }
    
    //Profile
    public Manager getProfile() {
        for (Manager m : loadAllManagers()) {
            if (m.getManagerId().equals(managerId)) return m;
        }
        return null;
    }
 
    public String getMyUsername() {
        return lookupUsername(managerId);
    }
 
    public String updateProfile(String name, String gender, String ageStr,
                                 String phone, String email, String address, String nationality,
                                 String newUsername, String newPassword) {
        String err = validatePerson(name, ageStr, phone, email, address, nationality);
        if (err != null) return err;
 
        int age = Integer.parseInt(ageStr.trim());
 
        List<String> lines = readLines(FILE_MANAGERS);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Manager m = Manager.fromFileString(line);
            if (m.getManagerId().equals(managerId)) {
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
 
        if ((newUsername != null && !newUsername.isBlank()) || (newPassword != null && !newPassword.isBlank())) {
            String accErr = updateOwnAccount(newUsername, newPassword);
            if (accErr != null) return accErr;
        }
        return "OK: Profile updated successfully!";
    }
 
    private String updateOwnAccount(String newUsername, String newPassword) {
        String myUserId = null;
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(managerId)) { myUserId = p[0].trim(); break; }
        }
        if (myUserId == null) return "ERR: User account not found.";
 
        if (newUsername != null && !newUsername.isBlank()) {
            if (newUsername.trim().length() < 3) return "ERR: Username must be at least 3 characters.";
            if (newUsername.contains(","))       return "ERR: Username cannot contain commas.";
            for (String line : readLines(FILE_USERS)) {
                String[] p = line.split(",", 5);
                if (p.length == 5 && p[1].trim().equalsIgnoreCase(newUsername.trim())
                        && !p[0].trim().equals(myUserId))
                    return "ERR: Username already taken.";
            }
        }
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 4) return "ERR: Password must be at least 4 characters.";
            if (newPassword.contains(",")) return "ERR: Password cannot contain commas.";
        }
 
        List<String> updated = new ArrayList<>();
        for (String line : readLines(FILE_USERS)) {
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
    
    //crud: manager
    public List<Manager> getAllManagers() { return loadAllManagers(); }
 
    public Manager getManagerById(String id) {
        for (Manager m : loadAllManagers()) {
            if (m.getManagerId().equals(id)) return m;
        }
        return null;
    }
 
    public List<Manager> searchManagers(String query) {
        List<Manager> all = loadAllManagers();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<Manager> result = new ArrayList<>();
        for (Manager m : all) {
            if (m.getManagerId().toLowerCase().contains(q) || m.getName().toLowerCase().contains(q)
                    || m.getPhone().contains(q) || m.getEmail().toLowerCase().contains(q))
                result.add(m);
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
        appendLine(FILE_MANAGERS, new Manager(newId, name.trim(), gender, age,
                phone.trim(), email.trim(), address.trim(), nationality.trim()).toFileString());
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
            Manager m = Manager.fromFileString(line);
            if (m.getManagerId().equals(id)) {
                m.setName(name.trim()); m.setGender(gender); m.setAge(age);
                m.setPhone(phone.trim()); m.setEmail(email.trim());
                m.setAddress(address.trim()); m.setNationality(nationality.trim());
                updated.add(m.toFileString());
                found = true;
            } else { updated.add(line); }
        }
        if (!found) return "ERR: Manager record not found.";
        writeLines(FILE_MANAGERS, "# managerId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Manager " + id + " updated successfully!";
    }
 
    public String deleteManager(String id) {
        if (id.equals(managerId)) return "ERR: You cannot delete your own account.";
        if (loadAllManagers().size() <= 1) return "ERR: At least one manager must remain.";
 
        List<Manager> all = loadAllManagers();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (Manager m : all) {
            if (m.getManagerId().equals(id)) { found = true; continue; }
            updated.add(m.toFileString());
        }
        if (!found) return "ERR: Manager not found.";
        writeLines(FILE_MANAGERS, "# managerId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Manager " + id + " deleted successfully!";
    }
    
    //crud: cs
    public List<CounterStaff> getAllCounterStaff() { return loadAllCounterStaff(); }
 
    public CounterStaff getCounterStaffById(String id) { return findCounterStaffById(id); }
 
    public List<CounterStaff> searchCounterStaff(String query) {
        List<CounterStaff> all = loadAllCounterStaff();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<CounterStaff> result = new ArrayList<>();
        for (CounterStaff cs : all) {
            if (cs.getCounterStaffId().toLowerCase().contains(q) || cs.getName().toLowerCase().contains(q)
                    || cs.getPhone().contains(q) || cs.getEmail().toLowerCase().contains(q))
                result.add(cs);
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
        appendLine(FILE_COUNTERSTAFF, new CounterStaff(newId, name.trim(), gender, age,
                phone.trim(), email.trim(), address.trim(), nationality.trim()).toFileString());
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
            CounterStaff cs = CounterStaff.fromFileString(line);
            if (cs.getCounterStaffId().equals(id)) {
                cs.setName(name.trim()); cs.setGender(gender); cs.setAge(age);
                cs.setPhone(phone.trim()); cs.setEmail(email.trim());
                cs.setAddress(address.trim()); cs.setNationality(nationality.trim());
                updated.add(cs.toFileString());
                found = true;
            } else { updated.add(line); }
        }
        if (!found) return "ERR: Counter Staff record not found.";
        writeLines(FILE_COUNTERSTAFF, "# counterStaffId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Counter Staff " + id + " updated successfully!";
    }
 
    public String deleteCounterStaff(String id) {
        List<CounterStaff> all = loadAllCounterStaff();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (CounterStaff cs : all) {
            if (cs.getCounterStaffId().equals(id)) { found = true; continue; }
            updated.add(cs.toFileString());
        }
        if (!found) return "ERR: Counter Staff not found.";
        writeLines(FILE_COUNTERSTAFF, "# counterStaffId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Counter Staff " + id + " deleted successfully!";
    }
    
    //crud: technician
    public List<Technician> getAllTechnicians() { return loadAllTechnicians(); }
 
    public Technician getTechnicianById(String id) { return findTechnicianById(id); }
 
    public List<Technician> searchTechnicians(String query) {
        List<Technician> all = loadAllTechnicians();
        if (query == null || query.isBlank()) return all;
        String q = query.toLowerCase().trim();
        List<Technician> result = new ArrayList<>();
        for (Technician t : all) {
            if (t.getTechnicianId().toLowerCase().contains(q) || t.getName().toLowerCase().contains(q)
                    || t.getPhone().contains(q) || t.getEmail().toLowerCase().contains(q))
                result.add(t);
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
        appendLine(FILE_TECHNICIANS, new Technician(newId, name.trim(), gender, age,
                phone.trim(), email.trim(), address.trim(), nationality.trim()).toFileString());
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
            Technician t = Technician.fromFileString(line);
            if (t.getTechnicianId().equals(id)) {
                t.setName(name.trim()); t.setGender(gender); t.setAge(age);
                t.setPhone(phone.trim()); t.setEmail(email.trim());
                t.setAddress(address.trim()); t.setNationality(nationality.trim());
                updated.add(t.toFileString());
                found = true;
            } else { updated.add(line); }
        }
        if (!found) return "ERR: Technician record not found.";
        writeLines(FILE_TECHNICIANS, "# technicianId,name,gender,age,phone,email,address,nationality", updated);
        return "OK: Technician " + id + " updated successfully!";
    }
 
    public String deleteTechnician(String id) {
        for (Appointment a : loadAllAppointments()) {
            if (a.getTechnicianId().equals(id) && !a.getStatus().equalsIgnoreCase("Completed"))
                return "ERR: Cannot delete – technician has active appointments. Reassign or wait until they are Completed.";
        }
        List<Technician> all = loadAllTechnicians();
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (Technician t : all) {
            if (t.getTechnicianId().equals(id)) { found = true; continue; }
            updated.add(t.toFileString());
        }
        if (!found) return "ERR: Technician not found.";
        writeLines(FILE_TECHNICIANS, "# technicianId,name,gender,age,phone,email,address,nationality", updated);
        removeUserByRoleRef(id);
        return "OK: Technician " + id + " deleted successfully!";
    }
    
    // Service Price
    public List<Service> getAllServices() { return loadAllServices(); }
 
    public Service getServiceById(String id) {
        for (Service s : loadAllServices()) {
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
            Service s = Service.fromFileString(line);
            if (s.getServiceId().equals(serviceId)) {
                updated.add(new Service(s.getServiceId(), s.getServiceName(), s.getDurationHours(), newPrice).toFileString());
                found = true;
            } else { updated.add(line); }
        }
        if (!found) return "ERR: Service not found.";
        writeLines(FILE_SERVICES, "# serviceId,serviceName,durationHours,price", updated);
        return "OK: Price for " + serviceId + " updated to RM " + String.format("%.2f", newPrice) + ".";
    }
    
    //Feedback and Comments
    public List<Feedback> getAllFeedbacks() { return loadAllFeedbacks(); }
 
    public List<Comment> getAllComments() { return loadAllComments(); }
 
    public List<String[]> getFeedbackRows(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<String[]> rows = new ArrayList<>();
        for (Feedback f : loadAllFeedbacks()) {
            String techName = lookupTechnicianName(f.getTechnicianId());
            String combined = (f.getFeedbackId() + " " + f.getAppointmentId() + " "
                    + techName + " " + f.getFeedbackText()).toLowerCase();
            if (!kw.isEmpty() && !combined.contains(kw)) continue;
            rows.add(new String[]{ f.getFeedbackId(), f.getAppointmentId(), techName, f.getFeedbackText() });
        }
        return rows;
    }
 
    public List<String[]> getCommentRows(String keyword) {
        String kw = (keyword == null) ? "" : keyword.trim().toLowerCase();
        List<String[]> rows = new ArrayList<>();
        for (Comment c : loadAllComments()) {
            String customerName = lookupCustomerName(c.getCustomerId());
            String targetName   = lookupTargetName(c.getTargetUserId());
            String combined = (c.getCommentId() + " " + c.getAppointmentId() + " "
                    + customerName + " " + targetName + " " + c.getCommentText()).toLowerCase();
            if (!kw.isEmpty() && !combined.contains(kw)) continue;
            rows.add(new String[]{ c.getCommentId(), c.getAppointmentId(), customerName, targetName, c.getCommentText() });
        }
        return rows;
    }
 
    // Decides whether a targetUserId is a technician or counter staff and returns a labelled name
    public String lookupTargetName(String targetUserId) {
        if (targetUserId == null) return "-";
        if (targetUserId.startsWith("T"))  return "[Tech] " + lookupTechnicianName(targetUserId);
        if (targetUserId.startsWith("CS")) return "[CS] "   + lookupCounterStaffName(targetUserId);
        return targetUserId;
    }
    
    //Reports
    // Report 1: REVENUE — total + per service breakdown
    public List<String[]> getRevenueReport() {
        Map<String, String>  svcNameMap      = buildServiceNameMap();
        Map<String, Double>  totalByService  = new LinkedHashMap<>();
        Map<String, Integer> countByService  = new LinkedHashMap<>();
 
        for (Service s : loadAllServices()) {
            totalByService.put(s.getServiceId(), 0.0);
            countByService.put(s.getServiceId(), 0);
        }
        for (Payment p : loadAllPayments()) {
            Appointment a = findAppointmentById(p.getAppointmentId());
            if (a == null) continue;
            totalByService.merge(a.getServiceId(), p.getAmount(), Double::sum);
            countByService.merge(a.getServiceId(), 1, Integer::sum);
        }
 
        List<String[]> rows = new ArrayList<>();
        double grandTotal = 0.0;
        int grandCount = 0;
        for (String svcId : totalByService.keySet()) {
            double tot = totalByService.get(svcId);
            int cnt    = countByService.get(svcId);
            grandTotal += tot; grandCount += cnt;
            rows.add(new String[]{ svcNameMap.getOrDefault(svcId, svcId), String.valueOf(cnt), String.format("RM %.2f", tot) });
        }
        rows.add(new String[]{ "-- TOTAL --", String.valueOf(grandCount), String.format("RM %.2f", grandTotal) });
        return rows;
    }
 
    public double getTotalRevenue() {
        double total = 0.0;
        for (Payment p : loadAllPayments()) total += p.getAmount();
        return total;
    }
 
    // Report 2: TECHNICIAN PERFORMANCE
    public List<String[]> getTechnicianPerformanceReport() {
        List<Appointment> all      = loadAllAppointments();
        List<Feedback>    feedbacks = loadAllFeedbacks();
        List<String[]>    rows     = new ArrayList<>();
        for (Technician t : loadAllTechnicians()) {
            int assigned = 0, completed = 0, fbCount = 0;
            for (Appointment a : all) {
                if (!a.getTechnicianId().equals(t.getTechnicianId())) continue;
                if (a.getStatus().equalsIgnoreCase("Assigned"))  assigned++;
                if (a.getStatus().equalsIgnoreCase("Completed")) completed++;
            }
            for (Feedback f : feedbacks) {
                if (f.getTechnicianId().equals(t.getTechnicianId())) fbCount++;
            }
            rows.add(new String[]{ t.getTechnicianId(), t.getName(), String.valueOf(assigned), String.valueOf(completed), String.valueOf(fbCount) });
        }
        return rows;
    }
 
    // Report 3: COUNTER STAFF PERFORMANCE
    public List<String[]> getCounterStaffPerformanceReport() {
        List<Appointment> all      = loadAllAppointments();
        List<Payment>     payments = loadAllPayments();
        List<String[]>    rows     = new ArrayList<>();
        for (CounterStaff cs : loadAllCounterStaff()) {
            int appts = 0, payCount = 0; double revenue = 0.0;
            Set<String> myApptIds = new HashSet<>();
            for (Appointment a : all) {
                if (a.getCounterStaffId().equals(cs.getCounterStaffId())) { appts++; myApptIds.add(a.getAppointmentId()); }
            }
            for (Payment p : payments) {
                if (myApptIds.contains(p.getAppointmentId())) { payCount++; revenue += p.getAmount(); }
            }
            rows.add(new String[]{ cs.getCounterStaffId(), cs.getName(), String.valueOf(appts), String.valueOf(payCount), String.format("RM %.2f", revenue) });
        }
        return rows;
    }
 
    // Report 4: SERVICE POPULARITY
    public List<String[]> getServicePopularityReport() {
        List<Appointment> all  = loadAllAppointments();
        List<String[]>    rows = new ArrayList<>();
        for (Service s : loadAllServices()) {
            int total = 0, completed = 0;
            for (Appointment a : all) {
                if (!a.getServiceId().equals(s.getServiceId())) continue;
                total++;
                if (a.getStatus().equalsIgnoreCase("Completed")) completed++;
            }
            rows.add(new String[]{ s.getServiceId(), s.getServiceName(), s.getDurationHours() + " hr(s)", String.valueOf(total), String.valueOf(completed) });
        }
        return rows;
    }
 
    // Report 5: CUSTOMER ACTIVITY
    public List<String[]> getCustomerActivityReport() {
        Map<String, Integer> apptCount = new HashMap<>();
        Map<String, Double>  spent     = new HashMap<>();
        for (Customer c : loadAllCustomers()) { apptCount.put(c.getCustomerId(), 0); spent.put(c.getCustomerId(), 0.0); }
        for (Appointment a : loadAllAppointments()) apptCount.merge(a.getCustomerId(), 1, Integer::sum);
        for (Payment p     : loadAllPayments())     spent.merge(p.getCustomerId(), p.getAmount(), Double::sum);
 
        List<Customer> sorted = new ArrayList<>(loadAllCustomers());
        sorted.sort((a, b) -> Double.compare(spent.getOrDefault(b.getCustomerId(), 0.0), spent.getOrDefault(a.getCustomerId(), 0.0)));
 
        List<String[]> rows = new ArrayList<>();
        for (Customer c : sorted) {
            rows.add(new String[]{ c.getCustomerId(), c.getName(),
                    String.valueOf(apptCount.getOrDefault(c.getCustomerId(), 0)),
                    String.format("RM %.2f", spent.getOrDefault(c.getCustomerId(), 0.0)) });
        }
        return rows;
    }
 
    // Report 6: APPOINTMENT STATUS SUMMARY
    public int[] getAppointmentStatusCounts() {
        int pending = 0, assigned = 0, completed = 0;
        for (Appointment a : loadAllAppointments()) {
            switch (a.getStatus().toLowerCase()) {
                case "pending":   pending++;   break;
                case "assigned":  assigned++;  break;
                case "completed": completed++; break;
            }
        }
        return new int[]{ pending, assigned, completed, pending + assigned + completed };
    }
 
    // Report 7: MONTHLY REVENUE
    public List<String[]> getMonthlyRevenueReport() {
        Map<String, Double>  totals = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Payment p : loadAllPayments()) {
            String month = p.getPaymentDate().length() >= 7 ? p.getPaymentDate().substring(0, 7) : "unknown";
            totals.merge(month, p.getAmount(), Double::sum);
            counts.merge(month, 1, Integer::sum);
        }
        List<String> sortedKeys = new ArrayList<>(totals.keySet());
        Collections.sort(sortedKeys);
        List<String[]> rows = new ArrayList<>();
        for (String m : sortedKeys) {
            rows.add(new String[]{ m, String.valueOf(counts.get(m)), String.format("RM %.2f", totals.get(m)) });
        }
        return rows;
    }
    
    //Dashboard quick stats
    public int countManagers()     { return loadAllManagers().size(); }
    public int countCounterStaff() { return loadAllCounterStaff().size(); }
    public int countTechnicians()  { return loadAllTechnicians().size(); }
    public int countCustomers()    { return loadAllCustomers().size(); }
 
    public double getCurrentMonthRevenue() {
        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        double total = 0.0;
        for (Payment p : loadAllPayments()) {
            if (p.getPaymentDate() != null && p.getPaymentDate().startsWith(thisMonth)) total += p.getAmount();
        }
        return total;
    }
    
    //User accounr management
    public String resetStaffAccount(String roleRefId, String newUsername, String newPassword) {
        boolean changeUser = newUsername != null && !newUsername.isBlank();
        boolean changePass = newPassword != null && !newPassword.isBlank();
        if (!changeUser && !changePass) return null;
 
        String targetUserId = null;
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(roleRefId)) { targetUserId = p[0].trim(); break; }
        }
        if (targetUserId == null) return "ERR: Login account not found for " + roleRefId + ".";
 
        if (changeUser) {
            if (newUsername.trim().length() < 3) return "ERR: Username must be at least 3 characters.";
            if (newUsername.contains(","))       return "ERR: Username cannot contain commas.";
            for (String line : readLines(FILE_USERS)) {
                String[] p = line.split(",", 5);
                if (p.length == 5 && p[1].trim().equalsIgnoreCase(newUsername.trim()) && !p[0].trim().equals(targetUserId))
                    return "ERR: Username already taken.";
            }
        }
        if (changePass) {
            if (newPassword.length() < 4) return "ERR: Password must be at least 4 characters.";
            if (newPassword.contains(",")) return "ERR: Password cannot contain commas.";
        }
 
        final String finalTargetUserId = targetUserId;
        List<String> updated = new ArrayList<>();
        for (String line : readLines(FILE_USERS)) {
            String[] p = line.split(",", 5);
            if (p.length == 5 && p[0].trim().equals(finalTargetUserId)) {
                if (changeUser) p[1] = newUsername.trim();
                if (changePass) p[2] = newPassword.trim();
                updated.add(String.join(",", p));
            } else { updated.add(line); }
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
        return "OK: Login credentials updated for " + roleRefId + ".";
    }
    
    //id generate
    private String generateManagerId() {
        int max = 0;
        for (Manager m : loadAllManagers()) {
            try { int n = Integer.parseInt(m.getManagerId().substring(1)); if (n > max) max = n; } catch (Exception ignored) {}
        }
        return String.format("M%03d", max + 1);
    }
 
    private String generateCounterStaffId() {
        int max = 0;
        for (CounterStaff cs : loadAllCounterStaff()) {
            try { int n = Integer.parseInt(cs.getCounterStaffId().substring(2)); if (n > max) max = n; } catch (Exception ignored) {}
        }
        return String.format("CS%03d", max + 1);
    }
 
    private String generateTechnicianId() {
        int max = 0;
        for (Technician t : loadAllTechnicians()) {
            try { int n = Integer.parseInt(t.getTechnicianId().substring(1)); if (n > max) max = n; } catch (Exception ignored) {}
        }
        return String.format("T%03d", max + 1);
    }
 
    // Expose for screens that need to display current usernames
    public List<Customer> getAllCustomers() { 
        return loadAllCustomers(); 
    }
    
    public List<Appointment> getAllAppointments() { 
        return loadAllAppointments(); 
    }
    
    public Appointment getAppointmentById(String id) { 
        return findAppointmentById(id); 
    }
    
    public List<Payment> getAllPayments() { 
        return loadAllPayments(); 
    }
}
