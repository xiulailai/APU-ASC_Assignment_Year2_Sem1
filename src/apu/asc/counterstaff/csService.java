package apu.asc.counterstaff;

import apu.asc.staff.CounterStaff;
import apu.asc.staff.Customer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class csService {
    private static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    private static final String FILE_CUSTOMERS    = "Data/customers.txt";
    private static final String FILE_USERS        = "Data/users.txt";
    
    //for csDashboard
    //Finds and returns the CounterStaff object matching the given ID. Returns null if not found.
    public CounterStaff getProfile(String counterStaffId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_COUNTERSTAFF))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;
                CounterStaff cs = CounterStaff.fromFileString(line);
                if (cs.getCounterStaffId().equals(counterStaffId)) {
                    return cs;
                }
            }
        } catch (Exception e) {
            System.err.println("csService.getProfile error: " + e.getMessage());
        }
        return null;
    }
    
    //for csEditProfile
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
        List<String> lines = new ArrayList<>();
        boolean found = false;
 
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_COUNTERSTAFF))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank()) {
                    CounterStaff cs = CounterStaff.fromFileString(line);
                    if (cs.getCounterStaffId().equals(counterStaffId)) {
                        cs.setName(name.trim());
                        cs.setGender(gender);
                        cs.setAge(age);
                        cs.setPhone(phone.trim());
                        cs.setEmail(email.trim());
                        cs.setAddress(address.trim());
                        cs.setNationality(nationality.trim());
                        lines.add(cs.toFileString());
                        found = true;
                        continue;
                    }
                }
                lines.add(line);
            }
        } catch (Exception e) {
            return "ERR: Could not read file — " + e.getMessage();
        }
 
        if (!found) return "ERR: Staff record not found.";
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_COUNTERSTAFF))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (Exception e) {
            return "ERR: Could not save file — " + e.getMessage();
        }
 
        return "OK: Profile updated successfully!";
    }
    
    //for csManageCustomer
    //gets all customer from file
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_CUSTOMERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;
                list.add(Customer.fromFileString(line));
            }
        } catch (Exception e) {
            System.err.println("csService.getAllCustomers error: " + e.getMessage());
        }
        return list;
    }
    
    //Returns customers whose ID, name, or phone contains the query (case-insensitive)
    //returns all customer if query is blank
    public List<Customer> searchCustomers(String query) {
        List<Customer> all = getAllCustomers();
        if (query == null || query.isBlank()) {
            return all;
        }
 
        List<Customer> result = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Customer c : all) {
            if (c.getCustomerId().toLowerCase().contains(q)
                    || c.getName().toLowerCase().contains(q)
                    || c.getPhone().contains(q)) {
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
 
        //Generate new ID
        String newId = generateCustomerId();
 
        // ── Append to customers.txt ───────────────────────────────────────
        Customer newCustomer = new Customer(newId, name.trim(), gender, age, phone.trim(), email.trim(), address.trim(), nationality.trim());
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_CUSTOMERS, true))) {
            bw.write(newCustomer.toFileString());
            bw.newLine();
        } catch (Exception e) {
            return "ERR: Could not save customer — " + e.getMessage();
        }
 
        //Create user account
        String userResult = createUserAccount(newId, name.trim());
        if (userResult.startsWith("ERR:")) return userResult;
 
        return "OK: Customer " + newId + " added successfully!";
    }
    
    //updates existing customers
    public String updateCustomer(String customerId, String name, String gender, String ageStr, String phone, String email, String address, String nationality) {
 
        // ── Validation ────────────────────────────────────────────────────
        if (name.isBlank() || ageStr.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank() || nationality.isBlank()) {
            return "ERR: All fields are required.";
        }
 
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
            if (age < 1 || age > 120) return "ERR: Age must be between 1 and 120.";
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
        List<Customer> all = getAllCustomers();
        boolean found = false;
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_CUSTOMERS))) {
            bw.write("# customerId,name,gender,age,phone,email,address,nationality");
            bw.newLine();
            for (Customer c : all) {
                if (c.getCustomerId().equals(customerId)) {
                    Customer updated = new Customer(customerId, name.trim(), gender, age, phone.trim(), email.trim(), address.trim(), nationality.trim());
                    bw.write(updated.toFileString());
                    found = true;
                } else {
                    bw.write(c.toFileString());
                }
                bw.newLine();
            }
        } catch (Exception e) {
            return "ERR: Could not save file — " + e.getMessage();
        }
 
        if (!found) return "ERR: Customer record not found.";
        return "OK: Customer updated successfully!";
    }
    
    //Deletes a customer by ID from customers.txt and their account from users.txt
    public String deleteCustomer(String customerId) {
        List<Customer> all = getAllCustomers();
        boolean found = false;
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_CUSTOMERS))) {
            bw.write("# customerId,name,gender,age,phone,email,address,nationality");
            bw.newLine();
            for (Customer c : all) {
                if (c.getCustomerId().equals(customerId)) {
                    found = true; // skip — effectively deletes
                } else {
                    bw.write(c.toFileString());
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            return "ERR: Could not delete customer — " + e.getMessage();
        }
 
        if (!found) return "ERR: Customer not found.";
 
        // Also remove from users.txt
        removeUserByRoleRef(customerId);
 
        return "OK: Customer " + customerId + " deleted successfully!";
    }
    
    //Finds a single customer by ID. Returns null if not found.
    public Customer getCustomerById(String customerId) {
        for (Customer c : getAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) return c;
        }
        return null;
    }
    
    //generates the next customer ID
    private String generateCustomerId() {
        int max = 0;
        for (Customer c : getAllCustomers()) {
            try {
                int num = Integer.parseInt(c.getCustomerId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }
    
    //creates a new user acc for customer
    private String createUserAccount(String customerId, String name) {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;
                String[] p = line.split(",", 5);
                if (p.length >= 1) {
                    try {
                        int num = Integer.parseInt(p[0].trim().substring(1));
                        if (num > maxId) maxId = num;
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            return "ERR: Could not read users file — " + e.getMessage();
        }
 
        String userId   = String.format("U%03d", maxId + 1);
        String idNum    = customerId.substring(1);
        // default username: first 4 letters of name (lowercase) + id number
        String safeName = name.replaceAll("\\s+", "").toLowerCase();
        String username = safeName.substring(0, Math.min(4, safeName.length())) + idNum;
        String password = "pass" + idNum;
        String userLine = String.join(",", userId, username, password, "customer", customerId);
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_USERS, true))) {
            bw.write(userLine);
            bw.newLine();
        } catch (Exception e) {
            return "ERR: Could not create user account — " + e.getMessage();
        }
 
        return "OK";
    }
    
    //removes user by id
    private void removeUserByRoleRef(String roleRefId) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank()) {
                    String[] p = line.split(",", 5);
                    if (p.length == 5 && p[4].trim().equals(roleRefId)) continue;
                }
                lines.add(line);
            }
        } catch (Exception ignored) {}
 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_USERS))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (Exception ignored) {}
    }
}
