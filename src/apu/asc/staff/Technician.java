package apu.asc.staff;

// Subclass of User. Adds technicianId as the unique identifier.
public class Technician extends User {
    private String technicianId;

    //Constructor
    public Technician(String technicianId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        super(name, gender, age, phone, email, address, nationality); // inherited fields
        this.technicianId = technicianId; // own field
    }

    //Getter
    public String getTechnicianId() { return technicianId; }

    //convert object to txt file
    @Override
    public String toFileString() {
        return String.join(",",technicianId, name, gender,String.valueOf(age),phone, email, address, nationality);
    }

    //conver txt file to object
    public static Technician fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new Technician(p[0].trim(), p[1].trim(), p[2].trim(),Integer.parseInt(p[3].trim()),p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Gender: %s | Age: %d | Phone: %s | Email: %s",technicianId, name, gender, age, phone, email);
    }
}