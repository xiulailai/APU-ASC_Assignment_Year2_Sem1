package apu.asc.staff;

// Subclass of User. Adds counterStaffId as the unique identifier.
public class CounterStaff extends User {
    private String counterStaffId;

    //constructor
    public CounterStaff(String counterStaffId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        super(name, gender, age, phone, email, address, nationality); // inherited fields
        this.counterStaffId = counterStaffId; // own field
    }

    //getter
    public String getCounterStaffId() { return counterStaffId; }

    //convert object to txt file
    @Override
    public String toFileString() {
        return String.join(",",counterStaffId, name, gender,String.valueOf(age),phone, email, address, nationality);
    }

    //conver txt file to object
    public static CounterStaff fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new CounterStaff(p[0].trim(), p[1].trim(), p[2].trim(),Integer.parseInt(p[3].trim()),p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String getDisplayInfo() {
        return "CounterStaff [" + counterStaffId + "] " + getName() + " | " + getPhone();
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Gender: %s | Age: %d | Phone: %s | Email: %s",counterStaffId, name, gender, age, phone, email);
    }
}