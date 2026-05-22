package apu.asc.staff;

//Subclass of User. Adds managerId as the unique identifier.
public class Manager extends User implements FileStorable {

    private String managerId;

    //constructor
    public Manager(String managerId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        super(name, gender, age, phone, email, address, nationality); // inherited fields
        this.managerId = managerId; // own field
    }
    
    //getter
    public String getManagerId() { return managerId; }

    //convert object to txt file
    @Override
    public String toFileString() {
        return String.join(",",managerId, name, gender,String.valueOf(age),phone, email, address, nationality);
    }
    
    //conver txt file to object
    public static Manager fromFileString(String line) {
        String[] p = line.split(",", 8);
        if (p.length < 8) {
            throw new IllegalArgumentException("Invalid manager record: " + line);
        }
        return new Manager(p[0].trim(), p[1].trim(), p[2].trim(),Integer.parseInt(p[3].trim()),p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String getDisplayInfo() {
        return "Manager [" + managerId + "] " + getName() + " | " + getEmail();
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Gender: %s | Age: %d | Phone: %s | Email: %s",managerId, name, gender, age, phone, email);
    }
}