package com.mycompany.app;
import jakarta.persistence.*;

@Entity
@Table(name = "BuddyInfo")
public class BuddyInfo {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // SQLite auto-increment
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;

    // Default Constructor
    public BuddyInfo() {
        this(null, null, "0000000");
    }

    // Overloaded Constructor
    public BuddyInfo(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public Long getId() { return id; }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // formatted output for printing
    @Override
    public String toString() {
        return name + " | " + address + " | " + phoneNumber;
    }

    // Test run for this class only
    public static void main(String[] args) {
        BuddyInfo info = new BuddyInfo("Jared", "232 Falconia Ave", "34162086");
        System.out.println("Hello " + info.getName());
        System.out.println("Buddy details: " + info);
    }
}


