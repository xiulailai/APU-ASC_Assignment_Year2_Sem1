package apu.asc.counterstaff;

import apu.asc.staff.Appointment;
import apu.asc.staff.BaseFileHelper;
import apu.asc.staff.CounterStaff;
import apu.asc.staff.Customer;
import apu.asc.staff.Payment;
import apu.asc.staff.Service;
import apu.asc.staff.Technician;
import java.util.ArrayList;
import java.util.List;

public class csService extends BaseFileHelper {
    
    // 1. Helper
    private void saveAllCounterStaff(List<CounterStaff> list) {
        List<String> lines = new ArrayList<>();
        for (CounterStaff cs : list) lines.add(cs.toFileString());
        writeLines(FILE_COUNTERSTAFF, "# counterStaffId,name,gender,age,phone,email,address,nationality", lines);
    }
 
    private void saveAllCustomers(List<Customer> list) {
        List<String> lines = new ArrayList<>();
        for (Customer c : list) lines.add(c.toFileString());
        writeLines(FILE_CUSTOMERS, "# customerId,name,gender,age,phone,email,address,nationality", lines);
    }
 
    private void saveAllAppointments(List<Appointment> list) {
        List<String> lines = new ArrayList<>();
        for (Appointment a : list) lines.add(a.toFileString());
        writeLines(FILE_APPOINTMENTS, "# appointmentId,customerId,technicianId,counterStaffId,serviceId,appointmentDate,appointmentTime,status", lines);
    }
    
    // 2. Dashboard
    // Finds and returns the CounterStaff object matching the given ID. Returns null if not found.
    public CounterStaff getProfile(String counterStaffId) {
        return findCounterStaffById(counterStaffId);
    }
 
    // Gets all counter staff — delegates to base loader
    public List<CounterStaff> getAllCounterStaff() {
        return loadAllCounterStaff();
    }
    
    // 3. Edit Profile
    // Validates then overwrites the matching existing record in counterstaff.txt
    public String updateProfile(String counterStaffId, String name, String gender, String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePersonFields(name, ageStr, phone, email, address, nationality);
        if (err != null) {
            return err;
        }
 
        int age = Integer.parseInt(ageStr.trim());
 
        List<CounterStaff> allStaff = loadAllCounterStaff();
        boolean found = false;
        for (CounterStaff cs : allStaff) {
            if (cs.getCounterStaffId().equals(counterStaffId)) {
                cs.setName(name.trim());
                cs.setGender(gender);
                cs.setAge(age);
                cs.setPhone(phone.trim());
                cs.setEmail(email.trim());
                cs.setAddress(address.trim());
                cs.setNationality(nationality.trim());
                found = true;
                break;
            }
        }
        if (!found) {
            return "ERR: Staff record not found.";
        }
        saveAllCounterStaff(allStaff);
        return "OK: Profile updated successfully!";
    }
    
    // 4. Manage Customer
    // Gets all customers from file
    public List<Customer> getAllCustomers() {
        return loadAllCustomers();
    }
 
    // Returns customers matching ID, name, or phone (case-insensitive). Returns all if query is blank.
    public List<Customer> searchCustomers(String query) {
        List<Customer> all = loadAllCustomers();
        if (query == null || query.isBlank()) {
            return all;
        }
 
        List<Customer> result = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Customer c : all) {
            if (c.getCustomerId().toLowerCase().contains(q) || c.getName().toLowerCase().contains(q) || c.getPhone().contains(q)) {
                result.add(c);
            }
        }
        return result;
    }
 
    // Validates and adds a new customer to customers.txt, then creates their user account
    public String addCustomer(String name, String gender, String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePersonFields(name, ageStr, phone, email, address, nationality);
        if (err != null) {
            return err;
        }
 
        int age = Integer.parseInt(ageStr.trim());
        String newId = generateCustomerId();
 
        Customer newCustomer = new Customer(newId, name.trim(), gender, age, phone.trim(), email.trim(), address.trim(), nationality.trim());
        appendLine(FILE_CUSTOMERS, newCustomer.toFileString());
 
        String userResult = createDefaultUserAccount(newId, name.trim());
        if (userResult.startsWith("ERR:")) {
            return userResult;
        }
 
        return "OK: Customer " + newId + " added successfully!";
    }
 
    // Generates the next customer ID (e.g. C004)
    private String generateCustomerId() {
        int max = 0;
        for (Customer c : loadAllCustomers()) {
            try {
                int num = Integer.parseInt(c.getCustomerId().substring(1)); //C004 becomes 004
                if (num > max) {
                    max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }
 
    // Updates an existing customer record
    public String updateCustomer(String customerId, String name, String gender, String ageStr, String phone, String email, String address, String nationality) {
        String err = validatePersonFields(name, ageStr, phone, email, address, nationality);
        if (err != null) {
            return err;
        }
 
        int age = Integer.parseInt(ageStr.trim());
 
        List<Customer> allCustomer = loadAllCustomers();
        boolean found = false;
        for (Customer c : allCustomer) {
            if (c.getCustomerId().equals(customerId)) {
                c.setName(name.trim());
                c.setGender(gender);
                c.setAge(age);
                c.setPhone(phone.trim());
                c.setEmail(email.trim());
                c.setAddress(address.trim());
                c.setNationality(nationality.trim());
                found = true;
                break;
            }
        }
        if (!found) {
            return "ERR: Customer record not found.";
        }
        saveAllCustomers(allCustomer);
        return "OK: Customer updated successfully!";
    }
 
    // Deletes a customer and their user account
    public String deleteCustomer(String customerId) {
        List<Customer> allCustomer = loadAllCustomers();
        boolean removed = allCustomer.removeIf(c -> c.getCustomerId().equals(customerId));
        if (!removed) {
            return "ERR: Customer not found.";
        }
 
        saveAllCustomers(allCustomer);
        removeUserByRoleRef(customerId);
        return "OK: Customer " + customerId + " deleted successfully!";
    }
 
    // Finds a single customer by ID. Returns null if not found.
    public Customer getCustomerById(String customerId) {
        return findCustomerById(customerId);
    }
    
    //Manage Appointment
    // Gets all appointments from file
    public List<Appointment> getAllAppointments() {
        return loadAllAppointments();
    }
 
    // Finds a single appointment by ID. Returns null if not found.
    public Appointment getAppointmentById(String appointmentId) {
        return findAppointmentById(appointmentId);
    }
 
    // Deletes an appointment by ID
    public String deleteAppointment(String appointmentId) {
        List<Appointment> all = loadAllAppointments();
        boolean removed = all.removeIf(a -> a.getAppointmentId().equals(appointmentId));
        if (!removed) {
            return "ERR: Appointment not found.";
        }
 
        saveAllAppointments(all);
        return "OK: Appointment " + appointmentId + " deleted successfully!";
    }
 
    // Gets all technicians from file
    public List<Technician> getAllTechnicians() {
        return loadAllTechnicians();
    }
 
    // Gets all services from file
    public List<Service> getAllServices() {
        return loadAllServices();
    }
 
    // Returns appointments filtered by status. Returns all if status is blank or "All".
    public List<Appointment> filterAppointmentsByStatus(String status) {
        List<Appointment> all = loadAllAppointments();
        if (status == null || status.isBlank() || status.equals("All")) {
            return all;
        }
 
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getStatus().equalsIgnoreCase(status.trim())) {
                result.add(a);
            }
        }
        return result;
    }
 
    // Returns technicians with no time clash on the target appointment's date and time window
    public List<Technician> getAvailableTechnicians(Appointment appointment) {
        List<Service> allServices = loadAllServices();
        List<Appointment> allAppointments = loadAllAppointments();
        List<Technician> allTechnicians = loadAllTechnicians();
 
        int targetDuration = 1;
        for (Service s : allServices) {
            if (s.getServiceId().equals(appointment.getServiceId())) {
                targetDuration = s.getDurationHours();
                break;
            }
        }
 
        java.time.LocalTime targetStart = java.time.LocalTime.parse(appointment.getAppointmentTime());
        java.time.LocalTime targetEnd = targetStart.plusHours(targetDuration);
        java.util.Set<String> busyTechs = new java.util.HashSet<>();  //create a collection with no duplicates
        
        // find appointment that may clash time (same day, assigned)
        for (Appointment a : allAppointments) {
            if (a.getAppointmentId().equals(appointment.getAppointmentId())) { //skip target itself 
                continue;
            }
            if (!a.getAppointmentDate().equals(appointment.getAppointmentDate())) { //skip appointments that are differnt days
                continue;
            }
            if (!a.getStatus().equals("Assigned")) { //skip if NOT assigned
                continue;
            }
 
            int duration = 1;
            for (Service s : allServices) {
                if (s.getServiceId().equals(a.getServiceId())) { 
                    duration = s.getDurationHours(); 
                    break; 
                }
            }
            java.time.LocalTime start = java.time.LocalTime.parse(a.getAppointmentTime());
            java.time.LocalTime end = start.plusHours(duration);
 
            if (targetStart.isBefore(end) && targetEnd.isAfter(start)) {
                busyTechs.add(a.getTechnicianId());
            }
        }
 
        List<Technician> available = new ArrayList<>();
        for (Technician t : allTechnicians) {
            if (!busyTechs.contains(t.getTechnicianId())) {
                available.add(t);
            }
        }
        return available;
    }
 
    // Assigns a technician to an appointment and sets status to Assigned
    public String assignAppointment(String appointmentId, String technicianId) {
        List<Appointment> all = loadAllAppointments();
        boolean found = false;
        for (Appointment a : all) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setTechnicianId(technicianId);
                a.setStatus("Assigned");
                found = true;
                break;
            }
        }
        if (!found) {
            return "ERR: Appointment not found.";
        }
        saveAllAppointments(all);
        return "OK: Technician assigned successfully!";
    }
 
    // Validates and creates a new appointment with status Pending and technicianId TBD
    public String createAppointment(String counterStaffId, String customerId, String serviceId, String date, String time) {
        if (customerId.isBlank() || serviceId.isBlank() || date.isBlank() || time.isBlank()) {
            return "ERR: All fields are required.";
        }
        if (!date.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "ERR: Date must be in yyyy-MM-dd format.";
        }
        try {
            java.time.LocalDate inputDate = java.time.LocalDate.parse(date.trim());
            if (inputDate.isBefore(java.time.LocalDate.now())) {
                return "ERR: Date cannot be in the past.";
            }
        } catch (Exception e) {
            return "ERR: Invalid date entered.";
        }
 
        String newId = generateAppointmentId();
        Appointment appt = new Appointment(newId, customerId, "TBD", counterStaffId, serviceId, date.trim(), time, "Pending");
        appendLine(FILE_APPOINTMENTS, appt.toFileString());
        return "OK: Appointment " + newId + " created successfully!";
    }
 
    // Generates the next appointment ID (e.g. A008)
    private String generateAppointmentId() {
        int max = 0;
        for (Appointment a : loadAllAppointments()) {
            try {
                int num = Integer.parseInt(a.getAppointmentId().substring(1)); //A008 becomes 008
                if (num > max) {
                    max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("A%03d", max + 1);
    }
    
    // 5. Payment
    // Returns assigned appointments that have no payment record yet
    public List<Appointment> getUnpaidAppointments() {
        java.util.Set<String> paidIds = new java.util.HashSet<>();
        for (Payment p : loadAllPayments()) {
            paidIds.add(p.getAppointmentId());
        }
 
        List<Appointment> unpaid = new ArrayList<>();
        for (Appointment a : loadAllAppointments()) {
            if (a.getStatus().equals("Assigned") && !paidIds.contains(a.getAppointmentId())) {
                unpaid.add(a);
            }
        }
        return unpaid;
    }
 
    // Gets all payments — delegates to base loader
    public List<Payment> getAllPayments() {
        return loadAllPayments();
    }
 
    // Collects payment for an appointment
    public String collectPayment(String appointmentId, String customerId, double amount) {
        String paymentId = generatePaymentId();
        String receiptNo = generateReceiptNo();
        String today = java.time.LocalDate.now().toString();
 
        Payment payment = new Payment(paymentId, appointmentId, customerId, amount, today, receiptNo);
        appendLine(FILE_PAYMENTS, payment.toFileString());
        return "OK:" + paymentId + "," + receiptNo + "," + today;
    }
 
    // Generates the next payment ID (e.g. P004)
    private String generatePaymentId() {
        int max = 0;
        for (Payment p : loadAllPayments()) {
            try {
                int num = Integer.parseInt(p.getPaymentId().substring(1)); //P004 becomes 004
                if (num > max) {
                    max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("P%03d", max + 1);
    }
 
    // Generates the next receipt number (e.g. R004)
    private String generateReceiptNo() {
        int max = 0;
        for (Payment p : loadAllPayments()) {
            try {
                int num = Integer.parseInt(p.getReceiptNo().substring(1)); //R004 becomes 004
                if (num > max) {
                    max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("R%03d", max + 1);
    }
}