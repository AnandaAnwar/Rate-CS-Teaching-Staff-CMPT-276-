package com.example.ratecsstaff.model;

public class StaffProfile extends StaffMemberProfile {
    public StaffProfile(String name) {
        super(name);
    }

    @Override
    public String displayTitle() {
        return name + " (Staff)";
    }

    @Override
    public String roleDescription() {
        return "Staff: supports students and the department outside the classroom.";
    }
}
