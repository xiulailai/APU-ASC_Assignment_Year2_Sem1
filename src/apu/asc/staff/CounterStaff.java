package apu.asc.staff;

public class CounterStaff {
    private String counterStaffId;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private String address;
    private String nationality;

    // Constructor
    public CounterStaff(String counterStaffId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        this.counterStaffId = counterStaffId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.nationality = nationality;
    }

    // Getters
    public String getCounterStaffId() { return counterStaffId; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public int    getAge() { return age; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getNationality() { return nationality; }

    // Setters (id is immutable)
    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAge(int age) { this.age = age; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    // Convert object to txt line
    public String toFileString() {
        return String.join(",",
                counterStaffId, name, gender, String.valueOf(age), phone, email, address, nationality);
    }

    // Convert txt line → object
    public static CounterStaff fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new CounterStaff(
                p[0].trim(), p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()), p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | Name: %s | Gender: %s | Age: %d | Phone: %s | Email: %s",
                counterStaffId, name, gender, age, phone, email);
    }
}