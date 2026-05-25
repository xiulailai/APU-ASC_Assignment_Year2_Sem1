package apu.asc.staff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseFileHelper {
    protected List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank())
                    lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("readLines error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }
    
    protected List<String> readRawLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("readRawLines error [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }
    
    protected void writeLines(String filePath, String header, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            if (header != null) { bw.write(header); bw.newLine(); }
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) {
            System.err.println("writeLines error [" + filePath + "]: " + e.getMessage());
        }
    }
    
    protected void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("appendLine error [" + filePath + "]: " + e.getMessage());
        }
    }
}
