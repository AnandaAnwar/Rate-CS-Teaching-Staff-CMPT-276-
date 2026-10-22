package com.example.ratecsstaff.model;

public class ProfProfile extends StaffMemberProfile {
    public ProfProfile(String name) {
        super(name);
    }

    @Override
    public String displayTitle() {
        return "Prof. " + name;
    }

    @Override
    public String roleDescription() {
        return "Professor: lectures, sets the course direction and supervises research.";
    }
}
