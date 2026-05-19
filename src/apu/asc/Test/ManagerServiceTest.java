package apu.asc.Test;

import apu.asc.manager.ManagerService;
import apu.asc.staff.CounterStaff;
import apu.asc.staff.Manager;
import apu.asc.staff.Service;
import apu.asc.staff.Technician;
import java.util.List;

//Sanity check for ManagerService. Read-only operations only. No file mutations.
public class ManagerServiceTest {

    public static void main(String[] args) {
        ManagerService svc = new ManagerService("M001");

        System.out.println("===== PROFILE =====");
        Manager me = svc.getProfile();
        System.out.println("getProfile(): " + (me == null ? "null" : me.getName() + " (" + me.getManagerId() + ")"));
        System.out.println("getMyUsername(): " + svc.getMyUsername());

        System.out.println("\n===== STAFF COUNTS =====");
        System.out.println("Managers:      " + svc.countManagers());
        System.out.println("CounterStaff:  " + svc.countCounterStaff());
        System.out.println("Technicians:   " + svc.countTechnicians());
        System.out.println("Customers:     " + svc.countCustomers());

        System.out.println("\n===== LIST MANAGERS =====");
        for (Manager m : svc.getAllManagers()) System.out.println("  " + m);

        System.out.println("\n===== LIST COUNTER STAFF =====");
        for (CounterStaff cs : svc.getAllCounterStaff()) System.out.println("  " + cs);

        System.out.println("\n===== LIST TECHNICIANS =====");
        for (Technician t : svc.getAllTechnicians()) System.out.println("  " + t);

        System.out.println("\n===== SERVICES =====");
        for (Service s : svc.getAllServices()) System.out.println("  " + s);

        System.out.println("\n===== VALIDATION TESTS =====");
        System.out.println("Valid person:     " + svc.validatePerson("Alice", "30", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("Bad age (text):   " + svc.validatePerson("Alice", "abc", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("Bad phone:        " + svc.validatePerson("Alice", "30", "12", "a@b.com", "Addr", "MY"));
        System.out.println("Bad email:        " + svc.validatePerson("Alice", "30", "0123456789", "noatsign", "Addr", "MY"));
        System.out.println("Empty field:      " + svc.validatePerson("", "30", "0123456789", "a@b.com", "Addr", "MY"));
        System.out.println("Valid account:    " + svc.validateAccount("freshuser", "pass1234", null));
        System.out.println("Dup username:     " + svc.validateAccount("manager1", "pass1234", null));
        System.out.println("Short password:   " + svc.validateAccount("freshuser", "x", null));
        System.out.println("Valid price:      " + svc.validatePrice("75.50"));
        System.out.println("Zero price:       " + svc.validatePrice("0"));
        System.out.println("Bad price:        " + svc.validatePrice("abc"));

        System.out.println("\n===== FEEDBACK ROWS (no filter) =====");
        List<String[]> fbs = svc.getFeedbackRows(null);
        System.out.println("Total feedback rows: " + fbs.size());
        for (String[] r : fbs) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== COMMENT ROWS (no filter) =====");
        List<String[]> cmts = svc.getCommentRows(null);
        System.out.println("Total comment rows: " + cmts.size());
        for (String[] r : cmts) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== REPORT 1: REVENUE =====");
        for (String[] r : svc.getRevenueReport()) System.out.println("  " + java.util.Arrays.toString(r));
        System.out.println("  Total revenue: RM " + String.format("%.2f", svc.getTotalRevenue()));

        System.out.println("\n===== REPORT 2: TECHNICIAN PERFORMANCE =====");
        for (String[] r : svc.getTechnicianPerformanceReport()) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== REPORT 3: COUNTER STAFF PERFORMANCE =====");
        for (String[] r : svc.getCounterStaffPerformanceReport()) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== REPORT 4: SERVICE POPULARITY =====");
        for (String[] r : svc.getServicePopularityReport()) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== REPORT 5: CUSTOMER ACTIVITY =====");
        for (String[] r : svc.getCustomerActivityReport()) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== REPORT 6: APPOINTMENT STATUS =====");
        int[] s = svc.getAppointmentStatusCounts();
        System.out.println("  Pending: " + s[0] + " | Assigned: " + s[1] + " | Completed: " + s[2] + " | Total: " + s[3]);

        System.out.println("\n===== REPORT 7: MONTHLY REVENUE =====");
        for (String[] r : svc.getMonthlyRevenueReport()) System.out.println("  " + java.util.Arrays.toString(r));

        System.out.println("\n===== ALL TESTS DONE =====");
    }
}
