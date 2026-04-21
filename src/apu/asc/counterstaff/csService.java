package apu.asc.counterstaff;

import apu.asc.staff.CounterStaff;
import java.io.BufferedReader;
import java.io.FileReader;

public class csService {
    private static final String FILE_COUNTERSTAFF = "Data/counterstaff.txt";
    
     //Finds and returns the CounterStaff object matching the given ID. Returns null if not found.
    public static CounterStaff getProfile(String counterStaffId) {
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
}
