package apu.asc.Test;

import apu.asc.staff.*;
import apu.asc.technician.TechnicianService;
import java.util.List;


public class TechnicianServiceTest {

    private static int totalTests  = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Tecnician ID used in txt file
    private static final String TEST_TECH_ID = "T001";

    public static void main(String[] args) {

        TechnicianService svc = new TechnicianService(TEST_TECH_ID);

        // Main Features in TechnicianService.java
        testGetProfile(svc);
        testGetMyAppointments(svc);
        testFilterByStatus(svc);
        testSearchTasks(svc);
        testMarkTaskCompleted(svc);
        testProvideFeedback(svc);
        testGetMyComments(svc);
        testGetUpcomingSchedule(svc);

        // Other Features in TechnicianService.java
        testGetTaskDetail(svc);
        testGetServiceInfo(svc);
        testGetCustomerById(svc);
        testGetCommentsForAppointment(svc);
        testBuildServiceNameMap(svc);
        testGetTotalHoursForDate(svc);
        testInheritance();

        // Make Summary Report
        printSummary();
    }

    // MAIN FEATURES — DETAILED OUTPUT
    // Test 1: getProfile()
    private static void testGetProfile(TechnicianService svc) {
        printSectionHeader("Test 1 - getProfile()");
        totalTests++;

        try {
            Technician t = svc.getProfile();

            System.out.println("Expected Technician ID: T001");
            System.out.println("Got Technician ID: " + t.getTechnicianId());

            System.out.println("Expected Name: Ahmad Faiz");
            System.out.println("Got Name: " + t.getName());

            System.out.println("Expected Gender: Male");
            System.out.println("Got Gender: " + t.getGender());

            System.out.println("Expected Age: 31");
            System.out.println("Got Age: " + t.getAge());

            System.out.println("Expected Phone: 0145678901");
            System.out.println("Got Phone: " + t.getPhone());

            System.out.println("Expected Email: ahmadfaiz@email.com");
            System.out.println("Got Email: " + t.getEmail());

            boolean pass = t.getTechnicianId().equals("T001")
                        && t.getName().equals("Ahmad Faiz")
                        && t.getGender().equals("Male")
                        && t.getAge() == 31
                        && t.getPhone().equals("0145678901")
                        && t.getEmail().equals("ahmadfaiz@email.com");

            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 2: getMyAppointments()
    private static void testGetMyAppointments(TechnicianService svc) {
        printSectionHeader("Test 2 - getMyAppointments()");
        totalTests++;

        try {
            List<Appointment> list = svc.getMyAppointments();

            System.out.println("Expected: only appointments where technicianId = T001");
            System.out.println("Total appointments returned : " + list.size());

            boolean allBelongToT001 = true;
            for (Appointment a : list) {
                System.out.println("Appt ID: " + a.getAppointmentId()
                        + " | TechID: " + a.getTechnicianId()
                        + " | Status: " + a.getStatus());
                if (!a.getTechnicianId().equals("T001")) {
                    allBelongToT001 = false;
                }
            }

            System.out.println("All belong to T001: " + allBelongToT001);
            System.out.println("Expected count: 2 (A001, A004)");
            System.out.println("Got count: " + list.size());

            boolean pass = allBelongToT001 && list.size() == 2;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 3: filterByStatus()
    private static void testFilterByStatus(TechnicianService svc) {
        printSectionHeader("Test 3 - filterByStatus()");
        totalTests++;

        try {
            List<Appointment> completed = svc.filterByStatus("Completed");
            List<Appointment> assigned = svc.filterByStatus("Assigned");
            List<Appointment> pending = svc.filterByStatus("Pending");
            List<Appointment> all = svc.filterByStatus(null);

            System.out.println("Filter: Completed");
            System.out.println("Expected: 1 (A001)");
            System.out.println("Got: " + completed.size());

            System.out.println("Filter: Assigned");
            System.out.println("Expected: 1 (A004)");
            System.out.println("Got: " + assigned.size());

            System.out.println("Filter: Pending");
            System.out.println("Expected: 0");
            System.out.println("Got: " + pending.size());

            System.out.println("Filter: null (all)");
            System.out.println("Expected: 2");
            System.out.println("Got: " + all.size());

            boolean pass = completed.size() == 1
                        && assigned.size() == 1
                        && pending.size() == 0
                        && all.size() == 2;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 4: searchTasks()
    private static void testSearchTasks(TechnicianService svc) {
        printSectionHeader("Test 4 - searchTasks()");
        totalTests++;

        try {
            List<Appointment> byApptId = svc.searchTasks("A001");
            List<Appointment> bySvcId = svc.searchTasks("S001");
            List<Appointment> bySvcName = svc.searchTasks("Normal Service");
            List<Appointment> noMatch = svc.searchTasks("ZZZZZ");
            List<Appointment> nullSearch = svc.searchTasks(null);

            System.out.println("Search: 'A001' (by appointment ID)");
            System.out.println("Expected: 1   Got : " + byApptId.size());

            System.out.println("Search: 'S001' (by service ID)");
            System.out.println("Expected: 1   Got : " + bySvcId.size());

            System.out.println("Search: 'Normal Service' (by service name)");
            System.out.println("Expected: 1   Got : " + bySvcName.size());

            System.out.println("Search: 'ZZZZZ' (no match)");
            System.out.println("Expected: 0   Got : " + noMatch.size());

            System.out.println("Search: null (returns all)");
            System.out.println("Expected : 2   Got : " + nullSearch.size());

            boolean pass = byApptId.size() == 1
                        && bySvcId.size() == 1
                        && bySvcName.size() == 1
                        && noMatch.size() == 0
                        && nullSearch.size() == 2;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 5: markTaskCompleted()
    private static void testMarkTaskCompleted(TechnicianService svc) {
        printSectionHeader("Test 5 - markTaskCompleted()");
        totalTests++;

        try {
            // Case 1: Try marking A001 (already Completed) — should fail
            String result1 = svc.markTaskCompleted("A001");
            System.out.println("Case 1: Mark already Completed task (A001)");
            System.out.println("Expected : starts with ERROR");
            System.out.println("Got: " + result1);
            boolean case1 = result1.startsWith("ERROR");

            // Case 2: Try marking non-existent appointment — should fail
            String result2 = svc.markTaskCompleted("A999");
            System.out.println("Case 2: Mark non-existent appointment (A999)");
            System.out.println("Expected : starts with ERROR");
            System.out.println("Got: " + result2);
            boolean case2 = result2.startsWith("ERROR");

            // Case 3: Try marking A004 (Assigned) — should succeed
            String result3 = svc.markTaskCompleted("A004");
            System.out.println("Case 3: Mark Assigned task (A004)");
            System.out.println("Expected : starts with SUCCESS");
            System.out.println("Got: " + result3);
            boolean case3 = result3.startsWith("SUCCESS");

            // Case 4: Try marking A004 again — should fail (already completed)
            String result4 = svc.markTaskCompleted("A004");
            System.out.println("Case 4: Mark A004 again (prevent double complete)");
            System.out.println("Expected : starts with ERROR");
            System.out.println("Got: " + result4);
            boolean case4 = result4.startsWith("ERROR");

            // Restore A004 back to Assigned for other tests
            restoreAppointmentStatus("A004", "Assigned");
            System.out.println("(A004 restored to Assigned for remaining tests)");

            boolean pass = case1 && case2 && case3 && case4;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 6: provideFeedback()
    private static void testProvideFeedback(TechnicianService svc) {
        printSectionHeader("Test 6 - provideFeedback()");
        totalTests++;

        try {
            // Case 1: Provide feedback for non-completed task (A004 = Assigned)
            String result1 = svc.provideFeedback("A004", "Test feedback text.");
            System.out.println("Case 1: Feedback for Assigned task (A004)");
            System.out.println("Expected: starts with ERROR");
            System.out.println("Got: " + result1);
            boolean case1 = result1.startsWith("ERROR");

            // Case 2: Provide feedback with empty text
            String result2 = svc.provideFeedback("A001", "");
            System.out.println("Case 2: Empty feedback text");
            System.out.println("Expected : starts with ERROR");
            System.out.println("Got: " + result2);
            boolean case2 = result2.startsWith("ERROR");

            // Case 3: Provide feedback for non-existent appointment
            String result3 = svc.provideFeedback("A999", "Some feedback.");
            System.out.println("Case 3: Feedback for non-existent appointment (A999)");
            System.out.println("Expected: starts with ERROR");
            System.out.println("Got: " + result3);
            boolean case3 = result3.startsWith("ERROR");

            // Case 4: Provide feedback for A001 (already has feedback F001)
            String result4 = svc.provideFeedback("A001", "Duplicate feedback attempt.");
            System.out.println("Case 4: Duplicate feedback for A001 (already has F001)");
            System.out.println("Expected: starts with ERROR");
            System.out.println("Got: " + result4);
            boolean case4 = result4.startsWith("ERROR");

            boolean pass = case1 && case2 && case3 && case4;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 7: getMyComments()
    private static void testGetMyComments(TechnicianService svc) {
        printSectionHeader("Test 7 - getMyComments()");
        totalTests++;

        try {
            List<Comment> comments = svc.getMyComments();

            System.out.println("Expected: only comments where targetUserId = T001");
            System.out.println("Total comments returned: " + comments.size());

            boolean allTargetT001 = true;
            for (Comment c : comments) {
                System.out.println("Comment ID: " + c.getCommentId()
                        + " | Appt: " + c.getAppointmentId()
                        + " | Target: " + c.getTargetUserId()
                        + " | Text: " + c.getCommentText());
                if (!c.getTargetUserId().equals("T001")) {
                    allTargetT001 = false;
                }
            }

            System.out.println("All directed at T001: " + allTargetT001);
            System.out.println("Expected count: 1 (CM001)");
            System.out.println("Got count: " + comments.size());

            boolean pass = allTargetT001 && comments.size() == 1;
            printResult(pass);

        } catch (Exception e) {
            printError(e);
        }
    }

    // Test 8: getUpcomingSchedule()
    private static void testGetUpcomingSchedule(TechnicianService svc) {
        printSectionHeader("Test 8 - getUpcomingSchedule()");
        totalTests++;

        try {
            List<Appointment> schedule = svc.getUpcomingSchedule();

            System.out.println("Expected: only non-Completed appointments for T001");
            System.out.println("Expected: sorted by date then time");
            System.out.println("Total upcoming returned: " + schedule.size());

            boolean noneCompleted = true;
            boolean sortedCorrectly = true;
            String prevDate = "";
            String prevTime = "";

            for (Appointment a : schedule) {
                System.out.println("Appt: " + a.getAppointmentId()
                        + " | Date: " + a.getAppointmentDate()
                        + " | Time: " + a.getAppointmentTime()
                        + " | Status: " + a.getStatus());

                if (a.getStatus().equalsIgnoreCase("Completed")) {
                    noneCompleted = false;
                }

                // Check sort order
                if (!prevDate.isEmpty()) {
                    int dateCmp = a.getAppointmentDate().compareTo(prevDate);
                    if (dateCmp < 0) sortedCorrectly = false;
                    if (dateCmp == 0 && a.getAppointmentTime().compareTo(prevTime) < 0) {
                        sortedCorrectly = false;
                    }
                }
                prevDate = a.getAppointmentDate();
                prevTime = a.getAppointmentTime();
            }

            System.out.println("No Completed tasks in list: " + noneCompleted);
            System.out.println("Sorted by date/time: " + sortedCorrectly);
            System.out.println("Expected count: 1 (A004 Assigned)");
            System.out.println("Got count: " + schedule.size());

            boolean pass = noneCompleted && sortedCorrectly && schedule.size() == 1;
            printResult(pass);
            System.out.println("\n"); //for seperate main and other features

        } catch (Exception e) {
            printError(e);
        }
    }

    // OTHER FEATURES — SIMPLE OUTPUT
    private static void testGetTaskDetail(TechnicianService svc) {
        totalTests++;
        try {
            Appointment a = svc.getTaskDetail("A001");
            boolean pass = a != null && a.getAppointmentId().equals("A001");
            System.out.println("Test - getTaskDetail(A001): " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;

            totalTests++;
            Appointment notOwned = svc.getTaskDetail("A002"); // belongs to T002
            boolean pass2 = notOwned == null;
            System.out.println("Test - getTaskDetail(A002) not owned: " + (pass2 ? "PASS" : "FAIL"));
            if (pass2) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - getTaskDetail: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testGetServiceInfo(TechnicianService svc) {
        totalTests++;
        try {
            Service s = svc.getServiceInfo("S001");
            boolean pass = s != null && s.getServiceName().equals("Normal Service");
            System.out.println("Test - getServiceInfo(S001): " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - getServiceInfo: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testGetCustomerById(TechnicianService svc) {
        totalTests++;
        try {
            Customer c = svc.getCustomerById("C001");
            boolean pass = c != null && c.getName().equals("Ali Hassan");
            System.out.println("Test - getCustomerById(C001): " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - getCustomerById: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testGetCommentsForAppointment(TechnicianService svc) {
        totalTests++;
        try {
            List<Comment> c = svc.getCommentsForAppointment("A001");
            boolean pass = c != null && !c.isEmpty();
            System.out.println("Test - getCommentsForAppointment: " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - getCommentsForAppointment: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testBuildServiceNameMap(TechnicianService svc) {
        totalTests++;
        try {
            java.util.Map<String, String> map = svc.buildServiceNameMap();
            boolean pass = map.containsKey("S001") && map.get("S001").equals("Normal Service");
            System.out.println("Test - buildServiceNameMap(): " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - buildServiceNameMap: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testGetTotalHoursForDate(TechnicianService svc) {
        totalTests++;
        try {
            int hours = svc.getTotalHoursForDate("2026-05-23");
            boolean pass = hours == 3; // A004 = Major Service = 3 hrs
            System.out.println("Test - getTotalHoursForDate(2026-05-23): " + (pass ? "PASS": "FAIL")
                    + " (got: " + hours + " hr(s), expected: 3)");
            if (pass) passedTests++; else failedTests++;
        } catch (Exception e) {
            System.out.println("Test - getTotalHoursForDate: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    private static void testInheritance() {
        totalTests++;
        try {
            Technician t = new Technician("T001", "Ahmad Faiz", "Male", 31,"0145678901", "ahmadfaiz@email.com","No. 9 Jalan Dahlia Ampang", "Malaysian");
            boolean pass = t instanceof User;
            System.out.println("Test - Technician instanceof User: " + (pass ? "PASS" : "FAIL"));
            if (pass) passedTests++; else failedTests++;

            totalTests++;
            Customer c = new Customer("C001", "Ali Hassan", "Male", 42,"0121112222", "alihassan@email.com","No. 6 Jalan Pelangi Kepong", "Malaysian");
            boolean pass2 = c instanceof User;
            System.out.println("Test - Customer instanceof User: " + (pass2 ? "PASS" : "FAIL"));
            if (pass2) passedTests++; else failedTests++;

        } catch (Exception e) {
            System.out.println("Test - Inheritance check: FAIL (" + e.getMessage() + ")");
            failedTests++;
        }
    }

    // restore appointment status after test 5 modifies it
    private static void restoreAppointmentStatus(String apptId, String status) {
        try {
            java.io.File file = new java.io.File("Data/appointments.txt");
            java.util.List<String> lines = new java.util.ArrayList<>();
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank()) {
                    Appointment a = Appointment.fromFileString(line);
                    if (a.getAppointmentId().equals(apptId)) {
                        a.setStatus(status);
                        lines.add(a.toFileString());
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
            br.close();
            java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(file, false));
            for (String l : lines) pw.println(l);
            pw.close();
        } catch (Exception e) {
            System.out.println("(Warning: could not restore " + apptId + " — " + e.getMessage() + ")");
        }
    }

    // PRINT HELPERS
    private static void printHeader(String title) {
        System.out.println();
        System.out.println("  " + title);
    }

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println("# " + title);
    }

    private static void printResult(boolean pass) {
        if (pass) {
            System.out.println("Result: PASS");
            passedTests++;
        } else {
            System.out.println("Result: FAIL");
            failedTests++;
        }
    }

    private static void printError(Exception e) {
        System.out.println("Result: FAIL (Exception: " + e.getMessage() + ")");
        failedTests++;
    }

    private static void printSummary() {
        System.out.println();
        System.out.println("TEST SUMMARY");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Result: " + (failedTests == 0 ? "ALL TESTS PASSED" : failedTests + " TEST(S) FAILED"));
    }
}