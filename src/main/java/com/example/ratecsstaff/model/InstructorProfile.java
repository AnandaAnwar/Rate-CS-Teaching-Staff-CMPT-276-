package com.example.ratecsstaff.model;

public class InstructorProfile extends StaffMemberProfile {
    public InstructorProfile(String name) {
        super(name);
    }

    @Override
    public String displayTitle() {
        return name + " (Instructor)";
    }

    @Override
    public String roleDescription() {
        return "Instructor: teaches courses with a focus on classroom delivery.";
    }
}