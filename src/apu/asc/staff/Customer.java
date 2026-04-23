package apu.asc.staff;

public class Customer {
    private String customerId;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private String address;
    private String nationality;
 
    // Constructor
    public Customer(String customerId, String name, String gender,
                    int age, String phone, String email,
                    String address, String nationality) {
        this.customerId  = customerId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.nationality = nationality;
    }
 
    // Getters
    public String getCustomerId(){ return customerId; }
    public String getName(){ return name; }
    public String getGender(){ return gender; }
    public int getAge(){ return age; }
    public String getPhone(){ return phone; }
    public String getEmail(){ return email; }
    public String getAddress(){ return address; }
    public String getNationality(){ return nationality; }
 
    // Convert txt line to object
    public static Customer fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new Customer(
                p[0].trim(), p[1].trim(), p[2].trim(),
                Integer.parseInt(p[3].trim()),
                p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }
    
    public String toFileString() {
    return String.join(",",
            customerId, name, gender,
            String.valueOf(age),
            phone, email, address, nationality);
}
 
    @Override
    public String toString() {
        return String.format("%s | %s | %s", customerId, name, phone);
    } 
}
