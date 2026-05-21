package apu.asc.staff;

public class Service implements FileStorable {
    private String serviceId;
    private String serviceName;
    private int durationHours;
    private double price;
    
    //constructor
    public Service(String serviceId, String serviceName, int durationHours, double price) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.durationHours = durationHours;
        this.price = price;
    }
     
    //getter
    public String getServiceId(){ return serviceId; }
    public String getServiceName(){ return serviceName; }
    public int getDurationHours(){ return durationHours; }
    public double getPrice(){ return price; }
    
    //convert object to txt file
    public String toFileString() {
        return String.join(",", serviceId, serviceName, String.valueOf(durationHours), String.format("%.2f", price));
    }
    
    //convert txt file to object
    public static Service fromFileString(String line) {
        String[] p = line.split(",", 4);
        return new Service(p[0].trim(), p[1].trim(),Integer.parseInt(p[2].trim()),Double.parseDouble(p[3].trim()));
    }  
    
    @Override
    public String toString() {
        return String.format("[%s] %s | Duration: %d hr(s) | Price: RM %.2f",serviceId, serviceName, durationHours, price);
    }
}
