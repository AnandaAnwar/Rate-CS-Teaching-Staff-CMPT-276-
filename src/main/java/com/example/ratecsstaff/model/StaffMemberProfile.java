package com.example.ratecsstaff.model;

/**
 * Role-based behaviour. Each role knows how to present itself.
 * The web pages call displayTitle() without caring which role it is: that is polymorphism.
 */
public abstract class StaffMemberProfile {

    protected final String name;

    protected StaffMemberProfile(String name) {
        this.name = name == null ? "" : name;
    }

    /** How the person's name is shown in lists and on the detail page. */
    public abstract String displayTitle();

    /** One-line description of what this role does. */
    public abstract String roleDescription();

    /** Picks the right subclass for a role. */
    public static StaffMemberProfile forRole(RoleType role, String name) {
        if (role == null) {
            return new StaffProfile(name);
        }
        return switch (role) {
            case TA -> new TaProfile(name);
            case PROF -> new ProfProfile(name);
            case INSTRUCTOR -> new InstructorProfile(name);
            case STAFF -> new StaffProfile(name);
        };
    }
}
