package apu.asc.Test;

import apu.asc.staff.*;
import apu.asc.staff.Technician; //for avoid Issues
import apu.asc.main.LoginFrame;
import apu.asc.main.LoginService;
import apu.asc.technician.*;
import apu.asc.counterstaff.*;
import apu.asc.customer.*;


public class TestAllClasses {

    public static void main(String[] args) throws Exception {

        // Staff Model
        // apu.asc.staff  —  Model Classes
        System.out.println(" apu.asc.staff — Model Classes");

        // User (abstract superclass) — tested through each subclass
        // Technician
        Technician t = new Technician("T001", "Ahmad Faiz", "Male", 31,
                "0145678901", "ahmadfaiz@email.com", "No. 9 Jalan Dahlia Ampang", "Malaysian");
        System.out.println("[Technician] extends User: " + (t instanceof User));
        System.out.println("[Technician] getName(): " + t.getName());
        System.out.println("[Technician] getTechnicianId(): " + t.getTechnicianId());
        System.out.println("[Technician] getDisplayInfo(): " + t.getDisplayInfo());
        System.out.println("[Technician] toFileString(): " + t.toFileString());

        // Customer
        Customer c = new Customer("C001", "Ali Hassan", "Male", 42,
                "0121112222", "alihassan@email.com", "No. 6 Jalan Pelangi Kepong", "Malaysian");
        System.out.println("[Customer]   extends User: " + (c instanceof User));
        System.out.println("[Customer]   getName(): " + c.getName());
        System.out.println("[Customer]   getCustomerId(): " + c.getCustomerId());
        System.out.println("[Customer]   getDisplayInfo(): " + c.getDisplayInfo());
        System.out.println("[Customer]   toFileString(): " + c.toFileString());

        // CounterStaff
        CounterStaff cs = new CounterStaff("CS001", "Amy Lee", "Female", 29,
                "0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian");
        System.out.println("[CounterStaff] extends User: " + (cs instanceof User));
        System.out.println("[CounterStaff] getName(): " + cs.getName());
        System.out.println("[CounterStaff] getCounterStaffId(): " + cs.getCounterStaffId());
        System.out.println("[CounterStaff] getDisplayInfo(): " + cs.getDisplayInfo());
        System.out.println("[CounterStaff] toFileString(): " + cs.toFileString());

        // Manager
        Manager mg = new Manager("M001", "John Tan", "Male", 38,
                "0123456789", "johntan@email.com", "No. 12 Jalan Mawar Kuala Lumpur", "Malaysian");
        System.out.println("[Manager] extends User: " + (mg instanceof User));
        System.out.println("[Manager] getName(): " + mg.getName());
        System.out.println("[Manager] getManagerId(): " + mg.getManagerId());
        System.out.println("[Manager] getDisplayInfo(): " + mg.getDisplayInfo());
        System.out.println("[Manager] toFileString(): " + mg.toFileString());

        // Appointment
        Appointment ap = new Appointment("A001", "C001", "T001", "CS001",
                "S001", "2026-06-20", "10:00", "Completed");
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


        // Login Function
        // apu.asc.main  —  Login
        System.out.println(" apu.asc.main — Login");

        LoginService loginSvc = new LoginService();

        // Valid logins — one per role
        String[] techLogin = loginSvc.authenticate("tech1",    "techpass");
        String[] csLogin = loginSvc.authenticate("counter1", "abc123");
        String[] custLogin = loginSvc.authenticate("ali123",   "pass123");
        String[] mgrLogin = loginSvc.authenticate("manager1", "pass123");

        System.out.println("[LoginService] tech1    → role: " + techLogin[0]  + " | id: " + techLogin[1]);
        System.out.println("[LoginService] counter1 → role: " + csLogin[0]    + " | id: " + csLogin[1]);
        System.out.println("[LoginService] ali123   → role: " + custLogin[0]  + " | id: " + custLogin[1]);
        System.out.println("[LoginService] manager1 → role: " + mgrLogin[0]   + " | id: " + mgrLogin[1]);

        // Invalid login — should return null
        String[] badLogin = loginSvc.authenticate("wronguser", "wrongpass");
        System.out.println("[LoginService] bad credentials → null: " + (badLogin == null));

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
        System.out.println("[getCustomerById] C001: " + techSvc.getCustomerById("C001").getName());

        // Schedule
        System.out.println("[getUpcomingSchedule]: " + techSvc.getUpcomingSchedule().size() + " upcoming task(s)");
        System.out.println("[getTotalHoursForDate] today: " + techSvc.getTotalHoursForDate(techSvc.getTodayString()) + " hr(s)");
        System.out.println("[getDayName] 2026-06-20: " + techSvc.getDayName("2026-06-20"));
        System.out.println("[getScheduleRows]: " + techSvc.getScheduleRows().size() + " row(s)");
        System.out.println("[getUpcomingStatusCounts]: total=" + techSvc.getUpcomingStatusCounts()[0]
                + " assigned=" + techSvc.getUpcomingStatusCounts()[1]
                + " pending=" + techSvc.getUpcomingStatusCounts()[2]);
        System.out.println("[getAllStatusCounts]: assigned=" + techSvc.getAllStatusCounts()[0]
                + " pending=" + techSvc.getAllStatusCounts()[1]
                + " completed=" + techSvc.getAllStatusCounts()[2]);

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


        // Counter Staff Role
        // apu.asc.counterstaff  —  Service Logic
        

        // Counter Staff Role GUI
        // apu.asc.counterstaff  —  GUI Frames
        


        // Customer Role
        // apu.asc.customer  —  Service Logic
        

        // Customer Role GUI
        // apu.asc.customer  —  GUI Frames
        
        
        // Manager Role
        // 
        

        // Manager Role GUI
        // 

        // End
        System.out.println(" All Tests Done — No data files modified");
        System.exit(0);
    }
}