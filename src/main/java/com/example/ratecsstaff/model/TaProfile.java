package com.example.ratecsstaff.model;

public class TaProfile extends StaffMemberProfile {
    public TaProfile(String name) {
        super(name);
    }

    @Override
    public String displayTitle() {
        return name + " (TA)";
    }

    @Override
    public String roleDescription() {
        return "Teaching assistant: runs labs and tutorials and marks coursework.";
    }
}
