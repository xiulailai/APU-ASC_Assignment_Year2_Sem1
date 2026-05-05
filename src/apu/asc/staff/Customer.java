package apu.asc.staff;


// Subclass of User. Adds customerId as the unique identifier.
public class Customer extends User {
    private String customerId;

    // constructor
    public Customer(String customerId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        super(name, gender, age, phone, email, address, nationality); // inherited fields
        this.customerId = customerId; // own field
    }

    // getter
    public String getCustomerId() { return customerId; }

    //convert object to txt file
    @Override
    public String toFileString() {
        return String.join(",",customerId, name, gender,String.valueOf(age),phone, email, address, nationality);
    }
    
    //conver txt file to object
    public static Customer fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new Customer(p[0].trim(), p[1].trim(), p[2].trim(),Integer.parseInt(p[3].trim()),p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", customerId, name, phone);
    }
}