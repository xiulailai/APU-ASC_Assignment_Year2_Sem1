package apu.asc.Test;

import apu.asc.staff.*;
import apu.asc.technician.*;
import apu.asc.main.LoginFrame;

public class TestAllClasses {

    public static void main(String[] args) throws Exception {

        // apu.asc.staff folder
        System.out.println("1. apu.asc.staff");

        Technician t = new Technician("T001", "Ahmad Faiz", "Male", 31,"0145678901", "ahmadfaiz@email.com", "No. 9 Jalan Dahlia Ampang", "Malaysian");
        System.out.println("Technician is User: " + (t instanceof User));
        System.out.println("Technician: " + t.getName() + " | " + t.getTechnicianId());

        Customer c = new Customer("C001", "Ali Hassan", "Male", 42,"0121112222", "alihassan@email.com", "No. 6 Jalan Pelangi Kepong", "Malaysian");
        System.out.println("Customer is User: " + (c instanceof User));
        System.out.println("Customer: " + c.getName() + " | " + c.getCustomerId());

        CounterStaff cs = new CounterStaff("CS001", "Amy Lee", "Female", 29,"0134567890", "amylee@email.com", "No. 33 Jalan Kenanga Shah Alam", "Malaysian");
        System.out.println("CounterStaff is User: " + (cs instanceof User));
        System.out.println("CounterStaff: " + cs.getName() + " | " + cs.getCounterStaffId());

        Manager mg = new Manager("M001", "John Tan", "Male", 38,"0123456789", "johntan@email.com", "No. 12 Jalan Mawar Kuala Lumpur", "Malaysian");
        System.out.println("Manager is User: " + (mg instanceof User));
        System.out.println("Manager: " + mg.getName() + " | " + mg.getManagerId());

        Appointment ap = new Appointment("A001", "C001", "T001", "CS001","S001", "2026-05-20", "10:00", "Completed");
        System.out.println("Appointment: " + ap.getAppointmentId() + " | " + ap.getStatus());

        Comment cm = new Comment("CM001", "A001", "C001", "T001", "Great service!");
        System.out.println("Comment: " + cm.getCommentId() + " | " + cm.getCommentText());

        Feedback fb = new Feedback("F001", "A001", "T001", "Oil changed successfully.");
        System.out.println("Feedback: " + fb.getFeedbackId() + " | " + fb.getFeedbackText());

        Service sv = new Service("S001", "Normal Service", 1, 50.00);
        System.out.println("Service: " + sv.getServiceName() + " | RM " + sv.getPrice());

        Payment py = new Payment("P001", "A001", "C001", 50.00, "2026-05-20", "R001");
        System.out.println("Payment: " + py.getPaymentId() + " | RM " + py.getAmount());

        // apu.asc.main folder — LoginFrame GUI
        System.out.println("\n2. GUI: LoginFrame");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            System.out.println("LoginFrame opened: " + login.isVisible());
            System.out.println("LoginFrame title: " + login.getTitle());
            login.dispose();
            System.out.println("LoginFrame closed: " + !login.isVisible());
        });
        
        
        // 2. apu.asc.technician
        // TechnicianService
        System.out.println("\n3. apu.asc.technician");
        System.out.println("# TechnicianService");
        TechnicianService techTest = new TechnicianService("T001");
        System.out.println("getProfile(): " + techTest.getProfile().getName());
        System.out.println("getMyAppointments(): " + techTest.getMyAppointments().size() + " appointment(s)");
        System.out.println("filterByStatus(Assigned): " + techTest.filterByStatus("Assigned").size());
        System.out.println("filterByStatus(Completed): " + techTest.filterByStatus("Completed").size());
        System.out.println("searchTasks(A001): " + techTest.searchTasks("A001").size() + " result(s)");
        System.out.println("searchTasks(Normal Service): " + techTest.searchTasks("Normal Service").size() + " result(s)");
        System.out.println("getTaskDetail(A001): " + techTest.getTaskDetail("A001").getAppointmentId());
        System.out.println("getTaskDetail(A002): " + techTest.getTaskDetail("A002")); // null — not T001's
        System.out.println("getServiceInfo(S001): " + techTest.getServiceInfo("S001").getServiceName());
        System.out.println("getMyComments(): " + techTest.getMyComments().size() + " comment(s)");
        System.out.println("getCustomerById(C001): " + techTest.getCustomerById("C001").getName());
        System.out.println("getUpcomingSchedule(): " + techTest.getUpcomingSchedule().size() + " upcoming");
        System.out.println("getTotalHoursForDate(): " + techTest.getTotalHoursForDate("2026-05-23") + " hr(s)");
        System.out.println("markTaskCompleted(A001): " + techTest.markTaskCompleted("A001")); // already completed
        System.out.println("markTaskCompleted(A004): " + techTest.markTaskCompleted("A004")); // success
        System.out.println("markTaskCompleted(A004 again): " + techTest.markTaskCompleted("A004")); // prevent double
        System.out.println("provideFeedback(A001, dup): " + techTest.provideFeedback("A001", "Test")); // duplicate
        System.out.println("provideFeedback(A004, txt): " + techTest.provideFeedback("A004", "Job done.")); // success

        // Technician GUI frames
        System.out.println("\n# GUI: tchnDashboard");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            dashboard.setVisible(true);
            System.out.println("tchnDashboard opened: " + dashboard.isVisible());
            System.out.println("tchnDashboard title: " + dashboard.getTitle());
            dashboard.dispose();
            System.out.println("tchnDashboard closed: " + !dashboard.isVisible());
        });

        System.out.println("\n# GUI: tchnProfile");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            tchnProfile profile = new tchnProfile("T001", dashboard);
            profile.setVisible(true);
            System.out.println("tchnProfile opened: " + profile.isVisible());
            System.out.println("tchnProfile title: " + profile.getTitle());
            profile.dispose();
            dashboard.dispose();
            System.out.println("tchnProfile closed: " + !profile.isVisible());
        });

        System.out.println("\n# GUI: tchnTasks");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnDashboard dashboard = new tchnDashboard("T001");
            tchnTasks tasks = new tchnTasks("T001", dashboard);
            tasks.setVisible(true);
            System.out.println("tchnTasks opened: " + tasks.isVisible());
            System.out.println("tchnTasks title: " + tasks.getTitle());
            tasks.dispose();
            dashboard.dispose();
            System.out.println("tchnTasks closed: " + !tasks.isVisible());
        });

        System.out.println("\n# GUI: tchnComments");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnComments comments = new tchnComments("T001");
            comments.setVisible(true);
            System.out.println("tchnComments opened: " + comments.isVisible());
            System.out.println("tchnComments title: " + comments.getTitle());
            comments.dispose();
            System.out.println("tchnComments closed: " + !comments.isVisible());
        });

        System.out.println("\n# GUI: tchnFeedback");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnFeedback feedback = new tchnFeedback("T001");
            feedback.setVisible(true);
            System.out.println("tchnFeedback opened: " + feedback.isVisible());
            System.out.println("tchnFeedback title: " + feedback.getTitle());
            feedback.dispose();
            System.out.println("tchnFeedback closed: " + !feedback.isVisible());
        });

        System.out.println("\n# GUI: tchnSchedule");
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            tchnSchedule schedule = new tchnSchedule("T001");
            schedule.setVisible(true);
            System.out.println("tchnSchedule opened: " + schedule.isVisible());
            System.out.println("tchnSchedule title: " + schedule.getTitle());
            schedule.dispose();
            System.out.println("tchnSchedule closed: " + !schedule.isVisible());
        });

        System.out.println("\nAll Tests Done");
        System.exit(0);
    }
}