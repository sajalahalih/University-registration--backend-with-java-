package com.sbs_pro;

import java.util.Optional;

public class  User {
    String name;
    String role;
    String contactDetails;
    public User(String name, String role, String contactDetails) {
        this.name = name;
        this.role = role;
        this.contactDetails = contactDetails;
    }

    public String getName() {
        return name;
    }

    
    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getContactDetails() {
        return contactDetails;
    }

 
    public synchronized void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    // Optional method for external use if needed
    public Optional<String> getOptionalContactDetails() {
        return Optional.ofNullable(contactDetails);
    }


    public String getRole() {
        return role;
    }


    public synchronized void setRole(String role) {
        this.role = role;
    }
    
}