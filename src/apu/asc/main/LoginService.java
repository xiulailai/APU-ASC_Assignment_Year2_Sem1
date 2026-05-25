package apu.asc.main;

import java.io.BufferedReader;
import java.io.FileReader;


public class LoginService {
    private static final String FILE_USERS = "Data/users.txt";

    // Authenticates a user against users.txt.

    public String[] authenticate(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;
                // userId, username, password, role, roleRefId
                String[] p = line.split(",", 5);
                if (p.length == 5 && p[1].trim().equals(username) && p[2].trim().equals(password)) {
                    return new String[]{ p[3].trim(), p[4].trim() };
                }
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }
}