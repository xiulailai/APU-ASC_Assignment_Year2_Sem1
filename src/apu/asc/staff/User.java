package apu.asc.staff;

// Abstract superclass for all person-type entities in the system.
public abstract class User {
    protected String name;
    protected String gender;
    protected int    age;
    protected String phone;
    protected String email;
    protected String address;
    protected String nationality;

    //Constructor
    public User(String name, String gender, int age, String phone, String email, String address, String nationality) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.nationality = nationality;
    }

    //Getters
    public String getName(){ return name; }
    public String getGender(){ return gender; }
    public int    getAge(){ return age; }
    public String getPhone(){ return phone; }
    public String getEmail(){ return email; }
    public String getAddress(){ return address; }
    public String getNationality(){ return nationality; }

    //Setters
    public void setName(String name){ this.name = name; }
    public void setGender(String gender){ this.gender = gender; }
    public void setAge(int age){ this.age = age; }
    public void setPhone(String phone){ this.phone = phone; }
    public void setEmail(String email){ this.email = email; }
    public void setAddress(String address){ this.address = address; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public abstract String toFileString();
}