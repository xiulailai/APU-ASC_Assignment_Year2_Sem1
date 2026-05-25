package apu.asc.Test;

import apu.asc.staff.*;
import apu.asc.staff.Technician; //for avoid Issues
import apu.asc.main.LoginFrame;
import apu.asc.main.LoginService;
import apu.asc.technician.*;
import apu.asc.counterstaff.*;
import apu.asc.manager.*;


public class TestAllClasses{
        // Test helper to expose protected BaseFileHelper methods without changing app code.
        static class BaseFileHelperTestProbe extends BaseFileHelper {
            int customerCount() { return loadAllCustomers().size(); }
            int technicianCount() { return loadAllTechnicians().size(); }
            int counterStaffCount() { return loadAllCounterStaff().size(); }
            int managerCount() { return loadAllManagers().size(); }
            int serviceCount() { return loadAllServices().size(); }
            int appointmentCount() { return loadAllAppointments().size(); }
            int paymentCount() { return loadAllPayments().size(); }
            int feedbackCount() { return loadAllFeedbacks().size(); }
            int commentCount() { return loadAllComments().size(); }

            boolean hasCustomer(String id) { return findCustomerById(id) != null; }
            boolean hasTechnician(String id) { return findTechnicianById(id) != null; }
            boolean hasCounterStaff(String id) { return findCounterStaffById(id) != null; }
            boolean hasAppointment(String id) { return findAppointmentById(id) != null; }

            int serviceNameMapCount() { return buildServiceNameMap().size(); }

            String validatePerson(String name, String age, String phone, String email, String address, String nationality) {
                return validatePersonFields(name, age, phone, email, address, nationality);
            }

            int missingFileLineCount() {
                return readLines("Data/not_exists_for_test.txt").size();
            }
        }

    public static void main(String[] args) throws Exception {

        // Staff Model
        // apu.asc.staff  -  Model Classes
        System.out.println(" apu.asc.staff - Model Classes");

        // User (abstract superclass) — tested through each subclass
        // Technician
        Technician t = new Technician("T001", "Ahmad Faiz", "Male", 31, "0145678901", "ahmadfaiz@email.com", "No. 9 Jalan Dahlia Ampang", "Malaysian");
        System.out.println("[Technician] extends User: " + (t instanceof User));
        System.out.println("[Technician] getName(): " + t.getName());
        System.out.println("[Technician] getTechnicianId(): " + t.getTechnicianId());
        System.out.println("[Technician] getDisplayInfo(): " + t.getDisplayInfo());
        System.out.println("[Technician] toFileString(): " + t.toFileString());

        // Customer
        Customer c = new Customer("C001", "Ali Hassan", "Male", 42, "0121112222", "alihassan@email.com", "No. 6 Jalan Pelangi Kepong", "Malaysian");
        System.out.println("[Customer] extends User: " + (c instanceof User));
        System.out.println("[Customer] getName(): " + c.getName());
        System.out.println("[Customer] getCustomerId(): " + c.getCustomerId());
        System.out.println("[Customer] getDisplayInfo(): " + c.getDisplayInfo());
        System.out.println("[Customer] toFileString(): " + c.toFileString());

        // CounterStaff
        CounterStaff cs = new CounterStaff("CS001", "Amy Lee", "Female", 29, "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian");
        System.out.println("[CounterStaff] extends User: " + (cs instanceof User));
        System.out.println("[CounterStaff] getName(): " + cs.getName());
        System.out.println("[CounterStaff] getCounterStaffId(): " + cs.getCounterStaffId());
        System.out.println("[CounterStaff] getDisplayInfo(): " + cs.getDisplayInfo());
        System.out.println("[CounterStaff] toFileString(): " + cs.toFileString());

        // Manager
        Manager mg = new Manager("M001", "John Tan", "Male", 38, "0123456789", "johntan@email.com", "No. 12 Jalan Mawar Kuala Lumpur", "Malaysian");
        System.out.println("[Manager] extends User: " + (mg instanceof User));
        System.out.println("[Manager] getName(): " + mg.getName());
        System.out.println("[Manager] getManagerId(): " + mg.getManagerId());
        System.out.println("[Manager] getDisplayInfo(): " + mg.getDisplayInfo());
        System.out.println("[Manager] toFileString(): " + mg.toFileString());

        // Appointment
        Appointment ap = new Appointment("A001", "C001", "T001", "CS001", "S001", "2026-06-20", "10:00", "Completed");
        System.out.println("[Appointment] getAppointmentId(): " + ap.getAppointmentId());
        System.out.println("[Appointment] getStatus(): " + ap.getStatus());
        System.out.println("[Appointment] toFileString(): " + ap.toFileString());

        // Service
        Service sv = new Service("S001", "Normal Service", 1, 50.00);
        System.out.println("[Service] getServiceName(): " + sv.getServiceName());
        System.out.println("[Service] getDurationHours(): " + sv.getDurationHours());
        System.out.println("[Service] getPrice(): RM " + sv.getPrice());
        System.out.println("[Service] toFileString(): " + sv.toFileString());

        // Payment
        Payment py = new Payment("P001", "A001", "C001", 50.00, "2026-05-20", "R001");
        System.out.println("[Payment] getPaymentId(): " + py.getPaymentId());
        System.out.println("[Payment] getAmount(): RM " + py.getAmount());
        System.out.println("[Payment] getReceiptNo(): " + py.getReceiptNo());
        System.out.println("[Payment] toFileString(): " + py.toFileString());

        // Feedback
        Feedback fb = new Feedback("F001", "A001", "T001", "Oil changed successfully.");
        System.out.println("[Feedback] getFeedbackId(): " + fb.getFeedbackId());
        System.out.println("[Feedback] getFeedbackText(): " + fb.getFeedbackText());
        System.out.println("[Feedback] toFileString(): " + fb.toFileString());

        // Comment
        Comment cm = new Comment("CM001", "A001", "C001", "T001", "Great service!");
        System.out.println("[Comment] getCommentId(): " + cm.getCommentId());
        System.out.println("[Comment] getCommentText(): " + cm.getCommentText());
        System.out.println("[Comment] toFileString(): " + cm.toFileString());

        System.out.println("[Technician] fromFileString(): " + Technician.fromFileString(t.toFileString()).getName());
        System.out.println("[Technician] implements FileStorable: " + (t instanceof FileStorable));
        System.out.println("[Technician] implements Displayable: " + (t instanceof Displayable));

        System.out.println("[Customer] fromFileString(): " + Customer.fromFileString(c.toFileString()).getName());
        System.out.println("[CounterStaff] fromFileString(): " + CounterStaff.fromFileString(cs.toFileString()).getName());
        System.out.println("[Manager] fromFileString(): " + Manager.fromFileString(mg.toFileString()).getName());
        System.out.println("[Appointment] fromFileString(): " + Appointment.fromFileString(ap.toFileString()).getAppointmentId());
        System.out.println("[Service] fromFileString(): " + Service.fromFileString(sv.toFileString()).getServiceName());
        System.out.println("[Payment] fromFileString(): " + Payment.fromFileString(py.toFileString()).getPaymentId());
        System.out.println("[Feedback] fromFileString(): " + Feedback.fromFileString(fb.toFileString()).getFeedbackId());
        System.out.println("[Comment] fromFileString(): " + Comment.fromFileString(cm.toFileString()).getCommentId());

        BaseFileHelperTestProbe baseProbe = new BaseFileHelperTestProbe();
        System.out.println("[BaseFileHelper] loadAllCustomers(): " + baseProbe.customerCount());
        System.out.println("[BaseFileHelper] loadAllTechnicians(): " + baseProbe.technicianCount());
        System.out.println("[BaseFileHelper] loadAllCounterStaff(): " + baseProbe.counterStaffCount());
        System.out.println("[BaseFileHelper] loadAllManagers(): " + baseProbe.managerCount());
        System.out.println("[BaseFileHelper] loadAllServices(): " + baseProbe.serviceCount());
        System.out.println("[BaseFileHelper] loadAllAppointments(): " + baseProbe.appointmentCount());
        System.out.println("[BaseFileHelper] loadAllPayments(): " + baseProbe.paymentCount());
        System.out.println("[BaseFileHelper] loadAllFeedbacks(): " + baseProbe.feedbackCount());
        System.out.println("[BaseFileHelper] loadAllComments(): " + baseProbe.commentCount());

        System.out.println("[BaseFileHelper] findCustomerById C001: " + baseProbe.hasCustomer("C001"));
        System.out.println("[BaseFileHelper] findTechnicianById T001: " + baseProbe.hasTechnician("T001"));
        System.out.println("[BaseFileHelper] findCounterStaffById CS001: " + baseProbe.hasCounterStaff("CS001"));
        System.out.println("[BaseFileHelper] findAppointmentById A001: " + baseProbe.hasAppointment("A001"));

        System.out.println("[BaseFileHelper] lookupCustomerName C001: " + baseProbe.lookupCustomerName("C001"));
        System.out.println("[BaseFileHelper] lookupTechnicianName T001: " + baseProbe.lookupTechnicianName("T001"));
        System.out.println("[BaseFileHelper] lookupCounterStaffName CS001: " + baseProbe.lookupCounterStaffName("CS001"));
        System.out.println("[BaseFileHelper] lookupServiceName S001: " + baseProbe.lookupServiceName("S001"));
        System.out.println("[BaseFileHelper] lookupServicePrice S001: RM " + baseProbe.lookupServicePrice("S001"));
        System.out.println("[BaseFileHelper] buildServiceNameMap(): " + baseProbe.serviceNameMapCount());
        System.out.println("[BaseFileHelper] lookupUsername T001: " + baseProbe.lookupUsername("T001"));

        System.out.println("[BaseFileHelper] validatePerson valid: " + baseProbe.validatePerson("Test", "25", "0111234567", "test@email.com", "Addr", "MY"));

        System.out.println("[BaseFileHelper] validatePerson bad age: " + baseProbe.validatePerson("Test", "abc", "0111234567", "test@email.com", "Addr", "MY"));

        System.out.println("[BaseFileHelper] missing file readLines(): " + baseProbe.missingFileLineCount());

        // Login Function
        // apu.asc.main  -  Login
        System.out.println(" apu.asc.main - Login");

        LoginService loginSvc = new LoginService();

        // Valid logins — one per role
        String[] techLogin = loginSvc.authenticate("tech1",    "techpass");
        String[] csLogin = loginSvc.authenticate("counter1", "abc123");
        String[] custLogin = loginSvc.authenticate("ali123",   "pass123");
        String[] mgrLogin = loginSvc.authenticate("manager1", "pass123");

        System.out.println("[LoginService] tech1 > role: " + techLogin[0]  + " | id: " + techLogin[1]);
        System.out.println("[LoginService] counter1 > role: " + csLogin[0]    + " | id: " + csLogin[1]);
        System.out.println("[LoginService] ali123 > role: " + custLogin[0]  + " | id: " + custLogin[1]);
        System.out.println("[LoginService] manager1 > role: " + mgrLogin[0]   + " | id: " + mgrLogin[1]);

        // Invalid login - should return null
        String[] badLogin = loginSvc.authenticate("wronguser", "wrongpass");
        System.out.println("[LoginService] bad credentials > null: " + (badLogin == null));

        // LoginFrame GUI
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            System.out.println("[LoginFrame] opened: " + login.isVisible());
            System.out.println("[LoginFrame] title : " + login.getTitle());
            login.dispose();
            System.out.println("[LoginFrame] closed: " + !login.isVisible());
        });


        // Technician Role
        // apu.asc.technician  —  Service Logic
        System.out.println(" apu.asc.technician — TechnicianService");

        TechnicianService techSvc = new TechnicianService("T001");

        // Profile
        System.out.println("[getProfile]: " + techSvc.getProfile().getName());
        System.out.println("[validateProfile] valid: " + techSvc.validateProfile("Ahmad Faiz", "Male", "0145678901", "ahmadfaiz@email.com", "31"));
        System.out.println("[validateProfile] blank: " + techSvc.validateProfile("", "Male", "0145678901", "ahmadfaiz@email.com", "31"));
        System.out.println("[validateProfile] bad phone: " + techSvc.validateProfile("Ahmad", "Male", "123", "a@b.com", "31"));
        System.out.println("[validateProfile] bad email: " + techSvc.validateProfile("Ahmad", "Male", "0145678901", "invalidemail", "31"));
        System.out.println("[validateProfile] bad gender: " + techSvc.validateProfile("Ahmad", "Other", "0145678901", "a@b.com", "31"));

        // Appointments
        System.out.println("[getMyAppointments]: " + techSvc.getMyAppointments().size() + " appointment(s) for T001");
        System.out.println("[filterByStatus] Assigned: " + techSvc.filterByStatus("Assigned").size());
        System.out.println("[filterByStatus] Completed: " + techSvc.filterByStatus("Completed").size());
        System.out.println("[filterByStatus] Pending: " + techSvc.filterByStatus("Pending").size());

        // Search
        System.out.println("[searchTasks] keyword 'A001': " + techSvc.searchTasks("A001", null).size() + " result(s)");
        System.out.println("[searchTasks] keyword 'Normal Service': " + techSvc.searchTasks("Normal Service", null).size() + " result(s)");
        System.out.println("[searchTasks] keyword + status filter: " + techSvc.searchTasks(null, "Assigned").size() + " assigned");

        // Task detail
        System.out.println("[getTaskDetail] A001 (T001's): " + techSvc.getTaskDetail("A001").getAppointmentId());
        System.out.println("[getTaskDetail] A002 (not T001): " + techSvc.getTaskDetail("A002")); // expects null
        System.out.println("[getTaskDetailMap] A001: " + techSvc.getTaskDetailMap("A001").get("service"));

        // Service and customer lookup
        System.out.println("[getServiceInfo] S001: " + techSvc.getServiceInfo("S001").getServiceName());

        // Schedule
        System.out.println("[getUpcomingSchedule]: " + techSvc.getUpcomingSchedule().size() + " upcoming task(s)");
        System.out.println("[getTotalHoursForDate] today: " + techSvc.getTotalHoursForDate(techSvc.getTodayString()) + " hr(s)");
        System.out.println("[getDayName] 2026-06-20: " + techSvc.getDayName("2026-06-20"));
        System.out.println("[getScheduleRows]: " + techSvc.getScheduleRows().size() + " row(s)");
        System.out.println("[getUpcomingStatusCounts]: total=" + techSvc.getUpcomingStatusCounts()[0] + " assigned=" + techSvc.getUpcomingStatusCounts()[1] + " pending=" + techSvc.getUpcomingStatusCounts()[2]);
        System.out.println("[getAllStatusCounts]: assigned=" + techSvc.getAllStatusCounts()[0] + " pending=" + techSvc.getAllStatusCounts()[1] + " completed=" + techSvc.getAllStatusCounts()[2]);

        // Comments
        System.out.println("[getMyComments]: " + techSvc.getMyComments().size() + " comment(s)");
        System.out.println("[getCommentsForAppointment] A001: " + techSvc.getCommentsForAppointment("A001").size() + " comment(s)");
        System.out.println("[getComments] all: " + techSvc.getComments(null).size() + " comment(s)");
        System.out.println("[getComments] filtered by A001: " + techSvc.getComments("A001").size() + " comment(s)");

        // Feedback — eligible list (read-only, no file write)
        System.out.println("[getEligibleAppointmentsForFeedback]: " + techSvc.getEligibleAppointmentsForFeedback().size() + " eligible");
        System.out.println("[getEligibleAppointmentLabels]: " + techSvc.getEligibleAppointmentLabels().size() + " label(s)");
        System.out.println("[getMyFeedbacks]: " + techSvc.getMyFeedbacks().size() + " submitted");

        // markTaskCompleted — guard checks (no actual file change)
        System.out.println("[markTaskCompleted] A001 already Completed: " + techSvc.markTaskCompleted("A001"));
        System.out.println("[markTaskCompleted] A006 not T001's task: " + techSvc.markTaskCompleted("A006")); // null → error

        // provideFeedback — guard checks (no actual file change)
        System.out.println("[provideFeedback] A001 already submitted: " + techSvc.provideFeedback("A001", "Test duplicate"));
        System.out.println("[provideFeedback] A009 not Completed yet: " + techSvc.provideFeedback("A009", "Not done yet"));
        System.out.println("[provideFeedback] empty text: " + techSvc.provideFeedback("A001", ""));

        System.out.println("[getTechnicianId]: " + techSvc.getTechnicianId());

        System.out.println("[editProfileFromInput] blank name: " + techSvc.editProfileFromInput("", "Male", "31", "0145678901", "ahmadfaiz@email.com", "No. 9 Jalan Dahlia Ampang", "Malaysian", ""));

        System.out.println("[editProfileFromInput] short password: " + techSvc.editProfileFromInput("Ahmad Faiz", "Male", "31", "0145678901", "ahmadfaiz@email.com", "No. 9 Jalan Dahlia Ampang", "Malaysian", "123"));

        System.out.println("[searchTasks] blank keyword: " + techSvc.searchTasks("").size() + " result(s)");

        System.out.println("[getTaskRows] all: " + techSvc.getTaskRows(null, null).size() + " row(s)");
        System.out.println("[getTaskRows] assigned: " + techSvc.getTaskRows(null, "Assigned").size() + " row(s)");

        System.out.println("[getTaskDetailMap] invalid: " + techSvc.getTaskDetailMap("A999"));
        
        
        // Technician Role GUI
        // apu.asc.technician  —  GUI Frames
        System.out.println(" apu.asc.technician — GUI Frames");

        // tchnDashboard
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            dashboard.setVisible(true);
            System.out.println("[tchnDashboard] opened: " + dashboard.isVisible());
            System.out.println("[tchnDashboard] title: " + dashboard.getTitle());
            dashboard.dispose();
            System.out.println("[tchnDashboard] closed: " + !dashboard.isVisible());
        });

        // tchnProfile
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            tchnProfile profile = new tchnProfile("T001", dashboard);
            profile.setVisible(true);
            System.out.println("[tchnProfile] opened: " + profile.isVisible());
            System.out.println("[tchnProfile] title: " + profile.getTitle());
            profile.dispose();
            dashboard.dispose();
            System.out.println("[tchnProfile] closed: " + !profile.isVisible());
        });

        // tchnTasks
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            tchnTasks tasks = new tchnTasks("T001", dashboard);
            tasks.setVisible(true);
            System.out.println("[tchnTasks] opened: " + tasks.isVisible());
            System.out.println("[tchnTasks] title: " + tasks.getTitle());
            tasks.dispose();
            dashboard.dispose();
            System.out.println("[tchnTasks] closed: " + !tasks.isVisible());
        });

        // tchnSchedule
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnSchedule schedule = new tchnSchedule("T001");
            schedule.setVisible(true);
            System.out.println("[tchnSchedule] opened: " + schedule.isVisible());
            System.out.println("[tchnSchedule] title: " + schedule.getTitle());
            schedule.dispose();
            System.out.println("[tchnSchedule] closed: " + !schedule.isVisible());
        });

        // tchnComments
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnComments comments = new tchnComments("T001");
            comments.setVisible(true);
            System.out.println("[tchnComments] opened: " + comments.isVisible());
            System.out.println("[tchnComments] title: " + comments.getTitle());
            comments.dispose();
            System.out.println("[tchnComments] closed: " + !comments.isVisible());
        });

        // tchnFeedback
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnFeedback feedback = new tchnFeedback("T001");
            feedback.setVisible(true);
            System.out.println("[tchnFeedback] opened: " + feedback.isVisible());
            System.out.println("[tchnFeedback] title: " + feedback.getTitle());
            feedback.dispose();
            System.out.println("[tchnFeedback] closed: " + !feedback.isVisible());
        });
        
        // Counter Staff 
        // Counter Staff Role
        System.out.println(" apu.asc.counterstaff — csService");
        csService csSvc = new csService();
        
        // Profile
        System.out.println("[getProfile] CS001: " + csSvc.getProfile("CS001").getName());
        System.out.println("[getProfile] CS999 (not found) → null: " + (csSvc.getProfile("CS999") == null));
        System.out.println("[getAllCounterStaff]: " + csSvc.getAllCounterStaff().size() + " staff member(s)");
        
        // updateProfile 
        System.out.println("[updateProfile] valid: " + csSvc.updateProfile("CS001", "Amy Lee Updated", "Female", "30", "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        System.out.println("[updateProfile] blank name: " + csSvc.updateProfile("CS001", "", "Female", "30", "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        System.out.println("[updateProfile] age out of range (150): " + csSvc.updateProfile("CS001", "Amy Lee", "Female", "150", "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        System.out.println("[updateProfile] non-numeric age: " + csSvc.updateProfile("CS001", "Amy Lee", "Female", "abc", "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        System.out.println("[updateProfile] short phone: " + csSvc.updateProfile("CS001", "Amy Lee", "Female", "29", "01234", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        System.out.println("[updateProfile] invalid email: " + csSvc.updateProfile("CS001", "Amy Lee", "Female", "29", "0134567890", "amyleeNOATsign", "No. 33 Jalan Kenanga Shah Alam", "Malaysian"));
        // restore CS001 after modifying
        csSvc.updateProfile("CS001", "Amy Lee", "Female", "29", "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian");
        System.out.println("[updateProfile] restored CS001: " + csSvc.getProfile("CS001").getName());
        
        // manageCustomer
        System.out.println("[getAllCustomers]: " + csSvc.getAllCustomers().size() + " customer(s)");
        System.out.println("[getCustomerById] C001: " + csSvc.getCustomerById("C001").getName());
        System.out.println("[getCustomerById] C999 (not found) → null: " + (csSvc.getCustomerById("C999") == null));
        
        // searchCustomers
        System.out.println("[searchCustomers] 'Ali' (by name): " + csSvc.searchCustomers("Ali").size() + " result(s)");
        System.out.println("[searchCustomers] '0164445555' (by phone): " + csSvc.searchCustomers("0164445555").size() + " result(s)");
        System.out.println("[searchCustomers] 'C003' (by ID): " + csSvc.searchCustomers("C003").size() + " result(s)");
        System.out.println("[searchCustomers] '' (blank → all): " + csSvc.searchCustomers("").size() + " result(s)");
        System.out.println("[searchCustomers] 'NOMATCH': " + csSvc.searchCustomers("NOMATCH").size() + " result(s)");
        
        // addCustomer
        System.out.println("[addCustomer] valid: " + csSvc.addCustomer("Test User", "Male", "25", "0111234567", "testuser@email.com", "No. 1 Jalan Test", "Malaysian"));
        System.out.println("[addCustomer] blank name: " + csSvc.addCustomer("", "Male", "25", "0111234567", "testuser@email.com", "No. 1 Jalan Test", "Malaysian"));
        System.out.println("[addCustomer] invalid age (999): " + csSvc.addCustomer("Test User", "Male", "999", "0111234567", "testuser@email.com", "No. 1 Jalan Test", "Malaysian"));
        System.out.println("[addCustomer] invalid phone: " + csSvc.addCustomer("Test User", "Male", "25", "123", "testuser@email.com", "No. 1 Jalan Test", "Malaysian"));
        System.out.println("[addCustomer] invalid email: " + csSvc.addCustomer("Test User", "Male", "25", "0111234567", "notanemail", "No. 1 Jalan Test", "Malaysian"));
        
        // updateCustomer
        System.out.println("[updateCustomer] valid (C002): " + csSvc.updateCustomer("C002", "Siti Rahimah Updated", "Female", "36", "0193334444", "sitirahimah@email.com", "No. 11 Jalan Setia Wangsa Maju", "Malaysian"));
        System.out.println("[updateCustomer] not found (C999): " + csSvc.updateCustomer("C999", "Ghost", "Male", "30", "0191234567", "ghost@email.com", "Nowhere", "Malaysian"));
        System.out.println("[updateCustomer] invalid phone: " + csSvc.updateCustomer("C002", "Siti Rahimah", "Female", "35", "999", "sitirahimah@email.com", "No. 11 Jalan Setia Wangsa Maju", "Malaysian"));
        // restore C002 after modifying
        csSvc.updateCustomer("C002", "Siti Rahimah", "Female", "35", "0193334444", "sitirahimah@email.com", "No. 11 Jalan Setia Wangsa Maju", "Malaysian");
        
        // deleteCustomer — delete the test customer added above
        String lastCustId = csSvc.getAllCustomers().get(csSvc.getAllCustomers().size() - 1).getCustomerId();
        System.out.println("[deleteCustomer] test customer (" + lastCustId + "): " + csSvc.deleteCustomer(lastCustId));
        System.out.println("[deleteCustomer] not found (C999): " + csSvc.deleteCustomer("C999"));
        
        // ManageAppointments 
        System.out.println("[getAllAppointments]: " + csSvc.getAllAppointments().size() + " appointment(s)");
        System.out.println("[getAppointmentById] A001: " + csSvc.getAppointmentById("A001").getAppointmentId());
        System.out.println("[getAppointmentById] A999 (not found) → null: " + (csSvc.getAppointmentById("A999") == null));
        
        // filterAppointmentByStatus
        System.out.println("[filterAppointmentsByStatus] 'Pending': " + csSvc.filterAppointmentsByStatus("Pending").size() + " appointment(s)");
        System.out.println("[filterAppointmentsByStatus] 'Assigned': " + csSvc.filterAppointmentsByStatus("Assigned").size() + " appointment(s)");
        System.out.println("[filterAppointmentsByStatus] 'Completed': " + csSvc.filterAppointmentsByStatus("Completed").size() + " appointment(s)");
        System.out.println("[filterAppointmentsByStatus] 'All': " + csSvc.filterAppointmentsByStatus("All").size() + " appointment(s)");
        
        // createAppointment
        System.out.println("[createAppointment] valid: " + csSvc.createAppointment("CS001", "C001", "S001", "2026-12-01", "10:00"));
        System.out.println("[createAppointment] past date: " + csSvc.createAppointment("CS001", "C001", "S001", "2020-01-01", "10:00"));
        System.out.println("[createAppointment] wrong date format: " + csSvc.createAppointment("CS001", "C001", "S001", "01-12-2026", "10:00"));
        System.out.println("[createAppointment] blank customerId: " + csSvc.createAppointment("CS001", "", "S001", "2026-12-01", "10:00"));
 
        // getAvailableTechnicians
        Appointment a005 = csSvc.getAppointmentById("A005");
        System.out.println("[getAvailableTechnicians] A005 (" + a005.getAppointmentDate() + " " + a005.getAppointmentTime() + "): " + csSvc.getAvailableTechnicians(a005).size() + " available");
 
        // assignAppointment
        System.out.println("[assignAppointment] valid (A007 → T003): " + csSvc.assignAppointment("A007", "T003"));
        System.out.println("[assignAppointment] not found (A999): " + csSvc.assignAppointment("A999", "T001"));
 
        // deleteAppointment — delete the appointment created above
        String lastApptId = csSvc.getAllAppointments().get(csSvc.getAllAppointments().size() - 1).getAppointmentId();
        System.out.println("[deleteAppointment] created appt (" + lastApptId + "): " + csSvc.deleteAppointment(lastApptId));
        System.out.println("[deleteAppointment] not found (A999): " + csSvc.deleteAppointment("A999"));
        
        // Lookup Helpers
        System.out.println("[lookupCustomerName] C001: " + csSvc.lookupCustomerName("C001"));
        System.out.println("[lookupCustomerName] C999 (not found → returns id): " + csSvc.lookupCustomerName("C999"));
        System.out.println("[lookupTechnicianName] T001: " + csSvc.lookupTechnicianName("T001"));
        System.out.println("[lookupTechnicianName] TBD → 'TBD': " + csSvc.lookupTechnicianName("TBD"));
        System.out.println("[lookupTechnicianName] null → 'TBD': " + csSvc.lookupTechnicianName(null));
        System.out.println("[lookupServiceName] S001: " + csSvc.lookupServiceName("S001"));
        System.out.println("[lookupServiceName] S999 (not found → returns id): " + csSvc.lookupServiceName("S999"));
        System.out.println("[lookupServicePrice] S001: RM " + csSvc.lookupServicePrice("S001"));
        System.out.println("[lookupServicePrice] S002: RM " + csSvc.lookupServicePrice("S002"));
        System.out.println("[lookupServicePrice] S999 (not found → 0.0): RM " + csSvc.lookupServicePrice("S999"));
        
        // Payments 
        System.out.println("[getAllPayments]: " + csSvc.getAllPayments().size() + " payment(s)");
        System.out.println("[getUnpaidAppointments]: " + csSvc.getUnpaidAppointments().size() + " unpaid");
 
        // collectPayment — create a test appointment, assign it, then collect payment
        csSvc.createAppointment("CS001", "C001", "S001", "2026-12-01", "10:00");
        String payTestApptId = csSvc.getAllAppointments().get(csSvc.getAllAppointments().size() - 1).getAppointmentId();
        csSvc.assignAppointment(payTestApptId, "T001");
        Appointment payTestAppt = csSvc.getAppointmentById(payTestApptId);
        System.out.println("[collectPayment] " + payTestApptId + ": " + csSvc.collectPayment(payTestApptId, payTestAppt.getCustomerId(), csSvc.lookupServicePrice(payTestAppt.getServiceId())));

        // Counter Staff Role GUI
        System.out.println(" apu.asc.counterstaff — GUI Frames");
 
        // csDashboard
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            dashboard.setVisible(true);
            System.out.println("[csDashboard] opened: " + dashboard.isVisible());
            System.out.println("[csDashboard] title: " + dashboard.getTitle());
            dashboard.dispose();
            System.out.println("[csDashboard] closed: " + !dashboard.isVisible());
        });
 
        // csEditProfile
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csEditProfile editProfile = new csEditProfile("CS001", dashboard);
            editProfile.setVisible(true);
            System.out.println("[csEditProfile] opened: " + editProfile.isVisible());
            System.out.println("[csEditProfile] title: " + editProfile.getTitle());
            editProfile.dispose();
            dashboard.dispose();
            System.out.println("[csEditProfile] closed: " + !editProfile.isVisible());
        });
 
        // csManageCustomer
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csManageCustomer manageCustomer = new csManageCustomer("CS001", dashboard);
            manageCustomer.setVisible(true);
            System.out.println("[csManageCustomer] opened: " + manageCustomer.isVisible());
            System.out.println("[csManageCustomer] title: " + manageCustomer.getTitle());
            manageCustomer.dispose();
            dashboard.dispose();
            System.out.println("[csManageCustomer] closed: " + !manageCustomer.isVisible());
        });
 
        // csEditCustomer
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csManageCustomer manageCustomer = new csManageCustomer("CS001", dashboard);
            csEditCustomer editCustomer = new csEditCustomer("CS001", "C001", manageCustomer);
            editCustomer.setVisible(true);
            System.out.println("[csEditCustomer] opened: " + editCustomer.isVisible());
            System.out.println("[csEditCustomer] title: " + editCustomer.getTitle());
            editCustomer.dispose();
            manageCustomer.dispose();
            dashboard.dispose();
            System.out.println("[csEditCustomer] closed: " + !editCustomer.isVisible());
        });
 
        // csManageAppointment
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csManageAppointment manageAppt = new csManageAppointment("CS001", dashboard);
            manageAppt.setVisible(true);
            System.out.println("[csManageAppointment] opened: " + manageAppt.isVisible());
            System.out.println("[csManageAppointment] title: " + manageAppt.getTitle());
            manageAppt.dispose();
            dashboard.dispose();
            System.out.println("[csManageAppointment] closed: " + !manageAppt.isVisible());
        });
 
        // csCreateAppointment
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csManageAppointment manageAppt = new csManageAppointment("CS001", dashboard);
            csCreateAppointment createAppt = new csCreateAppointment("CS001", manageAppt);
            createAppt.setVisible(true);
            System.out.println("[csCreateAppointment] opened: " + createAppt.isVisible());
            System.out.println("[csCreateAppointment] title: " + createAppt.getTitle());
            createAppt.dispose();
            manageAppt.dispose();
            dashboard.dispose();
            System.out.println("[csCreateAppointment] closed: " + !createAppt.isVisible());
        });
 
        // csAssignAppointment — use A010 (Pending)
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csManageAppointment manageAppt = new csManageAppointment("CS001", dashboard);
            csAssignAppointment assignAppt = new csAssignAppointment("CS001", "A010", manageAppt);
            assignAppt.setVisible(true);
            System.out.println("[csAssignAppointment] opened: " + assignAppt.isVisible());
            System.out.println("[csAssignAppointment] title: " + assignAppt.getTitle());
            assignAppt.dispose();
            manageAppt.dispose();
            dashboard.dispose();
            System.out.println("[csAssignAppointment] closed: " + !assignAppt.isVisible());
        });
 
        // csPayments
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            csDashboard dashboard = new csDashboard("CS001");
            csPayments payments = new csPayments("CS001", dashboard);
            payments.setVisible(true);
            System.out.println("[csPayments] opened: " + payments.isVisible());
            System.out.println("[csPayments] title: " + payments.getTitle());
            payments.dispose();
            dashboard.dispose();
            System.out.println("[csPayments] closed: " + !payments.isVisible());
        });
 
        System.out.println(" Counter Staff Tests Done");

        // Customer Role
        // apu.asc.customer  —  Service Logic
        

        // Customer Role GUI
        // apu.asc.customer  —  GUI Frames
        
        
        // Manager Role
        // apu.asc.manager  —  Service Logic
        System.out.println(" apu.asc.manager — ManagerService");

        ManagerService mgrSvc = new ManagerService("M001");

        // Profile
        System.out.println("[getProfile]: " + mgrSvc.getProfile().getName());
        System.out.println("[getMyUsername]: " + mgrSvc.getMyUsername());
        System.out.println("[lookupUsername] M002: " + mgrSvc.lookupUsername("M002"));

        // Validation — person fields
        System.out.println("[validatePerson] valid: " + mgrSvc.validatePerson("Alice", "30", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("[validatePerson] blank: " + mgrSvc.validatePerson("", "30", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("[validatePerson] bad age: " + mgrSvc.validatePerson("Alice", "abc", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("[validatePerson] bad phone: " + mgrSvc.validatePerson("Alice", "30", "12", "a@b.com", "Addr", "MY"));
        System.out.println("[validatePerson] bad email: " + mgrSvc.validatePerson("Alice", "30", "0123456789", "noatsign", "Addr", "MY"));

        // Validation — account
        System.out.println("[validateAccount] valid: " + mgrSvc.validateAccount("freshuser", "pass1234", null));
        System.out.println("[validateAccount] dup username: " + mgrSvc.validateAccount("manager1", "pass1234", null));
        System.out.println("[validateAccount] short password: " + mgrSvc.validateAccount("freshuser", "x", null));

        // Validation — price
        System.out.println("[validatePrice] valid: " + mgrSvc.validatePrice("75.50"));
        System.out.println("[validatePrice] zero: " + mgrSvc.validatePrice("0"));
        System.out.println("[validatePrice] non-numeric: " + mgrSvc.validatePrice("abc"));

        // Staff counts
        System.out.println("[countManagers]: " + mgrSvc.countManagers());
        System.out.println("[countCounterStaff]: " + mgrSvc.countCounterStaff());
        System.out.println("[countTechnicians]: " + mgrSvc.countTechnicians());
        System.out.println("[countCustomers]: " + mgrSvc.countCustomers());

        // CRUD reads (no mutations)
        System.out.println("[getAllManagers]: " + mgrSvc.getAllManagers().size() + " manager(s)");
        System.out.println("[getAllCounterStaff]: " + mgrSvc.getAllCounterStaff().size() + " counter staff");
        System.out.println("[getAllTechnicians]: " + mgrSvc.getAllTechnicians().size() + " technician(s)");
        System.out.println("[getAllServices]: " + mgrSvc.getAllServices().size() + " service(s)");

        // Lookups by ID
        System.out.println("[getManagerById] M001: " + (mgrSvc.getManagerById("M001") == null ? "null" : mgrSvc.getManagerById("M001").getName()));
        System.out.println("[getCounterStaffById] CS001: " + (mgrSvc.getCounterStaffById("CS001") == null ? "null" : mgrSvc.getCounterStaffById("CS001").getName()));
        System.out.println("[getTechnicianById] T001: " + (mgrSvc.getTechnicianById("T001") == null ? "null" : mgrSvc.getTechnicianById("T001").getName()));
        System.out.println("[getServiceById] S001: " + (mgrSvc.getServiceById("S001") == null ? "null" : mgrSvc.getServiceById("S001").getServiceName()));

        // Search
        System.out.println("[searchManagers] 'John': " + mgrSvc.searchManagers("John").size() + " result(s)");
        System.out.println("[searchCounterStaff] 'Amy': " + mgrSvc.searchCounterStaff("Amy").size() + " result(s)");
        System.out.println("[searchTechnicians] 'Ahmad': " + mgrSvc.searchTechnicians("Ahmad").size() + " result(s)");

        // Name lookups
        System.out.println("[lookupTechnicianName] T001: " + mgrSvc.lookupTechnicianName("T001"));
        System.out.println("[lookupCounterStaffName] CS001: " + mgrSvc.lookupCounterStaffName("CS001"));
        System.out.println("[lookupCustomerName] C001: " + mgrSvc.lookupCustomerName("C001"));
        System.out.println("[lookupServiceName] S001: " + mgrSvc.lookupServiceName("S001"));
        System.out.println("[lookupTargetName] T001: " + mgrSvc.lookupTargetName("T001"));
        System.out.println("[lookupTargetName] CS001: " + mgrSvc.lookupTargetName("CS001"));

        // Feedback & comment rows (used by GUI tables)
        System.out.println("[getFeedbackRows] no filter: " + mgrSvc.getFeedbackRows(null).size() + " row(s)");
        System.out.println("[getFeedbackRows] 'Ahmad': " + mgrSvc.getFeedbackRows("Ahmad").size() + " row(s)");
        System.out.println("[getCommentRows] no filter: " + mgrSvc.getCommentRows(null).size() + " row(s)");
        System.out.println("[getCommentRows] 'Amy': " + mgrSvc.getCommentRows("Amy").size() + " row(s)");

        // Reports (7 unique)
        System.out.println("[Report 1 Revenue]: " + mgrSvc.getRevenueReport().size() + " row(s)");
        System.out.println("[Report 2 Technician Performance]: " + mgrSvc.getTechnicianPerformanceReport().size() + " row(s)");
        System.out.println("[Report 3 Counter Staff Performance]: " + mgrSvc.getCounterStaffPerformanceReport().size() + " row(s)");
        System.out.println("[Report 4 Service Popularity]: " + mgrSvc.getServicePopularityReport().size() + " row(s)");
        System.out.println("[Report 5 Customer Activity]: " + mgrSvc.getCustomerActivityReport().size() + " row(s)");
        int[] aps = mgrSvc.getAppointmentStatusCounts();
        System.out.println("[Report 6 Appointment Status]: pending=" + aps[0] + " assigned=" + aps[1] + " completed=" + aps[2] + " total=" + aps[3]);
        System.out.println("[Report 7 Monthly Revenue]: " + mgrSvc.getMonthlyRevenueReport().size() + " row(s)");

        // Aggregate stats
        System.out.println("[getTotalRevenue]: RM " + String.format("%.2f", mgrSvc.getTotalRevenue()));
        System.out.println("[getCurrentMonthRevenue]: RM " + String.format("%.2f", mgrSvc.getCurrentMonthRevenue()));


        // Manager Role GUI
        // apu.asc.manager  —  GUI Frames
        System.out.println(" apu.asc.manager — GUI Frames");

        // mgrDashboard
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            dashboard.setVisible(true);
            System.out.println("[mgrDashboard] opened: " + dashboard.isVisible());
            System.out.println("[mgrDashboard] title: " + dashboard.getTitle());
            dashboard.dispose();
            System.out.println("[mgrDashboard] closed: " + !dashboard.isVisible());
        });

        // mgrEditProfile
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrEditProfile profile = new mgrEditProfile(mgrSvc, dashboard);
            profile.setVisible(true);
            System.out.println("[mgrEditProfile] opened: " + profile.isVisible());
            System.out.println("[mgrEditProfile] title: " + profile.getTitle());
            profile.dispose();
            dashboard.dispose();
            System.out.println("[mgrEditProfile] closed: " + !profile.isVisible());
        });

        // mgrSetPrices
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrSetPrices prices = new mgrSetPrices(mgrSvc, dashboard);
            prices.setVisible(true);
            System.out.println("[mgrSetPrices] opened: " + prices.isVisible());
            System.out.println("[mgrSetPrices] title: " + prices.getTitle());
            prices.dispose();
            dashboard.dispose();
            System.out.println("[mgrSetPrices] closed: " + !prices.isVisible());
        });

        // mgrManageStaff (all 3 staff types)
        for (final String type : new String[]{"Manager", "CounterStaff", "Technician"}) {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                mgrDashboard dashboard = new mgrDashboard("M001");
                mgrManageStaff manage = new mgrManageStaff(mgrSvc, dashboard, type);
                manage.setVisible(true);
                System.out.println("[mgrManageStaff " + type + "] opened: " + manage.isVisible());
                System.out.println("[mgrManageStaff " + type + "] title: " + manage.getTitle());
                manage.dispose();
                dashboard.dispose();
                System.out.println("[mgrManageStaff " + type + "] closed: " + !manage.isVisible());
            });
        }

        // mgrAddStaff
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrManageStaff manage = new mgrManageStaff(mgrSvc, dashboard, "Technician");
            mgrAddStaff add = new mgrAddStaff(mgrSvc, manage, "Technician");
            add.setVisible(true);
            System.out.println("[mgrAddStaff] opened: " + add.isVisible());
            System.out.println("[mgrAddStaff] title: " + add.getTitle());
            add.dispose();
            manage.dispose();
            dashboard.dispose();
            System.out.println("[mgrAddStaff] closed: " + !add.isVisible());
        });

        // mgrEditStaff
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrManageStaff manage = new mgrManageStaff(mgrSvc, dashboard, "Technician");
            mgrEditStaff edit = new mgrEditStaff(mgrSvc, manage, "Technician", "T001");
            edit.setVisible(true);
            System.out.println("[mgrEditStaff] opened: " + edit.isVisible());
            System.out.println("[mgrEditStaff] title: " + edit.getTitle());
            edit.dispose();
            manage.dispose();
            dashboard.dispose();
            System.out.println("[mgrEditStaff] closed: " + !edit.isVisible());
        });

        // mgrFeedbacks
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrFeedbacks fbView = new mgrFeedbacks(mgrSvc, dashboard);
            fbView.setVisible(true);
            System.out.println("[mgrFeedbacks] opened: " + fbView.isVisible());
            System.out.println("[mgrFeedbacks] title: " + fbView.getTitle());
            fbView.dispose();
            dashboard.dispose();
            System.out.println("[mgrFeedbacks] closed: " + !fbView.isVisible());
        });

        // mgrComments
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrComments cmView = new mgrComments(mgrSvc, dashboard);
            cmView.setVisible(true);
            System.out.println("[mgrComments] opened: " + cmView.isVisible());
            System.out.println("[mgrComments] title: " + cmView.getTitle());
            cmView.dispose();
            dashboard.dispose();
            System.out.println("[mgrComments] closed: " + !cmView.isVisible());
        });

        // mgrReports
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            mgrDashboard dashboard = new mgrDashboard("M001");
            mgrReports reports = new mgrReports(mgrSvc, dashboard);
            reports.setVisible(true);
            System.out.println("[mgrReports] opened: " + reports.isVisible());
            System.out.println("[mgrReports] title: " + reports.getTitle());
            reports.dispose();
            dashboard.dispose();
            System.out.println("[mgrReports] closed: " + !reports.isVisible());
        });


        // End
        System.out.println(" All Tests Done — No data files modified");
        System.exit(0);
    }
}