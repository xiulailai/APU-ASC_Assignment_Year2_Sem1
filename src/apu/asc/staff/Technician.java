package apu.asc.staff;

public class Technician {
    private String technicianId;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private String address;
    private String nationality;

    //Constructor
    public Technician(String technicianId, String name, String gender, int age, String phone, String email, String address, String nationality) {
        this.technicianId = technicianId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.nationality = nationality;
    }

    //Getter
    public String getTechnicianId(){ return technicianId; }
    public String getName(){ return name; }
    public String getGender(){ return gender; }
    public int getAge(){ return age; }
    public String getPhone(){ return phone; }
    public String getEmail(){ return email; }
    public String getAddress(){ return address; }
    public String getNationality(){ return nationality; }

    //Setter
    public void setName(String name){ this.name = name; }
    public void setGender(String gender){ this.gender = gender; }
    public void setAge(int age){ this.age = age; }
    public void setPhone(String phone){ this.phone = phone; }
    public void setEmail(String email){ this.email = email; }
    public void setAddress(String address){ this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    
    //Convert object to txt file
    public String toFileString() {
        return String.join(",",
                technicianId, name, gender,
                String.valueOf(age),
                phone, email, address, nationality);
    }
    
    // Convert txt file to object
    public static Technician fromFileString(String line) {
        String[] p = line.split(",", 8);
        return new Technician(
                p[0].trim(), p[1].trim(), p[2].trim(),
                Integer.parseInt(p[3].trim()),
                p[4].trim(), p[5].trim(), p[6].trim(), p[7].trim());
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | Name: %s | Gender: %s | Age: %d | Phone: %s | Email: %s",
                technicianId, name, gender, age, phone, email);
    }    
}
