package com.example.ratecsstaff.model;

/** The kinds of teaching staff a person can rate. */
public enum RoleType {
    TA("Teaching Assistant"),
    PROF("Professor"),
    INSTRUCTOR("Instructor"),
    STAFF("Staff");

    private final String label;

    RoleType(String label) {
        this.label = label;
    }

    /** Human-friendly name shown on the web pages. */
    public String getLabel() {
        return label;
    }
}