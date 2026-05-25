package apu.asc.customer;

import apu.asc.staff.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class CustomerService extends BaseFileHelper {
    private static final String FILE_CUSTOMERS    = "Data/customers.txt";
    private static final String FILE_APPOINTMENTS = "Data/appointments.txt";
    private static final String FILE_PAYMENTS     = "Data/payments.txt";
    private static final String FILE_FEEDBACKS    = "Data/feedback.txt";
    private static final String FILE_COMMENTS     = "Data/comments.txt";
    private static final String FILE_SERVICES     = "Data/services.txt";
    private static final String FILE_TECHNICIANS  = "Data/technicians.txt";
    private static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    private static final String FILE_USERS        = "Data/users.txt";
    private static final String COMMENT_CHAR      = "#";

    private final String customerId;

    public CustomerService(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }

    // 1. VIEW OWN PROFILE
    public Customer getProfile() {
        for (Customer c : loadAllCustomers()) {
            if (c.getCustomerId().equals(customerId)) return c;
        }
        throw new IllegalStateException("Customer not found: " + customerId);
    }

    // 2. EDIT OWN PROFILE
    public void editProfile(String name, String gender, int age, String phone,
                            String email, String address, String nationality,
                            String newPassword) throws IOException {
        List<String> rawLines = readRawLines(FILE_CUSTOMERS);
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) { updated.add(raw); continue; }
            Customer c = Customer.fromFileString(raw);
            if (c.getCustomerId().equals(customerId)) {
                if (name        != null && !name.isBlank())        c.setName(name);
                if (gender      != null && !gender.isBlank())      c.setGender(gender);
                if (age > 0)                                        c.setAge(age);
                if (phone       != null && !phone.isBlank())       c.setPhone(phone);
                if (email       != null && !email.isBlank())       c.setEmail(email);
                if (address     != null && !address.isBlank())     c.setAddress(address);
                if (nationality != null && !nationality.isBlank()) c.setNationality(nationality);
                updated.add(c.toFileString());
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_CUSTOMERS, "# customerId,name,gender,age,phone,email,address,nationality", updated);
        if (newPassword != null && !newPassword.isBlank()) updatePasswordInFile(newPassword);
    }

    private void updatePasswordInFile(String newPassword) throws IOException {
        List<String> rawLines = readRawLines(FILE_USERS);
        List<String> updated  = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.startsWith(COMMENT_CHAR) || raw.isBlank()) { updated.add(raw); continue; }
            String[] p = raw.split(",", 5);
            if (p.length == 5 && p[4].trim().equals(customerId)) {
                p[2] = newPassword;
                updated.add(String.join(",", p));
            } else {
                updated.add(raw);
            }
        }
        writeLines(FILE_USERS, "# userId,username,password,role,roleRefId", updated);
    }

    public String validateProfile(String name, String phone, String email, String ageStr) {
        if (name  == null || name.isBlank())    return "Name cannot be empty.";
        if (ageStr == null || ageStr.isBlank()) return "Age cannot be empty.";
        try {
            if (Integer.parseInt(ageStr) <= 0)  return "Age must be a positive number.";
        } catch (NumberFormatException e)       { return "Age must be a valid number."; }
        if (phone == null || phone.isBlank())   return "Phone cannot be empty.";
        if (!phone.matches("\\d{10,12}"))       return "Phone must be 10-12 digits.";
        if (email == null || email.isBlank())   return "Email cannot be empty.";
        if (!email.contains("@"))               return "Invalid email format.";
        return null;
    }

    // 3. SERVICE AND PAYMENT HISTORY
    public List<Appointment> getMyAppointments() {
        return loadAllAppointments().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public Payment getPaymentForAppointment(String appointmentId) {
        return loadAllPayments().stream()
                .filter(p -> p.getAppointmentId().equalsIgnoreCase(appointmentId)
                          && p.getCustomerId().equals(customerId))
                .findFirst().orElse(null);
    }

    public Service getServiceInfo(String serviceId) {
        return loadAllServices().stream()
                .filter(s -> s.getServiceId().equalsIgnoreCase(serviceId))
                .findFirst().orElse(null);
    }

    public List<Payment> getMyPayments() {
        return loadAllPayments().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    // Returns rows for service history table (6 cols): Appt ID, Date, Time, Service, Price, Status
    public List<String[]> getServiceHistoryRows() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : getMyAppointments()) {
            Service svc = getServiceInfo(a.getServiceId());
            rows.add(new String[]{
                a.getAppointmentId(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                svcNames.getOrDefault(a.getServiceId(), a.getServiceId()),
                svc != null ? String.format("RM %.2f", svc.getPrice()) : "-",
                a.getStatus()
            });
        }
        return rows;
    }

    // Returns rows for payment history table (5 cols): Payment ID, Appt ID, Payment Date, Amount, Receipt
    public List<String[]> getPaymentHistoryRows() {
        List<String[]> rows = new ArrayList<>();
        for (Payment p : getMyPayments()) {
            rows.add(new String[]{
                p.getPaymentId(),
                p.getAppointmentId(),
                p.getPaymentDate(),
                String.format("RM %.2f", p.getAmount()),
                p.getReceiptNo()
            });
        }
        return rows;
    }

    // Returns ready-to-display rows for history table
    public List<String[]> getHistoryRows() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : getMyAppointments()) {
            Payment pay = getPaymentForAppointment(a.getAppointmentId());
            Service svc = getServiceInfo(a.getServiceId());
            rows.add(new String[]{
                a.getAppointmentId(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                svcNames.getOrDefault(a.getServiceId(), a.getServiceId()),
                svc != null ? String.format("RM %.2f", svc.getPrice()) : "-",
                a.getStatus(),
                pay != null ? pay.getPaymentDate()                       : "-",
                pay != null ? String.format("RM %.2f", pay.getAmount()) : "Unpaid",
                pay != null ? pay.getReceiptNo()                         : "-"
            });
        }
        return rows;
    }

    // 4. VIEW FEEDBACKS FOR OWN APPOINTMENTS
    public List<Feedback> getFeedbacksForAppointment(String appointmentId) {
        boolean owned = getMyAppointments().stream()
                .anyMatch(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId));
        if (!owned) return Collections.emptyList();
        return loadAllFeedbacks().stream()
                .filter(f -> f.getAppointmentId().equalsIgnoreCase(appointmentId))
                .collect(Collectors.toList());
    }

    // Returns label pairs [appointmentId, displayLabel] for all customer appointments
    public List<String[]> getAppointmentLabels() {
        Map<String, String> svcNames = buildServiceNameMap();
        List<String[]> result = new ArrayList<>();
        for (Appointment a : getMyAppointments()) {
            String label = a.getAppointmentId()
                    + "  –  " + svcNames.getOrDefault(a.getServiceId(), a.getServiceId())
                    + "  (" + a.getAppointmentDate() + ")  [" + a.getStatus() + "]";
            result.add(new String[]{ a.getAppointmentId(), label });
        }
        return result;
    }

    // 5. PROVIDE COMMENTS FOR COUNTER STAFF AND TECHNICIANS
    public List<Comment> getCommentsForAppointment(String appointmentId) {
        boolean owned = getMyAppointments().stream()
                .anyMatch(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId));
        if (!owned) return Collections.emptyList();
        return loadAllComments().stream()
                .filter(c -> c.getAppointmentId().equalsIgnoreCase(appointmentId)
                          && c.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public String addComment(String appointmentId, String targetUserId, String commentText) throws IOException {
        Appointment appt = getMyAppointments().stream()
                .filter(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId))
                .findFirst().orElse(null);
        if (appt == null) return "ERROR: Appointment not found or does not belong to you.";

        boolean validTarget = appt.getTechnicianId().equalsIgnoreCase(targetUserId)
                           || appt.getCounterStaffId().equalsIgnoreCase(targetUserId);
        if (!validTarget) return "ERROR: Target user is not involved in this appointment.";

        if (commentText == null || commentText.isBlank()) return "ERROR: Comment text cannot be empty.";

        if (hasCommented(appointmentId, targetUserId))
            return "ERROR: You have already commented on this person for appointment " + appointmentId + ".";

        String newId = generateNextCommentId();
        appendLine(FILE_COMMENTS, new Comment(newId, appointmentId, customerId, targetUserId, commentText).toFileString());
        return "SUCCESS: Comment submitted (ID: " + newId + ").";
    }

    public boolean hasCommented(String appointmentId, String targetUserId) {
        return loadAllComments().stream()
                .anyMatch(c -> c.getAppointmentId().equalsIgnoreCase(appointmentId)
                            && c.getCustomerId().equals(customerId)
                            && c.getTargetUserId().equalsIgnoreCase(targetUserId));
    }

    // Returns [targetUserId, displayLabel] pairs for the technician and counter staff of an appointment
    public List<String[]> getCommentTargets(String appointmentId) {
        Appointment appt = getMyAppointments().stream()
                .filter(a -> a.getAppointmentId().equalsIgnoreCase(appointmentId))
                .findFirst().orElse(null);
        if (appt == null) return Collections.emptyList();

        List<String[]> targets = new ArrayList<>();
        Technician   tech = getTechnicianById(appt.getTechnicianId());
        CounterStaff cs   = getCounterStaffById(appt.getCounterStaffId());
        String techName = tech != null ? tech.getName() : appt.getTechnicianId();
        String csName   = cs   != null ? cs.getName()   : appt.getCounterStaffId();
        targets.add(new String[]{ appt.getTechnicianId(),   "Technician – "    + techName });
        targets.add(new String[]{ appt.getCounterStaffId(), "Counter Staff – " + csName   });
        return targets;
    }

    // 6. LOOKUP HELPERS
    public Technician getTechnicianById(String technicianId) {
        return loadAllTechnicians().stream()
                .filter(t -> t.getTechnicianId().equalsIgnoreCase(technicianId))
                .findFirst().orElse(null);
    }

    public CounterStaff getCounterStaffById(String counterStaffId) {
        return loadAllCounterStaff().stream()
                .filter(cs -> cs.getCounterStaffId().equalsIgnoreCase(counterStaffId))
                .findFirst().orElse(null);
    }

    // 7. ID GENERATION
    private String generateNextCommentId() {
        int max = 0;
        for (Comment c : loadAllComments()) {
            try { max = Math.max(max, Integer.parseInt(c.getCommentId().replaceAll("[^0-9]", ""))); }
            catch (NumberFormatException ignored) {}
        }
        return String.format("CM%03d", max + 1);
    }

    // 8. LOADERS
    public List<Customer> loadAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readLines(FILE_CUSTOMERS)) {
            try { list.add(Customer.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Appointment> loadAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        for (String line : readLines(FILE_APPOINTMENTS)) {
            try { list.add(Appointment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Payment> loadAllPayments() {
        List<Payment> list = new ArrayList<>();
        for (String line : readLines(FILE_PAYMENTS)) {
            try { list.add(Payment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Feedback> loadAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        for (String line : readLines(FILE_FEEDBACKS)) {
            try { list.add(Feedback.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Comment> loadAllComments() {
        List<Comment> list = new ArrayList<>();
        for (String line : readLines(FILE_COMMENTS)) {
            try { list.add(Comment.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Service> loadAllServices() {
        List<Service> list = new ArrayList<>();
        for (String line : readLines(FILE_SERVICES)) {
            try { list.add(Service.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<Technician> loadAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        for (String line : readLines(FILE_TECHNICIANS)) {
            try { list.add(Technician.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public List<CounterStaff> loadAllCounterStaff() {
        List<CounterStaff> list = new ArrayList<>();
        for (String line : readLines(FILE_COUNTERSTAFF)) {
            try { list.add(CounterStaff.fromFileString(line)); } catch (Exception ignored) {}
        }
        return list;
    }

    public Map<String, String> buildServiceNameMap() {
        Map<String, String> map = new HashMap<>();
        for (Service s : loadAllServices()) map.put(s.getServiceId(), s.getServiceName());
        return map;
    }
}
