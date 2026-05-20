package apu.asc.counterstaff;

import apu.asc.staff.Appointment;
import apu.asc.staff.CounterStaff;
import apu.asc.staff.Customer;
import apu.asc.staff.Payment;
import apu.asc.staff.Service;
import apu.asc.staff.Technician;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class csService {

    private static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    private static final String FILE_CUSTOMERS = "Data/customers.txt";
    private static final String FILE_USERS = "Data/users.txt";
    private static final String FILE_APPOINTMENTS = "Data/appointments.txt";
    private static final String FILE_TECHNICIANS = "Data/technicians.txt";
    private static final String FILE_SERVICES = "Data/services.txt";
    private static final String FILE_PAYMENTS = "Data/payments.txt";

    //File I/O activities
    // Reads all lines from a file. Returns empty list on error.
    private List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            System.err.println("csService.readLines error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }

    // Overwrites a file with the given list of lines.
    private void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("csService.writeLines error [" + filePath + "]: " + e.getMessage());
        }
    }

    // Appends a single line to the end of a file.
    private void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (Exception e) {
            System.err.println("csService.appendLine error [" + filePath + "]: " + e.getMessage());
        }
    }

    //for Dashboard
    //Finds and returns the CounterStaff object matching the given ID. Returns null if not found.
    public CounterStaff getProfile(String counterStaffId) {
        for (String line : readLines(FILE_COUNTERSTAFF)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            CounterStaff cs = CounterStaff.fromFileString(line);
            if (cs.getCounterStaffId().equals(counterStaffId)) {
                return cs;
            }
        }
        return null;
    }

    //for EditProfile
    //validates then overwrites the matching existing record in counterstaff.txt
    public String updateProfile(String counterStaffId, String name, String gender, String ageStr, String phone, String email, String address, String nationality) {

        //Validation 
        if (name.isBlank() || ageStr.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank() || nationality.isBlank()) {
            return "ERR: All fields are required.";
        }
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
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

        //Write to file
        List<String> lines = readLines(FILE_COUNTERSTAFF);
        boolean found = false;
        List<String> updated = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            CounterStaff cs = CounterStaff.fromFileString(line);
            if (cs.getCounterStaffId().equals(counterStaffId)) {
                cs.setName(name.trim());
                cs.setGender(gender);
                cs.setAge(age);
                cs.setPhone(phone.trim());
                cs.setEmail(email.trim());
                cs.setAddress(address.trim());
                cs.setNationality(nationality.trim());
                updated.add(cs.toFileString());
                found = true;
                continue;
            }
            updated.add(line);
        }

        if (!found) {
            return "ERR: Staff record not found.";
        }

        writeLines(FILE_COUNTERSTAFF, updated);
        return "OK: Profile updated successfully!";
    }

    //for ManageCustomer
    //gets all customer from file
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readLines(FILE_CUSTOMERS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            list.add(Customer.fromFileString(line));
        }
        return list;
    }

    //Returns customers based on ID, name, or phone in the query (case-insensitive), returns all customer if query is blank
    public List<Customer> searchCustomers(String query) {
        List<Customer> allCustomer = getAllCustomers();
        if (query == null || query.isBlank()) {
            return allCustomer;
        }

        List<Customer> result = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Customer c : allCustomer) {
            if (c.getCustomerId().toLowerCase().contains(q) || c.getName().toLowerCase().contains(q) || c.getPhone().contains(q)) {
                result.add(c);
            }
        }
        return result;
    }

    //validates and adds a new customer to customers.txt
    //also adds in user.txt
    public String addCustomer(String name, String gender, String ageStr, String phone, String email, String address, String nationality) {
        //validation
        if (name.isBlank() || ageStr.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank() || nationality.isBlank()) {
            return "ERR: All fields are required.";
        }
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 100) {
                return "ERR: Age must be between 0 and 100.";
            }
        } catch (NumberFormatException e) {
            return "ERR: Age must be a valid number.";
        }
        if (!phone.trim().matches("\\d{10,12}")) {
            return "ERR: Phone must be 10 to 12 digits.";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "ERR: Enter a valid email address.";
        }

        //Generate new ID
        String newId = generateCustomerId();

        //Append to customers.txt
        Customer newCustomer = new Customer(newId, name.trim(), gender, age, phone.trim(), email.trim(), address.trim(), nationality.trim());
        appendLine(FILE_CUSTOMERS, newCustomer.toFileString());

        //Create user account
        String userResult = createUserAccount(newId, name.trim());
        if (userResult.startsWith("ERR:")) {
            return userResult;
        }

        return "OK: Customer " + newId + " added successfully!";
    }
    
    //generates the next customer ID
    private String generateCustomerId() {
        int max = 0;
        for (Customer c : getAllCustomers()) {
            try {
                int num = Integer.parseInt(c.getCustomerId().substring(1)); //takes the numbers after the first character (Cxxx becomes xxx)
                if (num > max) {
                    max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }

    //updates existing customers
    public String updateCustomer(String customerId, String name, String gender, String ageStr, String phone, String email, String address, String nationality) {

        //Validation 
        if (name.isBlank() || ageStr.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank() || nationality.isBlank()) {
            return "ERR: All fields are required.";
        }
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
            if (age < 0 || age > 100) {
                return "ERR: Age must be between 0 and 100.";
            }
        } catch (NumberFormatException e) {
            return "ERR: Age must be a valid number.";
        }

        if (!phone.trim().matches("\\d{10,12}")) {
            return "ERR: Phone must be 10 to 12 digits.";
        }

        if (!email.contains("@") || !email.contains(".")) {
            return "ERR: Enter a valid email address.";
        }

        //Write to file
        boolean found = false;
        List<String> updated = new ArrayList<>();

        updated.add("# customerId,name,gender,age,phone,email,address,nationality");
        for (Customer c : getAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) {
                c.setName(name.trim());
                c.setGender(gender);
                c.setAge(age);
                c.setPhone(phone);
                c.setEmail(email);
                c.setAddress(address);
                c.setNationality(nationality);
                updated.add(c.toFileString());
                found = true;
            } else {
                updated.add(c.toFileString());
            }
        }
        if (!found) return "ERR: Customer record not found.";
        
        writeLines(FILE_CUSTOMERS, updated);
        return "OK: Customer updated successfully!";
    }

    //Deletes a customer by ID from customers.txt and their account from users.txt
    public String deleteCustomer(String customerId) {
        boolean found = false;
        List<String> updated = new ArrayList<>();

        updated.add("# customerId,name,gender,age,phone,email,address,nationality");
        for (Customer c : getAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) {
                found = true; // skip to not store in updated list (which means deleted)
            } else {
                updated.add(c.toFileString());
            }
        }

        if (!found) {
            return "ERR: Customer not found.";
        }

        writeLines(FILE_CUSTOMERS, updated);

        // Also remove from users.txt
        removeUserByRoleRef(customerId);

        return "OK: Customer " + customerId + " deleted successfully!";
    }
    
    //removes user by id
    private void removeUserByRoleRef(String roleRefId) {
        List<String> lines = readLines(FILE_USERS);
        List<String> updated = new ArrayList<>();
        updated.add(" # userId,username,password,role,roleRefId");
        
        for (String line : lines) {
            if (line.startsWith("#") || line.isBlank()) { 
                continue; //skips comment n blank lines
            }
            String[] data = line.split(",", 5);
            if (data.length == 5 && data[4].trim().equals(roleRefId)) { 
                continue; //skip to not store in updated
            }
            updated.add(line);
        }

        writeLines(FILE_USERS, updated);
    }

    //Finds a single customer by ID. Returns null if not found
    public Customer getCustomerById(String customerId) {
        for (Customer c : getAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    //creates a new user acc for customer
    private String createUserAccount(String customerId, String name) {
        int maxId = 0;
        for (String line : readLines(FILE_USERS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            String[] data = line.split(",", 5);
            if (data.length >= 1) {
                try {
                    int num = Integer.parseInt(data[0].trim().substring(1)); //takes the numbers after the first character (Uxxx becomes xxx)
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (Exception ignored) {}
            }
        }

        String userId = String.format("U%03d", maxId + 1);
        String idNum = customerId.substring(1);
        // default username: first 4 letters of name (lowercase) + id number
        String safeName = name.replaceAll("\\s+", "").toLowerCase();
        String username = safeName.substring(0, Math.min(4, safeName.length())) + idNum;
        String password = "pass" + idNum;
        String userLine = String.join(",", userId, username, password, "customer", customerId);

        appendLine(FILE_USERS, userLine);
        return "OK";
    }
    
    //for ManageAppointment
    //gets all appointments from file
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readLines(FILE_APPOINTMENTS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            list.add(Appointment.fromFileString(line));
        }
        return list;
    }
    
    //finds a single appointment by ID. Returns null if not found.
    public Appointment getAppointmentById(String appointmentId) {
        for (Appointment a : getAllAppointments()) {
            if (a.getAppointmentId().equals(appointmentId)) {
                return a;
            }
        }
        return null;
    }
    
    //deletes an appointment (only allowed if status is pending)
    public String deleteAppointment(String appointmentId) {
        List<Appointment> allAppointment = getAllAppointments();
        boolean found = false;
        List<String> updated = new ArrayList<>();
 
        updated.add("# appointmentId,customerId,technicianId,counterStaffId,serviceId,appointmentDate,appointmentTime,status");
        for (Appointment a : allAppointment) {
            if (a.getAppointmentId().equals(appointmentId)) {
                found = true; 
                if (!a.getStatus().equals("Pending")) {
                    return "ERR: Only Pending appointments can be deleted."; //not added into updated (skipped n deleted 
                }
            } else {
                updated.add(a.toFileString());
            }
        }
 
        if (!found) {
            return "ERR: Appointment not found.";
        }
 
        writeLines(FILE_APPOINTMENTS, updated);
        return "OK: Appointment " + appointmentId + " deleted successfully!";
    }
    
    //gets all technicians from file
    public List<Technician> getAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readLines(FILE_TECHNICIANS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            list.add(Technician.fromFileString(line));
        }
        return list;
    }
 
    //gets all services from file
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readLines(FILE_SERVICES)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            list.add(Service.fromFileString(line));
        }
        return list;
    }
 
    //looks up a customer name by ID. Returns the ID itself if not found
    public String lookupCustomerName(String customerId) {
        Customer c = getCustomerById(customerId);
        if (c != null) {
            return c.getName();
        }
        return customerId;
    }
 
    //looks up a technician name by ID. Returns "TBD" if not assigned yet.
    public String lookupTechnicianName(String technicianId) {
        if (technicianId == null || technicianId.equals("TBD")) {
            return "TBD";
        }
        for (Technician t : getAllTechnicians()) {
            if (t.getTechnicianId().equals(technicianId)) {
                return t.getName();
            }
        }
        return technicianId;
    }
 
    //looks up a service name by ID. Returns the ID itself if not found.
    public String lookupServiceName(String serviceId) {
        for (Service s : getAllServices()) {
            if (s.getServiceId().equals(serviceId)) {
                return s.getServiceName();
            }
        }
        return serviceId;
    }
    
    //validates and creates a new appointment with status Pending and technicianId TBD
    public String createAppointment(String counterStaffId, String customerId, String serviceId, String date, String time) {
        //Validation
        if (customerId.isBlank() || serviceId.isBlank() || date.isBlank() || time.isBlank()) {
            return "ERR: All fields are required.";
        }
        //Validate date format yyyy-MM-dd
        if (!date.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "ERR: Date must be in yyyy-MM-dd format.";
        }
        //Validate date is not in the past
        try {
            java.time.LocalDate inputDate = java.time.LocalDate.parse(date.trim());
            if (inputDate.isBefore(java.time.LocalDate.now())) {
                return "ERR: Date cannot be in the past.";
            }
        } catch (Exception e) {
            return "ERR: Invalid date entered.";
        }
 
        //Generate new appointment ID
        String newId = generateAppointmentId();
 
        //Write to file with default status pending and technicianId TBD
        Appointment newAppointment = new Appointment(newId, customerId, "TBD", counterStaffId, serviceId, date.trim(), time, "Pending");
        appendLine(FILE_APPOINTMENTS, newAppointment.toFileString());
 
        return "OK: Appointment " + newId + " created successfully!";
    }
 
    //generates the next appointment ID (e.g. A008)
    private String generateAppointmentId() {
        int max = 0;
        for (Appointment a : getAllAppointments()) {
            try {
                int num = Integer.parseInt(a.getAppointmentId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("A%03d", max + 1);
    }
    
    //returns appointments filtered by status. Returns all if status is blank
    public List<Appointment> filterAppointmentsByStatus(String status) {
        List<Appointment> allAppointment = getAllAppointments();
        if (status == null || status.isBlank()){
            return allAppointment;
        }
 
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : allAppointment) {
            if (a.getStatus().equalsIgnoreCase(status.trim())) {
                result.add(a);
            }
        }
        return result;
    }
    
    //returns list of technicians with no time clash on the target appointment's date and time window
    public List<Technician> getAvailableTechnicians(Appointment appointment) {
        List<Service> allServices = getAllServices();
        List<Appointment> allAppointments = getAllAppointments();
        List<Technician> allTechnicians = getAllTechnicians();

        // get duration of target appointment
        int targetDuration = 1; //default to 1
        for (Service s : allServices) {
            if (s.getServiceId().equals(appointment.getServiceId())) {
                targetDuration = s.getDurationHours(); 
                break;
            }
        }
        //find the time gap of the target appointment
        java.time.LocalTime targetStart = java.time.LocalTime.parse(appointment.getAppointmentTime());
        java.time.LocalTime targetEnd = targetStart.plusHours(targetDuration); 

        java.util.Set<String> busyTechs = new java.util.HashSet<>(); //create a collection with no duplicates

        for (Appointment a : allAppointments) {

            if (a.getAppointmentId().equals(appointment.getAppointmentId())) { //skip if it is the target appointment
                continue;
            }
            if (!a.getAppointmentDate().equals(appointment.getAppointmentDate())) { //skip if not same day (will not clash time
                continue;
            }
            if (a.getStatus().equals("Pending")) { //skip if pending appointment (no tech assigned
                continue;
            }

            int duration = 1;
            for (Service s : allServices) {
                if (s.getServiceId().equals(a.getServiceId())) {
                    duration = s.getDurationHours();
                    break;
                }
            }
            //find time gap of existing appointment (that may clash)
            java.time.LocalTime start = java.time.LocalTime.parse(a.getAppointmentTime());
            java.time.LocalTime end = start.plusHours(duration);

            boolean overlaps = targetStart.isBefore(end) && targetEnd.isAfter(start);
            if (overlaps) {
                busyTechs.add(a.getTechnicianId());
            }
        }

        List<Technician> availableTechs = new ArrayList<>();

        for (Technician t : allTechnicians) {
            if (!busyTechs.contains(t.getTechnicianId())) {
                availableTechs.add(t);
            }
        }

        return availableTechs;
    }
 
    //assigns a technician to an appointment and updates status to Assigned
    public String assignAppointment(String appointmentId, String technicianId) {
        List<Appointment> allAppointment = getAllAppointments();
        boolean found = false;
        List<String> updated = new ArrayList<>();
 
        updated.add("# appointmentId,customerId,technicianId,counterStaffId,serviceId,appointmentDate,appointmentTime,status");
        for (Appointment a : allAppointment) {
           if (a.getAppointmentId().equals(appointmentId)) {
                a.setTechnicianId(technicianId);
                a.setStatus("Assigned");
                updated.add(a.toFileString());
                found = true;
            } else {
                updated.add(a.toFileString());
            }
        }
 
        if (!found) {
            return "ERR: Appointment not found.";
        }
 
        writeLines(FILE_APPOINTMENTS, updated);
        return "OK: Technician assigned successfully!";
    }
    
    //for csPayments 
    //returns Assigned appointments that have no payment record yet
    public List<Appointment> getUnpaidAppointments() {
        //collect all appointment IDs that already have a payment
        java.util.Set<String> paidAppointmentIds = new java.util.HashSet<>(); //create and store only unique paid appointment ids
        for (String line : readLines(FILE_PAYMENTS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            String[] data = line.split(",", 6);
            if (data.length >= 2) {
                paidAppointmentIds.add(data[1].trim());
            }
        }
 
        //return Assigned appointments not in the paid set
        List<Appointment> unpaidAppointment = new ArrayList<>();
        for (Appointment a : getAllAppointments()) {
            if (a.getStatus().equals("Assigned") && !paidAppointmentIds.contains(a.getAppointmentId())) {
                unpaidAppointment.add(a);
            }
        }
        return unpaidAppointment;
    }
 
    //looks up the price of a service by ID. Returns 0.0 if not found
    public double lookupServicePrice(String serviceId) {
        for (Service s : getAllServices()) {
            if (s.getServiceId().equals(serviceId)) {
                return s.getPrice();
            }
        }
        return 0.0;
    }
 
    //collects payment for an appointment, writes to payments.txt and marks appointment as completed
    public String collectPayment(String appointmentId, String customerId, double amount) {
        //generate payment ids
        String paymentId = generatePaymentId();
        String receiptNo = generateReceiptNo();
        String today = java.time.LocalDate.now().toString();
 
        //write payment record
        Payment payment = new Payment(paymentId, appointmentId, customerId, amount, today, receiptNo);
        appendLine(FILE_PAYMENTS, payment.toFileString());
 
        //update appointment status to Completed
        List<Appointment> allAppointment = getAllAppointments();
        List<String> updated = new ArrayList<>();
        updated.add("# appointmentId,customerId,technicianId,counterStaffId,serviceId,appointmentDate,appointmentTime,status");
        for (Appointment a : allAppointment) {
            if (a.getAppointmentId().equals(appointmentId)) {
            a.setStatus("Completed");
            updated.add(a.toFileString());
            } else {
                updated.add(a.toFileString());
            }
        }
        writeLines(FILE_APPOINTMENTS, updated);
        return "OK:" + paymentId + "," + receiptNo + "," + today;
    }
    
    //generates the next payment ID (e.g. P004)
    private String generatePaymentId() {
        int max = 0;
        for (String line : readLines(FILE_PAYMENTS)) {
            if (line.startsWith("#") || line.isBlank()){
                continue;
            }
            String[] data = line.split(",", 6);
            if (data.length >= 1) {
                try {
                    int num = Integer.parseInt(data[0].trim().substring(1)); //only grabs the number Pxxx becomes xxx
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("P%03d", max + 1);
    }
 
    //generates the next receipt number (e.g. R004)
    private String generateReceiptNo() {
        int max = 0;
        for (String line : readLines(FILE_PAYMENTS)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            String[] data = line.split(",", 6);
            if (data.length >= 6) {
                try {
                    int num = Integer.parseInt(data[5].trim().substring(1)); //only grabs number Rxxx becomes xxx
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("R%03d", max + 1);
    }
 
    //returns all payment records from file
    public List<String[]> getPaymentHistory() {
        List<String[]> list = new ArrayList<>();
        for (String line : readLines(FILE_PAYMENTS)) {
            if (line.startsWith("#") || line.isBlank()) continue;
            list.add(line.split(",", 6));
        }
        return list;
    }
}